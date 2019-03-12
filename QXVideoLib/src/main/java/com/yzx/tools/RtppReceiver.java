package com.yzx.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RtppReceiver extends BroadcastReceiver {

/*	@Override
	public void onReceive(Context context, Intent intent) {
		ServiceConfigTools.cancleRtppPing();
		ServiceConfigTools.pingRtpp();
	}*/
	
	
	@Override
	public void onReceive(final Context mContext, Intent intent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				RtppConfigTools.cancleRtppPing(mContext);
				RtppConfigTools.pingRtpp(mContext);
			}
		}).start();
	}
}
