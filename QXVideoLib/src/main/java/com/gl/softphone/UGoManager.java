package com.gl.softphone;


import com.yzxtcp.tools.CustomLog;

import android.content.Context;
import android.util.Log;

import com.gl.softphone.DialingConfig;
import com.gl.softphone.CallPushConfig;
import com.gl.softphone.LogTraceConfig;
import com.gl.softphone.UGoAPIParam.MeVideoProfilePreset;

/**
 * @author£º ÌÆµ¤Ñô
 * @date£º 2013-1-16
 * @description£º
 */
public class UGoManager {
    native int UGoDebugEnabled(boolean enable, String filepath);

    native int UGoInit();

    native int UGoDestroy();

    native int UGoRegister(String uid, int mode);

    native int UGoUnRegister();

    native int UGoCallPush(CallPushConfig param);

    native int UGoDial(DialingConfig dialConfig);

    native int UGoGroupDial(GroupDialingConfig dialConfig);

    native int UGoConferenceDial(Object para);//add by wuzhaoyang20140721

    native int UGoConferenceInv(Object para);

    native int UGoConferenceDel(int reason);

    native int UGoAnswer();

    native int UGoHangup(int reason);

    native String UGoGetVersion();

    native int UGoTcpRecvMsg(int recvlen, byte[] recvbuf);

    native int UGoTcpUpdateState(int state);

    native int UGoUpdateNetworkType(int type);

    native int UGoUpdateSystemState(int state);

    native int UGoSetMicMute(boolean enable);

    native boolean UGoGetMicMute();

    native int UGoSetLoudSpeakerStatus(boolean enable);

    native boolean UGoGetLoudSpeakerStatus();

    native boolean UGoGetRecordingDeviceStatus();

    native int UGoSendDTMF(char dtmf);

    native void Callbacks(IUGoCallbacks cb);

    native void setAndroidContext(Context context);

    native int UGoLoadMediaEngine();

    native int UGoSetConfig(int mothed, Object config, int version);

    native int UGoGetConfig(int mothed, Object config, int version);

    native int UGoGetState();

    native int UGoSetApi(int level);

    native int UGoSetLogFile(LogTraceConfig logTraceConfig, int version);

    native int UGoGetCallReport(CallReport callReport);

    native int UGoStartRecord(AudioRecordConfig recordConfig);

    native int UGoStopRecord();

    native int UGoPlayFile(AudioPlayConfig para);

    native int UGoStopFile();

    native int UGoVideoSetCaptureCapability(VideoCaptureConfig captureConfig);

    native int UGoStartVideo(int sendReceive);

    native int UGoStopVideo(int sendReceive);

    native int UGoStartRecordVideo(VideoRecordConfig videoRecordConfig);

    native int UGoStopRecordVideo(int streamSelect);

    native int UGoVideoGetCaptureCapability(VideoCaptureConfig captureConfig);

    native int UGoScreenshotStart(int islocal, int type);

    native int UGoVideoUpdateLocalRotation(int landscape, int recived_rotation);

    native int UGoPresetVideo(int preset);

    native int UGoVideoIncomingFrame(byte[] data, int len);

    final private static String TAG = "UGoManager";

    //all callback function address
    private IUGoCallbacks UGoCallbacks;

    private static boolean bHasVideoLib = true; //ÊÇ·ñ¾ß±¸ÊÓÆµ¿â£¬

    private static boolean isHangup = false; // ÅÐ¶Ïµç»°ÊÇ·ñ¹Ò¶Ï

    static {
        if (android.os.Build.VERSION.SDK_INT < 9) {
            System.loadLibrary("OpenSLES");
        }
        try {
            System.loadLibrary("H264Encoder");
            System.loadLibrary("H264Decoder");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            bHasVideoLib = false;
            e.printStackTrace();
        }
        //System.loadLibrary("ViGo");
        System.loadLibrary("UGo");
    }

    private static volatile UGoManager Single = null;

    public static UGoManager getInstance() {
        if (Single == null) {
            synchronized (UGoManager.class) {
                if (Single == null) {
                    Single = new UGoManager();
                }
            }
        }
        return Single;
    }

    public static void ReleaseInstance() {
        if (null != Single) {
            Log.i(TAG, "ReleaseInstance!!");
            Single = null;
        }
    }


    private UGoManager() {

    }

    public boolean isHasVideoLib() {
        return bHasVideoLib;
    }

    public boolean getHangup() {
        return isHangup;
    }

    public void setHangup(boolean hangup) {
        isHangup = hangup;
    }

    /**
     * Enabled UGo signaling layer log
     *
     * @param enable   enabled or disabled
     * @param filepath log write path(include file name)
     * @return 0 for success
     */
    public synchronized int pub_UGoDebugEnabled(boolean enable, String filepath) {
        return UGoDebugEnabled(enable, filepath);
    }

