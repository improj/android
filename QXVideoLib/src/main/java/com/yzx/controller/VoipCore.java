package com.yzx.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.gl.softphone.CallReport;
import com.gl.softphone.GroupDialingConfig;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.controller.listenercallback.VoipAudioDeviceUpdateCallBack;
import com.yzx.controller.listenercallback.VoipLoginCallBack;
import com.yzx.controller.listenercallback.VoipReloginCallBack;
import com.yzx.controller.listenercallback.VoipSdkStatusCallBack;
import com.yzx.controller.listenercallback.VoipTcpRecvCallBack;
import com.yzx.http.HttpTools;
import com.yzx.http.HttpTools.HttpCallbackListener;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.preference.UserData;
import com.yzx.tools.CallLogTools;
import com.yzx.tools.CpsTools;
import com.yzx.tools.CpuTools;
import com.yzx.tools.FileTools;
import com.yzx.tools.NotifyAudioDeviceUpdate;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.listener.OnRecvPerviewImgTransListener;
import com.yzxtcp.listener.OnSendTransRequestListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.tcp.packet.common.UCSTransStock;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Title VoipCore
 * @Description  处理电话业务核心类
 * @Company yunzhixun
 * @author xhb
 * @date 2016-9-23 上午11:44:09
 */
public class VoipCore extends UGoCallbacks {

	private static Context mContext;
	private static volatile VoipCore voipCore;
	private boolean isVideo2Vioce = false; //是否是视频呼叫被协商成了音频通话
	private boolean self_recordenable = false;//录音使能是否由自己开启
	private String mCalledUid;	//
	private String mCalledPhone;
	private String mCallid;
	private String mPreviewImgUrl;
	private static String currentCallId;
	public static Context getContext() {
		return mContext;
	}

	public static VoipCore getInstance(Context mContext){
		if(voipCore == null && mContext != null){
			synchronized(VoipCore.class) {
				if (voipCore == null) {
					voipCore = new VoipCore(mContext);
				}
			}
		}
		return voipCore;
	}

	private VoipCore(Context mC){
		mContext = mC;
		initConfig();
		initPackage(mC);
		CustomLog.v("voipCore created");
	}

	public void initConfig(){
		UCSManager.setLoginListener(new VoipLoginCallBack());
		UCSManager.setReLoginListener(new VoipReloginCallBack());
		UCSManager.setISdkStatusListener(new VoipSdkStatusCallBack());
		NotifyAudioDeviceUpdate.setAudioDeviceUpdateListener(new VoipAudioDeviceUpdateCallBack());		// 驱动适配和cps适配回调	 onAudioDeviceUpdate onCpsConfigUpdate
		UCSManager.setTcpRecvListener(ITcpRecvListener.VOIPSDK,new VoipTcpRecvCallBack());
//		FileTools.createFolder();	// 这个在tcp SDK初始化的时候也做了
//		CpuTools.initCpuArchitecture(); 
//		DevicesTools.initIsDoubleTelephone(mContext);	// 初始化单卡还是双卡
//		ForwardingTools.initForwarding(mContext, new VoipProvidersForwardingCallBack());
		UGo_device_init();

		listenerNetworkStateBroadcast();
		CustomLog.v("voipCore mContext:" + mContext.toString());
        // 设置视频预览透传监听回调
		UCSManager.setPerviewImgTransListener(new OnRecvPerviewImgTransListener() {
			@Override
			public void onRecvTranslate(String callid, String previewImgUrl) {
				CustomLog.v("callid:"+ callid + ", previewImgurl:" + previewImgUrl);
				mCallid = callid;
				mPreviewImgUrl = previewImgUrl;
				// 先来电，再来透传,根据callid来判断是否是同一通电话
				if(!TextUtils.isEmpty(mCallid) && mCallid.equals(UCSCall.getCurrentCallId())) {
					CustomLog.v("start download previewImg");
					HttpTools.sendHttpRequest(mPreviewImgUrl, new MyHttpCallbackListener());
				}
			}
		});
	}

