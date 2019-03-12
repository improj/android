package com.yzxtcp.data;


import com.yzxtcp.core.YzxTCPCore;

import android.content.Context;

public class UserData {

	private static final String PREFERENCE_NAME = "yzxTCP";
	
/*	*//**
	 * 保存图片发送状态
	 * 
	 * @return
	 *//*
	public static int getImgSendState() {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 0)
				.getInt("YZX_IMGSENDSTATE", 0) : 0;
	}

	public static void saveImgSendState(int state) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putInt("YZX_IMGSENDSTATE", state).commit();
		}
	}*/
	
	public static void saveUserId(String userId) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_USERID_INDEX", userId).commit();
		}
	}
	
	public static String getUserId() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_USERID_INDEX", "") : "";
	}

//	private static boolean logSwitch = true;

	public static void saveLogSwitch(Context mContext, boolean isSwitch) {
//		logSwitch = isSwitch;
		if (mContext != null) {
			mContext.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		} else if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		}
	}

	public static boolean isLogSwitch() {
		return YzxTCPCore.getContext() != null ? (YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getBoolean("YZX_CLIENT_LOG_SWITCH", true)) : true;
	}
/*	*//**
	 * 当前联接TCP的索引
	 *//*
	public static int getCurrentTcpIndex() {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 0)
				.getInt("YZX_TCP_INDEX", 0) : 0;
	}

	*//**
	 * 保存当前连接TCP的索引
	 * 
	 * @param index
	 *//*
	public static void saveCurrentTcpIndex(int index) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putInt("YZX_TCP_INDEX", index).commit();
		}
	}*/

	/**
	 * 保存iVal值
	 * @return
	 */
	// TODO 这个数据只在IM SDK里面使用，并没有在TCP SDK里面使用
	public static int getiVal(String userName) {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME+userName, 0)
				.getInt("YZX_VAL_INDEX", 0) : 0;
	}

	public static void saveiVal(int iVal, String userName) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME+userName, 0).edit()
					.putInt("YZX_VAL_INDEX", iVal).commit();
		}
	}

	/**
	 * 保存联系人iVal值
	 * 
	 * @return
	 */
	// TODO 这个数据只在IM SDK里面使用，并没有在TCP SDK里面使用
	public static int getGroupiVal(String userName) {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME+userName, 0)
				.getInt("YZX_GROUPVAL_INDEX", 0) : 0;
	}

	public static void saveGroupiVal(int iVal, String userName) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME+userName, 0).edit()
					.putInt("YZX_GROUPVAL_INDEX", iVal).commit();
		}
	}
	
	/**
	 * 保存iUin值
	 * 
	 * @return
	 */
	public static int getiUin() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getInt("YZX_UIN_INDEX", 0) : 0;
	}

	public static void saveiUin(int iUin) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putInt("YZX_UIN_INDEX", iUin).commit();
		}
	}

	/**
	 * 保存登陆token
	 * 
	 * @return
	 */
	public static String getLoginToken() {
		 return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_LOGINTOKEN_INDEX", "") : "";
	}

	public static void saveLoginToken(String loginToken) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_LOGINTOKEN_INDEX", loginToken).commit();
		}
	}
	public static void clearLoginToken() {
		saveLoginToken("");
	}
	
	public static String getCSAddress(){
			return YzxTCPCore.getContext() != null ? 
					YzxTCPCore.getContext().getSharedPreferences(
					"YZX_DEMO_DEFAULT", 0).getString("YZX_CSADDRESS", "")
					: "";
	}
	
	public static String getCpsAddress(){
		return YzxTCPCore.getContext() != null ? 
				YzxTCPCore.getContext().getSharedPreferences(
				"YZX_DEMO_DEFAULT", 0).getString("YZX_CPSADDRESS", "")
				: "";
	}
	
	
/*	public static void saveLoginEnvironment(boolean environment){
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0).edit().putBoolean("YZX_ENVIRMONENT", environment).commit();
		}
	}
	
	public static boolean isLoginEnvironment(){
		if (YzxTCPCore.getContext() != null) {
			return YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0).getBoolean("YZX_ENVIRMONENT", false);
		}else{
			return false;
		}
	}*/
	//产生Crash文件标识
	public static void saveCrash(String crashFile) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_CRASHFILE_INDEX", crashFile).commit();
		}
	}
	public static void clearCrash() {
		saveLoginToken("");
	}

	public static String getCrashFile() {
		 return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_CRASHFILE_INDEX", "") : "";
	}
	
	public static String getAppid() {
		 return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_APPID_INDEX", "") : "";
	}

	public static void saveAppid(String loginToken) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_APPID_INDEX", loginToken).commit();
		}
	}
	
	public static String getClientId() {
		 return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_CLIENTID_INDEX", "") : "";
	}

	public static void saveClientId(String loginToken) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_CLIENTID_INDEX", loginToken).commit();
		}
	}
	
	/**
	 * 保存获取从CPS获取到的IP
	 */
	public static String getPorxyIP() {
		 return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 0)
				.getString("YZX_PROXYIP_INDEX", "") : "";
	}

	public static void saveProxyIP(String loginToken) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 0).edit()
					.putString("YZX_PROXYIP_INDEX", loginToken).commit();
		}
	}
}
