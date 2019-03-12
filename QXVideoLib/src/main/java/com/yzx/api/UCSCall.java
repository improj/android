package com.yzx.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gl.softphone.CallPushConfig;
import com.gl.softphone.CodecConfig;
import com.gl.softphone.EncodeConfig;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoAPIParam.MeVideoProfilePreset;
import com.gl.softphone.UGoManager;
import com.gl.softphone.VideoDecParam;
import com.gl.softphone.VideoEncParam;
import com.gl.softphone.VideoRecordConfig;
import com.yzx.controller.CollectCallLog;
import com.yzx.controller.TimerHandler;
import com.yzx.controller.UGoSetConfig;
import com.yzx.controller.VoipCore;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.PreviewImgUrlListener;
import com.yzx.listenerInterface.VoipListenerManager;
import com.yzx.preference.UserData;
import com.yzx.protocol.packet.IGGUploadPreviewImgRequest;
import com.yzx.tools.AudioManagerTools;
import com.yzx.tools.BitmapUtils;
import com.yzx.tools.CpsTools;
import com.yzx.tools.DefinitionAction;
import com.yzx.tools.FileFilter;
import com.yzx.tools.FileTools;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

import org.webrtc.videoengine.ViERenderer;
import org.webrtc.videoengine.VideoCaptureAndroid;

import java.io.File;
import java.util.ArrayList;

/**
 * 电话管理类
 */
public class UCSCall {

    /**
     * 被叫号码为空
     */
    public static final int CALL_NUMBER_IS_EMPTY = 300006;

    /**
     * 余额不足
     */
    public static final int CALL_VOIP_NOT_ENOUGH_BALANCE = 300211;

    /**
     * 对方正忙
     */
    public static final int CALL_VOIP_BUSY = 300212;

    /**
     * 对方拒绝接听
     */
    public static final int CALL_VOIP_REFUSAL = 300213;

    /**
     * 不在线
     */
    public static final int CALL_VOIP_NUMBER_OFFLINE = 300214;

    /**
     * 呼叫ID不存在
     */
    public static final int CALL_VOIP_CALLID_NOT_EXIST = 300244;

    /**
     * 用户ID不存在
     */
    public static final int CALL_VOIP_USERID_NOT_EXIST = 300245;

    /**
     * 被叫号码错误
     */
    public static final int CALL_VOIP_NUMBER_WRONG = 300215;

    /**
     * 被叫账户被冻结
     */
    public static final int CALL_VOIP_REJECT_ACCOUNT_FROZEN = 300217;

    /**
     * 主叫账户被冻结
     */
    public static final int CALL_VOIP_ACCOUNT_FROZEN = 300216;

    /**
     * 主叫账户过期
     */
    public static final int CALL_VOIP_ACCOUNT_EXPIRED = 300218;

    /**
     * 不能拨打自己绑定号码
     */
    public static final int CALL_VOIP_CALLYOURSELF = 300219;

    /**
     * 呼叫请求超时
     */
    public static final int CALL_VOIP_NETWORK_TIMEOUT = 300220;

    /**
     * 对方无人应答
     */
    public static final int CALL_VOIP_NOT_ANSWER = 300221;

    /**
     * 转直拨
     */
    public static final int CALL_VOIP_TRYING_183 = 300222;

    /**
     * 对方正在响铃
     */
    public static final int CALL_VOIP_RINGING_180 = 300247;

    /**
     * 鉴权失败(TCP未认证)
     */
    public static final int CALL_VOIP_SESSION_EXPIRATION = 300223;

    /**
     * 服务器错误
     */
    public static final int CALL_VOIP_ERROR = 300210;

    /**
     * 被叫方没有应答
     */
    public static final int HUNGUP_NOT_ANSWER = CALL_VOIP_NOT_ANSWER;

    /**
     * 其他原因错误
     */
    public static final int OTHER_ERROR = 300224;

    /**
     * 自己挂断电话
     */
    public static final int HUNGUP_MYSELF = 300225;

    /**
     * 对方挂断电话
     */
    public static final int HUNGUP_OTHER = 300226;

    /**
     * RTP超时电话被挂断
     */
    public static final int HUNGUP_RTP_TIMEOUT = 300227;

    /**
     * 其他原因电话被挂断
     */
    public static final int HUNGUP_OTHER_REASON = 300228;

    /**
     * 2G时不允许拨打电话（回拨除外）
     */
    public static final int HUNGUP_WHILE_2G = 300267;

    /**
     * 对方拒绝接听
     */
    public static final int HUNGUP_REFUSAL = CALL_VOIP_REFUSAL;

    /**
     * 自己拒绝
     */
    public static final int HUNGUP_MYSELF_REFUSAL = 300248;

    /**
     * 余额不足
     */
    public static final int HUNGUP_NOT_ENOUGH_BALANCE = CALL_VOIP_NOT_ENOUGH_BALANCE;

    /**
     * 无网络
     */
    public static final int NOT_NETWORK = 300318;

    /**
     * 呼叫失败（频繁呼叫已被列入黑名单）
     */
    public static final int CALL_FAIL_BLACKLIST = 300250;

    /**
     * 不支持视频电话
     */
    public static final int CALL_VIDEO_DOES_NOT_SUPPORT = 300249;

    /**
     * 消息路由不可达
     */
    public static final int CALL_REASON_UNREACHABLE = 300251;

    /**
     * 消息头解析错误
     */
    public static final int EVENT_REASON_HEAD_ERROR = 300252;

    /**
     * 消息体解析错误
     */
    public static final int EVENT_REASON_BODY_ERROR = 300253;

    /**
     * 会话已存在
     */
    public static final int CALL_REASON_CONVERSATION_EXIST = 300254;

    /**
     * 未知错误
     */
    public static final int EVENT_REASON_UNKOWN_ERROR = 300255;

    /**
     * TCP异常挂断
     */
    public static final int HANGUP_REASON_TCP_UNUSUAL = 300256;

    /**
     * 落地线路无法接通
     */
    public static final int CALL_REASON_CONNECTION_FAIL = 300257;

    /**
     * 网络类型不支持
     */
    public static final int CALL_REASON_NEWWORK_DISABLE = 300258;

    /**
     * 信令超时
     */
    public static final int CALL_REASON_SIGNAL_TIMEOUT = 300259;

    /**
     * 同振呼叫，被叫的个数超过五个
     */
    public static final int CALL_REASON_CALLED_BEYOND_FIVE = 300260;

    /**
     * 同振挂断
     */
    public static final int HUNGUP_GROUP = 300261;

    public static ArrayList<CallStateListener> getCallStateListener() {
        return VoipListenerManager.getInstance().getCallStateListener();
    }

    public static void addCallStateListener(CallStateListener csl) {
        VoipListenerManager.getInstance().addCallStateListener(csl);
    }

    public static void removeCallStateListener(CallStateListener csl) {
        VoipListenerManager.getInstance().removeCallStateListener(csl);
    }

    public static String getCurrentCallId() {
        String currentCallId = VoipCore.getCurrentCallId();
        return currentCallId != null && currentCallId.length() > 0 ? currentCallId : "";
    }

