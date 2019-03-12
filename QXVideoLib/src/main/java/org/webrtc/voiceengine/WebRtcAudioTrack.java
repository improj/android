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
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.yzxtcp.tools.CustomLog;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

public class WebRtcAudioTrack {
	private static final boolean DEBUG = false;

	private static final String TAG = "WebRtcAudioTrack";

	private final boolean testSwitch = false; // the switch to write file;
	private boolean existSdcard = false;
	private final String playFileName = "/sdcard/playout.pcm";
	private FileOutputStream fosPlay = null;

	// Mono playout is default.
	// TODO(henrika): add stereo support.
	private static final int CHANNELS = 1;

	// Default audio data format is PCM 16 bit per sample.
	// Guaranteed to be supported by all devices.
	private static final int BITS_PER_SAMPLE = 16;

	// Number of bytes per audio frame.
	// Example: 16-bit PCM in stereo => 2*(16/8)=4 [bytes/frame]
	private static final int BYTES_PER_FRAME = CHANNELS * (BITS_PER_SAMPLE / 8);

	// Requested size of each recorded buffer provided to the client.
	private static final int CALLBACK_BUFFER_SIZE_MS = 10;

	// Average number of callbacks per second.
	private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;
	
	// Max byteBuffer size set to support 48khz samplerate
	private static final int MAX_BYTE_BUFFER_SIZE = BYTES_PER_FRAME * 480;

	private ByteBuffer byteBuffer;
	private int bytesPerBuffer;
	private int framesPerBuffer;
	private final int sampleRate;
	private byte[] tempBufPlay;

	private final long nativeAudioTrack;
	private final Context context;
	private final AudioManager audioManager;

	private AudioTrack audioTrack = null;
	private AudioTrackThread audioThread = null;
	private AudioDeviceParam audioDeviceParam = null;
	
	private int bufferedRecSamples = 0;
    private int bufferedPlaySamples = 0;
    private int playPosition = 0;
    private int samplesPerMs = 0;
    private int playDelayMs = 0;
    
    private boolean NoPlay = false;

	/**
	 * Audio thread which keeps calling AudioTrack.write() to stream audio. Data
	 * is periodically acquired from the native WebRTC layer using the
	 * nativeGetPlayoutData callback function. This thread uses a
	 * Process.THREAD_PRIORITY_URGENT_AUDIO priority.
	 */
	private class AudioTrackThread extends Thread {
		private volatile boolean keepAlive = true;

