package com.yzxtcp.tcp.identity.impl;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tcp.identity.IReconnectPlicy;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.TCPLog;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
/**
 * 循环重连策略
 * @author zhuqian
 */
public class CycleReconnectPlicy extends IReconnectPlicy{
	private TCPServer tcpServer;
	
	//重连码
	private static final int RECONNECT_CODE = 4004;
	
	private int[] mRetyrArray = { 1, 1, 1, 1, 2, 2, 4, 6 };
	private int reConnCount = 0;//重连次数
//	private static final int MAXRECONNCOUNT = 8;//最大重连次数
	private Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECONNECT_CODE:
				TCPLog.d("TCPReconnectHelper 开启重连 reConnCount ： "+reConnCount);
				reConnCount++;
				tcpServer.CycleReconnect();
				break;
			default:
				break;
			}
		};
	};
	
	public CycleReconnectPlicy(TCPServer tcpServer){
		this.tcpServer = tcpServer;
	}
	
	@Override
	public void reconn(){
		if(mHandler.hasMessages(RECONNECT_CODE)){
			mHandler.removeMessages(RECONNECT_CODE);
		}
		// 重连时如果检测无网络不让重连，直接返回
		if(!NetWorkTools.isNetWorkConnect(YzxTCPCore.getContext())) {
			TCPLog.d("TCP CycleReconnect failure, reason:no network ...");
			TCPListenerManager.getInstance().notifySdkStatus(
					new UcsReason(UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT)
							.setMsg("网络未连接"));
			//更新网络状态
			if(YzxTCPCore.getContext() != null) {
				LocalBroadcastManager.getInstance(YzxTCPCore.getContext()).sendBroadcast(
						new Intent("com.yzx.update.network.state").putExtra("update_network_state", YzxTCPCore.STATUS_ERROR));
//				YzxIMCoreService.getInstance().sendBroadcast(new Intent("com.yzx.update.network.state").putExtra("update_network_state", YzxIMCoreService.STATUS_ERROR));
			}
			reConnCount = 0;
			return;
		}
		if(reConnCount >= mRetyrArray.length){ // reConnCouture为8时，重连结束
			TCPLog.d("TCPReconnectHelper 重连8次结束");
			reConnCount = 0;
			return;
		}
		mHandler.sendEmptyMessageDelayed(RECONNECT_CODE, mRetyrArray[reConnCount]*30000);
	}
	@Override
	public void cancelReconn() {
		mHandler.removeMessages(RECONNECT_CODE);
		reConnCount = 0;
	}
}
