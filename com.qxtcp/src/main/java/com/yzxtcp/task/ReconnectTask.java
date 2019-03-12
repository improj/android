package com.yzxtcp.task;

import java.util.Date;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.task.factory.ITcpTaskFactory;
import com.yzxtcp.tcp.AlarmTools;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tcp.listener.ConnectCallback;
import com.yzxtcp.tcp.listener.TCPResultCallback;
import com.yzxtcp.tools.CpsUtils;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.TCPLog;

/**
 * 重连任务
 * 
 * @author zhuqian
 */
public class ReconnectTask<Result> extends BaseTcpTask<Result> {
	private final TCPServer tcpServer;

	public ReconnectTask(TCPServer tcpServer) {
		super();
		this.tcpServer = tcpServer;
	}

	@Override
	public Result doInBackground() {
		TCPLog.d("reconnectTask doInBackground ... thread: " + Thread.currentThread().getName());
		TCPResult  tcpResult = new TCPResult();
		//先断开
		this.tcpServer.tcpManager.dissConnect();
		if(!NetWorkTools.isNetWorkConnect(YzxTCPCore.getContext())){
			//当前没有网络，连接失败
			TCPLog.d("current NetWork is not connect... reconnect error...");
			tcpResult.isSuccess = false;
			tcpResult.errorCode = UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT;
			//更新网络状态
			if(YzxTCPCore.getContext() != null) {
				LocalBroadcastManager.getInstance(YzxTCPCore.getContext()).sendBroadcast(
						new Intent("com.yzx.update.network.state").putExtra("update_network_state", YzxTCPCore.STATUS_ERROR));
//				YzxIMCoreService.getInstance().sendBroadcast(new Intent("com.yzx.update.network.state").putExtra("update_network_state", YzxIMCoreService.STATUS_ERROR));
			}
			// 下面的方法更新不了coreService里面的网络状态Status
//			if(YzxIMCoreService.getInstance() != null &&
//					YzxIMCoreService.getInstance() instanceof YzxIMCoreService){
//				((YzxIMCoreService)YzxIMCoreService.getInstance()).setStatus(YzxIMCoreService.STATUS_ERROR);
//			}
			return (Result) tcpResult;
		}
		
		// 每天的第一次连接都优先从cps拉取
		// 其余优先取本地的proxy列表，若本地没有则从cps拉取
		boolean isSameDay = CpsUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(CpsUtils.lastGetProxyTime));
		boolean isroxyIPOK = false;
		if (isSameDay) {
			isroxyIPOK = CpsUtils.hasLocalProxyIP() || CpsUtils.getProxyIP();
		} else {
			isroxyIPOK = CpsUtils.getProxyIP() || CpsUtils.hasLocalProxyIP();
		}
		if (isroxyIPOK) {
			if(this.tcpServer.tcpManager.reconnect()){
				tcpResult.isSuccess = true;
			}else{
				if (isSameDay) {
					//重连失败后更新本地proxy列表
					CpsUtils.getProxyIP();
				}
				tcpResult.isSuccess = false;
				tcpResult.errorCode = UcsErrorCode.NET_ERROR_TCPCONNECTFAIL;
			}
		} else {
			tcpResult.isSuccess = false;
			tcpResult.errorCode = UcsErrorCode.NET_ERROR_GET_CPS;
		}
		return (Result) tcpResult;
	}
	@Override
	public void onPostExecute(Result result) {
		ConnectCallback connectCallback = (ConnectCallback) callback;
		TCPResult tcpResult = (com.yzxtcp.task.BaseTcpTask.TCPResult) result;
		if(tcpResult.isSuccess){
			TCPLog.d("ReconnectTask onPostExecute success ...");
			connectCallback.onSuccess();
		}else{
			TCPLog.d("ReconnectTask onPostExecute failure ...");
			connectCallback.onFail(new UcsReason().setReason(tcpResult.errorCode));
		}
		//移除任务引用
		TCPLog.d("ReconnectTask onPostExecute ："+this.tcpServer.tcpFactory.removeTask(ITcpTaskFactory.RECONNECT_TASK));
	}
	@Override
	public void onPreExecute() {
		//停止心跳
		AlarmTools.stopAll();
		TCPListenerManager.getInstance().notifySdkStatus(
				new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTING)
						.setMsg("TCP 连接中"));
	}

	@Override
	protected void onCancelled(Result result) {
		if(result != null){
			TCPResult tcpResult = (TCPResult) result;
			if(tcpResult.isSuccess){
				TCPLog.d("ReconnectTask has cancel no needed callback..");
			}
		}
		TCPLog.d("ReconnectTask onCancelled...");
	}
	@Override
	public String toString() {
		return "ReconnectTask ...";
	}

	@Override
	public void setTcpCallback(TCPResultCallback callback) {
		this.callback = callback;
	}
}
