package org.webrtc.threadengine;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import android.R.integer;
import android.content.Context;
import android.os.Process;
import android.util.Log;

public class WebRtcThreadManager {
	private static final String TAG = "WebRtcThreadManager";
	
	private final Context mContext;
	
	private final long mNativeThread;
	private WebRtcThread webrtcThread = null;
	
	private int mPrio = Process.THREAD_PRIORITY_DEFAULT;
	private String mThreadName = null;
	private int mThreadId = 0;
	
	private class WebRtcThread extends Thread {
		private volatile boolean keepAlive = true;

		public WebRtcThread(String name) {
			// TODO Auto-generated constructor stub
			super(name);
		}
		
		@Override
		public void run() {
			Logi("WebRtcThread" + WebRtcAudioUtils.getThreadInfo() + "@prio=" + mPrio);
			
			Process.setThreadPriority(mPrio);
			
			while(keepAlive) {
				if (!nativeProcess(mNativeThread)) {
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
	
	public WebRtcThreadManager(Context context, long nativeThread) {
		this.mContext = context;
		this.mNativeThread = nativeThread;		
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
		this.mPrio = ConvertToSystemPriority(priority);
		return true;
	}
	
	public boolean SetThreadName(String name) {		
		if ( null == name ) {
			this.mThreadName = "webrtc";
		} else {
			this.mThreadName = name;
		}
		return true;
	}
	
	public int GetThreadId() {		
		this.mThreadId = (int) webrtcThread.getId();
		return this.mThreadId;
	}
	
	public boolean StartThread() {
		Logi("StartThread " + WebRtcAudioUtils.getThreadInfo());
		if ( null != webrtcThread ) {
			webrtcThread.joinThread();
			webrtcThread = null;
		}
		
		webrtcThread = new WebRtcThread(mThreadName);
		webrtcThread.start();
		return true;
	}
	
	public boolean StopThread() {
		Logi("StopThread " + WebRtcAudioUtils.getThreadInfo());
		if ( null != webrtcThread ) {
			webrtcThread.joinThread();
			webrtcThread = null;
		}
		return true;
	}
	
	private static void Logi(String msg) {
		Log.i(TAG, msg);
	}

	private static void Loge(String msg) {
		Log.e(TAG, msg);
	}
	
	private native boolean nativeProcess(long nativeThread);
}
