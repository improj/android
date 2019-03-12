package com.yzxtcp.tools.tcp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.TCPLog;

public class MsgBackReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TCPLog.d( "心跳重连...");
		TCPServer.obtainTCPService().reconnect();
	}
}
