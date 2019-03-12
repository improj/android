package com.yzxtcp.tools;

import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.CustomLog.Log1Runnable;

import android.util.Log;

public class TCPLog {
	
	private static final String TAG = "TCPLog";
	private static final String FILENAME = "TCP_LOG_";
	
	public static void v(final String logMe){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.v(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void v(final String logMe, final String fileName){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.v(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}
	
	public static void d(final String logMe){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.d(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void d(final String logMe, final String fileName){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.d(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}
	
	public static void i(final String logMe){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.i(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void i(final String logMe, final String fileName){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.i(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}
	
	public static void w(final String logMe){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.w(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void w(final String logMe,final String fileName){
		if (UserData.isLogSwitch() && logMe != null) {
			Log.w(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}
	
	public static void e(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			Log.e(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void e(final String logMe,final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			Log.e(TAG, logMe);
		}
		CustomLog.obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}

}
