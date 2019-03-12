package org.webrtc.threadengine;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import android.content.Context;
import android.os.Process;
import android.util.Log;

public class ICEAndroidThreadManager {
	private final static String TAG = "ICEThreadMgr";
	
	private final Context mContext;
	private final long mNativeICE;
	private ICEThread mICEThread = null;
	
	private int mPrio = Process.THREAD_PRIORITY_DEFAULT;
	private String mThreadName = null;
	private long mThreadId = 0;
	
	private class ICEThread extends Thread {
		private volatile boolean keepAlive = true;

		public ICEThread(String name) {
			// TODO Auto-generated constructor stub
			super(name);
		}
		
		@Override
		public void run() {
			Logi("ICEThread " + WebRtcAudioUtils.getThreadInfo() + "@prio=" + mPrio);
			
			Process.setThreadPriority(mPrio);
			
			while(keepAlive) {
				if (!nativeProcess(mNativeICE)) {
					keepAlive = false;
					return;
				}
			}
		}
		
		public void joinThread() {
			keepAlive = false;
			while (isAlive()) {
				try {
					join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ICEAndroidThreadManager(Context context, long nativeIce) {
		Logi("@ctor");
		this.mContext = context;
		this.mNativeICE = nativeIce;
	}
	
	public int ConvertToSystemPriority(int priority) {
		switch (priority) {
		case 1: // kLowPriority
			return Process.THREAD_PRIORITY_BACKGROUND;
		case 2: // kNormalPriority
			return Process.THREAD_PRIORITY_DEFAULT;
		case 3: // kHighPriority
			return Process.THREAD_PRIORITY_DISPLAY;
		case 4: // kHighestPriority
			return Process.THREAD_PRIORITY_URGENT_DISPLAY;
		case 5: // kRealtimePriority
			return Process.THREAD_PRIORITY_URGENT_AUDIO;
		default:
			return Process.THREAD_PRIORITY_DEFAULT;
		}
	}
	
	public boolean SetThreadPriority(int priority) {
		Logi("@SetThreadPriority: " + priority);
		this.mPrio = ConvertToSystemPriority(priority);
		return true;
	}
	
	public boolean SetThreadName(String name) {		
		if ( null == name ) {
			this.mThreadName = "webrtc";
		} else {
			this.mThreadName = name;
		}
		Logi("@SetThreadName: " + mThreadName);
		return true;
	}
	
	public long GetThreadId() {		
		this.mThreadId = mICEThread.getId();
		return this.mThreadId;
	}
	
	public boolean StartThread() {
		Logi("StartThread " + WebRtcAudioUtils.getThreadInfo());
		if ( null != mICEThread ) {
			mICEThread.joinThread();
			mICEThread = null;
		}
		
		mICEThread = new ICEThread(mThreadName);
		mICEThread.start();
		return true;
	}
	
	public boolean StopThread() {
		Logi("StopThread " + WebRtcAudioUtils.getThreadInfo());
		if ( null != mICEThread ) {
			mICEThread.joinThread();
			mICEThread = null;
		}
		return true;
	}
	
	private static void Logi(String msg) {
		Log.i(TAG, msg);
	}

	private static void Loge(String msg) {
		Log.e(TAG, msg);
	}
	
	private native boolean nativeProcess(long nativeICE);
}
