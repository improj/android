package com.yzxtcp.task;

import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.task.factory.ITcpTaskFactory;
import com.yzxtcp.tcp.AlarmTools;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tcp.listener.ConnectCallback;
import com.yzxtcp.tcp.listener.TCPResultCallback;
import com.yzxtcp.tools.CpsUtils;
import com.yzxtcp.tools.TCPLog;

/**
 * 连接任务
 * 
 * @author zhuqian
 */
public class ConnectTask<Result> extends BaseTcpTask<Result> {

	private final TCPServer tcpServer;

	public ConnectTask(TCPServer tcpServer) {
		super();
		this.tcpServer = tcpServer;
	}

	@Override
	public Result doInBackground() {
		TCPLog.d("connectTask doInBackground ... thread: " + Thread.currentThread().getName());
		TCPResult  tcpResult = new TCPResult();
		if (CpsUtils.getProxyIP() || CpsUtils.hasLocalProxyIP()) {
			// 获取cps成功
			if(this.tcpServer.tcpManager.reconnect()){
				tcpResult.isSuccess = true;
			}else{
				tcpResult.isSuccess = false;
				tcpResult.errorCode = UcsErrorCode.NET_ERROR_CONNECTFAIL;
			}
		} else {
			tcpResult.isSuccess = false;
			tcpResult.errorCode = UcsErrorCode.NET_ERROR_GET_CPS;
		}
		return (Result) tcpResult;
	}

	@Override
	public void onPostExecute(Result result) {
		TCPLog.d("connectTask onPostExecute ... thread: " + Thread.currentThread().getName());
		// 移除任务引用
		ConnectCallback connectCallback = (ConnectCallback) callback;
		TCPResult tcpResult = (com.yzxtcp.task.BaseTcpTask.TCPResult) result;
		if(tcpResult.isSuccess){
			connectCallback.onSuccess();
		}else{
			connectCallback.onFail(new UcsReason().setReason(tcpResult.errorCode));
		}
		TCPLog.d("ConnectTask onPostExecute ："+this.tcpServer.tcpFactory.removeTask(ITcpTaskFactory.CONNECT_TASK));
	}

	@Override
	public void onPreExecute() {
		TCPLog.d("connectTask onPreExecute ... thread: " + Thread.currentThread().getName());
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
		TCPLog.d("ConnectTask onCancelled...");
	}

	@Override
	public String toString() {
		return "ConnectTask ...";
	}

	@Override
	public void setTcpCallback(TCPResultCallback callback) {
		this.callback = callback;
	}
}
