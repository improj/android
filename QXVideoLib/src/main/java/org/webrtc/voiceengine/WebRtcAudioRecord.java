/*
 *  Copyright (c) 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.voiceengine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.yzxtcp.tools.CustomLog;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

public class WebRtcAudioRecord {
	private static final boolean DEBUG = false;

	private static final String TAG = "WebRtcAudioRecord";

	private final boolean testSwitch = false; // the switch to write file;
	private boolean existSdcard = false;
	private final String recFileName = "/sdcard/record.pcm";
	private FileOutputStream fosRecord = null;

	/**
	 * Stereo recording channel is default.
	 */
	private static final int CHANNELS = 1;//chgx set this default value to 2

	/**
	 * Default audio data format is PCM 16 bit per sample.
	 * Guaranteed to be supported by all devices.
	 */
	private static final int BITS_PER_SAMPLE = 16;

	/**
	 * Number of bytes per audio frame for mono channel.
	 * Example: 16-bit PCM in mono => 1*(16/8) = 2 [bytes/frame]
	 */
	private static final int BYTES_PER_FRAME_MONO = BITS_PER_SAMPLE / 8;
	
	/**
	 * Number of bytes per audio frame for stereo channel.
	 * Example: 16-bit PCM in stereo => 2*(16/8)=4 [bytes/frame]
	 */
	private static final int BYTES_PER_FRAME = CHANNELS * (BITS_PER_SAMPLE / 8);

	/**
	 * Requested size of each recorded buffer provided to the client.
	 */
	private static final int CALLBACK_BUFFER_SIZE_MS = 10;

	/**
	 * Average number of callbacks per second.
	 */
	private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;
	
	/**
	 * Max byteBuffer size set to support 48khz samplerate
	 */
	private static final int MAX_BYTE_BUFFER_SIZE = BYTES_PER_FRAME * 480;

	private ByteBuffer byteBuffer;
	private byte[] tempBufRec;
	private int bytesPerBuffer;
	private int framesPerBuffer;
	private int sampleRate;

	private final long nativeAudioRecord;
	private final AudioManager audioManager;
	private final Context context;

	private AudioRecord audioRecord = null;
	private AudioRecordThread audioThread = null;
	private AudioDeviceParam audioDeviceParam = null;

	// del by vintonliu for support lower api
	// private AcousticEchoCanceler aec = null;
	private boolean useBuiltInAEC = false;

	private String brandString = null;
	private String modelString = null;
	private int apiLevel = 0;
	private boolean NoRecord=false;
	/**
	 * If recording in Stereo channel
	 */
	private boolean useStereo = false;
	
	/**
	 * Audio thread which keeps calling ByteBuffer.read() waiting for audio to
	 * be recorded. Feeds recorded data to the native counterpart as a periodic
	 * sequence of callbacks using DataIsRecorded(). This thread uses a
	 * Process.THREAD_PRIORITY_URGENT_AUDIO priority.
	 */
	private class AudioRecordThread extends Thread {
		private volatile boolean keepAlive = true;

		public AudioRecordThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
			Logd("AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());

			if ( testSwitch ) {
				if ( existSdcard ) {
					try {
						fosRecord = new FileOutputStream(recFileName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			long lastTime = System.nanoTime();
			try {
				while (keepAlive) {
					if(NoRecord)
					{
						nativeDataIsRecorded(bytesPerBuffer, nativeAudioRecord);
						Thread.sleep(1);
						continue;
					}
					int bytesRead = 0;
					byteBuffer.rewind();  // Reset the position to start of buffer
					if ( !WebRtcAudioUtils.runningOnJellyBeanOrHigher() ) {
						bytesRead = audioRecord.read(tempBufRec, 0, bytesPerBuffer);
						byteBuffer.put(tempBufRec);
					} else {
						bytesRead = audioRecord.read(byteBuffer, bytesPerBuffer);
						
						if (testSwitch ){
							byteBuffer.get(tempBufRec);
						}
					}
					if (bytesRead == bytesPerBuffer) {
						nativeDataIsRecorded(bytesRead, nativeAudioRecord);
					} else {
						Loge("AudioRecord.read failed: " + bytesRead);
						if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
							keepAlive = false;
						}
					}
					if (DEBUG) {
						long nowTime = System.nanoTime();
						long durationInMs = TimeUnit.NANOSECONDS.toMillis((nowTime - lastTime));
						lastTime = nowTime;
						Logd("bytesRead[" + durationInMs + "] " + bytesRead);
					}
	
					if (testSwitch ) {
						if ( fosRecord != null ) {
							try {
								fosRecord.write(tempBufRec, 0, bytesRead);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				Logd("AudioRecordThread debug 1");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
				Logd("AudioRecordThread debug 2");
				if(!NoRecord)
					audioRecord.stop();
				Logd("AudioRecordThread debug 3");
			} catch (IllegalStateException e) {
				Loge("AudioRecord.stop failed: " + e.getMessage());
			}
		}

		public void joinThread() {
			Logd("try to stop AudioRecordJavaThread");
			keepAlive = false;
			while (isAlive()) {
				try {
					// wait 20ms
					join(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Logd("AudioRecordJavaThread stop successed.");
		}
	}

	public WebRtcAudioRecord(Context context, long nativeAudioRecord) {
		Logd("ctor" + WebRtcAudioUtils.getThreadInfo());
		this.context = context;
		this.nativeAudioRecord = nativeAudioRecord;
		this.audioDeviceParam = AudioDeviceParam.getInstance();
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
//		sampleRate = GetNativeSampleRate();
		this.NoRecord=audioDeviceParam.bNoRecord;
		sampleRate = getRecordSampleRate(GetNativeSampleRate());
		bytesPerBuffer = BYTES_PER_FRAME * (sampleRate / BUFFERS_PER_SECOND);
		framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
//		byteBuffer = byteBuffer.allocateDirect(bytesPerBuffer);
		byteBuffer = ByteBuffer.allocateDirect(MAX_BYTE_BUFFER_SIZE); // Max 10 ms @ 48
		if ( bytesPerBuffer > MAX_BYTE_BUFFER_SIZE ) {
			Loge("WebRtcAudioRecord instance bytesPerBuffer over max buffer size.");
		}
		Logd("byteBuffer.capacity: " + byteBuffer.capacity());
		tempBufRec = new byte[MAX_BYTE_BUFFER_SIZE];

		// Rather than passing the ByteBuffer with every callback (requiring
		// the potentially expensive GetDirectBufferAddress) we simply have the
		// the native class cache the address to the memory once.
		nativeCacheDirectBufferAddress(byteBuffer, nativeAudioRecord);

		// if (DEBUG) {
		WebRtcAudioUtils.logDeviceInfo(TAG);
		// }
		
		brandString = WebRtcAudioUtils.Brand();
		modelString = WebRtcAudioUtils.Model();
		apiLevel = WebRtcAudioUtils.SDK();
		
		if (testSwitch) {
			try {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Logd("SD file is exits....");
					existSdcard = true;
				} else {
					Loge("SD file don't exits....");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

	private int GetNativeSampleRate() {
		int recSampleRate =  WebRtcAudioUtils.GetNativeSampleRate(audioManager);
		recSampleRate = getRecordSampleRate(recSampleRate);
		return recSampleRate;
	}

	public static boolean BuiltInAECIsAvailable() {
		// AcousticEchoCanceler was added in API level 16 (Jelly Bean).
		if (!WebRtcAudioUtils.runningOnJellyBeanOrHigher()) {
			return false;
		}
		// TODO(henrika): add black-list based on device name. We could also
		// use uuid to exclude devices but that would require a session ID from
		// an existing AudioRecord object.
		// del by vintonliu for support lower api
		// return AcousticEchoCanceler.isAvailable();
		return false;
	}

	private boolean EnableBuiltInAEC(boolean enable) {
		Logd("EnableBuiltInAEC(" + enable + ')');
		// AcousticEchoCanceler was added in API level 16 (Jelly Bean).
		if (!WebRtcAudioUtils.runningOnJellyBeanOrHigher()) {
			return false;
		}
		// Store the AEC state.
		useBuiltInAEC = enable;
		// Set AEC state if AEC has already been created.
		// del by vintonliu for support lower api
		/*
		 * if (aec != null) { 
		 * int ret = aec.setEnabled(enable); if (ret !=
		 * AudioEffect.SUCCESS) {
		 * Loge("AcousticEchoCanceler.setEnabled failed"); return false; }
		 * Logd("AcousticEchoCanceler.getEnabled: " + aec.getEnabled()); }
		 */
		return true;
	}

	@SuppressWarnings("unused")
	private int InitRecording(int sampleRate) {
		if ( audioRecord != null ) {
			Logd("InitRecording: Release the previous audioRecord.");
			audioRecord.release();
			audioRecord = null;
		}
		
		Logd("InitRecording(sampleRate=" + sampleRate + ")");
		
		sampleRate = getRecordSampleRate(sampleRate);
		
		if(NoRecord)
		{
			return framesPerBuffer;
		}
		
		/* Get channelConfig and audioSource for MobiePhone */
		int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        channelConfig = getRecordChannel(channelConfig);
        
        int audioSource = AudioSource.DEFAULT;
        audioSource = getAudioSource(audioSource);
        
        //update framesPerBuffer maybe sampleRate is changed
  		framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
  		if ( (channelConfig == AudioFormat.CHANNEL_IN_MONO)
  				|| ( channelConfig == AudioFormat.CHANNEL_CONFIGURATION_MONO)) {
  			bytesPerBuffer = BYTES_PER_FRAME_MONO  * framesPerBuffer;
  			useStereo = false;
  		} else {
  			bytesPerBuffer = BYTES_PER_FRAME * framesPerBuffer;
  			useStereo = true;
  		}  		
  		
  		Logd("InitRecording: bytesPerBuffer: " + bytesPerBuffer);
  		if ( bytesPerBuffer > MAX_BYTE_BUFFER_SIZE ) {
  			Loge("WebRtcAudioRecord InitRecording bytesPerBuffer over max buffer size.");
  		}
        
		// Get the minimum buffer size required for the successful creation of
		// an AudioRecord object, in byte units.
		// Note that this size doesn't guarantee a smooth recording under load.
		// TODO(henrika): Do we need to make this larger to avoid underruns?
		int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
							channelConfig, AudioFormat.ENCODING_PCM_16BIT);
		Logd("AudioRecord.getMinBufferSize: " + minBufferSize);
		if ( minBufferSize < 2048 ) {
			minBufferSize *= 2;
		}

		// double size to be more safe
		minBufferSize = minBufferSize * 2;
        
		// del by vintonliu for support lower api
		/*
		 * if (aec != null) { aec.release(); aec = null; }
		 */

		int bufferSizeInBytes = Math.max(byteBuffer.capacity(), minBufferSize);
		Logd("bufferSizeInBytes: " + bufferSizeInBytes);
		try {
			audioRecord = new AudioRecord(audioSource,
										sampleRate, 
										channelConfig,
										AudioFormat.ENCODING_PCM_16BIT, 
										bufferSizeInBytes);

		} catch (IllegalArgumentException e) {
			Logd(e.getMessage());
			return -1;
		}
		
		// check that the audioRecord is ready to be used
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Loge("AudioRecord create failed. sampleRate = " + sampleRate);
            Loge("InitRecording: try again to new AudioRecord.");

            //try it again when first new AudioRecord fail add by zch 2013/10/23
            StopRecording();
            try {
                audioRecord = new AudioRecord(
                		AudioSource.DEFAULT,
                        sampleRate,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSizeInBytes);

            } catch (Exception e) {
                Loge(e.getMessage());
                return -1;
            }
        }
        
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Loge("InitRecording: failed to new AudioRecord!!");
            if ( null != audioRecord ) {
    			audioRecord.release();
    			audioRecord = null;
    		}
            return -1;
        }

		Logd("AudioRecord "
				+
				// "session ID: " + audioRecord.getAudioSessionId() + ", " +
				"audio format: " + audioRecord.getAudioFormat() + ", "
				+ "channels: " + audioRecord.getChannelCount() + ", "
				+ "sample rate: " + audioRecord.getSampleRate());
		Logd("AcousticEchoCanceler.isAvailable: " + BuiltInAECIsAvailable());
		if (!BuiltInAECIsAvailable()) {
			return framesPerBuffer;
		}

		// del by vintonliu for support lower api
		/*
		 * aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
		 * if (aec == null) { Loge("AcousticEchoCanceler.create failed"); return
		 * -1; } int ret = aec.setEnabled(useBuiltInAEC); if (ret !=
		 * AudioEffect.SUCCESS) {
		 * Loge("AcousticEchoCanceler.setEnabled failed"); return -1; }
		 * Descriptor descriptor = aec.getDescriptor();
		 * Logd("AcousticEchoCanceler " + "name: " + descriptor.name + ", " +
		 * "implementor: " + descriptor.implementor + ", " + "uuid: " +
		 * descriptor.uuid); Logd("AcousticEchoCanceler.getEnabled: " +
		 * aec.getEnabled());
		 */
		return framesPerBuffer;
	}

	private boolean StartRecording() {
		Logd("StartRecording enter");
		if(NoRecord)
		{
			audioThread = new AudioRecordThread("AudioRecordJavaThread");
			audioThread.start();
			return true;
		}
		if ( null == audioRecord ) {
			Loge("StartRecording: Failed on audioRecord is null.");
			return false;
		}
		if ( null != audioThread ) {
			Logd("StartRecording: release previous audioThread.");
			audioThread.joinThread();
			audioThread = null;
		}
		
		try {
			audioRecord.startRecording();
		} catch (IllegalStateException e) {
			Loge("AudioRecord.startRecording failed: " + e.getMessage());
			if ( null != audioRecord ) {
    			audioRecord.release();
    			audioRecord = null;
    		}
			return false;
		}
		if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
			Loge("AudioRecord recording state(" + audioRecord.getRecordingState() +") error.");
			if ( null != audioRecord ) {
    			audioRecord.release();
    			audioRecord = null;
    		}
			return false;
		}
		
		audioThread = new AudioRecordThread("AudioRecordJavaThread");
		audioThread.start();
		Logd("StartRecording leave");
		return true;
	}

	private boolean StopRecording() {
		Logd("StopRecording enter");
		if ( null != audioThread ) {
			Logd("StopRecording debug 1");
			audioThread.joinThread();
			Logd("StopRecording debug 2");
			audioThread = null;
		}
		// del by vintonliu for support lower api
		/*
		 * if (aec != null) { aec.release(); aec = null; }
		 */
		if ( null != audioRecord ) {
			Logd("StopRecording debug 3");
			audioRecord.release();
			Logd("StopRecording debug 4");
			audioRecord = null;
		}
		
		if ( testSwitch ) {
			if ( existSdcard ) {
	            if ( fosRecord != null ) {
	                try {
	                	fosRecord.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
			}
        }
		Logd("StopRecording leave");
		return true;
	}
	
	/**
	 * If stereo recording available
	 * @return true for available, else false
	 */
	private boolean StereoRecordingAvailable() {
		return useStereo;
	}
	
	/**
     * Adaptive audioSource for difference MobiePhone
     *
     * @param audioSource default value
     * @return audioSource suitable for phone
     */
    private int getAudioSource(int audioSource) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int newAudioSource = audioSource;
        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newAudioSource = audioDeviceParam.getRecordSource();

            switch (newAudioSource) {
                case 0:
                    newAudioSource = AudioSource.DEFAULT;
                    break;

                case 1:
                    newAudioSource = AudioSource.MIC;
                    break;

                case 2:
                    newAudioSource = AudioSource.VOICE_CALL;
                    break;

                case 3:
                    newAudioSource = AudioSource.VOICE_COMMUNICATION;
                    break;

                default:
                    newAudioSource = audioSource;
                    break;
            }
        } else {
            //brand13310modelHUAWEIY310-T10
            if ( (WebRtcAudioUtils.Model().equals("MI3W")) ||
                (WebRtcAudioUtils.Model().equals("HUAWEIY310-T10"))) {
                newAudioSource = AudioSource.VOICE_CALL;
            } else if (WebRtcAudioUtils.Brand().equals("Lenovo")) {
                if ((WebRtcAudioUtils.Model().equals("LenovoA788t"))) {
                    newAudioSource = AudioSource.VOICE_CALL;
                } else if ((WebRtcAudioUtils.Model().equals("LenovoA760"))) {
                    //board = 7x27modelLenovoA760
                    newAudioSource = AudioSource.VOICE_COMMUNICATION;
                }
            } else if ( WebRtcAudioUtils.Brand().equalsIgnoreCase("htc")) {
            	if ( WebRtcAudioUtils.Model().equals("HTCD816t")) {
            		newAudioSource = AudioSource.DEFAULT;
            	}
            }else if(WebRtcAudioUtils.Brand().equalsIgnoreCase("GIONEE")){
            	if ( WebRtcAudioUtils.Model().equals("GN8002")){
            		newAudioSource = AudioSource.VOICE_COMMUNICATION;
            	}
            } 
        }

        Logd("getAudioSource: change old = " + audioSource + " new = " + newAudioSource);
        return newAudioSource;
    }
    
    /**
     * Adaptive channelConfig for difference MobiePhone
     *
     * @param channelConfig
     * @return channelConfig suitable for phone
     */
    private int getRecordChannel(int channelConfig) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int newChannelConfig = channelConfig;
        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newChannelConfig = audioDeviceParam.getRecordChannel();

            switch (newChannelConfig) {
                case 0:
                    newChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
                    break;

                case 1:
                    newChannelConfig = AudioFormat.CHANNEL_IN_MONO;
                    break;

                default:
                    newChannelConfig = channelConfig;
                    break;
            }
        } else {
            /* for AudioRecord */
            if (WebRtcAudioUtils.Brand().equals("Lenovo")) {
                if ((WebRtcAudioUtils.Model().equals("LenovoA788t"))
                || (WebRtcAudioUtils.Model().equals("LenovoA820e"))) {
                    newChannelConfig = AudioFormat.CHANNEL_IN_MONO;
                }
            } else if ((WebRtcAudioUtils.Model().equals("ZTEU793"))
                || (WebRtcAudioUtils.Model().equals("ZTEU950"))) {
                newChannelConfig = AudioFormat.CHANNEL_IN_MONO;
            }
        }

        Logd("getRecordChannel: changed. old = " + channelConfig + " new = " + newChannelConfig);
        return newChannelConfig;
    }
    
    /**
     * Adaptive sample rate for phone
     *
     * @param sampleRate
     * @return sampleRate suitable for phone
     */
    private int getRecordSampleRate(int sampleRate) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int newSampleRate = sampleRate;
        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newSampleRate = audioDeviceParam.getRecordSampleRate();

            Logd("getRecordSampleRate: Brand = " + WebRtcAudioUtils.Brand() 
            		+ " Model = " + WebRtcAudioUtils.Model());
            
            switch (newSampleRate) {
                case 0:
                    newSampleRate = 8000;
                    break;

                case 1:
                    newSampleRate = 16000;
                    break;

                case 2:
                    newSampleRate = 44100;
                    break;

                default:
                    newSampleRate = sampleRate;
                    break;
            }
        } else {
            if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Huawei")) {
                if (WebRtcAudioUtils.Model().equals("HUAWEIG520-0000")
                        || WebRtcAudioUtils.Model().equals("HUAWEIC8813Q")
                        || WebRtcAudioUtils.Model().equals("HUAWEIG610-C00")
                        || WebRtcAudioUtils.Model().equals("HUAWEIC8815")
                        || WebRtcAudioUtils.Model().equals("HUAWEIU8818")) {

                    newSampleRate = 16000;
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Nokia")) {
                if ((WebRtcAudioUtils.Model().equals("Nokia_X"))) {

                    newSampleRate = 16000;
                }
            } else if (WebRtcAudioUtils.Brand().equals("Lenovo")) {
                if ((WebRtcAudioUtils.Model().equals("LenovoA788t"))) {

                    newSampleRate = 8000;
                } else if ((WebRtcAudioUtils.Model().equals("LenovoA760"))) {

                    newSampleRate = 16000;
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Coolpad")) {
                if ((WebRtcAudioUtils.Model().equals("Coolpad5890"))
                        || (WebRtcAudioUtils.Model().equals("Coolpad5891"))
                        || (WebRtcAudioUtils.Model().equals("Coolpad5950"))) {

                    newSampleRate = 16000;
                }
            } else if ( WebRtcAudioUtils.Model().equals("AOSPonHV01-G")) {
        		newSampleRate = 44100;
        	}
        }

        Logd("getRecordSampleRate changed " + "old = " + sampleRate
        		+ "  new = " + newSampleRate);
        return (newSampleRate);
    }

	/** Helper method which throws an exception when an assertion has failed.
	 * Please don't use this api, can't cause app exit. */
	private static void assertTrue(boolean condition) {
		if (!condition) {
			throw new AssertionError("Expected condition to be true");
		}
	}

	private static void Logd(String msg) {
		CustomLog.i(TAG, msg);
	}

	private static void Loge(String msg) {
		CustomLog.e(TAG, msg);
	}

	private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer,
			long nativeAudioRecord);

	private native void nativeDataIsRecorded(int bytes, long nativeAudioRecord);
}