    /**
     * @param callType     呼叫类型， DIRECT ：直拨	VOIP：免费    VIDEO:视频
     * @param calledNumner 用户clientId或者手机号码
     * @param userData     拨打电话时的透传数据
     * @return void    返回类型
     * @Description 发起呼叫
     * @date 2016-8-10 上午10:05:10
     * @author xhb
     */
    public static void dial(CallType callType, String calledNumber, String userData) {
        // 正常拨打，需要把缓存视频预览图片删除，防止去透传图片
        UserData.setPreviewImgUrl("");
        CustomLog.v(" 1 -----------------");
        if (VoipCore.getInstance(null) != null) {
            CustomLog.v(" 2 -----------------");
            if (callType != null && !TextUtils.isEmpty(calledNumber)) {
                CustomLog.v(" 3 -----------------" + callType);
                String tranData = !TextUtils.isEmpty(userData) ? userData : "";
                switch (callType) {
                    case VOIP:        // 音频免费
                        VoipCore.getInstance(null).dial(6, calledNumber, "", tranData);
                        CollectCallLog.setSDKPhoneMsg("UCSCall dial() VOIP ...");
                        break;
                    case VIDEO:        // 视频免费
                        VoipCore.getInstance(null).dial(3, calledNumber, "", tranData);
                        CollectCallLog.setSDKPhoneMsg("UCSCall dial() VIDEO ...");
                        break;
                    case DIRECT:    // 直拨
                        if (calledNumber.length() > 31) {    // 直接返回被叫号码异常
                            notifyDialFailed(new UcsReason(CALL_VOIP_NUMBER_WRONG).setMsg("the calledNumner error"));
                        } else {
                            VoipCore.getInstance(null).dial(4, "", calledNumber, tranData);
                            CollectCallLog.setSDKPhoneMsg("UCSCall dial() DIRECT ...");
                        }
                        break;
                }
            } else {
                notifyDialFailed(new UcsReason(CALL_NUMBER_IS_EMPTY).setMsg("calledNumner is null "));
            }
        } else {
            for (ConnectionListener cl : UCSService.getConnectionListener()) {
                cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
            }
        }
    }

    /**
     * 结束通话(挂断)
     *
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:36:58
     */
    public static void hangUp(String callId) {
        CollectCallLog.setSDKPhoneMsg("UCSCall hangUp()  ...");
        UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
//		if(VoipCore.getInstance(null) != null){
//			UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
//		}else{
//			for(ConnectionListener cl:UCSService.getConnectionListener()){
//				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
//			}
//		}
    }

    /**
     * 接听电话
     *
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:37:34
     */
    public static void answer(String callId) {
        UGoManager.getInstance().pub_UGoAnswer();
//		if(VoipCore.getInstance(null) != null){
//			UGoManager.getInstance().pub_UGoAnswer();
//		}else{
//			for(ConnectionListener cl:UCSService.getConnectionListener()){
//				cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
//			}
//		}
    }


    /**
     * 发送DTMF音
     *
     * @param keyCode
     * @param call_dtmf
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:40:41
     */
    public static void sendDTMF(int keyCode, EditText call_dtmf) {
        String text = "";
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                text = "0";
                break;
            case KeyEvent.KEYCODE_1:
                text = "1";
                break;
            case KeyEvent.KEYCODE_2:
                text = "2";
                break;
            case KeyEvent.KEYCODE_3:
                text = "3";
                break;
            case KeyEvent.KEYCODE_4:
                text = "4";
                break;
            case KeyEvent.KEYCODE_5:
                text = "5";
                break;
            case KeyEvent.KEYCODE_6:
                text = "6";
                break;
            case KeyEvent.KEYCODE_7:
                text = "7";
                break;
            case KeyEvent.KEYCODE_8:
                text = "8";
                break;
            case KeyEvent.KEYCODE_9:
                text = "9";
                break;
            case KeyEvent.KEYCODE_POUND:
                text = "#";
                break;
            case KeyEvent.KEYCODE_STAR:
                text = "*";
                break;
            default:
                break;
        }
        if (text != null && text.length() > 0) {
            if (call_dtmf != null) {
                call_dtmf.getEditableText().insert(call_dtmf.getText().length(), text);
            }
            UGoManager.getInstance().pub_UGoSendDTMF(text.charAt(0));
        }
    }

    /**
     * 设备是否免提
     *
     * @param isSpeakerphoneOn true:免提   false:内放
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:42:18
     */
    public static void setSpeakerphone(final Context mContext, final boolean isSpeakerphoneOn) {
        CustomLog.v("2-SET_SPEAKER_PHONE_ON:" + isSpeakerphoneOn);
        AudioManagerTools.getInstance(mContext).setSpeakerPhoneOn(isSpeakerphoneOn);
    }

    /**
     * 获取扬声器状态
     *
     * @return
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:43:49
     */
    public static boolean isSpeakerphoneOn(Context mContext) {
        return AudioManagerTools.getInstance(mContext).isSpeakerphoneOn();
    }

    /**
     * 设备静音
     *
     * @param isMicMute true:静音   false:正常
     * @author: xiaozhenhua
     * @data:2014-4-21 上午10:45:06
     */
    public static void setMicMute(boolean isMicMute) {
        UGoManager.getInstance().pub_UGoSetMicMute(isMicMute);
    }

    /**
     * 获得静音状态
     *
     * @author: xiongjijian
     * @data:2014-10-31 下午17:45:06
     */
    public static boolean isMicMute() {
        return UGoManager.getInstance().pub_UGoGetMicMute();
    }

    private static boolean isAudioDeviceAdapter = false;

    /**
     * 设备是否驱动智能适配
     *
     * @param isAudioDeviceDapter true:启动智能适配   false:不启动
     * @author: lion
     * @data:2014-4-21 上午10:42:18
     */
    private static void setAudioDeviceAdapter(Context mContext, boolean isAudioDeviceAdapterOn) {
        isAudioDeviceAdapter = isAudioDeviceAdapterOn;
        CpsTools.getCpsAdListParam(mContext);
    }

    /**
     * 设备驱动智能适配是否成功
     *
     * @param isADAdapterSuccess true:智能适配成功   false:不成功
     * @author: lion
     * @data:2014-4-21 上午10:42:18
     */
    private static void setADAdapterSuccess(Context mContext, boolean isADAdapterSuccess) {
        if (isADAdapterSuccess) {
            CpsTools.postCpsAndroidDeviceParam(mContext);
        } else {
            CpsTools.postCpsAdExceptionParam(mContext);
        }

    }

    /**
     * 第三方通知激活接口（该接口是给IOS用的，安卓用不到）
     *
     * @param param
     * @return
     * @author: xiaozhenhua
     * @data:2015-3-24 下午2:54:29
     */
    private static int callPush(CallPushConfig param) {
        return UGoManager.getInstance().pub_UGoCallPush(param);
    }

    /**
     * 获取驱动智能适配状态
     *
     * @return
     * @author: lion
     * @data:2014-4-21 上午10:43:49
     */
    private static boolean isAudioDeviceAdapterOn() {
        return isAudioDeviceAdapter;
    }

    /**
     * 播放来电音
     *
     * @param RingtoneManagerType
     * @param vibrator
     * @author: xiaozhenhua
     * @data:2013-2-19 下午4:39:04
     */
    public static void startRinging(Context mContext, boolean isVibrator) {
        AudioManagerTools.getInstance(mContext).startRinging(isVibrator);
    }

    /**
     * 停止来电音
     *
     * @author: xiaozhenhua
     * @data:2014-6-3 上午11:26:20
     */
    public static void stopRinging(Context mContext) {
        AudioManagerTools.getInstance(mContext).stopRinging();
    }

    /**
     * 播放去电音
     *
     * @author: xiaozhenhua
     * @data:2014-6-3 上午11:28:11
     */
    public static void startCallRinging(Context mContext, String fileName) {
        AudioManagerTools.getInstance(mContext).startCallRinging(fileName);
    }

    /**
     * 停止播放去电音
     *
     * @author: xiaozhenhua
     * @data:2014-6-3 上午11:35:59
     */
    public static void stopCallRinging(Context mContext) {
        CustomLog.v("STOP CALL RINGING");
        AudioManagerTools.getInstance(mContext).stopCallRinging();
    }

    /**
     * @param mContext 上下文
     * @param fileName 语音文件，已放在“assets”中
     * @param bLoop    是否循环播放
     * @return void    返回类型
     * @Description 播放自动语音（通话接听后，播放给对方听）
     * @date 2017年5月9日 上午10:17:44
     * @author zhj
     */
    public static void startAutoVoice(Context mContext, String fileName, boolean bLoop) {
        CustomLog.v("START AUTO VOICE");
        AudioManagerTools.getInstance(mContext).startAutoVoice(fileName, bLoop);
    }

    public static void stopAutoVoice(Context mContext) {
        CustomLog.v("STOP AUTO VOICE");
        AudioManagerTools.getInstance(mContext).stopCallRinging();
    }

    private static void notifyDialFailed(UcsReason dial) {
        for (CallStateListener csl : getCallStateListener()) {
            csl.onDialFailed(UCSCall.getCurrentCallId(), dial);
        }
//		//收集错误码头(保留)
//		if(VoipCore.getContext() != null){
//			ErrorCodeReportTools.collectionErrorCode(VoipCore.getContext(), 
//					UserData.getUserId(VoipCore.getContext()), "onDialFailed", dial.getReason(), dial.getMsg());
//		}
    }

