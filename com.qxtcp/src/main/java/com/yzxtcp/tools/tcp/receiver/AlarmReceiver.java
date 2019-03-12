package com.yzxtcp.tools.tcp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.yzxtcp.tcp.AlarmTools;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		sendPacket();
	}
	
	
	/**
	 * 发送心跳
	 * 
	 */
	private static void sendPacket(){
		TCPLog.d("SEND PING...");
		// 因为setWindow只执行一次，所以要重新定义闹钟实现循环。
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AlarmTools.startAlarm(AlarmTools.PING_TIME);
		}
		AlarmTools.startBackTcpPing();
		IGGBaseRequest basereq = new IGGBaseRequest() {
			@Override
			public void onSendMessage() {
				TCPServer.obtainTCPService().sendPacket(600100,this);
			}
		};
		basereq.onSendMessage();
	}

}
