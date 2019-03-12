package com.yzxtcp.tools;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.yzxtcp.data.UserData;

/**
 * 日志工具类
 * 
 * @author xiaozhenhua
 * 
 */
public class CustomLog {
	private volatile static Executor THREAD_SINGLE_EXECUTOR = null;
	
	public final static String LOGTAG = "yunzhixun";	// logcat名称
	public final static String FILENAME = "YZX_SDK_";	// 保存文件名称

	public static Executor obtainSingleExecutor() {
		if(THREAD_SINGLE_EXECUTOR == null) {
			synchronized (CustomLog.class) {
				if(THREAD_SINGLE_EXECUTOR == null) {
					THREAD_SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();
				}
			}
		}
		return THREAD_SINGLE_EXECUTOR;
	}
	
	public static void v(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.v(LOGTAG, logMe);
		}
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
	}

	public static void v(String tag, final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.v(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}
	
	public static void v(String tag, final String logMe,final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.v(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}

	public static void d(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.d(LOGTAG, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}

	public static void d(String tag, final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.d(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}
	
	public static void d(String tag, final String logMe, final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.d(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}

	public static void i(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.i(LOGTAG, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}

	public static void i(String tag, final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.i(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}

	public static void i(String tag, final String logMe, final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.i(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName);
			}
		});
	}
	
	public static void w(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.w(LOGTAG, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}

	public static void w(String tag, final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.w(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}
	
	public static void w(String tag, final String logMe, final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.w(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName);
			}
		});
	}
	
	public static void e(final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(LOGTAG, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME);
			}
		});
	}

	public static void e(String tag, final String logMe) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, FILENAME); 
			}
		});
	}
	
	public static void e(String tag, final String logMe, final String fileName) {
		if (UserData.isLogSwitch() && logMe != null) {
			android.util.Log.e(tag, logMe);
		}
//		FileTools.saveSdkLog(logMe, "YZX_SDK_");
//		obtainSingleExecutor().execute(new LogRunnable(FILENAME, logMe));
		obtainSingleExecutor().execute(new Log1Runnable() {
			@Override
			public void doInBackground() {
				FileTools.saveSdkLog(logMe, fileName); 
			}
		});
	}
	
	/**
	 * @Title Log1Runnable   
	 * @Description  日志线程执行者
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-3 上午10:15:25
	 */
	public static abstract class Log1Runnable implements Runnable {
		public void run() {
			doInBackground();
		}
		public abstract void doInBackground();
	}
}