//	private static ForwardingListener forListener;
//	
//	public static ForwardingListener getForwardListener() {
//		return forListener;
//	}

//	private static void setCallForwardingOption(Activity mContext,boolean forwarding,ForwardingListener forwardingListener){
////		forListener = forwardingListener;
//		VoipListenerManager.getInstance().setForwardingListener(forwardingListener);
//		if(forwarding){
//			CustomLog.v("开启呼转 ... ");
//			ForwardingTools.openForwardingOperator(mContext, UserData.getForwardNumber(mContext));
//		}else{
//			CustomLog.v("关闭呼转 ... ");
//			ForwardingTools.closeCallForwardingOption(mContext);
//		}
//	}
//
//	private static void setCallForwardingOption(Activity mContext,boolean forwarding,String forwardNumber,ForwardingListener forwardingListener){
//		if(forwardNumber != null && forwardNumber.length() > 0){
//			UserData.saveForwardNumber(mContext, forwardNumber);
//		}
//		setCallForwardingOption(mContext, forwarding, forwardingListener);
//	}

    private static boolean isCallForwarding(Context mContext) {
        return UserData.isForwarding(mContext);
    }

    /**
     * @param filePath
     * @return 0:成功；-1:失败
     * @Description 开启录音
     * @date 2016-2-29 下午6:04:14
     * @author xhb
     */
    public static int StartRecord(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            CustomLog.v("录音 文件名不能为空");
            return -1;
        }
        CustomLog.v("开始通话录音" + filePath);
        UGoAPIParam.getInstance().audioRecordConfig.filePath = filePath;
        UGoAPIParam.getInstance().audioRecordConfig.fileFormat = 1; // wav文件格式
        UGoAPIParam.getInstance().audioRecordConfig.recordMode = 0;    //record mode, 0: all, 1: mic, 2: speaker
        int result = UGoManager.getInstance().pub_UGoStartRecord(UGoAPIParam.getInstance().audioRecordConfig);
        return result;
    }

    /**
     * @return 0:成功；-1:失败
     * @Description 停止录音
     * @date 2016-2-29 下午6:04:42
     * @author xhb
     */
    public static int StopRecord() {
        CustomLog.v("结束通话录音");
        return UGoManager.getInstance().pub_UGoStopRecord();
    }

    /**
     * @param isEnable true:开启；false：不开启
     * @return void    返回类型
     * @Description 控制是否开启外部音频传输
     * @date 2016-3-30 下午4:51:55
     * @author xhb
     */
    public static void setExtAudioTransEnable(Context context, boolean isEnable) {
        if (!DefinitionAction.isLicenseVersion())
            return;
        UserData.saveExtAudioTransEnable(context, isEnable);

        CpsTools.setNoRecordEnable(isEnable);
        CpsTools.setNoTrackEnable(isEnable);

        UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID, UGoAPIParam.getInstance().stMediaCfg, 0);
        UGoAPIParam.getInstance().stMediaCfg.extAudioTransEnabled = isEnable;
        int media_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID, UGoAPIParam.getInstance().stMediaCfg, 0);
        CustomLog.v(media_config_result == 0 ? "setExtAudioTransEnable success" : "setExtAudioTransEnable failure");
    }


    private static SurfaceView remoteSurfaceView;
    private static SurfaceView localSurfaceView;
    private static LinearLayout remoteLL;
    private static LinearLayout localLL;
    private static Context SurfaceViewContext;

    private static SurfaceView createLocalSurfaceView(Context mContext) {
        localSurfaceView = new SurfaceView(mContext);
        return localSurfaceView;
    }

    /**
     * @param mActivity
     * @param remoteLinearLayout:显示对方视频的布局
     * @param localLinearLayout：显示自己视频的布局
     * @author: xiaozhenhua
     * @data:2014-8-7 下午6:22:21
     */
    public static void initCameraConfig(Context mContext, LinearLayout remoteLinearLayout, LinearLayout localLinearLayout) {
        SurfaceViewContext = mContext;
        localLL = localLinearLayout;
        remoteLL = remoteLinearLayout;
        remoteSurfaceView = ViERenderer.CreateRenderer(mContext, true);
        localSurfaceView = createLocalSurfaceView(mContext);
        VideoCaptureAndroid.setLocalPreview(localSurfaceView.getHolder());

        remoteLinearLayout.addView(remoteSurfaceView);
        localLinearLayout.addView(localSurfaceView);
//		int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID,UGoAPIParam.getInstance().stMediaCfg, 0);
        //CustomLog.v("初始化配置:"+result);

//		UGoAPIParam.getInstance().stRTPCfg.uiRTPTimeout = 45;
//		result = UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_RTP_CFG_MODULE_ID,UGoAPIParam.getInstance().stRTPCfg, 0);
        //CustomLog.v("配置stRTPCfg:"+result);
        //UGoAPIParam.getInstance().stVideoRenderCfg.renderMode = 1;
        //UGoAPIParam.getInstance().stVideoRenderCfg.pWindowLocal = localSurfaceView;
        //UGoAPIParam.getInstance().stVideoRenderCfg.pWindowRemote = remoteSurfaceView;
        //result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_RENDER_CFG_MODULE_ID, UGoAPIParam.getInstance().stVideoRenderCfg, 0);
        //CustomLog.v("配置stVideoRenderCfg:"+result);
    }

    /**
     * 用户自定义Dec
     *
     * @param videoDecparam:编码参数
     * @param videoEncParam：解码参数
     * @author: xiaozhenhua
     * @data:2015-5-5 下午5:27:14
     */
    public static void setVideoAttr(VideoDecParam videoDecparam, VideoEncParam videoEncParam) {
        if (videoDecparam != null) {
//			UGoAPIParam.getInstance().stVideoDecCfg.usWidth = videoDecparam.usWidth;
//	        UGoAPIParam.getInstance().stVideoDecCfg.usHeight = videoDecparam.usHeight;
            UGoAPIParam.getInstance().videoDecodeConfig.maxFrameRate = videoDecparam.ucmaxFramerate;
            UGoAPIParam.getInstance().videoDecodeConfig.maxDefinition = videoDecparam.uiMaxDefinition;
            UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_DEC_CFG_MODULE_ID, UGoAPIParam.getInstance().videoDecodeConfig, 0);
        }
        if (videoEncParam != null) {
            UGoAPIParam.getInstance().videoEncodeConfig.usMinBitrate = videoEncParam.usMinBitrate;
            UGoAPIParam.getInstance().videoEncodeConfig.usMaxBitrate = videoEncParam.usMaxBitrate;
            UGoAPIParam.getInstance().videoEncodeConfig.usStartBitrate = videoEncParam.usStartBitrate;
            UGoAPIParam.getInstance().videoEncodeConfig.usMaxFramerate = videoEncParam.ucmaxFramerate;
            UGoAPIParam.getInstance().videoEncodeConfig.usWidth = videoEncParam.usWidth;
            UGoAPIParam.getInstance().videoEncodeConfig.usHeight = videoEncParam.usHeight;
            UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_ENC_CFG_MODULE_ID, UGoAPIParam.getInstance().videoEncodeConfig, 0);
        }
    }

    /**
     * 设置音频编码格式
     *
     * @param audioEncParam
     */
    public static void setAudioAttr(EncodeConfig audioEncParam) {
        if (audioEncParam != null) {
            ArrayList<CodecConfig> codecList = new ArrayList<CodecConfig>();
            CodecConfig codecConfig = new CodecConfig();
            codecConfig.pltype = audioEncParam.pltype;
            codecConfig.plname = audioEncParam.plname;
            codecConfig.enabled = audioEncParam.enabled == 1 ? true : false;
            codecList.add(codecConfig);
            UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CODECS_CFG_MODULE_ID, codecList, 0);
        }
    }

    /**
     * 刷新摄像头
     *
     * @param cameraType:要刷新摄像头的枚举
     * @param frameType:           是否加上边框的枚举
     * @return
     * @author: xiaozhenhua
     * @data:2014-8-12 下午12:29:57
     */
    public static int refreshCamera(UCSCameraType cameraType, UCSFrameType frameType) {
        CustomLog.v("refreshCamera enter type:" + cameraType);
        int result = -1;
        result = refreshLinearLayout(cameraType, SurfaceViewContext, frameType);
//	    if(cameraType == UCSCameraType.LOCALCAMERA){
//	    	//result = UGoManager.getInstance().pub_UGoStartVideo(0x01 + 0x08);
//	    }
//	    else if (cameraType == UCSCameraType.REMOTECAMERA) {
//	    	//result = UGoManager.getInstance().pub_UGoStartVideo(0x04 + 0x10);
//	    }
        CustomLog.v("refreshCamera result:" + result);
        return result;
    }

    /**
     * 刷新摄像头
     *
     * @param cameraType:要刷新摄像头的枚举
     * @param frameType:           是否加上边框的枚举
     * @param isSend
     * @return
     */
    public static int refreshCamera(UCSCameraType cameraType, UCSFrameType frameType, boolean isSend) {
        CustomLog.v("refreshCamera enter type:" + cameraType);
        int result = -1;
        result = refreshLinearLayout(cameraType, SurfaceViewContext, frameType, isSend);
//	    if(cameraType == UCSCameraType.LOCALCAMERA){
//	    	//result = UGoManager.getInstance().pub_UGoStartVideo(0x01 + 0x08);
//	    }
//	    else if (cameraType == UCSCameraType.REMOTECAMERA) {
//	    	//result = UGoManager.getInstance().pub_UGoStartVideo(0x04 + 0x10);
//	    }
        CustomLog.v("refreshCamera result:" + result);
        return result;
    }


    private static int refreshLinearLayout(UCSCameraType cameraType, Context mContext, UCSFrameType frameMode) {
        int result = -1;
        if (cameraType == UCSCameraType.ALL || cameraType == UCSCameraType.LOCALCAMERA || cameraType == UCSCameraType.REMOTECAMERA || cameraType == UCSCameraType.BACKGROUNDCAMERA) {
            if (remoteLL != null) {
                remoteLL.removeView(remoteSurfaceView);
                remoteSurfaceView = ViERenderer.CreateRenderer(mContext, true);
            }
            localLL.removeView(localSurfaceView);
            localSurfaceView = createLocalSurfaceView(mContext);
            VideoCaptureAndroid.setLocalPreview(localSurfaceView.getHolder());
            //localSurfaceView.setVisibility(View.GONE);

            UGoAPIParam.getInstance().videoRenderConfig.pWindowRemote = remoteSurfaceView;
            UGoAPIParam.getInstance().videoRenderConfig.renderMode = frameMode.ordinal();
            //CustomLog.i("height:" + DensityUtil.getScreenHeight(mActivity) + " width:" + DensityUtil.getScreenWidth(mActivity));
            CustomLog.i("remote height:" + remoteLL.getHeight() + " width:" + remoteLL.getWidth());
//			UGoAPIParam.getInstance().stVideoRenderCfg.remoteHeight = DensityUtil.getScreenHeight(mActivity);
//			UGoAPIParam.getInstance().stVideoRenderCfg.remoteWidth = DensityUtil.getScreenWidth(mActivity);
            UGoAPIParam.getInstance().videoRenderConfig.remoteHeight = remoteLL.getHeight();
            UGoAPIParam.getInstance().videoRenderConfig.remoteWidth = remoteLL.getWidth();
            UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_RENDER_CFG_MODULE_ID, UGoAPIParam.getInstance().videoRenderConfig, 0);
            remoteLL.addView(remoteSurfaceView);
            //result = UGoManager.getInstance().pub_UGoStartVideo(7);
            if (cameraType == UCSCameraType.ALL) {
                result = UGoManager.getInstance().pub_UGoStartVideo(31);
            } else if (cameraType == UCSCameraType.LOCALCAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(0x01 + 0x08);
            } else if (cameraType == UCSCameraType.REMOTECAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(0x04 + 0x10);
            } else if (cameraType == UCSCameraType.BACKGROUNDCAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(27);
            }
            if (localLL != null) {
                localLL.addView(localSurfaceView);
            }
            localSurfaceView.setZOrderOnTop(true);
            localSurfaceView.setVisibility(View.VISIBLE);
            localLL.setVisibility(View.VISIBLE);
        }
        if (localLL != null && cameraType != UCSCameraType.REMOTECAMERA) {
            localLL.setVisibility(View.VISIBLE);
            localLL.bringToFront();
        }
        return result;
    }

    /**
     * 预览时发送视频流（录像使用）
     *
     * @param cameraType
     * @param mContext
     * @param frameMode
     * @param isSend
     * @return
     */
    private static int refreshLinearLayout(UCSCameraType cameraType, Context mContext, UCSFrameType frameMode, boolean isSend) {
        int result = -1;
        if (cameraType == UCSCameraType.ALL || cameraType == UCSCameraType.LOCALCAMERA || cameraType == UCSCameraType.REMOTECAMERA || cameraType == UCSCameraType.BACKGROUNDCAMERA) {
            if (remoteLL != null) {
                remoteLL.removeView(remoteSurfaceView);
                remoteSurfaceView = ViERenderer.CreateRenderer(mContext, true);
            }
            localLL.removeView(localSurfaceView);
            localSurfaceView = createLocalSurfaceView(mContext);
            VideoCaptureAndroid.setLocalPreview(localSurfaceView.getHolder());
            //localSurfaceView.setVisibility(View.GONE);

            UGoAPIParam.getInstance().videoRenderConfig.pWindowRemote = remoteSurfaceView;
            UGoAPIParam.getInstance().videoRenderConfig.renderMode = frameMode.ordinal();
            //CustomLog.i("height:" + DensityUtil.getScreenHeight(mActivity) + " width:" + DensityUtil.getScreenWidth(mActivity));
            CustomLog.i("remote height:" + remoteLL.getHeight() + " width:" + remoteLL.getWidth());
//			UGoAPIParam.getInstance().stVideoRenderCfg.remoteHeight = DensityUtil.getScreenHeight(mActivity);
//			UGoAPIParam.getInstance().stVideoRenderCfg.remoteWidth = DensityUtil.getScreenWidth(mActivity);
            UGoAPIParam.getInstance().videoRenderConfig.remoteHeight = remoteLL.getHeight();
            UGoAPIParam.getInstance().videoRenderConfig.remoteWidth = remoteLL.getWidth();
            UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_RENDER_CFG_MODULE_ID, UGoAPIParam.getInstance().videoRenderConfig, 0);
            remoteLL.addView(remoteSurfaceView);
            //result = UGoManager.getInstance().pub_UGoStartVideo(7);
            if (cameraType == UCSCameraType.ALL) {
                result = UGoManager.getInstance().pub_UGoStartVideo(31);
            } else if (cameraType == UCSCameraType.LOCALCAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(0x01 + 0x08 + 0x02);
            } else if (cameraType == UCSCameraType.REMOTECAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(0x04 + 0x10);
            } else if (cameraType == UCSCameraType.BACKGROUNDCAMERA) {
                result = UGoManager.getInstance().pub_UGoStartVideo(27);
            }
            if (localLL != null) {
                localLL.addView(localSurfaceView);
            }
            localSurfaceView.setZOrderOnTop(true);
            localSurfaceView.setVisibility(View.VISIBLE);
            localLL.setVisibility(View.VISIBLE);
        }
        if (localLL != null && cameraType != UCSCameraType.REMOTECAMERA) {
            localLL.setVisibility(View.VISIBLE);
            localLL.bringToFront();
        }
        return result;
    }

    /**
     * 获取摄像头数量
     *
     * @return
     * @author: xiaozhenhua
     * @data:2014-8-1 下午4:56:21
     */
    public static int getCameraNum() {
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            return Camera.getNumberOfCameras();
        } else {
            return 1;
        }
    }

    /**
     * @return true：是；false：否
     * @Description 获取是否使用外部摄像头来拨打视频
     * @date 2016-2-29 下午7:21:12
     * @author xhb
     */
    public static boolean getVideoExternCapture() {
        if (!DefinitionAction.isLicenseVersion())
            return false;

        return UserData.getVideoExternCapture();
    }

    public static int getCurrentCameraIndex() {
        return UGoAPIParam.getInstance().videoCaptureConfig.cameraIdx;
    }

    /**
     * 切换摄像头
     *
     * @param cameraIndex:要切换摄像头的索引
     * @param rotateType:视频翻转角度
     * @return
     * @author: xiaozhenhua
     * @data:2015-6-5 上午10:05:40
     */
    public static int switchCameraDevice(int cameraIndex, RotateType rotateType) {
        if (cameraIndex < getCameraNum()) {
            UGoAPIParam.getInstance().videoCaptureConfig.cameraIdx = cameraIndex;
            if (rotateType != null) {
                if (rotateType == RotateType.DEFAULT) {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = -1; // 顺时针旋转90度
                } else if (rotateType == RotateType.RETATE_0) {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = 0;  // 横的90度，头朝x轴负方向
                } else if (rotateType == RotateType.RETATE_90) {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = 90;    // 顺时针旋转90度
                } else if (rotateType == RotateType.RETATE_180) {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = 180;    // 顺时针旋转180度
                } else if (rotateType == RotateType.RETATE_270) {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = 270;    // 顺时针旋转270度
                } else {
                    UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = -1;
                }
            } else {
                UGoAPIParam.getInstance().videoCaptureConfig.rotateAngle = -1;
            }
            UGoAPIParam.getInstance().videoCaptureConfig.width = 0;
            UGoAPIParam.getInstance().videoCaptureConfig.height = 0;
            UGoAPIParam.getInstance().videoCaptureConfig.maxFps = 15;
            return UGoManager.getInstance().pub_UGoVideoSetCaptureCapability(UGoAPIParam.getInstance().videoCaptureConfig);
        } else {
            notifyDialFailed(new UcsReason(CALL_VIDEO_DOES_NOT_SUPPORT).setMsg("the device does not support video calls，" + "cameraIndex：" + cameraIndex + ";cameraNumber:" + getCameraNum()));
            return -1;
        }
    }

    /**
     * 打开摄像头
     *
     * @param camera:打开远程或本地摄像头 1:预览 2：发送 4：接收
     * @return
     * @author: xiaozhenhua
     * @data:2014-8-12 下午12:31:00
     */
    public static int openCamera(UCSCameraType camera) {
        if (camera == UCSCameraType.LOCALCAMERA) {
            localSurfaceView.setVisibility(View.VISIBLE);
        } else {
            remoteSurfaceView.setVisibility(View.VISIBLE);
        }
        //return UGoManager.getInstance().pub_UGoStartVideo(camera == UCSCameraType.LOCALCAMERA ? 2 : 1);
        //return UGoManager.getInstance().pub_UGoStartVideo(camera == UCSCameraType.LOCALCAMERA ? 31 : 1);
        //return UGoManager.getInstance().pub_UGoStartVideo(camera == UCSCameraType.LOCALCAMERA ? 1 : 16);
        return UGoManager.getInstance().pub_UGoStartVideo(31);
    }

    /**
     * 视频模式切换
     *
     * @param cameraType UCSCameraType.LOCALCAMERA:发送
     *                   UCSCameraType.REMOTECAMERA:接收
     *                   UCSCameraType.ALL:亦发送 亦接收
     * @return result
     * @author xhb
     * @data 2015-11-3
     */
    public static int switchVideoMode(UCSCameraType cameraType) {
        int result = -1;
        CustomLog.v("switchVideoMode enter type:" + cameraType);
        /** add by xhb 20151103
         *	0x01: stop capture
         *	0x02: stop send
         *	0x04: stop receive
         *	0x08: stop render local
         *	0x10: stop render remote
         */
        //现将所有的模式打开， 而后根据type值选择关闭相应的模式。
        UGoManager.getInstance().pub_UGoStartVideo(0x1F);// 0x1F = 0x01+0x02+0x04+0x08+0x10
        if (cameraType == UCSCameraType.LOCALCAMERA) {
            //发送模式 ， 在所有模式的开启的情况下 关闭接收模式 和 对端视图显示
            result = UGoManager.getInstance().pub_UGoStopVideo(0x14);
        } else if (cameraType == UCSCameraType.REMOTECAMERA) {
            //接收模式 ， 在所有模式的开启的情况下 关闭发送模式 和 本地视图显示
            result = UGoManager.getInstance().pub_UGoStopVideo(0x0B);
        }
//			result = (cameraType == UCSCameraType.ALL ? UGoManager.getInstance().pub_UGoStartVideo(31) : UGoManager.getInstance().pub_UGoStopVideo(cameraType == UCSCameraType.LOCALCAMERA ? 11 : 20));
//		if(UserData.getCallType() == 3) {	// 如果是视频通话则保存当前视频模式状态
//			UserData.saveLocalCameraType(cameraType);
//		}
        CustomLog.v("switchVideoMode type:" + cameraType + "，result:" + result);
        return result;
    }


    /**
     * 关闭摄像头
     *
     * @param camera:关闭远程或本地摄像头
     * @return
     * @author: xiaozhenhua
     * @data:2014-8-12 下午12:32:22
     */
    public static int closeCamera(UCSCameraType camera) {
        if (camera == UCSCameraType.ALL) {
            //return UGoManager.getInstance().pub_UGoStopVideo(7);
            return UGoManager.getInstance().pub_UGoStopVideo(31);
        } else if (camera == UCSCameraType.BACKGROUNDCAMERA) {
            return UGoManager.getInstance().pub_UGoStopVideo(27);
        } else {
            if (camera == UCSCameraType.LOCALCAMERA) {
                localSurfaceView.setVisibility(View.INVISIBLE);
                return UGoManager.getInstance().pub_UGoStopVideo(1);
            } else {
                remoteSurfaceView.setVisibility(View.INVISIBLE);
                //return UGoManager.getInstance().pub_UGoStopVideo(4);
                return UGoManager.getInstance().pub_UGoStopVideo(16);
            }
            //return UGoManager.getUGoManager().pub_UGoStopVideo(camera == UCSCameraType.LOCALCAMERA ? 2 : 1);
        }
    }

    /**
     * 视频来电时是否支持预览
     *
     * @param isPreView YES:支  NO:不支持
     * @author: xiaozhenhua
     * @data:2015-5-20 下午4:18:11
     */
    public static void setCameraPreViewStatu(Context mContext, boolean isPreView) {
        UserData.saveVideoEnabled(mContext, isPreView ? 2 : 1);
    }

    /**
     * 是否开启未接来电时的视频预览功能
     *
     * @param mContext
     * @return
     * @author: xiaozhenhua
     * @data:2015-5-21 下午2:53:14
     */
    public static boolean isCameraPreviewStatu(Context mContext) {
        return UserData.getVideoEnabled(mContext) == 2;
    }

    /**
     * 视频截图
     *
     * @param isLocal:本地视频or远程视频
     * @param filename:文件名称
     * @param savePath：文件路径
     * @author: xiaozhenhua
     * @data:2015-5-21 下午3:13:57
     */
    public static void videoCapture(UCSCameraType isLocal, String filenName, String savePath) {
        CustomLog.v("videoCapture()");
        if (TextUtils.isEmpty(filenName) || TextUtils.isEmpty(savePath)) {
            for (CallStateListener listener : UCSCall.getCallStateListener()) {
                listener.onCameraCapture(null);
            }
            return;
        }
        UserData.saveScreenFilePath(savePath);
        UserData.saveScreenFileName(filenName);
        int result = UGoManager.getInstance().pub_UGoScreenshotStart(isLocal == UCSCameraType.REMOTECAMERA ? 0 : 1, 0);
        CustomLog.v("pub_UGoScreenshotStart() result = " + result);
    }

    /**
     * 视频旋转
     *
     * @param landscape        视频显示，横屏还是竖屏 0:portrait   1:landscape
     * @param recived_rotation 接收的视频需要旋转的角度
     * @author maoyuanqing
     * @data 2015-7-29 上午 11:52:24
     */
    public static void videoUpdateLocalRotation(int landscape, int recived_rotation) {
        UGoManager.getInstance().pub_UGoVideoUpdateLocalRotation(landscape, recived_rotation);
    }

    /**
     * 闪关灯 控制
     *
     * @param mode 设置模式 true 打开闪光灯 , false关闭闪光灯
     * @author maoyuanqing
     * @data 2015-7-29 上午11:57:23
     */
    public static void setFlashCode(boolean mode) {
        VideoCaptureAndroid.setFlashMode(mode);
    }

    /**
     * 获取闪光灯状态
     *
     * @return
     */
    public static boolean getFlashCode() {
        return VideoCaptureAndroid.getFlashMode();
    }


    /**
     * @param context
     * @return
     * @author zhangbin
     * @2016-2-18
     * @descript:获取CPS下发的参数，JSON格式
     */
    public static String getCpsParamterDebug(Context context) {
        return CpsTools.getCpsParamterDebug(context);
    }

    /**
     * @param bEnable 是否打开开关
     * @return void    返回类型
     * @Description 媒体流加密开关
     * @date 2016年3月1日 下午6:50:00
     * @author zhj
     */
    public static void setEncryptEnable(boolean bEnable) {
        if (!DefinitionAction.isLicenseVersion())
            return;

        UGoAPIParam.getInstance().stMediaCfg.rtpEncEnabled = bEnable;

        int media_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID, UGoAPIParam.getInstance().stMediaCfg, 0);
    }

    /**
     * 用于虚拟摄像头，从data将数据插入，配合ME_video_extern_capture_param_t结 构体。
     *
     * @param data 输入数据流
     * @param len  输入流的长度
     * @return int  成功返回0  失败返回-1
     * @Description 插入外部文件数据（h264或者yuv）
     * @date 2015-12-19 上午11:18:39
     * @author xhb
     */
    public static int VideoIncomingFrame(byte[] data, int len) {
        if (!DefinitionAction.isLicenseVersion())
            return -1;

        return UGoManager.getInstance().pub_UGoVideoIncomingFrame(data, len);
    }

    /**
     * @param VideoExternFormat 0:i420 1:h264
     * @param useExternCapture  true:使用外部摄像头
     * @return void    返回类型
     * @Description 设置是否使用外部摄像头和输入流的文件格式
     * @date 2015-12-19 上午11:24:20
     * @author xhb
     */
    public static int setVideoExternCapture(VideoExternFormat ucExternformate, boolean useExternCapture) {
        if (!DefinitionAction.isLicenseVersion())
            return -1;

        UserData.saveVideoExternCapture(useExternCapture);
        UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_VIDEO_EXTERN_CAPTURE_CFG_MODULE_ID,
                UGoAPIParam.getInstance().stVideoExternCapture, 0);
        UGoAPIParam.getInstance().stVideoExternCapture.ucExternformate = ucExternformate.ordinal();
        UGoAPIParam.getInstance().stVideoExternCapture.useExternCapture = useExternCapture;
        CustomLog.v("Externformate:" + UGoAPIParam.getInstance().stVideoExternCapture.ucExternformate);
        CustomLog.v("ExternCapture:" + UGoAPIParam.getInstance().stVideoExternCapture.useExternCapture);
        int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_EXTERN_CAPTURE_CFG_MODULE_ID, UGoAPIParam.getInstance().stVideoExternCapture, 0);
        CustomLog.v(result == 0 ? "set virtual camera success" : "set virtual camera failure");
        return result;
    }

    /**
     * @param agc_rx_enable            true：设置为接收增益
     * @param agc_enable               true：设置为发送增益
     * @param agc_compressionGaindB    发送端自适应语音增益的压缩增益
     *                                 设置范围值为1 - 30；建议值为6 - 12，6为小音量，9为默认音量，12为大音量， 1 - 6 或者 12 - 30 的值需要谨慎使用，通话前或者通话中设置均有效，建议通话前设置。
     * @param agc_targetDbfs           发送端自适应语音增益模块的目标电平
     *                                 自适应语音增益的目标电平: 取值范围:1---15; 此值越小音量越大；建议值为3 - 9，9为小音量，6为默认音量，3为大音量，1 - 3 或者 9 - 15 的值需要谨慎使用。
     * @param agc_Rx_compressionGaindB 接收端自适应语音增益的压缩增益
     *                                 设置范围值为1 - 30；建议值为6 - 12，6为小音量，9为默认音量，12为大音量， 1 - 6 或者 12 - 30 的值需要谨慎使用，通话前或者通话中设置均有效，建议通话前设置。
     * @param agc_Rx_targetDbfs        接收端自适应语音增益模块的目标电平
     *                                 自适应语音增益的目标电平: 取值范围:1---15; 此值越小音量越大；建议值为3 - 9，9为小音量，6为默认音量，3为大音量，1 - 3 或者 9 - 15 的值需要谨慎使用。
     * @return int 成功返回0  失败返回-1
     * @Description 设置AGC音效增益  此接口建议在拨打电话之前设置
     * @date 2016-5-20 下午3:37:04
     * @author xhb
     */
    public static int setAGCPlus(boolean agc_rx_enable, boolean agc_enable, int agc_compressionGaindB, int agc_targetDbfs, int agc_Rx_compressionGaindB, int agc_Rx_targetDbfs) {
        if (agc_targetDbfs >= 1 && agc_targetDbfs <= 15 && agc_compressionGaindB >= 1 && agc_compressionGaindB <= 30) {
            UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_VQE_CFG_MODULE_ID, UGoAPIParam.getInstance().stVQECfg, 0);
            UGoAPIParam.getInstance().stVQECfg.AgcRxEnable = agc_rx_enable;
            UGoAPIParam.getInstance().stVQECfg.AgcEnable = agc_enable;
            UGoAPIParam.getInstance().stVQECfg.AgcCompressionGaindB = agc_compressionGaindB;
            UGoAPIParam.getInstance().stVQECfg.AgcTargetDbfs = agc_targetDbfs;
            UGoAPIParam.getInstance().stVQECfg.AgcRxCompressionGaindB = agc_Rx_compressionGaindB;
            UGoAPIParam.getInstance().stVQECfg.AgcRxTargetDbfs = agc_Rx_targetDbfs;
            int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VQE_CFG_MODULE_ID, UGoAPIParam.getInstance().stVQECfg, 0);
            CustomLog.v(result == 0 ? "set agc success" : "set agc failure");
            return result;
        } else {
            CustomLog.v("set agc failure agc_targetDbfs:" + agc_targetDbfs + " agc_compressionGaindB:" + agc_compressionGaindB);
            return -1;
        }
    }

    /**
     * @param ecEnable  回声消除使能
     * @param agcEnable 增益使能
     * @param nsEnable  噪声抑制使能
     * @param agcEnable 接收端增益使能
     * @param nsEnable  接收端噪声抑制使能
     * @return int    返回类型
     * @Description 3A算法使能开关
     * @date 2017年6月17日 下午6:15:54
     * @author zhj
     */

    public static int setVqeEnable(boolean ecEnable, boolean agcEnable, boolean nsEnable, boolean agcRxEnable, boolean nsRxEnable) {
        UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_VQE_CFG_MODULE_ID, UGoAPIParam.getInstance().stVQECfg, 0);
        UGoAPIParam.getInstance().stVQECfg.EcEnable = ecEnable;
        UGoAPIParam.getInstance().stVQECfg.AgcEnable = agcEnable;
        UGoAPIParam.getInstance().stVQECfg.NsEnable = nsEnable;
        UGoAPIParam.getInstance().stVQECfg.AgcRxEnable = agcRxEnable;
        UGoAPIParam.getInstance().stVQECfg.NsRxEnable = nsRxEnable;
        int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VQE_CFG_MODULE_ID, UGoAPIParam.getInstance().stVQECfg, 0);
        CustomLog.v(result == 0 ? "setVqeEnable success" : "setVqeEnable failure");
        return result;
    }

    /**
     * @param isopen true：是；false：不是
     * @return void    返回类型
     * @Description 在RTP超时时是否挂断电话
     * @date 2016-7-31 上午8:43:28
     * @author xhb
     */
    public static void setRtpAutoHangup(Context context, boolean isopen) {
        if (!DefinitionAction.isLicenseVersion())
            return;

        UserData.setRtpAtuoHangup(context, isopen);
    }

    /**
     * 设置屏幕显示的方向
     *
     * @param context
     * @param orientation false：竖屏,目前只支持竖屏
     * @date 2016-11-10
     */
    public static void setScreenOrientation(Context context, boolean orientation) {
        UserData.setScreenOrientation(context, false);
    }

    /**
     * @param callType 呼叫类型
     * @param numbers  用于发起呼叫的号码列表
     * @return void    返回类型
     * @Description 呼叫同振
     * @date 2017-1-9 下午4:44:21
     * @author xhb
     */
    public static void groupDial(CallType callType, String[] numbers) {
        // 正常拨打，需要把缓存视频预览图片删除，防止去透传图片
        UserData.setPreviewImgUrl("");
        CustomLog.v(" 1 -----------------");
        if (VoipCore.getInstance(null) != null) {
            CustomLog.v(" 2 -----------------");
            if (callType != null && numbers != null && numbers.length <= 5) {
                CustomLog.v(" 3 -----------------" + callType);
                switch (callType) {
                    case VOIP:        // 同振音频免费
                        VoipCore.getInstance(null).groupDial(4, numbers);
                        break;
                    case VIDEO:        // 同振视频免费
                        VoipCore.getInstance(null).groupDial(5, numbers);
                        break;
                    default:
                        break;
                }
            } else {
                notifyDialFailed(new UcsReason(CALL_REASON_CALLED_BEYOND_FIVE).setMsg("called beyond five"));
            }
        } else {
            for (ConnectionListener cl : UCSService.getConnectionListener()) {
                cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
            }
        }
    }

    /**
     * @param path         图片地址
     * @param callType     呼叫类型，目前支持视频，同振视频
     * @param calledNumner 呼叫号码
     * @param timeOut      上传图片的超时时间，单位是毫秒
     * @return void    返回类型
     * @Description 发送预览图片并拨打电话
     * @date 2017-2-16 下午4:21:18
     * @author xhb
     */
    public static void dialWithPreviewImg(String path, final CallType callType, final String[] calledNumber, int timeOut) {
        // 使用此功能时，先把前面保存的预览图片地址置空，防止出现图片地址错位的情况
        UserData.setPreviewImgUrl("");
        if (checkDialPreviewImgParams(path, callType)) {
            // 设置上传预览图片回调监听,主要是定时器回调和服务器返回URL地址回调
            VoipListenerManager.getInstance().setPreviewImgUrlListener(new PreviewImgUrlListener() {
                @Override
                public void callback() {
                    // 如果在保存图片之前就调用了挂断，那就不拨打电话。
                    boolean isHangup = UGoManager.getInstance().getHangup();
                    if (isHangup) {
                        CustomLog.d("isHangup:" + isHangup);
                        // 要重置isHangup,防止下一通电话不能拨打
                        setHangup();
                        return;
                    }
                    dialWithPreviewImg(callType, calledNumber);
                }
            });
            // 开始定时器
            TimerHandler.getInstance().startVideoPreviewTimer(timeOut);
            // 图片上传
            IGGUploadPreviewImgRequest previewImgRequest = new IGGUploadPreviewImgRequest(path);
            previewImgRequest.onSendMessage();
            return;
        }
        dialWithPreviewImg(callType, calledNumber);
    }

    private static boolean checkDialPreviewImgParams(String path, final CallType callType) {
        if (callType != null && callType != CallType.VIDEO) {
            // 只有视频呼叫才会使用此功能
            CustomLog.d("dialPreviewImg callType:" + callType);
            return false;
        }

        if (TextUtils.isEmpty(path)) {
            // 图片路径不能为空
            notifyDialFailed(new UcsReason().setReason(300280).setMsg("图片地址为空"));
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            // 图片是否存在
            notifyDialFailed(new UcsReason().setReason(300281).setMsg("图片地址不存在"));
            return false;
        }

        FileFilter fileFilter = new FileFilter(path);
        if (!fileFilter.format()) {
            // 判断图片格式 目前支持jpg png
            notifyDialFailed(new UcsReason().setReason(300282).setMsg("图片格式不正确"));
            return false;
        }

        if (!FileTools.isFileSizeExceed(path, 5, FileTools.FileUnit.M)) {
            // 用户传入的图片大小5M以内
            notifyDialFailed(new UcsReason().setReason(300283).setMsg("文件大小超过5M"));
            return false;
        }

        if (FileTools.isFileSizeExceed(path, 128, FileTools.FileUnit.K)) {
            // 如果用户传的图片大小小于128K，则不进行压缩
            return true;
        }

        // 图片压缩
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapForFile(path, 240, 320);
//		// 旋转图片270度 	
//		Bitmap adjustBitmap = BitmapUtils.adjustPhotoRotation(bitmap, 270);
        // 保存
        BitmapUtils.savePic(BitmapUtils.Bitmap2Byte(bitmap), file);

        if (!FileTools.isFileSizeExceed(path, 128, FileTools.FileUnit.K)) {
            // 压缩后的大小不能超过128，因为一个包是64K，最大分为2个包
            notifyDialFailed(new UcsReason().setReason(300284).setMsg("文件压缩后的大小超过128K"));
            return false;
        }
        return true;
    }

    private static void dialWithPreviewImg(final CallType callType, final String[] calledNumber) {
        if (calledNumber != null && calledNumber.length > 0) {
            // 保存被叫号码，用于作为透传的被叫
            saveNumbers(calledNumber);
            CustomLog.v(" 1 -----------------");
            if (VoipCore.getInstance(null) != null) {
                CustomLog.v(" 2 -----------------");
                if (callType != null && calledNumber.length <= 5) {
                    CustomLog.v(" 3 -----------------" + callType);
                    switch (callType) {
                        case VIDEO:        // 视频免费
                            if (calledNumber.length == 1) { // 单呼
                                VoipCore.getInstance(null).dial(3, calledNumber[0], "", "");
                            } else {    // 同振视频免费
                                VoipCore.getInstance(null).groupDial(5, calledNumber);
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    notifyDialFailed(new UcsReason(CALL_REASON_CALLED_BEYOND_FIVE).setMsg("called beyond five"));
                }
            } else {
                for (ConnectionListener cl : UCSService.getConnectionListener()) {
                    cl.onConnectionFailed(new UcsReason().setReason(300206).setMsg("ApplocationContext can not empty"));
                }
            }
        } else {
            notifyDialFailed(new UcsReason(CALL_NUMBER_IS_EMPTY).setMsg("calledNumner is null "));
        }
    }

    private static void saveNumbers(String[] calledNumbers) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < calledNumbers.length; i++) {
            if (i == calledNumbers.length - 1) { // 用：把每个被叫号码分隔开，最后面不要加：
                builder.append(calledNumbers[i]);
            } else {
                builder.append(calledNumbers[i] + ":");
            }
        }
        UserData.setCalledNumber(builder.toString());
    }

    /**
     * @return void    返回类型
     * @Description 在视频预览呼叫前使用，设置为没有挂断,主要是为了防止在拨打视频图片预览电话时先挂断了，但是拨打信令还是可以发出去
     * @date 2017-2-24 下午2:39:31
     * @author xhb
     */
    public static void setHangup() {
        UGoManager.getInstance().setHangup(false);
    }

    /**
     * @param bEnable 使能开关
     * @return void    返回类型
     * @Description 视频720p效果的使能开关
     * @date 2017年7月30日 下午7:34:58
     * @author zhj
     */
    public static void set720pEnable(boolean bEnable) {
        CustomLog.v("set720pEnable:" + bEnable);
        UserData.save720pEnable(bEnable);
        if (bEnable) {
            UGoManager.getInstance().pub_UGoPresetVideo(MeVideoProfilePreset.kME_VIE_PROFILE_PRESET_1280x720);
        } else {
            UGoManager.getInstance().pub_UGoPresetVideo(MeVideoProfilePreset.kME_VIE_PROFILE_PRESET_640x480);
        }
        
        UGoSetConfig.setDefaultBitrate();
    }

    /**
     * @param minBitrate   最小码率
     * @param maxBitrate   最小码率
     * @param startBitrate 最小码率
     * @return void    返回类型
     * @Description 设置视频码率
     * @date 2017年9月8日 上午10:27:32
     * @author zhj
     */
    public static void setVideoBitrate(int minBitrate, int maxBitrate, int startBitrate) {
        UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_VIDEO_ENC_CFG_MODULE_ID, UGoAPIParam.getInstance().videoEncodeConfig, 0);
        if (minBitrate > 0)
            UGoAPIParam.getInstance().videoEncodeConfig.usMinBitrate = minBitrate;
        if (maxBitrate > 0)
            UGoAPIParam.getInstance().videoEncodeConfig.usMaxBitrate = maxBitrate;
        if (startBitrate > 0)
            UGoAPIParam.getInstance().videoEncodeConfig.usStartBitrate = startBitrate;
        UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_ENC_CFG_MODULE_ID, UGoAPIParam.getInstance().videoEncodeConfig, 0);
    }

    public static void setLocalPreviewRotation(int rotation_camera) {
        VideoCaptureAndroid.setLocalPreviewRotation(rotation_camera);
    }

    /**
     * 开始录像，可以自己配置视频参数
     *
     * @param filePath
     * @param iDirect   0： input 远端视频; 1： output 本地视频
     * @param fileType  0：avi
     * @param width
     * @param height
     * @param bitrate   video record bitrate, in kbps
     * @param framerate video record framerate
     */
    public static void startVideoRecord(String filePath, int iDirect, int fileType, int width, int height, int bitrate, int framerate) {
        VideoRecordConfig videoRecordConfig = new VideoRecordConfig(filePath, iDirect, fileType, width, height, bitrate, framerate);
        UGoManager.getInstance().pub_UGoStartRecordVideo(videoRecordConfig);
    }

    /**
     * 开始录像，使用默认录制参数
     *
     * @param filePath
     * @param iDirect  0： input 远端视频; 1： output 本地视频
     */
    public static void startVideoRecord(String filePath, int iDirect) {
        VideoRecordConfig videoRecordConfig = new VideoRecordConfig(filePath, iDirect);
        UGoManager.getInstance().pub_UGoStartRecordVideo(videoRecordConfig);
    }

    /**
     * 停止录像
     */
    public static void stopVideoRecord() {
        UGoManager.getInstance().pub_UGoStopRecordVideo();
    }

    /**
     * @Description 设置视频分级编码参数
     * @param ucsHierEncAttr 分级编码参数
     * @return void    返回类型 
     * @date 2017年9月27日 上午9:43:39
     * @author zhj
     */
    public static void setHierEncAttr(UCSHierEncAttr ucsHierEncAttr) {
    	UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w240 = ucsHierEncAttr.low_framerate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w360 = ucsHierEncAttr.low_framerate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w480 = ucsHierEncAttr.low_framerate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w720 = ucsHierEncAttr.low_framerate_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w240 = ucsHierEncAttr.medium_framerate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w360 = ucsHierEncAttr.medium_framerate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w480 = ucsHierEncAttr.medium_framerate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w720 = ucsHierEncAttr.medium_framerate_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w240 = ucsHierEncAttr.high_framerate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w360 = ucsHierEncAttr.high_framerate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w480 = ucsHierEncAttr.high_framerate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w720 = ucsHierEncAttr.high_framerate_w720;
	    
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w240=ucsHierEncAttr.low_complexity_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w360=ucsHierEncAttr.low_complexity_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w480=ucsHierEncAttr.low_complexity_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w720=ucsHierEncAttr.low_complexity_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w240=ucsHierEncAttr.medium_complexity_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w360=ucsHierEncAttr.medium_complexity_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w480=ucsHierEncAttr.medium_complexity_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w720=ucsHierEncAttr.medium_complexity_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w240=ucsHierEncAttr.high_complexity_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w360=ucsHierEncAttr.high_complexity_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w480=ucsHierEncAttr.high_complexity_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w720=ucsHierEncAttr.high_complexity_w720;
	    
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w240=ucsHierEncAttr.low_bitrate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w360=ucsHierEncAttr.low_bitrate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w480=ucsHierEncAttr.low_bitrate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w720=ucsHierEncAttr.low_bitrate_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w240=ucsHierEncAttr.medium_bitrate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w360=ucsHierEncAttr.medium_bitrate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w480=ucsHierEncAttr.medium_bitrate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w720=ucsHierEncAttr.medium_bitrate_w720;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w240=ucsHierEncAttr.high_bitrate_w240;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w360=ucsHierEncAttr.high_bitrate_w360;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w480=ucsHierEncAttr.high_bitrate_w480;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w720=ucsHierEncAttr.high_bitrate_w720;
	    
	    int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_PRESET_ADAPTER_CFG_MODULE_ID, UGoAPIParam.getInstance().videoPresetAdapter, 0);
        CustomLog.v("配置videoPresetAdapter:"+result);
    }
}

