package com.yzx.controller;  

import java.util.ArrayList;

import com.gl.softphone.CodecConfig;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.preference.UserData;
import com.yzx.tools.CpsTools;
import com.yzx.tools.CpuTools;
import com.yzx.tools.FileTools;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

/**
 * @Title UGoSetConfig   
 * @Description  设置组件参数
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-28 下午3:40:00
 */
public class UGoSetConfig {
	private UGoSetConfig() {}
	
	public static void setConfig() {
		boolean avDebug = VoipCore.getContext().getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_AVDEBUG", true);
		ugoDebugEnable(avDebug);
        
		//设置默认值
		UGoAPIParam.getInstance().stMediaCfg.vieFecEnabled = false;
		UGoAPIParam.getInstance().stMediaCfg.vieNackEnabled = true;
		UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID, UGoAPIParam.getInstance().stMediaCfg, 0);
				
		ugoSetICEConfig();
	    
		boolean autoadapter = UserData.isAudioAutoAdapter(VoipCore.getContext());
		CpsTools.setDynamicPolicyEnable(autoadapter);
		
		ugoSetMediaConfig(autoadapter);

		ugoSetRtpConfig();
		
		ugoSetUgoConfig();
		
		ugoSetLogConfig(avDebug);
		
		ugoGetVersion();
	}

	private static void ugoGetVersion() {
		CustomLog.v("PHONE_VERSION:"+UGoManager.getInstance().pub_UGoGetVersion());
	}

	private static void ugoSetLogConfig(boolean avDebug) {
		if(avDebug){
			UGoAPIParam.getInstance().logTraceConfig.level = 0x40ff;
		}else{
			UGoAPIParam.getInstance().logTraceConfig.level = 0x0;
		}
		UGoAPIParam.getInstance().logTraceConfig.path = FileTools.getSdCardFilePath() + "/log"  + "/" + YzxTCPCore.getContext().getPackageName() + "/engine.txt";
		int log_config = UGoManager.getInstance().pub_UGoSetLogFile(UGoAPIParam.getInstance().logTraceConfig, 0);
		CustomLog.v((log_config == 0 ? "LOG配置成功:" : "LOG配置失败:") + log_config);
	}

	private static void ugoSetUgoConfig() {
		UGoAPIParam.getInstance().stUGoCfg.rc4Enabled = false;
		UGoAPIParam.getInstance().stUGoCfg.tlvEnabled = true;
		UGoAPIParam.getInstance().stUGoCfg.compressEnabled = true;  // 3.0协议为true
		//是否具备视频库
        if (! UGoManager.getInstance().isHasVideoLib()) {
            UGoAPIParam.getInstance().stUGoCfg.videoEnabled = 0;
            CustomLog.v("isHasVideoLib = false, video_enabled = 0");
        } else {
            UGoAPIParam.getInstance().stUGoCfg.videoEnabled = UserData.getVideoEnabled(VoipCore.getContext());
        }
        //获取网络类型传给组件
        int networkType = NetWorkTools.getCurrentNetWorkType(VoipCore.getContext());
        if (networkType == 4) {
        	networkType = 8;
        } else if (networkType == 8) {
        	networkType = 1;
        }
		UGoAPIParam.getInstance().stUGoCfg.atype = UserData.getLoginType(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.platform = 0x04;
		UGoAPIParam.getInstance().stUGoCfg.netType = networkType;
		UGoAPIParam.getInstance().stUGoCfg.brand = "yzx_"+UserData.getPackageName(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.phone = UserData.getPhoneNumber(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.uid = UserData.getClientNumber(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.userid = UserData.getUserId(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.nickName = UserData.getNickName(VoipCore.getContext());
		UGoAPIParam.getInstance().stUGoCfg.localAddr = NetWorkTools.getIPAddress(true);
		CustomLog.v("video_enabled:" + UGoAPIParam.getInstance().stUGoCfg.videoEnabled);
		CustomLog.v("CURRENT_LOGIN_PHONE:"+UGoAPIParam.getInstance().stUGoCfg.phone);
		CustomLog.v("CURRENT_LOGIN_CLIENTID:"+UGoAPIParam.getInstance().stUGoCfg.uid);
		CustomLog.v("CURRENT_LOGIN_userid:"+UGoAPIParam.getInstance().stUGoCfg.userid);
		int ugo_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_CFG_PARAM_MODULE_ID,UGoAPIParam.getInstance().stUGoCfg,0);
		CustomLog.v((ugo_config_result == 0 ? "UGO配置成功:" : "UGO配置失败:") + ugo_config_result);
	}

	private static void ugoSetRtpConfig() {
		UGoAPIParam.getInstance().stRTPCfg.rtpTimeout = UserData.getRtpAutoHangupSwith() == true ? 20 : 0;
		UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_RTP_CFG_MODULE_ID,UGoAPIParam.getInstance().stRTPCfg,0);
	}

	private static void ugoSetMediaConfig(boolean autoadapter) {
		int audiofec = UserData.getAudioFec(VoipCore.getContext());
	    int vqmenable = UserData.getVpmEnable(VoipCore.getContext());
	    int prtpenable = UserData.getPrtpEnable(VoipCore.getContext());
	    CustomLog.v("VPN:"+vqmenable+"   FEC:"+audiofec+"    RTP:"+prtpenable+"   ADAPTER:"+autoadapter);
		UGoAPIParam.getInstance().stMediaCfg.emodelEnabled = (vqmenable==0?false:true);
		UGoAPIParam.getInstance().stMediaCfg.fecEnabled = (audiofec==0?false:true);
		UGoAPIParam.getInstance().stMediaCfg.realTimeType = prtpenable;

        CustomLog.v("ExtAudioTransEnable:" + UserData.getExtAudioTransEnable());
        CustomLog.v("fecEnabled:" + UGoAPIParam.getInstance().stMediaCfg.fecEnabled);
		UGoAPIParam.getInstance().stMediaCfg.extAudioTransEnabled = UserData.getExtAudioTransEnable();	//控制是否开启外部音频传输
		int media_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_CTRL_CFG_MODULE_ID,UGoAPIParam.getInstance().stMediaCfg,0);
		CustomLog.v((media_config_result == 0 ? "MediaCfg配置成功:" : "MediaCfg配置失败:") + media_config_result);
	}

	private static void ugoSetICEConfig() {
		String stunAdd = UserData.getStunAddressList(VoipCore.getContext());
		CustomLog.v("STUN_ADD:"+stunAdd);
		UGoAPIParam.getInstance().stIceCfg.iceEnabled = stunAdd.length() > 0 ? UserData.isIceEnable(VoipCore.getContext()):false;
		UGoAPIParam.getInstance().stIceCfg.stunServer = stunAdd;
		CustomLog.v("ice:" + UserData.isIceEnable(VoipCore.getContext()));
		int ice_config_result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_CFG_ICE_MODULE_ID,UGoAPIParam.getInstance().stIceCfg,0);
		CustomLog.v((ice_config_result == 0 ? "ICE配置成功:" : "ICE配置失败:") + ice_config_result);
	}

	private static void ugoDebugEnable(boolean avDebug) {
		if(avDebug){
			UGoManager.getInstance().pub_UGoDebugEnabled(true, FileTools.getSdCardFilePath()+ "/log"  +"/" + YzxTCPCore.getContext().getPackageName()+ "/UGo.txt");
		}else{
			UGoManager.getInstance().pub_UGoDebugEnabled(false, "");
		}
	}
	
	/**
	 * 去除视频VP8编码
	 * @Description TODO(这里用一句话描述这个方法的作用) 
	 * @return void    返回类型 
	 * @date 2017年9月26日 下午5:08:17 
	 * @author zhj
	 */
	public static void disableVP8() {
		// disabled VP8
        ArrayList<CodecConfig> codecList = new ArrayList<CodecConfig>();
        CodecConfig codecConfig = new CodecConfig();
        codecConfig.enabled = false;
        codecConfig.plname = "VP8";
        codecConfig.pltype = 122;
        codecList.add(codecConfig);
        int ret = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_CODEC_CFG_MODULE_ID, codecList, 0);
        CustomLog.v("pub_UGoSetConfig UGoAPIParam.ME_VIDEO_CODEC_CFG_MODULE_ID result:"+ret);
	}
	
	/**
	 * @Description 设置默认码率 
	 * @return void    返回类型 
	 * @date 2017年9月26日 下午4:59:11 
	 * @author zhj
	 */
	public static void setDefaultBitrate() {
		UGoManager.getInstance().pub_UGoGetConfig(UGoAPIParam.ME_VIDEO_ENC_CFG_MODULE_ID,UGoAPIParam.getInstance().videoEncodeConfig, 0);
		
		if (UserData.get720pEnable()) {
	    	UGoAPIParam.getInstance().videoEncodeConfig.usMinBitrate = 400;
	    	UGoAPIParam.getInstance().videoEncodeConfig.usMaxBitrate = 1000;
	    	UGoAPIParam.getInstance().videoEncodeConfig.usStartBitrate = 450;
	    }
	    else {
	    	UGoAPIParam.getInstance().videoEncodeConfig.usMinBitrate = 120;
	    	UGoAPIParam.getInstance().videoEncodeConfig.usMaxBitrate = 800;
	    	UGoAPIParam.getInstance().videoEncodeConfig.usStartBitrate = 250;
	    }
		
		CustomLog.v("usStartBitrate:" + UGoAPIParam.getInstance().videoEncodeConfig.usStartBitrate
        		+ " usMinBitrate:" + UGoAPIParam.getInstance().videoEncodeConfig.usMinBitrate
        		+ " usMaxBitrate:" + UGoAPIParam.getInstance().videoEncodeConfig.usMaxBitrate);
        int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_ENC_CFG_MODULE_ID,UGoAPIParam.getInstance().videoEncodeConfig, 0);
        CustomLog.v("配置videoEncodeConfig:"+result);
	}
	
	/**
	 * @Description 设置默认分级编码参数
	 * @return void    返回类型 
	 * @date 2017年9月26日 下午4:53:15 
	 * @author zhj
	 */
	public static void setDefaultVideoPreset() {
		UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w240 = 12;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w360 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w480 = -1;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.framerate_w720 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w240 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w360 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w480 = 13;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.framerate_w720 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w240 = 14;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w360 = 15;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w480 = 15;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.framerate_w720 = 14;
	    
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w240=2;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w360=1;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w480=1;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.complexity_w720=0;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w240=3;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w360=2;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w480=1;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.complexity_w720=0;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w240=3;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w360=2;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w480=2;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.complexity_w720=1;
	    
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w240=200;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w360=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w480=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.low.bitrate_w720=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w240=200;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w360=400;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w480=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.medium.bitrate_w720=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w240=200;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w360=400;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w480=-1;
	    UGoAPIParam.getInstance().videoPresetAdapter.high.bitrate_w720=-1;
	    
	    int result = UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.ME_VIDEO_PRESET_ADAPTER_CFG_MODULE_ID, UGoAPIParam.getInstance().videoPresetAdapter, 0);
        CustomLog.v("配置videoPresetAdapter:"+result);
	}

}
  