    /**
     * Set media engine layer log filter and file path
     *
     * @param logTraceConfig set filter level and file path
     * @param version        0
     * @return 0 for success
     */
    public synchronized int pub_UGoSetLogFile(LogTraceConfig logTraceConfig, int version) {
        return UGoSetLogFile(logTraceConfig, version);
    }

    /**
     * Get current call state
     *
     * @return call state
     */
    public synchronized int pub_UGoGetState() {
        return UGoGetState();
    }

    /**
     * Set android sdk target api
     *
     * @param level sdk target api
     * @return 0 for success
     */
    public synchronized int pub_UGoSetApi(int level) {
        return UGoSetApi(level);
    }

    /**
     * Set config by method id
     *
     * @param methodID see "method id" in UGoAPIParam class
     * @param config   difference object for method id
     * @param version  0
     * @return 0 for success
     */
    public synchronized int pub_UGoSetConfig(int methodID, Object config, int version) {
        return UGoSetConfig(methodID, config, version);
    }

    /**
     * Get config by method id
     *
     * @param method  see "method id" in UGoAPIParam class
     * @param config  difference object for method id
     * @param version 0
     * @return 0 for success
     */
    public synchronized int pub_UGoGetConfig(int method, Object config, int version) {
        return UGoGetConfig(method, config, version);
    }

    /**
     * Send dtmf event to remote peer
     *
     * @param dtmf dtmf event value
     * @return 0 for success
     */
    public synchronized int pub_UGoSendDTMF(char dtmf) {
        return UGoSendDTMF(dtmf);
    }

    /**
     * Set local side media be muted or not
     *
     * @param enable
     * @return 0 for success
     */
    public synchronized int pub_UGoSetMicMute(boolean enable) {
        return UGoSetMicMute(enable);
    }

    /**
     * Get local media muted status
     *
     * @return muted status
     */
    public synchronized boolean pub_UGoGetMicMute() {
        return UGoGetMicMute();
    }

    /**
     * Set loud speaker status
     *
     * @param enable true to open loud speaker, false to earpiece
     * @return 0 for success
     */
    public synchronized int pub_UGoSetLoudSpeakerStatus(boolean enable) {
        return UGoSetLoudSpeakerStatus(enable);
    }

    /**
     * Get loud speaker status
     *
     * @return loud speaker status
     */
    public synchronized boolean pub_UGoGetLoudSpeakerStatus() {
        return UGoGetLoudSpeakerStatus();
    }

    /**
     * Get Android AudioRecord device status
     *
     * @return true for recording device available, false for unavailable
     */
    public synchronized boolean pub_UGoGetRecordingDeviceStatus() {
        return UGoGetRecordingDeviceStatus();
    }

    /**
     * Get current call report, include e-model and session information
     *
     * @param callReport return call report
     * @return 0 for success
     */
    public synchronized int pub_UGoGetCallReport(CallReport callReport) {
        return UGoGetCallReport(callReport);
    }

    /**
     * Create media engine
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoLoadMediaEngine() {
        return UGoLoadMediaEngine();
    }

    /**
     * Initial UGo module
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoInit() {
        //set callback function
        if (UGoCallbacks != null) {
            Callbacks(UGoCallbacks);
        }

        return UGoInit();
    }

    /**
     * Set UGo callback
     *
     * @param cb IUGoCallbacks interface
     */
    public synchronized void pub_UGoCallbacks(IUGoCallbacks cb) {
        UGoCallbacks = cb;
        Callbacks(UGoCallbacks);
    }

    /**
     * Destroy UGo module and media engine
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoDestroy() {
        Log.i(TAG, "java pub_UGoDestroy()");
        return UGoDestroy();
    }

    /**
     * Register to VPS server, for testing
     *
     * @param uid  user id
     * @param mode
     * @return 0 for success
     */
    public synchronized int pub_UGoRegister(String uid, int mode) {
        Log.v(TAG, "java pub_UGoRegister uid= " + uid + "  mode=" + mode);
        return UGoRegister(uid, mode);
    }

    /**
     * Unregister, for testing
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoUnRegister() {
        return UGoUnRegister();
    }

    /**
     * Message push like IOS
     *
     * @param pushConfig
     * @return 0 for success
     */
    public synchronized int pub_UGoCallPush(CallPushConfig pushConfig) {
        return UGoCallPush(pushConfig);
    }

    /**
     * Making Audio/Video call to called
     *
     * @param dialConfig include called information
     * @param version    0
     * @return 0 for success
     */
    public synchronized int pub_UGoDial(DialingConfig dialConfig, int version) {
        if (dialConfig != null)
            Log.v(TAG, "java pub_UGoDial uid= " + dialConfig.uid + "  phone=" + dialConfig.phone + "  mode=" + dialConfig.callMode);
        return UGoDial(dialConfig);
    }

