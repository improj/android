package com.yzx.controller;  

import com.gl.softphone.UGoManager.IUGoCallbacks;
import com.yzx.api.UCSCall;
import com.yzx.listenerInterface.CallStateListener;

public abstract class UGoCallbacks implements IUGoCallbacks {

	@Override
	public abstract void eventCallback(int ev_type, int ev_reason, String message,String param);

	@Override
	public abstract void sendCallback(byte[] message, int len);

	@Override
	public abstract void traceCallback(String summary, String detail, int level);
	
	@Override
	public abstract void screenshotCallback(byte[] dst_argb, int dst_stride, int width,
			int height, int islocal, int screen_type);

	@Override
	public void encryptCallback(byte[] inMsg, byte[] outMsg, int inLen,int[] outLen) {
//		long encryptTimeFront = System.nanoTime() / 1000;
	    for (CallStateListener csl : UCSCall.getCallStateListener()) {
            csl.onEncryptStream(inMsg, outMsg, inLen, outLen);
        }
//	    long encryptTimeBehind = System.nanoTime() / 1000; // 微秒为单位 1毫秒=1000微秒
//	    if((encryptTimeBehind - encryptTimeFront) > 3000) {
//	    	CustomLog.v("callId:" + UCSCall.getCurrentCallId() + " encryptTime: " + (encryptTimeBehind - encryptTimeFront),"encrypt_log");
//	    }
	}

	@Override
	public void decryptCallback(byte[] inMsg, byte[] outMsg, int inLen,
			int[] outLen) {
//		long decryptTimeFront = System.nanoTime() / 1000;
	    for (CallStateListener csl : UCSCall.getCallStateListener()) {
            csl.onDecryptStream(inMsg, outMsg, inLen, outLen);
        }
//	    long decryptTimeBehind = System.nanoTime() / 1000;
//	    if((decryptTimeBehind - decryptTimeFront) > 3000) {
//	    	CustomLog.v("callId:" + UCSCall.getCurrentCallId() + " decryptTime: " + (decryptTimeBehind - decryptTimeFront),"decrypt_log");
//	    }
	}

	@Override
	public int mediaProcCallback(short[] inSample, short[] outSample,
			int samples, int freqHz, boolean isStereo) {
		return 0;
	}

	@Override
	public void initPlayout(int sample_rate, int bytes_per_sample,
			int num_of_channels) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.initPlayout(sample_rate, bytes_per_sample, num_of_channels);
		}
	}

	@Override
	public void initRecording(int sample_rate, int bytes_per_sample,
			int num_of_channels) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.initRecording(sample_rate, bytes_per_sample, num_of_channels); 
		}
	}

	@Override
	public int writePlayoutData(byte[] outData, int outSize) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			return csl.writePlayoutData(outData, outSize);
		}
		return -1;
	}

	@Override
	public int readRecordingData(byte[] inData, int inSize) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			return csl.readRecordingData(inData, inSize);
		}
		return -1;
	}

}
  