	private void listenerNetworkStateBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(receiver, intentFilter);
	}

	public void uninit() {
		mContext.unregisterReceiver(receiver);
	    UGoManager.getInstance().pub_UGoDestroy();
	    CustomLog.v( "销毁组件 ... ");
	}

	private static BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(intent.getAction(),ConnectivityManager.CONNECTIVITY_ACTION)) {
				 //获取网络类型传给组件
		        int networkType = NetWorkTools.getCurrentNetWorkType(mContext);
		        CustomLog.v("voipCore network change ... networkType = " + networkType);
		        if (getCurrentCallId() != null && getCurrentCallId().length() > 0) {
		        	//记录网络变化
			        CallLogTools.saveNetChange(networkType, false);
		        }

		        if (networkType == 4) {
		        	networkType = 8;
		        } else if (networkType == 8) {
		        	networkType = 1;
		        }
		        UGoManager.getInstance().pub_UGoUGoUpdateNetworkType(networkType);
			}
		}
	};

	private void initPackage(Context mContext){
		try {
			UserData.saveVersionName(mContext,mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
			UserData.savePackageName(mContext,mContext.getPackageName());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void eventCallback(int ev_type, int ev_reason, String message, String param) {
		switchEvent(ev_type, ev_reason, param, message);
	}

	private void switchEvent(int event, int reason, String param, String message) {
	    if (event != UGoAPIParam.EventTypeEnum.eUGo_NETWORK_EV.ordinal()) {//网络状态组件每秒上报一次，日志太多暂不打印
	        CustomLog.v("switchEvent event = " + event + ",  reason = " + reason + ",  param = " + param + ", message = " + message);
	    }
	    //增加关键日志上报
	    if (event == UGoAPIParam.EventTypeEnum.eUGo_CALLDIALING_EV.ordinal() || event == UGoAPIParam.EventTypeEnum.eUGo_CALLINCOMING_EV.ordinal()
	            || event == UGoAPIParam.EventTypeEnum.eUGo_CALLANSWER_EV.ordinal() || event == UGoAPIParam.EventTypeEnum.eUGo_CALLHUNGUP_EV.ordinal()) {
			CollectCallLog.setSDKPhoneMsg("SwitchEvent event = " + event + ",reason = " + reason);
		}
	    UGoAPIParam.EventTypeEnum eventType = UGoAPIParam.EventTypeEnum.values()[event];
		switch (eventType) {
		case eUGo_CALLDIALING_EV:// eUGo_CALLDIALING_EV(呼叫事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success: {	// 接听电话
				CustomLog.v( "接听电话  ... ");
				handlerCallAnswer(param);
//				set_rotate(); // 针对星光客户
				break;
			}
			case UGoAPIParam.eUGo_Reason_NotAccept:// 不接受或无法受理(媒体协商失败)
//			case UGoAPIParam.eUGo_Reason_RtppTimeOut:// RTPP 超时 挂断事件返回码
//			case UGoAPIParam.eUGo_Reason_UpdateMediaFail:// 媒体更新失败  组件没有上报
			case 700:// 服务器错误
				CustomLog.v( reason + ":媒体协商失败  ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_ERROR).setMsg("media negotiation failure"));
				break;
			case UGoAPIParam.eUGo_Reason_NoBalance:// 余额不足
				CustomLog.v( "余额不足  ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_NOT_ENOUGH_BALANCE).setMsg("sorry, your credit is running low"));
				break;
			case UGoAPIParam.eUGo_Reason_Busy:// 对方正忙
				CustomLog.v( "对方正忙  ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_BUSY).setMsg("the other side is busy"));
				break;
			case 480:// 标准的SIP反回对方拒绝
			case UGoAPIParam.eUGo_Reason_Reject:// 对方拒绝接听
				CustomLog.v( "对方拒绝接听 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_REFUSAL).setMsg("the other side refuse to answer"));
				break;
			case UGoAPIParam.eUGo_Reason_NotFind:// 该用户不在线
				CustomLog.v( "该用户不在线 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_NUMBER_OFFLINE).setMsg("the clientid not find"));
				break;
			case UGoAPIParam.eUGo_Reason_TooShort:// 被叫号码错误
				CustomLog.v( "被叫号码错误 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_NUMBER_WRONG).setMsg("the clientid error"));
				break;
			case UGoAPIParam.eUGo_Reason_CalleeFrozen:// 被叫号码冻结
				CustomLog.v( "被叫号码冻结 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_REJECT_ACCOUNT_FROZEN).setMsg("the other side frozen"));
				break;
			case UGoAPIParam.eUGo_Reason_Freeze:// 主叫帐号冻结
				CustomLog.v( "主叫号码冻结 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_ACCOUNT_FROZEN).setMsg("the clientid frozen"));
				break;
			case UGoAPIParam.eUGo_Reason_Expired:// 主叫帐号过期
				CustomLog.v( "主叫帐号过期 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_ACCOUNT_EXPIRED).setMsg("the clientid be overdue"));
				break;
			case UGoAPIParam.eUGo_Reason_Forbidden:// 不能拨打自己绑定号码
				CustomLog.v( "不能拨打自己绑定号码 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_CALLYOURSELF).setMsg(""));
				break;
			case UGoAPIParam.eUGo_Reason_NoResponse:// 被叫无应答(应答超时)
				CustomLog.v( "被叫无应答 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_NETWORK_TIMEOUT).setMsg("repuest time out"));//
				break;
			case UGoAPIParam.eUGo_Reason_NetworkDisable:// 网络类型不支持
				CustomLog.v( "网络类型不支持 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_REASON_NEWWORK_DISABLE).setMsg("network does not support"));
				break;
			case UGoAPIParam.eUGo_Reason_UnReachable: // 消息路由不可达
				CustomLog.v("消息路由不可达 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_REASON_UNREACHABLE).setMsg("message route unreachable"));
				break;
			case UGoAPIParam.eUGo_Reason_CallidNotExist:// VPS会话CallID不存在
                CustomLog.v( "该呼叫ID不存在... ");
                notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_CALLID_NOT_EXIST).setMsg("the clallid not exist"));
                break;
			case UGoAPIParam.eUGo_Reason_UserIdNotExist:// Called userid not exist
				CustomLog.v( "用户ID不存在... ");
                notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_USERID_NOT_EXIST).setMsg("the userid not exist"));
                break;
			case UGoAPIParam.eUGo_Reason_NoAnswer:// 对方无人应答
				CustomLog.v( "对方无人应答 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_NOT_ANSWER).setMsg("the other side not answer"));
				break;
			case UGoAPIParam.eUGo_Reason_ConnectFaild:// 落地线路无法接通
				CustomLog.v( "落地线路无法接通 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_REASON_CONNECTION_FAIL).setMsg("repuest time out"));
				break;
			case UGoAPIParam.eUGo_Reason_BlackList:	// 呼叫失败（频繁呼叫已被列入黑名单）
				CustomLog.v("呼叫失败（频繁呼叫已被列入黑名单）");
				notifyDialFailed(new UcsReason(UCSCall.CALL_FAIL_BLACKLIST).setMsg("frequent calls have been included in the blacklist"));
				break;
			case UGoAPIParam.eUGo_Reason_ProxyAuth:// (未登录)鉴权失败
				CustomLog.v( "Proxy鉴权失败 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_SESSION_EXPIRATION).setMsg("session expiration"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgHeadError: // 消息头解析错误
				CustomLog.v( "消息头解析错误 ... ");
				notifyDialFailed(new UcsReason(UCSCall.EVENT_REASON_HEAD_ERROR).setMsg("message head error"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgBodyError:	// 消息体解析错误
				CustomLog.v( "消息体解析错误 ... ");
				notifyDialFailed(new UcsReason(UCSCall.EVENT_REASON_BODY_ERROR).setMsg("message body error"));
				break;
			case UGoAPIParam.eUGo_Reason_CallIDExist: //会话已存在
				CustomLog.v( "会话已存在 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_REASON_CONVERSATION_EXIST).setMsg("conversation exist"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgTimeOut:// 信令超时
				CustomLog.v( "信令超时 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_REASON_SIGNAL_TIMEOUT).setMsg("repuest time out"));
				break;
			case 32:// 转直拨
			case UGoAPIParam.eUGo_Reason_NotifyPeerTimeout://转直拨呼叫超时
			case UGoAPIParam.eUGo_Reason_NotifyPeerNotFind:
			case UGoAPIParam.eUGo_Reason_NotifyPeerOffLine:
				CustomLog.v( "转直拨 ... ");
				notifyDialFailed(new UcsReason(UCSCall.CALL_VOIP_TRYING_183).setMsg("direct call"));
				break;
			case 47:
			case UGoAPIParam.eUGo_Reason_Connecting:
				CustomLog.v( "正在接通对方 ... ");
				handlerCallConnecting(param);
				break;
			case 38:// 对方正在响铃
			case 48:
			case UGoAPIParam.eUGo_Reason_Ringing:
				CustomLog.v( "对方正在响铃 ... ");
				CollectCallLog.setSDKPhoneMsg("The other side is alerting ...");
				for (CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onAlerting(UCSCall.getCurrentCallId());
				}
				// TODO 这个需要发送透传数据给用户，透传数据是callid和filepath
				CustomLog.d("previewImgUrl:" + UserData.getPreviewImgUrl());
				if(!TextUtils.isEmpty(UserData.getPreviewImgUrl())) {
					// 有预览图片地址，才进行透传
					String calledNumbers = UserData.getCalledNumber();
					if(!TextUtils.isEmpty(calledNumbers)) {
						String[] targetIds = calledNumbers.split(":");
						for (String targetId : targetIds) {
							UCSManager.sendTransData(targetId, new UCSTransStock() {
								@Override
								public String onTranslate() {
									return "测试";
								}
								@Override
								public String onPreviewImgData() {
									return UCSCall.getCurrentCallId() + "@@@" + UserData.getPreviewImgUrl();
								}
							}, new OnSendTransRequestListener() {
								@Override
								public void onSuccess(String msgId, String ackData) {

								}

								@Override
								public void onError(int errorCode, String msgId) {

								}
							});
						}

					}
				}
				break;
			case UGoAPIParam.eUGo_Reason_UnkownError: // 未知错误
				CustomLog.v( "未知错误 ... ");
				notifyDialFailed(new UcsReason(UCSCall.EVENT_REASON_UNKOWN_ERROR).setMsg("unkown error"));
				break;
			default:// 拨打失败
				if(reason >=10000 && reason <= 20000){ // 第三方AS服务器返回的自定义错误码
					//第三方AS服务器返回码
					CustomLog.v( "第三方AS服务器返回码:"+reason);
					notifyDialFailed(new UcsReason(reason).setMsg(""));
				}else{
					CustomLog.v( "拨打失败 ... reason=" + reason);
					notifyDialFailed(new UcsReason(UCSCall.OTHER_ERROR).setMsg("other error："+reason));
				}
				break;
			}
			break;
		case eUGo_CALLINCOMING_EV:// eUGo_CALLINCOMING_EV(电话呼入事件)
		    self_recordenable = false;
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success:
				CustomLog.v( "新的来电 ... "+param);
			    handlerCallIncoming(param);
			    // TODO 先来透传，再来电，要判断是否有透传数据，如果有则上抛给上层
			    if(!TextUtils.isEmpty(mCallid) && mCallid.equals(UCSCall.getCurrentCallId()) && !TextUtils.isEmpty(mPreviewImgUrl)) {
			    	// 透传数据的callid和这通电话的callid做对比，如果相同，则进行下载和上报。
			    	HttpTools.sendHttpRequest(mPreviewImgUrl, new MyHttpCallbackListener());
			    }
				break;
			default:
				CustomLog.v("其他来电错误码：reason=" + reason);
				break;
			}
			break;
		case eUGo_CALLANSWER_EV:// eUGo_CALLANSWER_EV(接听事件)
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_Success:// 接听、
				CustomLog.v( "接听 ... ");
				handlerCallIncomingAnswer();
//				set_rotate(); // 针对星光客户
				break;
			default:
				CustomLog.v("其他接听的错误码：reason=" + reason);
				break;
			}
			break;
		case eUGo_CALLHUNGUP_EV:// eUGo_CALLHUNGUP_EV(挂断事件)
			TimerHandler.getInstance().stopAnswerTimer();
			if (TimerHandler.getInstance().getAudioRecording()) {
				TimerHandler.getInstance().stopAudioRecordimer();
			    UCSCall.StopRecord();
			    TimerHandler.getInstance().setAudioRecording(false);
			}
			switch (reason) {
			case UGoAPIParam.eUGo_Reason_RtppTimeOut:// RTPP 超时
				CustomLog.v( "RTPP 超时 ... ");
				notifyHangUp(new UcsReason(UCSCall.CALL_VOIP_ERROR).setMsg("RTPP time out"));
				break;
			case UGoAPIParam.eUGo_Reason_CallidNotExist:// VPS会话CallID不存在
                CustomLog.v( "该呼叫ID不存在... ");
                notifyHangUp(new UcsReason(UCSCall.CALL_VOIP_CALLID_NOT_EXIST).setMsg("the clallid not exist"));
				break;
			case UGoAPIParam.eUGo_Reason_Cancel://Terminater for Cancel
			case UGoAPIParam.eUGo_Reason_HungupMyself:// 自己挂断电话
				CustomLog.v("自己挂断电话 ...");
				notifyHangUp(new UcsReason(UCSCall.HUNGUP_MYSELF).setMsg("myself hangup"));
				break;
			case UGoAPIParam.eUGo_Reason_Success://2014-8-29(兼容组件的一个信令错误) 挂断成功
			case UGoAPIParam.eUGo_Reason_HungupPeer:// 对方挂断电话
				CustomLog.v( reason+"对方挂断电话 ... ");
				notifyHangUp(new UcsReason(UCSCall.HUNGUP_OTHER).setMsg("the other side hangup"));
				break;
			case UGoAPIParam.eUGo_Reason_HungupTCPDisconnected: // TCP连接异常
				CustomLog.v(reason + "TCP连接异常...");
				notifyHangUp(new UcsReason(UCSCall.HANGUP_REASON_TCP_UNUSUAL).setMsg("tcp connection unusual"));
				break;
			case UGoAPIParam.eUGo_Reason_HungupRTPTimeout://RTP 超时
			    CustomLog.v( reason+"RTP超时挂断电话 ... ");
                notifyHangUp(new UcsReason(UCSCall.HUNGUP_RTP_TIMEOUT).setMsg("RTP time out hangup"));
                break;
			case UGoAPIParam.eUGo_Reason_MsgHeadError: // 消息头解析错误
				CustomLog.v( "消息头解析错误 ... ");
				notifyHangUp(new UcsReason(UCSCall.EVENT_REASON_HEAD_ERROR).setMsg("message head error"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgBodyError:	// 消息体解析错误
				CustomLog.v( "消息体解析错误 ... ");
				notifyHangUp(new UcsReason(UCSCall.EVENT_REASON_BODY_ERROR).setMsg("message body error"));
				break;
			case UGoAPIParam.eUGo_Reason_MsgTimeOut:// 信令超时
				CustomLog.v( "信令超时 ... ");
				notifyHangUp(new UcsReason(UCSCall.CALL_REASON_SIGNAL_TIMEOUT).setMsg("repuest time out"));
				break;
			case UGoAPIParam.eUGo_Reason_VpsGroupHunpup: // 同振挂断
				CustomLog.v(reason + "同振挂断...");
				notifyHangUp(new UcsReason(UCSCall.HUNGUP_GROUP).setMsg("group hungup"));
				break;
			case UGoAPIParam.eUGo_Reason_UnkownError: // 未知错误
				CustomLog.v( "未知错误 ... ");
				notifyHangUp(new UcsReason(UCSCall.EVENT_REASON_UNKOWN_ERROR).setMsg("unkown error"));
				break;
			default:
				CustomLog.v( reason+"其他原因挂断电话 ... ");
				notifyHangUp(new UcsReason(UCSCall.HUNGUP_OTHER_REASON).setMsg("the other side hangup"));
				break;
			}
			break;
		case eUGo_NETWORK_EV:// eUGo_NETWORK_EV(网络状态上报)
			//音视频质量数据已合并
			// voip音视频呼叫保存质量数据
			TimerHandler.getInstance().setCalllogLastMessage(param);
			CallLogTools.saveIceChange(param);
			CallLogTools.saveVideoRatio(param);

			int callType = UserData.getCallType();
			/*音视频通话质量数据已合并
			if ((callType == 3) && !isVideo2Vioce) {
				break;
			}
			*/
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onNetWorkState(reason, message);
			}
			break;

		case eUGo_UPSINGLEPASS_EV:// UP RTP single pass
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.singlePass(reason);
			}
		    CustomLog.v( " 单通 UP RTP single pass ");
		    //CollectCallLog.setSDKPhoneMsg("单通 UP RTP single pass");//“cqs”中已经包含了单通信息，这里暂不上报
		    //暂时不上报视频单通数据
		    if (CollectCallLog.getUpSPCount() < CallLogTools.SINGLEPASS_MAX_ITEMS && (message == null || !message.contains("video"))) {
		    	CollectCallLog.setUpSPCount(CollectCallLog.getUpSPCount() + 1);
		    	CallLogTools.saveUpSP();
		    }
            break;
		case eUGo_DNSINGLEPASS_EV:// DN RTP single pass
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.singlePass(reason);
			}
		    CustomLog.v( " 单通 DN RTP single pass ");
		    //CollectCallLog.setSDKPhoneMsg("单通 DN RTP single pass");//“cqs”中已经包含了单通信息，这里暂不上报
		    //暂时不上报视频单通数据
		    if (CollectCallLog.getDownSPCount() < CallLogTools.SINGLEPASS_MAX_ITEMS && (message == null || !message.contains("video"))) {
		    	CollectCallLog.setDownSPCount(CollectCallLog.getDownSPCount() + 1);
		    	CallLogTools.saveDownSP();
		    }
            break;
		case eUGo_TCPTRANSPORT_EV:
			break;
		case eUGo_GETDTMF_EV: //UGoAPIParam.eUGo_GETDTMF_EV:
			CustomLog.v("DTMF event ... reason=" + reason);
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onDTMF(reason);
			}
			break;
		case eUGo_REMOTE_VIDEO_STATE_NOTIFY_EV:	//对方视频模式切换
			CustomLog.v("视频模式切换...");
			CollectCallLog.setSDKPhoneMsg("video mode switch...reason=" + reason);
			switch (reason) {
			case 11:	//发送模式
//				UserData.saveRemoteCameraType(UCSCameraType.LOCALCAMERA);
				for(CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onRemoteCameraMode(UCSCameraType.LOCALCAMERA);
				}
				break;
			case 20:	//接收模式
//				UserData.saveRemoteCameraType(UCSCameraType.REMOTECAMERA);
				for(CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onRemoteCameraMode(UCSCameraType.REMOTECAMERA);
				}
				break;
			case 31:	//正常模式
//				UserData.saveRemoteCameraType(UCSCameraType.ALL);
				for(CallStateListener csl : UCSCall.getCallStateListener()) {
					csl.onRemoteCameraMode(UCSCameraType.ALL);
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void handlerCallIncomingAnswer() {
		CollectCallLog.setSDKPhoneMsg("Answer ...");
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onAnswer(UCSCall.getCurrentCallId());
		}
		TimerHandler.getInstance().startAnswerTimer();
		CallLogTools.initCallLog();
		TimerHandler.getInstance().startCallLogSampleTimer();
//		UserData.saveMySelfRefusal(mContext,false);
		recordEnable();
	}


	private void handlerCallIncoming(String param) {
		isVideo2Vioce = false;//视频呼叫协商成音频只发生在主叫端
		String phone = "";
		String userId="";
		String nickName = "";
		String userdata = "";
		int type = 0;
		CollectCallLog.setSDKPhoneMsg("New incoming call ...");
		CallLogTools.isCaller = false;
		if(param != null && param.length() > 0){
			try {
				JSONObject json = new JSONObject(param);
				if (json.has("callid")) {
					setCurrentCallId(json.getString("callid"));
				}
				if(json.has("fphone")){
					phone = json.getString("fphone");
				}
				if(json.has("fuid")){
					phone = json.getString("fuid");
				}
				if(json.has("fuserid") /*&& phone.length() <=0*/){
					userId =json.getString("fuserid");
				}
				if(json.has("videoflag")){    //0:音频    1：视频
					type = json.getInt("videoflag");
				}
				if(json.has("fnickname")){
					nickName = json.getString("fnickname");
				}
				if(json.has("user_data")){
					userdata = json.getString("user_data");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(type == 0){
			CustomLog.v( "音频来电 ... ");
			CollectCallLog.setSDKPhoneMsg("Voice incoming call ...");
			UserData.saveCallType(6);  // 保存拨打方式，音频来电拨打方式是6
		}else{
			CustomLog.v( "视频来电 ... ");
			CollectCallLog.setSDKPhoneMsg("Video incoming call ...");
			UserData.saveCallType(3);	// 保存拨打方式，视频来电拨打方式是3
		}
		if(UCSManager.isConnect()){//解决有些手机在锁屏是会修眠断网,但开屏时又会有收到来电的问题
			CustomLog.v( "report incoming ... ");
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onIncomingCall(UCSCall.getCurrentCallId(), type+"", userId, nickName, userdata);
				CustomLog.v("video sdk controller informaton phone:"+phone+"nickName"+nickName);
			}
//			UserData.saveMySelfRefusal(mContext,true);
//			UserData.saveLocalCameraType(UCSCameraType.ALL);	// 如果作为被叫，新的来电，初始化本地视频模式为ALL
//			UserData.saveRemoteCameraType(UCSCameraType.ALL);	// 如果作为被叫，新的来电，初始化远程视频模式为ALL
		}else{
			CustomLog.v( "discard incoming ... ");
			CollectCallLog.setSDKPhoneMsg("discard incoming ...");
		}
	}

	private void handlerCallConnecting(String param) {
		CollectCallLog.setSDKPhoneMsg("Call connecting the other side ...");
		if(param != null && param.length() > 0){
			try {
				JSONObject json = new JSONObject(param);
				if (json.has("callid")) {
					setCurrentCallId(json.getString("callid"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void handlerCallAnswer(String param) {
		CollectCallLog.setSDKPhoneMsg("Call answer  ...");
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onAnswer(UCSCall.getCurrentCallId());
		}
//		UserData.saveMySelfRefusal(mContext,false);
		TimerHandler.getInstance().startAnswerTimer();
		CallLogTools.initCallLog();
		TimerHandler.getInstance().startCallLogSampleTimer();
		recordEnable();

		//判断是否是视频呼叫并且被协商成为音频通话
		if (UserData.getCallType() == 3) {
		    try {
		        JSONObject json = new JSONObject(param);
		        if (json.has("videoflag")) {
		            if (json.getInt("videoflag") == 0) {
		                isVideo2Vioce = true;
		            }
		            else {
		                isVideo2Vioce = false;
		            }
		        }
		    } catch (JSONException e) {
		        e.printStackTrace();
		    }
		}
	}

	private void recordEnable() {
		//判断是否开启录音
		String recordenable = (String) SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.RECORD_KEY, "");
		CustomLog.v("record_enable： " + recordenable);
		if (self_recordenable || (recordenable != null && recordenable.equals("1"))) {
		    //开启录音
			UCSCall.StartRecord(FileTools.createAudioRecordFile(UCSCall.getCurrentCallId()));
		    TimerHandler.getInstance().setAudioRecording(true);
		    TimerHandler.getInstance().startAudioRecordimer();
		}
	}

	private void notifyHangUp(UcsReason hangup) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onHangUp(UCSCall.getCurrentCallId(), hangup);
		}
		callLogHandler(hangup);
	}

	private void notifyDialFailed(UcsReason dial) {
		for (CallStateListener csl : UCSCall.getCallStateListener()) {
			csl.onDialFailed(UCSCall.getCurrentCallId(),dial);
		}
		callLogHandler(dial);
	}

	private void callLogHandler(UcsReason dial) {
		//获取eModel值
		CallReport callReport = new CallReport();
        int res = UGoManager.getInstance().pub_UGoGetCallReport(callReport);
        if ( res != 0 )
        {
        	CustomLog.v("callLogHandler fail,  pub_UGoGetCallReport return " + res);
        	return;
        }
		CallLogTools.parseEmodel(callReport);

		TimerHandler.getInstance().stopCallLogSampleTimer();
		CollectCallLog.setSDKPhoneMsg(dial.getReason()+"|"+dial.getMsg());
		CollectCallLog.sdkPhoneReport(UCSCall.getCurrentCallId());
		setCurrentCallId("");
//		ErrorCodeReportTools.collectionErrorCode(mContext, UserData.getUserId(mContext), "", dial.getReason(), dial.getMsg());
	}

	@Override
	public void sendCallback(byte[] message, int len) {
		CustomLog.v( "UGO SEND MESSAGE LENGTH:" + message.length);
		UCSManager.sendPacket(102, message);
	}

	@Override
	public void traceCallback(String summary, String detail, int level) {
//		CustomLog.v( "TRACE_CALL_BACK:" + "  summary:" + summary + "   detail:" + detail + "   level:" + level);
		//if(detail.contains("CALLID") &&detail.contains("CALL_LOG"))
		//只上报report级别的日志，与iOS保持一致
		if(level == 0x4000) {
			CollectCallLog.setSDKPhoneMsg("TRACE_CALL_BACK:" + "|summary:" + summary + "|detail:" + detail + "|level:" + level);
		}
	}

	private void UGo_device_init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int media_result = UGoManager.getInstance().pub_UGoLoadMediaEngine();
				CustomLog.v((media_result == 0 ? "媒体组件初始化成功:" : "媒体组件初始化失败:") + media_result);
				UGoManager.getInstance().pub_setAndroidContext(mContext);
				int init_result = UGoManager.getInstance().pub_UGoInit();
				CustomLog.v((init_result == 0 ? "UGo组件初始化成功:" : "UGo组件初始化失败:") + init_result);

				//cps的相关操作放在UGO初始化之后，在组件初始化之前不要对组件设置参数
				CpsTools.setCpsAudioAdapterParam(mContext);
		        CpsTools.setCpsDefPermission(mContext);
		        UGoManager.getInstance().pub_UGoCallbacks(VoipCore.this);
				checkVideoLib();
				
				//因组件编码协商问题，暂将VP8编码去除
				UGoSetConfig.disableVP8();
				UGoSetConfig.setDefaultBitrate();
				//设置默认分级编码参数
				UGoSetConfig.setDefaultVideoPreset();
				//获取设备cpu核心个数
				CustomLog.v("Sum of cpu core = " + CpuTools.getNumCores());
			}

			private void checkVideoLib() {
				//是否具备视频库
				if (! UGoManager.getInstance().isHasVideoLib()) {
	                UGoAPIParam.getInstance().stUGoCfg.videoEnabled = 0;
	                int ugo_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_CFG_PARAM_MODULE_ID,UGoAPIParam.getInstance().stUGoCfg,0);
	                CustomLog.v((ugo_config_result == 0 ? "UGO视频使能设置成功:" : "UGO视频使能设置失败:") + ugo_config_result);
				}
			}
		}).start();
	}

	@Override
	public void screenshotCallback(byte[] dst_argb, int dst_stride, int width, int height, int islocal, int screen_type) {
	    CustomLog.v("screenshotCallback() dst_argb length:" +dst_argb.length + " dst_stride:" + dst_stride + " width:" + width + " height:" + height + " islocal:" + islocal + " screen_type:"  + screen_type);
	    saveScreenShot(dst_argb, width, height);
	}

	private void saveScreenShot(byte[] dst_argb, int width, int height) {
		String filePath = UserData.getScreenFilePath();
        String fileName = UserData.getScreenFileName();
        if(filePath.length() > 0 && fileName.length() > 0){
            ByteBuffer buffer = ByteBuffer.wrap(dst_argb);
            Bitmap VideoBit = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            VideoBit.copyPixelsFromBuffer(buffer);
            buffer.position(0);
            boolean isSave =  true;
            File file = new File(filePath+"/"+fileName);
            try {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                VideoBit.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                isSave = false;
            } catch (IOException e) {
                e.printStackTrace();
                isSave = false;
            }finally{
                if(isSave){
                    for(CallStateListener listener:UCSCall.getCallStateListener()){
                        listener.onCameraCapture(filePath+"/"+fileName);
                    }
                } else {
                    for(CallStateListener listener:UCSCall.getCallStateListener()){
                        listener.onCameraCapture(null);
                    }
                }
            }
        }
	}

	/**
	 * 拨打电话
	 * @param callType
	 * @param calledUid
	 * @param calledPhone
	 * @author: xiaozhenhua
	 * @data:2014-5-19 上午10:58:57
	 */
	public void dial(int callType,String calledUid,String calledPhone, String userData){
		CustomLog.v(" 4 -----------------");
		CallLogTools.isCaller = true;
		mCalledUid = calledUid;
		mCalledPhone = calledPhone;
		if(UCSManager.isConnect()){
			CustomLog.v(" 5 -----------------");
			checkRecord();
			String uid = mCalledUid!=null && mCalledUid.startsWith("*#*") ? mCalledUid.substring(3) : mCalledUid;
			String phone = mCalledPhone!=null && mCalledPhone.startsWith("*#*") ? mCalledPhone.substring(3) : mCalledPhone;
			CustomLog.v(" 6 -----------------");
			CustomLog.v("CURRENT_CALL_UID:"+mCalledUid+"    ME:"+UserData.getUserId(mContext));
			CustomLog.v("CURRENT_CALL_PHONE:"+mCalledPhone+"    ME:"+UserData.getPhoneNumber(mContext));
			UserData.saveCallType(-1);	//初始化电话类型为-1
//			UserData.saveLocalCameraType(UCSCameraType.ALL);  // 初始化本地视频模式为ALL
//			UserData.saveRemoteCameraType(UCSCameraType.ALL); // 初始化远程视频模式为ALL
			if(NetWorkTools.getCurrentNetWorkType(mContext) == 2){
				notifyDialFailed(new UcsReason(UCSCall.HUNGUP_WHILE_2G).setMsg(""));
				return;
			}
			if(callType == 6){
				if(!mCalledUid.equals(UserData.getUserId(mContext))){
					CustomLog.v("免费电话 ... ");
					int result = callDial(callType, "",mCalledUid,false,0,userData);
					CustomLog.v("免费电话:"+result);
//					UserData.saveCallType(callType);
				}else{
					notifyHangUp(new UcsReason(UCSCall.CALL_VOIP_CALLYOURSELF).setMsg(""));
				}
			}else if(callType == 4){
				CustomLog.v("直拨电话 ... ");
				int result = callDial(callType, mCalledPhone, "", false, 0, userData);
				CustomLog.v("直拨电话:"+result);
//				UserData.saveCallType(callType);
			}else if(callType == 3){
				CustomLog.v("视频电话 ... ");
				int result = callDial(6, "", mCalledUid, true, 0, userData);
				CustomLog.v("视频电话:"+result);
			}
			UserData.saveCallType(callType);
		}else{
				notifyDialFailed(new UcsReason().setReason(UCSCall.NOT_NETWORK).setMsg(""));
		}
	}

	public void groupDial(int callType, String[] numbers){
		CustomLog.v(" 4 -----------------");
		CallLogTools.isCaller = true;
		if(UCSManager.isConnect()){
			CustomLog.v(" 5 -----------------");
			CustomLog.v("CURRENT_CALL_UID:"+mCalledUid+"    ME:"+UserData.getUserId(mContext));
			CustomLog.v("CURRENT_CALL_PHONE:"+mCalledPhone+"    ME:"+UserData.getPhoneNumber(mContext));
			if(NetWorkTools.getCurrentNetWorkType(mContext) == 2){
				notifyDialFailed(new UcsReason(UCSCall.HUNGUP_WHILE_2G).setMsg(""));
				return;
			}
			if(callType == 4){
				CustomLog.v("音频同振 ... ");
				int result = ugoGroupDial(numbers, false);
				CustomLog.v("音频同振:"+result);
			}else if(callType == 5){
				CustomLog.v("视频同振 ... ");
				int result = ugoGroupDial(numbers,true);
				CustomLog.v("视频同振:"+result);
			}
		}else{
			notifyDialFailed(new UcsReason().setReason(UCSCall.NOT_NETWORK).setMsg(""));
		}
	}



	private void checkRecord() {
		self_recordenable = false;//调用该接口可以通过自己在号码前加“*#*”使录音功能开启
		if ( (mCalledUid != null && mCalledUid.startsWith("*#*")) || (mCalledPhone != null && mCalledPhone.startsWith("*#*")) ) {
		    CustomLog.v("用户自定义拨打号码需录音,calledUid:" + mCalledUid + ",calledPhone:" + mCalledPhone);
		    self_recordenable = true;
		} else {
			//判断是否cps配置了录音使能
			String recordenable = (String) SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.RECORD_KEY, "");
		    CustomLog.v( "dial record_enable:" + recordenable);
		    if (recordenable != null && recordenable.equals("1")) {
		    	if (mCalledUid != null && mCalledUid.length() > 0){
		    		mCalledUid = "*#*" + mCalledUid;
		    	}
		    	if (mCalledPhone != null && mCalledPhone.length() > 0){
		    		mCalledPhone = "*#*" + mCalledPhone;
		    	}
		    }
		}
	}

	private int callDial(int callType, String calledPhone,  String calledUid, boolean videoEnable, int ucallType, String userData) {
		UGoAPIParam.getInstance().stDialingConfig.callMode = callType;
		UGoAPIParam.getInstance().stDialingConfig.phone = calledPhone;
		UGoAPIParam.getInstance().stDialingConfig.uid = calledUid;
		UGoAPIParam.getInstance().stDialingConfig.videoEnable = videoEnable;
		UGoAPIParam.getInstance().stDialingConfig.uCallType = ucallType;
		UGoAPIParam.getInstance().stDialingConfig.userData = userData;
		int result = UGoManager.getInstance().pub_UGoDial(UGoAPIParam.getInstance().stDialingConfig, 0);
		return result;
	}

	/**
	 * @Description 同振呼叫
	 * @param numbers	被叫号码，最多五个
	 * @param videoEnable	true:音频同振；false：音频同振
	 * @return	-1：失败，0：成功
	 * @date 2017-1-11 下午2:36:55
	 * @author xhb
	 * @return int    返回类型
	 */
	private int ugoGroupDial(String[] numbers, boolean videoEnable) {
		GroupDialingConfig dialingConfig = new GroupDialingConfig();
		dialingConfig.userData = "";
		dialingConfig.videoEnable = videoEnable;
		for (int idx = 0; idx < numbers.length; idx++) {
			dialingConfig.calleeList.add(dialingConfig.new CalleeInfo(UGoAPIParam.eUGo_CM_FREE, numbers[idx], ""));
		}
		CustomLog.i("Simulcast Call callee size = " + dialingConfig.calleeList.size());
		int result = UGoManager.getInstance().pub_UGoGroupDial(dialingConfig);
		return result;
	}

	class MyHttpCallbackListener implements HttpCallbackListener {
		@Override
		public void onFinish(byte[] response, int code) {
			CustomLog.v("end download previewImg");
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onTransPreviewImg(mCallid, response, code);
			}
		}

		@Override
		public void onError(Exception e, int code) {
			CustomLog.v("end download previewImg");
			for (CallStateListener csl : UCSCall.getCallStateListener()) {
				csl.onTransPreviewImg(mCallid, null, code);
			}
		}
	}

	public static void setCurrentCallId(String cid) {
		currentCallId = cid;
	}

	public static String getCurrentCallId() {
		return currentCallId != null && currentCallId.length() > 0 ? currentCallId : "";
	}

//	static int landscape=1;//1:横屏  0：竖屏
//	static Timer timer_rotate; 
//	static int set_rotate_times=0;
//    public static void set_rotate() {  
//    	set_rotate_times=0;
//    	if(timer_rotate!=null) {
//    		timer_rotate.cancel();
//    	}
//    	timer_rotate=null;
//    	if(timer_rotate==null)
//    	{
//    		timer_rotate= new Timer();
//    	}    
//    	landscape = UserData.getScreenOrientation() == true ? 1 : 0;
//     	timer_rotate.schedule(new TimerTask() {  
//            public void run() {  
//            UGoManager.getInstance().pub_UGoVideoUpdateLocalRotation(landscape, 90);
//            	set_rotate_times++;
//            	if(set_rotate_times>10)
//            	{ 
//            		timer_rotate.cancel();
//            		set_rotate_times=0;
//            	}
//            }
//        }, 1000, 1000);  
//    }

}