    /**
     * Making Audio/Video group call
     *
     * @param dialConfig
     * @return
     */
    public synchronized int pub_UGoGroupDial(GroupDialingConfig dialConfig) {
        if (dialConfig == null) {
            return -1;
        }

        return UGoGroupDial(dialConfig);
    }

    /**
     * Making Audio conference call
     *
     * @param para
     * @param version
     * @return 0 for success
     */
    public synchronized int pub_UGoConferenceDial(ConferenceDialingConfig dialingConfig, int version) {
        if (dialingConfig != null)
            Log.v(TAG, "java pub_UGoConferenceDial start ");
        return UGoConferenceDial(dialingConfig);
    }

    /**
     * Invite someone in dialingConfig to the conference
     *
     * @param dialingConfig
     * @param version
     * @return
     */
    public synchronized int pub_UGoConferenceInv(ConferenceDialingConfig dialingConfig, int version) {
        if (dialingConfig == null) {
            return -1;
        }
        return UGoConferenceInv(dialingConfig);
    }

    /**
     * Finish the conference
     *
     * @param reason
     * @return 0 for success
     */
    public synchronized int pub_UGoConferenceDel(int reason) {
        return UGoConferenceDel(reason);
    }

    /**
     * Answer the incoming call
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoAnswer() {
        return UGoAnswer();
    }

    /**
     * Start the video record
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoStartRecordVideo(VideoRecordConfig config) {
        return UGoStartRecordVideo(config);
    }

    /**
     * Stop the video record
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoStopRecordVideo() {
        return UGoStopRecordVideo(2);
    }

    /**
     * Hang off the call
     *
     * @param reason
     * @return
     */
    public synchronized int pub_UGoHangup(int reason) {
        CustomLog.v("java pub_UGoHangup ");
        isHangup = true;
        return UGoHangup(reason);
    }

    /**
     * Get media engine version
     *
     * @return version string
     */
    public synchronized String pub_UGoGetVersion() {
        return UGoGetVersion();
    }

    /**
     * Push received signaling message to UGo module
     *
     * @param recvlen
     * @param recvbuf
     * @return 0 for success
     */
    public synchronized int pub_UGoTcpRecvMsg(int recvlen, byte[] recvbuf) {
        return UGoTcpRecvMsg(recvlen, recvbuf);
    }

    /**
     * Push external captured video frame to media engine
     *
     * @param data
     * @param len
     * @return 0 for success
     */
    public synchronized int pub_UGoVideoIncomingFrame(byte[] data, int len) {
        return UGoVideoIncomingFrame(data, len);
    }

    /**
     * Update TCP connect state to UGo module
     *
     * @param state see "TCP transport state" in UGoAPIParam class
     * @return 0 for success
     */
    public synchronized int pub_UGoTcpUpdateState(int state) {
        return UGoTcpUpdateState(state);
    }

    /**
     * Update network type
     *
     * @param type see "Network type" in UGoAPIParam class
     * @return 0 for success, else failed
     */
    public synchronized int pub_UGoUGoUpdateNetworkType(int type) {
        return UGoUpdateNetworkType(type);
    }

    /**
     * Update system phone call state to UGo module
     *
     * @param state see "system call state" in UGoAPIParam class
     * @return 0 for success
     */
    public synchronized int pub_UGoUpdateSystemState(int state) {
        return UGoUpdateSystemState(state);
    }

    /**
     * Set android application context to media engine
     *
     * @param context application context
     */
    public synchronized void pub_setAndroidContext(Context context) {
        setAndroidContext(context);
    }

    /**
     * Start audio record by param in recordConfig
     *
     * @param recordConfig
     * @return 0 for success
     */
    public synchronized int pub_UGoStartRecord(AudioRecordConfig recordConfig) {
        return UGoStartRecord(recordConfig);
    }

    /**
     * Stop audio record
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoStopRecord() {
        return UGoStopRecord();
    }

    /**
     * Set audio play file to media engine
     *
     * @param playConfig
     * @return 0 for success
     */
    public synchronized int pub_UGoPlayFile(AudioPlayConfig playConfig) {
        return UGoPlayFile(playConfig);
    }

    /**
     * Stop audio file play
     *
     * @return 0 for success
     */
    public synchronized int pub_UGoStopFile() {
        return UGoStopFile();
    }

    /**
     * Start video with specific state
     *
     * @param sendReceive see "Video state" in UGoAPIParam class
     * @return 0 for success
     */
    public synchronized int pub_UGoStartVideo(int sendReceive) {
        return UGoStartVideo(sendReceive);
    }