		public AudioTrackThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
			Logd("AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
			if(NoPlay)
			{
				while (keepAlive) {
					nativeGetPlayoutData(bytesPerBuffer, nativeAudioTrack);
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			}

			try {
				// In MODE_STREAM mode we can optionally prime the output buffer
				// by
				// writing up to bufferSizeInBytes (from constructor) before
				// starting.
				// This priming will avoid an immediate underrun, but is not
				// required.
				// TODO(henrika): initial tests have shown that priming is not
				// required.
				audioTrack.play();
				if ( audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
					Loge("AudioTrack.play failed on wrong state.");
				}
			} catch (IllegalStateException e) {
				Loge("AudioTrack.play failed: " + e.getMessage());
				return;
			}

			if ( testSwitch ) {
				if ( existSdcard ) {					
					try {
						fosPlay = new FileOutputStream(playFileName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			// Fixed size in bytes of each 10ms block of audio data that we ask
			// for
			// using callbacks to the native WebRTC client.
			final int sizeInBytes = bytesPerBuffer;
			long lastTime = System.nanoTime();
			long getDurationInMs;
			long writeDurationInMs;
			while (keepAlive) {
				// Get 10ms of PCM data from the native WebRTC client. Audio
				// data is
				// written into the common ByteBuffer using the address that was
				// cached at construction.
				nativeGetPlayoutData(sizeInBytes, nativeAudioTrack);
				if (DEBUG) {
					long nowTime = System.nanoTime();
					getDurationInMs = TimeUnit.NANOSECONDS.toMillis((nowTime - lastTime));
					lastTime = nowTime;
				}
				
				// Write data until all data has been written to the audio sink.
				// Upon return, the buffer position will have been advanced to
				// reflect
				// the amount of data that was successfully written to the
				// AudioTrack.
				if ( byteBuffer.remaining() < sizeInBytes ) {
					continue;
				}
				
				int bytesWritten = 0;				
				byteBuffer.get(tempBufPlay);
				bytesWritten = audioTrack.write(tempBufPlay, 0, sizeInBytes);
				
				if (bytesWritten != sizeInBytes) {
					Loge("AudioTrack.write failed: " + bytesWritten);
					if (bytesWritten == AudioTrack.ERROR_INVALID_OPERATION) {
						keepAlive = false;
					}
				}
				
				if (DEBUG) {
					long nowTime = System.nanoTime();
					writeDurationInMs = TimeUnit.NANOSECONDS.toMillis((nowTime - lastTime));
					lastTime = nowTime;
					if ( writeDurationInMs > 30 ) {
//						Logd("bytesGet[" + getDurationInMs + "] [" + sizeInBytes + "] bytesWrite[" + writeDurationInMs + "] " + bytesWritten);
					}
				}
				
				if (testSwitch ) {
					if ( fosPlay != null ) {
						// recording to files
						try {
							fosPlay.write(tempBufPlay, 0, sizeInBytes);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				// The byte buffer must be rewinded since byteBuffer.position()
				// is
				// increased at each call to AudioTrack.write(). If we don't do
				// this,
				// next call to AudioTrack.write() will fail.
				byteBuffer.rewind();

				// TODO(henrika): it is possible to create a delay estimate here
				// by
				// counting number of written frames and subtracting the result
				// from
				// audioTrack.getPlaybackHeadPosition().
				
				// increase by number of written samples
	            bufferedPlaySamples += (bytesWritten >> 1);

	            // current playout position in the playout buffer
	            int pos = audioTrack.getPlaybackHeadPosition();
	            if (pos < playPosition) { // wrap or reset by driver
	                playPosition = 0; // reset
	            }
	            bufferedPlaySamples -= (pos - playPosition);
	            //Logd("bufferedPlaySamples = " + bufferedPlaySamples);
	            playDelayMs = bufferedPlaySamples / samplesPerMs;
	            playPosition = pos;
			}

			try {
				audioTrack.stop();
			} catch (IllegalStateException e) {
				Loge("AudioTrack.stop failed: " + e.getMessage());
			}
			if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
				Loge("AudioTrack.stop failed on wrong state.");
			}
			audioTrack.flush();
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

	public WebRtcAudioTrack(Context context, long nativeAudioTrack) {
		Logd("ctor" + WebRtcAudioUtils.getThreadInfo());
		this.context = context;
		this.nativeAudioTrack = nativeAudioTrack;
		this.audioDeviceParam = AudioDeviceParam.getInstance();
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
//		sampleRate = GetNativeSampleRate();
		this.NoPlay=audioDeviceParam.bNoTrack;
		sampleRate = getTrackSampleRate(GetNativeSampleRate());
		framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
		bytesPerBuffer = BYTES_PER_FRAME * framesPerBuffer;		
		/*byteBuffer = ByteBuffer.allocateDirect(BYTES_PER_FRAME
				* (sampleRate / BUFFERS_PER_SECOND));*/
		byteBuffer = ByteBuffer.allocateDirect(MAX_BYTE_BUFFER_SIZE);
		samplesPerMs = sampleRate / 1000;
		Logd("byteBuffer.capacity: " + byteBuffer.capacity());
		tempBufPlay = new byte[MAX_BYTE_BUFFER_SIZE];
		
		// Rather than passing the ByteBuffer with every callback (requiring
		// the potentially expensive GetDirectBufferAddress) we simply have the
		// the native class cache the address to the memory once.
		nativeCacheDirectBufferAddress(byteBuffer, nativeAudioTrack);

		if (DEBUG) {
			WebRtcAudioUtils.logDeviceInfo(TAG);
		}

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
		int playSampleRate = WebRtcAudioUtils.GetNativeSampleRate(audioManager); 
		return getTrackSampleRate(playSampleRate);
	}

	private int  GetNativePlayDelay() {
		return playDelayMs;
	}
	
	private int InitPlayout(int sampleRate) {		
		if ( audioTrack != null ) {
			Logd("InitPlayout: Release previous audioTrack.");
			audioTrack.release();
			audioTrack = null;
		}
		
		Logd("InitPlayout(sampleRate=" + sampleRate + ")");
		bufferedPlaySamples = 0;
		
		/* modify sample rate */
        sampleRate = getTrackSampleRate(sampleRate);
        samplesPerMs = sampleRate / 1000;
        framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
		bytesPerBuffer = BYTES_PER_FRAME * framesPerBuffer;
        Logd("Initplayout: sampleRate = " + sampleRate);
        Logd("Initplayout:  bytesPerBuffer: " + bytesPerBuffer);
        if ( bytesPerBuffer > MAX_BYTE_BUFFER_SIZE ) {
        	Loge("InitPlayout: failed on bytesPerBuffer over max buffer size.");
        	return -1;
        }

        /* Get channel config */
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        channelConfig = getTrackChannel(channelConfig);
        
		// Get the minimum buffer size required for the successful creation of
		// an
		// AudioTrack object to be created in the MODE_STREAM mode.
		// Note that this size doesn't guarantee a smooth playback under load.
		// TODO(henrika): should we extend the buffer size to avoid glitches?
		int minBufferSizeInBytes = AudioTrack.getMinBufferSize(
											sampleRate, channelConfig,
											AudioFormat.ENCODING_PCM_16BIT);
		Logd("InitPlayout.getMinBufferSize: " + minBufferSizeInBytes);
		if ( minBufferSizeInBytes < 6000 ) {
			minBufferSizeInBytes *= 2;
		}
		Logd("InitPlayout: minBufferSizeInBytes = " + minBufferSizeInBytes);		
		
        if(NoPlay)
        {
        	return (1000 * (minBufferSizeInBytes / BYTES_PER_FRAME) / sampleRate);
        }

		/* Get Stream Type */
        int stream_type = AudioManager.STREAM_VOICE_CALL;
        stream_type = getStreamType(stream_type);
        Logd("InitPlayout: stream_type = " + stream_type);
        
		// For the streaming mode, data must be written to the audio sink in
		// chunks of size (given by byteBuffer.capacity()) less than or equal
		// to the total buffer size |minBufferSizeInBytes|.		
		minBufferSizeInBytes = Math.max(byteBuffer.capacity(), minBufferSizeInBytes);
		try {
			// Create an AudioTrack object and initialize its associated audio
			// buffer.
			// The size of this buffer determines how long an AudioTrack can
			// play
			// before running out of data.
			audioTrack = new AudioTrack(stream_type,
										sampleRate, channelConfig,
										AudioFormat.ENCODING_PCM_16BIT,
										minBufferSizeInBytes,
										AudioTrack.MODE_STREAM);
		} catch (IllegalArgumentException e) {
			Logd(e.getMessage());
			return -1;
		}
		
		// check that the audioTrack is ready to be used
        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            Loge("InitPlayout: init failed, state != STATE_INITIALIZED state =" + audioTrack.getState());

            Logd("InitPlayout: try again to new AudioTrack.");
            //try again if failed; --fix xiaomi2 single pass and crash bug. commit by fushuhua2013-8-8
            StopPlayout();
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSizeInBytes, AudioTrack.MODE_STREAM);            
        }
        
		if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
			Loge("InitPlayout: init failed, state != STATE_INITIALIZED state =" + audioTrack.getState());
			return -1;
		}
		
		if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
			Loge("InitPlayout: init failed, playstate != PLAYSTATE_STOPPED state =" + audioTrack.getState());
			return -1;
		}
		
		// Return a delay estimate in milliseconds given the minimum buffer
		// size.
		return (1000 * (minBufferSizeInBytes / BYTES_PER_FRAME) / sampleRate);
	}

	private boolean StartPlayout() {
		Logd("StartPlayout");
		if(NoPlay)
		{
			SetAudioMode(true);
			audioThread = new AudioTrackThread("AudioTrackJavaThread");
			audioThread.start();
			return true;
		}
		if (audioTrack == null) {
			Loge("StartPlayout: Failed on null audioTrack.");
			return false;
		}
		if (audioThread != null) {
			Logd("StartPlayout: Release previous audioThread.");
			audioThread.joinThread();		
			audioThread = null;
		}
		
		SetAudioMode(true);
		audioThread = new AudioTrackThread("AudioTrackJavaThread");
		audioThread.start();
		return true;
	}

	private boolean StopPlayout() {
		Logd("StopPlayout");
		if ( null != audioThread )
		{
			audioThread.joinThread();		
			audioThread = null;
		}
		SetAudioMode(false);
		if (audioTrack != null) {
			audioTrack.release();
			audioTrack = null;
		}
		return true;
	}

	private boolean GetPlayoutSpeaker()	{
		if (audioManager == null) {
			Loge("Could not change audio routing - no audio manager");
			return false;
		}
		
		return audioManager.isSpeakerphoneOn();
	}
	
	private int SetPlayoutSpeaker(boolean loudspeakerOn) {
		Logd("setPlayoutSpeaker is " + loudspeakerOn);
		
		if (audioManager == null) {
			Loge("Could not change audio routing - no audio manager");
			return -1;
		}

		Logd("SetPlayoutSpeaker: apiLevel = " + WebRtcAudioUtils.SDK());
        Logd("SetPlayoutSpeaker: bandString = " + WebRtcAudioUtils.Brand()
        		+ " modelString = " + WebRtcAudioUtils.Model());

        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            int mode = getSpeakerMode(loudspeakerOn);
            audioManager.setMode(mode);
            audioManager.setSpeakerphoneOn(loudspeakerOn);

            if (audioManager.getMode() != mode) {
                Loge("Could not set audio mode (" + mode + ") for current device");
            } else {
                Logd("set audio mode for current device success");
            }
        } else {
            if ((3 == WebRtcAudioUtils.SDK()) || (4 == WebRtcAudioUtils.SDK())) {
                // 1.5 and 1.6 devices
                if (loudspeakerOn) {
                    // route audio to back speaker
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                } else {
                    // route audio to earpiece
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
            } else {
                // 2.x devices
                if ((WebRtcAudioUtils.Brand().equalsIgnoreCase("Samsung")) &&
                        ((5 == WebRtcAudioUtils.SDK()) || 
            		    (6 == WebRtcAudioUtils.SDK()) ||
                        (7 == WebRtcAudioUtils.SDK())) ) {
                    // Samsung 2.0, 2.0.1 and 2.1 devices
                    if (loudspeakerOn) {
                        // route audio to back speaker
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                        Loge("Samsung and Samsung 2.1 and down devices:route audio to  back speaker success");
                    } else {
                        // route audio to earpiece
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                        audioManager.setMode(AudioManager.MODE_NORMAL);
                        Loge("Samsung and Samsung 2.1 and down devices:route audio to  earpiece success");
                    }
                } else {
                    if (WebRtcAudioUtils.Brand().equals("yusu")
                        || WebRtcAudioUtils.Brand().equals("yusuH701")
                        || WebRtcAudioUtils.Brand().equals("yusuA2")
                        || WebRtcAudioUtils.Brand().equals("qcom")
                        || WebRtcAudioUtils.Brand().equals("motoME525")) {

                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if (WebRtcAudioUtils.Brand().equals("Huawei") && 
                    		(WebRtcAudioUtils.Model().equals("HUAWEIP6-C00"))) {
                        audioManager.setMode(AudioManager.MODE_NORMAL);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if (WebRtcAudioUtils.Brand().equals("Lenovo")
                            && ((WebRtcAudioUtils.Model().equals("LenovoA788t"))
                            || (WebRtcAudioUtils.Model().equals("LenovoA760")))) {

                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if (WebRtcAudioUtils.Brand().equals("Huawei") && 
                    		(WebRtcAudioUtils.Model().equals("U9200"))) {
                        if (loudspeakerOn) {
                            audioManager.setMode(AudioManager.MODE_NORMAL);
                            audioManager.setSpeakerphoneOn(loudspeakerOn);
                        } else {
                            audioManager.setSpeakerphoneOn(loudspeakerOn);
                            SetAudioMode(true);
                        }
                    } else if (WebRtcAudioUtils.Model().equalsIgnoreCase("MI2")
                            || WebRtcAudioUtils.Model().equalsIgnoreCase("MI2S")) {

                        SetAudioMode(true);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if ((WebRtcAudioUtils.Brand().equalsIgnoreCase("Nokia"))
                            && (WebRtcAudioUtils.Model().equalsIgnoreCase("Nokia_X"))) {

                        SetAudioMode(true);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("ErenEben")) {
                        if (WebRtcAudioUtils.Model().equalsIgnoreCase("EBENM1")) {
                            audioManager.setSpeakerphoneOn(loudspeakerOn);
                            SetAudioMode(true);
                        }
                    } else if (WebRtcAudioUtils.Model().equalsIgnoreCase("HUAWEIC8815")) {
                        SetAudioMode(true);
                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else if ( WebRtcAudioUtils.Brand().equalsIgnoreCase("Meizu")) {
                    	if ( WebRtcAudioUtils.Model().equalsIgnoreCase("MX4Pro")) {
                    		SetAudioMode(true);
                    	}
                    	audioManager.setSpeakerphoneOn(loudspeakerOn);
                    } else {
                        // Non-Samsung and Samsung 2.2 and up devices
                        Loge("Non-Samsung and Samsung 2.2 and up devices:route audio to  back speaker? "
                                + loudspeakerOn + " success. " + " mode = " + audioManager.getMode());

                        audioManager.setSpeakerphoneOn(loudspeakerOn);
                    }
                }
            }
        }
		return 0;
	}
	
	/**
     * Adaptive audio mode for phone
     *
     * @param startCall true is on call, else is wait state
     */
    private void SetAudioMode(boolean startCall) {
        if (audioManager == null) {
            Loge("Could not set audio mode - no audio manager");
            return;
        }

        int mode = audioManager.getMode();
        Logd("SetAudioMode: current mode = " + mode + " startCall = " + startCall);

        // ***IMPORTANT*** When the API level for honeycomb (H) has been
        // decided,
        // the condition should be changed to include API level 8 to H-1.
        Logd("SetAudioMode: brandString = " + WebRtcAudioUtils.Brand() 
        		+ " modelString = " + WebRtcAudioUtils.Model());
        Logd("SetAudioMode: apiLevel = " + WebRtcAudioUtils.SDK());

        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            mode = startCall ? getCallMode() : AudioManager.MODE_NORMAL;
        } else {
            if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Samsung")) {  // for Samsung
                if ((WebRtcAudioUtils.SDK() == 8)) {
                    // Set Samsung specific VoIP mode for 2.2 devices
                    mode = (startCall ? 4 : AudioManager.MODE_NORMAL); // 4 is VoIP mode??not find in api
                }
            } else if (WebRtcAudioUtils.Brand().equals("Lenovo")) {  // Lenovo
                if ((WebRtcAudioUtils.Model().equals("LenovoA788t"))) {

                    mode = AudioManager.MODE_IN_CALL;
                } else if ((WebRtcAudioUtils.Model().equalsIgnoreCase("LenovoS850e"))
                        || (WebRtcAudioUtils.Model().equalsIgnoreCase("LenovoA60"))
                        || (WebRtcAudioUtils.Model().equalsIgnoreCase("LenovoA780"))
                        || (WebRtcAudioUtils.Model().equalsIgnoreCase("LenovoA820e"))) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Huawei")) { // huawei
                if (WebRtcAudioUtils.Model().equals("HUAWEIP6-C00")) {
                    mode = (startCall ? AudioManager.MODE_NORMAL : AudioManager.MODE_IN_CALL);
                } else if ( WebRtcAudioUtils.Model().equalsIgnoreCase("HUAWEIC8815")) {
                    mode = AudioManager.MODE_NORMAL;
                } else if (!WebRtcAudioUtils.Model().equalsIgnoreCase("HUAWEIY220T")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("HUAWEIT8600")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("HUAWEIY310-T10")) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("ZTE")) {  // zte
                if (!WebRtcAudioUtils.Model().equalsIgnoreCase("ZTEU880E")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("ZTEV985")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("ZTEU950")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("ZTE-TU880")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("ZTE-TU960s")
                        && !WebRtcAudioUtils.Model().equalsIgnoreCase("ZTEU793")) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("motorola")) { // motorola
                if ((WebRtcAudioUtils.Model().equals("MOT-XT788"))) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Coolpad")) { // coolpad
                if (WebRtcAudioUtils.Model().equals("Coolpad5950") 
            		|| WebRtcAudioUtils.Model().equals("Coolpad5891")) {

                    mode = (startCall ? 4 : AudioManager.MODE_NORMAL);
                } else if ((WebRtcAudioUtils.Model().equalsIgnoreCase("Coolpad5890"))
                        || (WebRtcAudioUtils.Model().equalsIgnoreCase("7260"))) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("xiaomi")) { // xiaomi
                if (WebRtcAudioUtils.Model().equals("MI1S") 
            		|| WebRtcAudioUtils.Model().equals("HM1SC")) {

                    mode = (startCall ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_NORMAL);
                    boolean speakon = audioManager.isSpeakerphoneOn();
                    speakon = false;
                    audioManager.setSpeakerphoneOn(speakon);
                } else if (WebRtcAudioUtils.Model().equals("MI2S")
                		|| WebRtcAudioUtils.Model().equals("MI2")) {

                    mode = AudioManager.MODE_NORMAL;
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Sony")) { // Sony
                if ((WebRtcAudioUtils.Model().equals("M35c"))) {

                    mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Nokia")) {  // Nokia
                if ((WebRtcAudioUtils.Model().equalsIgnoreCase("Nokia_X"))) {

                    mode = (startCall ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_NORMAL);
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("ErenEben")) {
                if (WebRtcAudioUtils.Model().equalsIgnoreCase("EBENM1")) {
                    mode = (startCall ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_NORMAL);
                }
            } else if ( WebRtcAudioUtils.Brand().equalsIgnoreCase("Meizu")) {
            	if ( WebRtcAudioUtils.Model().equalsIgnoreCase("MX4Pro")) {
            		mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
            	}
            } else if ( (WebRtcAudioUtils.Brand().equalsIgnoreCase("yusu") // others
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("yusuH701")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("yusuA2")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("qcom")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("motoME525")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("lge")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("SEMC")
                    || WebRtcAudioUtils.Model().equalsIgnoreCase("HTCA510e")
                    || WebRtcAudioUtils.Brand().equalsIgnoreCase("ChanghongV10")
                    || WebRtcAudioUtils.Model().equalsIgnoreCase("MT788")
                    || WebRtcAudioUtils.Model().equalsIgnoreCase("MI-ONEPlus")) ) {
                mode = (startCall ? AudioManager.MODE_IN_CALL : AudioManager.MODE_NORMAL);
            }
        }

        audioManager.setMode(mode);
        if ( audioManager.getMode() != mode) {
            Loge("Could not set audio mode (" + mode + ") for current device");
        }

        Logd("SetAudioMode: Success. mode = " + audioManager.getMode());
    }
	
	/**
     * Adaptive streamtype for difference MobiePhone
     *
     * @param streamType default value
     * @return streamtype suitable for phone
     */
    private int getStreamType( int streamType ) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }
        int newStreamType = streamType;

        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newStreamType = audioDeviceParam.getPlayStreamType();

            switch (newStreamType) {
                case 0:
                    newStreamType = AudioManager.STREAM_SYSTEM;
                    break;

                case 1:
                    newStreamType = AudioManager.STREAM_VOICE_CALL;
                    break;
                
                case 2:
                	newStreamType = AudioManager.STREAM_MUSIC;
                	break;

                default:
                    newStreamType = streamType;
                    break;
            }
        } else {
            if ( WebRtcAudioUtils.Brand().equalsIgnoreCase("motorola") ) {  //for motorola
                if ( WebRtcAudioUtils.Model().equals("MOT-XT788") ) {
                    newStreamType = AudioManager.STREAM_SYSTEM; //AudioManager.STREAM_DTMF,ok
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Huawei")) {  //for huawei
                if ((WebRtcAudioUtils.Model().equals("HUAWEIC8813Q"))
                || (WebRtcAudioUtils.Model().equals("HUAWEIY300-0000"))
                || (WebRtcAudioUtils.Model().equals("HUAWEIG520-0000"))
                || (WebRtcAudioUtils.Model().equals("HUAWEIC8813"))) {
                    newStreamType = AudioManager.STREAM_SYSTEM;
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Lenovo")) {  //for Lenovo
                if ((WebRtcAudioUtils.Model().equals("LenovoA788t"))
                || (WebRtcAudioUtils.Model().equals("LenovoA820e"))) {
                    newStreamType = AudioManager.STREAM_SYSTEM;
                }
            } else if (WebRtcAudioUtils.Brand().equalsIgnoreCase("innos_smartphone")) {  //for innos
                newStreamType = AudioManager.STREAM_SYSTEM;
            } else if ( WebRtcAudioUtils.Brand().equalsIgnoreCase("htc")) {
        		if ( WebRtcAudioUtils.Model().equals("HTCM8w")
        			|| WebRtcAudioUtils.Model().equals("HTCD816t")) {
        			newStreamType = AudioManager.STREAM_MUSIC;
        		}
        	}
        }

        Logd("GetStreamType: changed. old = " + streamType
        		+ " new = " + newStreamType);
        return newStreamType;
    }
    
    /**
     * Adaptive channelConfig for difference MobiePhone
     *
     * @param channelConfig
     * @return channelConfig suitable for phone
     */
    private int getTrackChannel(int channelConfig) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int newChannelConfig = channelConfig;
        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newChannelConfig = audioDeviceParam.getPlayChannel();

            switch (newChannelConfig) {
                case 0:
                    newChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
                    break;

                case 1:
                    newChannelConfig = AudioFormat.CHANNEL_OUT_MONO;
                    break;

                default:
                    newChannelConfig = channelConfig;
                    break;
            }
        } else {
            //ToDo
        }

        Logd("getTrackChannel: changed. old = " + channelConfig 
        		+ " new = " + newChannelConfig);
        return newChannelConfig;
    }
    
    /**
     * Adaptive sample rate for phone
     *
     * @param sampleRate
     * @return sampleRate suitable for phone
     */
    private int getTrackSampleRate(int sampleRate) {
        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int newSampleRate = sampleRate;
        if ( audioDeviceParam.getDynamicPolicyEnable() ) {
            newSampleRate = audioDeviceParam.getPlaySampleRate();

            Logd("getTrackSampleRate: Brand = " + WebRtcAudioUtils.Brand() 
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
        	//ToDo
        	if (WebRtcAudioUtils.Brand().equalsIgnoreCase("Huawei")) {
        		if (WebRtcAudioUtils.Model().equals("HUAWEIU8818")) {
        			newSampleRate = 16000;
        		}
        	} else if ( WebRtcAudioUtils.Model().equals("AOSPonHV01-G")) {
        		newSampleRate = 44100;
        	}
        }

        Logd("AudioTrack SampleRate changed. " + "old = " + sampleRate
        		+ "  new = " + newSampleRate);
        return (newSampleRate);
    }
    
    /**
     * Get audio mode for calling
     * @return  audio mode for calling
     */
    private int getCallMode() {
        if (audioManager == null) {
        	Loge("Could not change audio routing - no audio manager");
            return -1;
        }

        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int mode = audioDeviceParam.getCallMode();

        switch ( mode ) {
            case 0:
                mode = AudioManager.MODE_NORMAL;
                break;

            case 1:
                mode = AudioManager.MODE_IN_CALL;
                break;

            case 2:
                mode = AudioManager.MODE_IN_COMMUNICATION;
                break;

            case 3:
                mode = 4;
                break;

            default:
                Logd("getCallMode: default!!!");
                mode = audioManager.getMode();
                break;
        }

        Logd("getCallMode: mode = " + mode);
        return mode;
    }
    
    /**
     * Get speaker audio mode
     * @param bSpeakerOn true for get speaker mode, false for get earpiece mode
     * @return speaker audio mode
     */
    private int getSpeakerMode( boolean bSpeakerOn ) {
        if ( audioManager == null ) {
            Loge("Could not change audio routing - no audio manager");
            return -1;
        }

        if ( audioDeviceParam == null ) {
            audioDeviceParam = AudioDeviceParam.getInstance();
        }

        int mode = bSpeakerOn ? audioDeviceParam.getSpeakerMode() : audioDeviceParam.getEarpieceMode();
        switch ( mode ) {
            case 0:
                mode = AudioManager.MODE_NORMAL;
                break;

            case 1:
                mode = AudioManager.MODE_IN_CALL;
                break;

            case 2:
                mode = AudioManager.MODE_IN_COMMUNICATION;
                break;

            case 3:
                mode = 4;
                break;

            default:
                Logd("getSpeakerMode: default!!!");
                mode = audioManager.getMode();
                break;
        }

        Logd("getSpeakerMode: bSpeakerOn = " + bSpeakerOn + " mode = " + mode);
        return mode;
    }

	/** Helper method which throws an exception when an assertion has failed. */
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

	private native void nativeGetPlayoutData(int bytes, long nativeAudioRecord);
}
