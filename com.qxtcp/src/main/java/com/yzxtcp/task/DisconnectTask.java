package com.yzxtcp.task;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.task.factory.ITcpTaskFactory;
import com.yzxtcp.tcp.AlarmTools;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tcp.listener.ShutConnCallback;
import com.yzxtcp.tcp.listener.TCPResultCallback;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.TCPLog;

/**
 * 断开任务
 * 
 * @author zhuqian
 */
public class DisconnectTask<Result> extends BaseTcpTask<Result> {
	private TCPServer tcpServer;

	public DisconnectTask(TCPServer tcpServer) {
		super();
		this.tcpServer = tcpServer;
	}

	@Override
	public Result doInBackground() {
		TCPLog.d("disconnectTask doInBackground ... thread: " + Thread.currentThread().getName());
		// 后台运行断开连接
		tcpServer.tcpManager.dissConnect();
		// 停止心跳
		AlarmTools.stopAll();
		return null;
	}

	@Override
	public void onPostExecute(Result result) {
		TCPLog.d("disconnectTask onPostExecute ... thread: " + Thread.currentThread().getName());
		if (callback != null) {
			ShutConnCallback callBack = (ShutConnCallback) callback;
			callBack.onShutConnFinish();
		}
	}

	@Override
	public void onPreExecute() {
		TCPLog.d("disconnectTask onPreExecute ... thread: " + Thread.currentThread().getName());
		// 移除任务引用
		TCPLog.d("DisconnectTask onPreExecute ："
				+ this.tcpServer.tcpFactory
						.removeTask(ITcpTaskFactory.DISCONNECT_TASK));
		if (!NetWorkTools.isNetWorkConnect(YzxTCPCore.getContext())) {
			TCPListenerManager.getInstance().notifySdkStatus(
					new UcsReason(UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT)
							.setMsg("网络未连接"));
			//更新网络状态
			if(YzxTCPCore.getContext() != null) {
				LocalBroadcastManager.getInstance(YzxTCPCore.getContext()).sendBroadcast(
						new Intent("com.yzx.update.network.state").putExtra("update_network_state", YzxTCPCore.STATUS_ERROR));
			}
		} else {
			// 通知连接失败
			TCPListenerManager.getInstance().notifySdkStatus(
					new UcsReason().setReason(
							UcsErrorCode.NET_ERROR_TCPCONNECTFAIL).setMsg(
							"TCP 连接失败"));
		}
	}

	@Override
	public String toString() {
		return "DisconnectTask ...";
	}

	@Override
	public void setTcpCallback(TCPResultCallback callback) {
		this.callback = callback;
	}
}