    /**
     * Stop video with specific state
     *
     * @param sendReceive see "Video state" in UGoAPIParam class
     * @return 0 for success
     */
    public synchronized int pub_UGoStopVideo(int sendReceive) {
        return UGoStopVideo(sendReceive);
    }

    /**
     * Video screen shot
     *
     * @param islocal true for local screen shot
     * @param type
     * @return 0 for success
     */
    public synchronized int pub_UGoScreenshotStart(int islocal, int type) {
        return UGoScreenshotStart(islocal, type);
    }

    /**
     * Video set device rotation
     *
     * @param send_rotation
     * @param recived_rotation
     * @return 0 for success
     */
    public synchronized int pub_UGoVideoUpdateLocalRotation(int landscape, int recived_rotation) {
        return UGoVideoUpdateLocalRotation(landscape, recived_rotation);
    }


    /**
     * Camera switch with capability
     *
     * @param captureConfig capture capability
     * @return 0 for success
     */
    public synchronized int pub_UGoVideoSetCaptureCapability(VideoCaptureConfig captureConfig) {
        return UGoVideoSetCaptureCapability(captureConfig);
    }

    /**
     * Get Camera capture capability
     *
     * @param captureConfig
     * @return 0 for success
     */
    public synchronized int pub_UGoVideoGetCaptureCapability(VideoCaptureConfig captureConfig) {
        return UGoVideoGetCaptureCapability(captureConfig);
    }

    /**
     * Video default config for device
     *
     * @param key
     * @param val
     * @return
     */
    public synchronized int pub_UGoPresetVideo(MeVideoProfilePreset preset) {
        return UGoPresetVideo(preset.ordinal());
    }

    public static abstract interface IUGoCallbacks {
        /**
         * The method used to report calling event for application
         *
         * @param ev_type   calling event
         * @param ev_reason event reason
         * @param message   event description
         * @param param     event parameters object
         */
        public abstract void eventCallback(int ev_type,
                                           int ev_reason,
                                           String message,
                                           String param);

        /**
         * The method used to send signaling message
         *
         * @param message
         * @param len
         */
        public abstract void sendCallback(byte[] message, int len);

        /**
         * The method used to report key message to application
         *
         * @param summary
         * @param detail
         * @param level
         */
        public abstract void traceCallback(String summary, String detail, int level);

        /**
         * rtp/rtcp packets encrypt callback
         *
         * @param inMsg  the original packet
         * @param outMsg the encrypted packet for to send
         * @param inLen  the length of inMsg
         * @param outLen the length of outMsg
         */
        public abstract void encryptCallback(byte[] inMsg, byte[] outMsg,
                                             int inLen, int[] outLen);

        /**
         * rtp/rtcp packets decrypt callback
         *
         * @param inMsg  the packet have been encrypted
         * @param outMsg the packet decrypt from inMsg
         * @param inLen  the length of inMsg
         * @param outLen the length of outMsg
         */
        public abstract void decryptCallback(byte[] inMsg, byte[] outMsg,
                                             int inLen, int[] outLen);

        public abstract void screenshotCallback(byte[] dst_argb, int dst_stride,
                                                int width, int height,
                                                int islocal, int screen_type);

        /**
         * external pcm media processing in every 10ms
         *
         * @param inSample  pcm media data should being processing
         * @param outSample pcm media data that have been processed
         * @param samples   samples num in every 10 ms
         * @param freqHz    sample frequency in hz
         * @param isStereo  whether stereo, means channel is 2 if stereo, else channel is 1
         */
        public abstract int mediaProcCallback(short[] inSample, short[] outSample,
                                              int samples, int freqHz, boolean isStereo);

        /**
         * The method used to tell application the parameters that audio playout data would used.
         *
         * @param sample_rate
         * @param bytes_per_sample how many bytes of one sample, always 2 bytes = 16bit
         * @param num_of_channels
         */
        public abstract void initPlayout(int sample_rate,
                                         int bytes_per_sample,
                                         int num_of_channels);

        /**
         * The method used to tell application the parameters that audio recording should used
         *
         * @param sample_rate
         * @param bytes_per_sample how many bytes of one sample, always 2 bytes = 16bit
         * @param num_of_channels
         */
        public abstract void initRecording(int sample_rate,
                                           int bytes_per_sample,
                                           int num_of_channels);

        /**
         * The method used to push audio playout data periodical for application to playout
         *
         * @param outData
         * @param outSize
         * @return 0 for success, -1 for failure. if failure, would play to internal device
         */
        public abstract int writePlayoutData(byte[] outData, int outSize);

        /**
         * The method used to read audio recording data periodical form application
         *
         * @param inData
         * @param inSize
         * @return 0 for success, -1 for failure. if failure, would use internal device recording data
         */
        public abstract int readRecordingData(byte[] inData, int inSize);
    }

}
