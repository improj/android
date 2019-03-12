package com.yzx.preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceUtil;

import android.content.Context;
import android.text.TextUtils;

import com.yzx.controller.VoipCore;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.tools.DefinitionAction;
import com.yzx.tools.RC4Tools;
import com.yzxtcp.tools.CustomLog;

public class UserData {
	
	
	/**
	 * 转呼号码
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-25 上午11:53:51
	 */
	public static String getForwardNumber(Context mContext){
		return mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_FORWARD_NUMBER", ""):"";
	}
	public static void saveForwardNumber(Context mContext, String forwardNumber){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_FORWARD_NUMBER", forwardNumber).commit();
		}
	}
	
	/**
	 * 是否呼叫转移
	 * @param isForwarding
	 * @author: xiaozhenhua
	 * @data:2014-11-25 下午5:19:01
	 */
	public static void saveForwarding(Context mContext ,boolean isForwarding){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_FORWARDING", isForwarding).commit();
		}
	}
	public static boolean isForwarding(Context mContext){
		return mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_FORWARDING", false) : false;
	
	}
	
	public static String getUserId(Context mContext) {
		String clientId = mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_USER_ID", "") : "";
		if (clientId.length() > 0) {
			clientId = RC4Tools.decry_RC4(clientId);
		}
		return clientId;
	}

	public static void saveUserId(Context mContext, String userid) {
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_USER_ID", RC4Tools.encry_RC4_string(userid)).commit();
		}
	}
	
	public static String getClientNumber(Context mContext) {
		String clientId = mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_CLIENT_NUMBER", "") : "";
		if (clientId.length() > 0) {
			clientId = RC4Tools.decry_RC4(clientId);
		}
		return clientId;
	}

	public static void saveClientNumber(Context mContext, String userid) {
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_CLIENT_NUMBER", RC4Tools.encry_RC4_string(userid)).commit();
		}
	}
	
	public static String getPhoneNumber(Context mContext) {
		String phone = mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME,0).getString("YZX_CLIENT_PHONE", "") : "";
		if(phone.length() > 0){
			phone = RC4Tools.decry_RC4(phone);
		}
		return phone;
	}

	public static void savePhoneNumber(Context mContext, String phone) {
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0)
			.edit().putString("YZX_CLIENT_PHONE", RC4Tools.encry_RC4_string(phone)).commit();
		}
	}
	
	public static String getClientPwd(Context mContext){
		String clientpwd = mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_CLIENT_PWD", "") : "";
		if(clientpwd != null && clientpwd.length() > 0){
			clientpwd = RC4Tools.decry_RC4(clientpwd);
		}
		return clientpwd;
	}
	
	public static void saveClientPwd(Context mContext, String clientPwd){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0)
			 .edit().putString("YZX_CLIENT_PWD", RC4Tools.encry_RC4_string(clientPwd)).commit();
		}
	}
	
	/**
	 * 昵称 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-25 上午11:50:44
	 */
	public static String getNickName(Context mContext){
		return mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_NICK_NAME", ""):"";
	}
	public static void saveNickName(Context mContext, String nickName){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_NICK_NAME", nickName).commit();
		}
	}
	
	public static String getVersionName(Context mContext){
		return mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_VERSION_NAME", ""):"";
	}
	public static void saveVersionName(Context mContext,String versionName){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_VERSION_NAME", versionName).commit();
		}
	}
	
	public static String getPackageName(Context mContext){
		return mContext != null ? (mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_PACKAGE_NAME", "")):"";
	}
	public static void savePackageName(Context mContext,String packageName){
		mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_PACKAGE_NAME", packageName).commit();
	}
	
	public static String getImSsid(Context mContext){
		return mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_AC", "");
	}
	public static void saveImSsid(Context mContext,String imssId){
		mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_AC", imssId).commit();
	}

	
	public static String getAppid(Context mContext){
		return mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_APPID", "");
	}
	public static void saveAppid(Context mContext,String appId){
		mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_APPID", appId).commit();
	}
	/**
	 * 获取登录类型     0：明文 (有主账号与子账号信息)    1：密文(token)
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-16 下午3:40:21
	 */
	public static int getLoginType(Context mContext){
		return mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getInt("YZX_LOGIN_TYPE", 0) : 0;
	}
	
	public static void saveLoginType(Context mContext, int type){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putInt("YZX_LOGIN_TYPE", type).commit();
		}
	}
	
	/**
	 * 保存RTPP List
	 * @param rtpp
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:29:20
	 */
	public static void saveRtppAddressList(Context mContext, String rtpp){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_RTPP_ADDRESS_LIST", RC4Tools.encry_RC4_string(rtpp)).commit();
		}
	}
	
	public static String getRtppAddressList(Context mContext){
		String rtpp = mContext != null ? mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_RTPP_ADDRESS_LIST", ""):"";
		if(rtpp != null && rtpp.length() > 0){
//			if(isEncryption(rtpp)){
				rtpp = RC4Tools.decry_RC4(rtpp);
//			}
		}
		return rtpp;
	}
	
	//获取设置界面指定的rtpp
	public static String getRtppAddress(Context mContext) {
		 return mContext != null ? mContext.getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_RTPPADDRESS", ""):"";
	}
	/**
	 * 
	 * @param rtpp
	 * @author: xiaozhenhua
	 * @data:2014-10-23 下午12:24:39
	 */
	public static void saveStunAddressList(Context mContext, String stun){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_STUN_ADDRESS_LIST", RC4Tools.encry_RC4_string(stun)).commit();
		}
	}
	
	public static String getStunAddressList(Context mContext){
		String stun = mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_STUN_ADDRESS_LIST", "");
		CustomLog.v("stun:" + stun);
		String stunAdd = "";
		try {
			if(!TextUtils.isEmpty(stun)){
				stun = RC4Tools.decry_RC4(stun);
			}
			if(TextUtils.isEmpty(stun)) 
				return "";
			JSONArray array = new JSONArray(stun);
			stunAdd = array.get(0).toString(); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return stunAdd;
	}
	
	/**
	 * 是否接听电话
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-31 下午4:05:09
	 */
	public static boolean isAnswer(Context mContext){
		return mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_ANSWER", false);
	}
	public static void saveAnswer(Context mContext,boolean refusal){
		mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_ANSWER", refusal).commit();
	}
	
/*	public static boolean isMySelfRefusal(Context mContext){
		if(mContext != null){
			return mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_MYSELF_REFUSAL", false);
		}else{
			return false;
		}
	}
	public static void saveMySelfRefusal(Context mContext, boolean refusal){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_MYSELF_REFUSAL", refusal).commit();
		}
	}*/

	//-----------------------策略控制-------------------------
	public static boolean isIceEnable(Context mContext){
		return parseBooleanJson(mContext,BooleanType.ICE);
	}
	public static boolean isAudioAutoAdapter(Context mContext){
		return parseBooleanJson(mContext,BooleanType.ADAPTER);
	}
	public static int getAudioFec(Context mContext){
		return parseIntJson(mContext, IntType.FEC);
	}
	public static int getVpmEnable(Context mContext){
		return parseIntJson(mContext, IntType.VPM);
	}
	public static int getPrtpEnable(Context mContext){
		return parseIntJson(mContext, IntType.PRTP);
	}
	enum BooleanType{
		ICE,ADAPTER
	}
	enum IntType{
		FEC,VPM,PRTP
	}
	
	private static boolean parseBooleanJson(Context mContext,BooleanType type){
		boolean booleanType = false;
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		//CustomLog.v("permission:"+permission);
		if(permission.length() > 0){
			try {
				JSONObject jsObj = new JSONObject(permission);
				if(type == BooleanType.ICE){
					booleanType = jsObj.getInt("iceenable") != 0 ? true : false;
				}else if(type == BooleanType.ADAPTER){
					booleanType = jsObj.getInt("autoadapter") != 0 ? true : false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return booleanType;
	}
	
	private static int parseIntJson(Context mContext,IntType type){
		int intType = 0;
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		//CustomLog.v("permission:"+permission);
		if(permission.length() > 0){
			try {
				JSONObject jsObj = new JSONObject(permission);
				if(type == IntType.FEC){
					intType = jsObj.getInt("audiofec");
				}else if(type == IntType.VPM){
					intType = jsObj.getInt("vqmenable");
				}else if(type == IntType.PRTP){
					intType = jsObj.getInt("prtpenable");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return intType;
	}
	
	public static void saveVideoEnabled(Context mContext,int isPreView){
		if(mContext != null){
			mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putInt("YZX_VIDEO_ENABLED", isPreView).commit();
		}
	}
	public static int getVideoEnabled(Context mContext){
		if(mContext != null){
			return mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, Context.MODE_PRIVATE).getInt("YZX_VIDEO_ENABLED", 1);
		}else{
			return 1;
		}
	}
	
	/**
	 * 保存视频截图路径
	 * @param filePath
	 * @author: xiaozhenhua
	 * @data:2015-5-21 下午3:10:28
	 */
	public static void saveScreenFilePath(String filePath){
		if(VoipCore.getContext() != null){
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_SCREEN_FILE_PATH", filePath).commit();
		}
	}
	public static String getScreenFilePath(){
		if(VoipCore.getContext() != null){
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_SCREEN_FILE_PATH", "");
		}else{
			return "";
		}
	}
	
	/**
	 * 保存视频截图名称
	 * @param filePath
	 * @author: xiaozhenhua
	 * @data:2015-5-21 下午3:10:28
	 */
	public static void saveScreenFileName(String filePath){
		if(VoipCore.getContext() != null){
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("YZX_SCREEN_FILE_NAME", filePath).commit();
		}
	}
	public static String getScreenFileName(){
		if(VoipCore.getContext() != null){
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("YZX_SCREEN_FILE_NAME", "");
		}else{
			return "";
		}
	}
	
	/**
	 * 保存拨打状态
	 * @param type 	拨打模式
	 * @author xhb
	 * @data 2015-11-2 下午3:51
	 */
	public static void saveCallType(int type) {
		if(VoipCore.getContext() != null) {
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putInt("YZX_CALL_TYPE",type).commit();
		}
	}
	
	/**
	 * 获取当前拨打状态
	 * @author xhb
	 * @data 2015-11-2 下午3:51
	 */
	public static int getCallType() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getInt("YZX_CALL_TYPE", -1);
		} else {
			return -1;
		}
	}
	
/*	*//**
	 * 保存本地视频模式状态
	 * @param cameraType 本地视频模式
	 * @author xhb
	 * @data 2015-11-2 下午4:46
	 *//*
	public static void saveLocalCameraType(UCSCameraType cameraType) {
		if(VoipCore.getmContext() != null) {
			VoipCore.getmContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putInt("YZX_LOCAL_CAMERA_TYPE",cameraType.ordinal()).commit();
		}
	}
	
	*//**
	 * 获取本地视频模式状态
	 * @return 本地视频模式状态
	 * @author xhb
	 * @data 2015-11-2 下午4:46
	 *//*
	public static int getLocalCameraType() {
		if(VoipCore.getmContext() != null) {
			return VoipCore.getmContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getInt("YZX_LOCAL_CAMERA_TYPE", -1);
		} else {
			return -1;
		}
	}
	
	*//**
	 * 保存远地视频模式状态
	 * @param cameraType 远地视频模式
	 * @author xhb
	 * @data 2015-11-2 下午4:46
	 *//*
	public static void saveRemoteCameraType(UCSCameraType cameraType) {
		if(VoipCore.getmContext() != null) {
			VoipCore.getmContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putInt("YZX_REMOTE_CAMERA_TYPE",cameraType.ordinal()).commit();
		}
	}
	
	*//**
	 * 获取远程视频模式状态
	 * @return 远程视频模式
	 * @author xhb
	 * @data 2015-11-2 下午4:46
	 *//*
	public static int getRemoteCameraType() {
		if(VoipCore.getmContext() != null) {
			return VoipCore.getmContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getInt("YZX_REMOTE_CAMERA_TYPE", -1);
		} else {
			return -1;
		}
	}*/
	
	public static void saveExtAudioTransEnable(Context context, boolean isEnable) {
		if(context != null) {
			context.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_EXT_AUDIO_TRANS_ENABLE", isEnable).commit();
		}
	}
	
	public static boolean getExtAudioTransEnable() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_EXT_AUDIO_TRANS_ENABLE", false);
		}
		return false;
	}
	
	public static void save720pEnable(boolean isEnable) {
		if(VoipCore.getContext() != null) {
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_720P_ENABLE", isEnable).commit();
		}
	}
	
	public static boolean get720pEnable() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_720P_ENABLE", false);
		}
		return false;
	}
	
	/**
	 * @Description 是否使用外部摄像头
	 * @return	true：使用外部摄像头；false：不使用外部摄像头
	 * @date 2016-2-29 下午6:10:58 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	public static boolean getVideoExternCapture() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_VIDEO_EXTERN_CAPTURE", false);
		} else {
			return false;
		}
	}
	
	/**
	 * @Description 保存是否使用外部摄像头的状态  
	 * @param useExternCapture	TODO(参数描述)	
	 * @date 2016-2-29 下午6:13:28 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void saveVideoExternCapture(boolean useExternCapture) {
		if(VoipCore.getContext() != null) {
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_VIDEO_EXTERN_CAPTURE",useExternCapture).commit();
		}
	}
	
	/**
	 * @Description 在RTP超时时是否挂断电话
	 * @param isopen	true：是；false：不是
	 * @date 2016-7-31 上午8:34:55 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void setRtpAtuoHangup(Context context,boolean isopen) {
		if(context != null) {
			context.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("YZX_RTP_AUTO_HANGUP",isopen).commit();
		}
	}
	
	/**
	 * @Description 获取RTP超时时是否会自动挂断开关
	 * @return	true 自动挂断；false 不自动挂断	
	 * @date 2016-7-31 上午8:40:35 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	public static boolean getRtpAutoHangupSwith() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("YZX_RTP_AUTO_HANGUP", true);
		} else {
			return true;
		}
	}
	
	
	/**
	 * 设置屏幕显示的方向
	 * @param orientation true：横屏；false：竖屏，默认是横屏
	 * @date 2016-11-10
	 */
	public static void setScreenOrientation(Context context, boolean orientation) {
		if(context != null) {
			context.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putBoolean("SCREEN_ORIENTATION", orientation).commit();
		}
	}
	
	/**
	 * 获取屏幕显示的方向
	 * @return true：横屏；false：竖屏
	 */
	public static boolean getScreenOrientation() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getBoolean("SCREEN_ORIENTATION", true);
		} else {
			return true;
		}
	}

	/**
	 * @Description 保存服务器返回的视频预览图片下载地址
	 * @param previewImgUrl	视频预览图片下载地址
	 * @date 2017-2-20 下午3:58:02 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void setPreviewImgUrl(String previewImgUrl) {
		if(VoipCore.getContext() != null) {
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("PREVIEW_IMG_URL", previewImgUrl).commit();
		}
	}
	
	/**
	 * @Description 获取视频预览图片下载地址 
	 * @return	视频预览图片下载地址
	 * @date 2017-2-20 下午3:59:46 
	 * @author xhb  
	 * @return String    返回类型
	 */
	public static String getPreviewImgUrl() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("PREVIEW_IMG_URL", "");
		}
		return "";
	}
	
	/**
	 * @Description 保存被叫号码
	 * @param calledNumber	被叫号码	
	 * @date 2017-2-23 上午10:54:27 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void setCalledNumber(String calledNumber) {
		if(VoipCore.getContext() != null) {
			VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).edit().putString("CALLED_NUMBER", calledNumber).commit();
		}
	}
	
	public static String getCalledNumber() {
		if(VoipCore.getContext() != null) {
			return VoipCore.getContext().getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0).getString("CALLED_NUMBER", "");
		}
		return "";
	}
}
