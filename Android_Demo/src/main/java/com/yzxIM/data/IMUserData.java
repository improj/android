package com.yzxIM.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.tools.RC4Tools;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.FileTools;

public class IMUserData {

	private static String PREFERENCE_NAME = "yzxIM";
	public static boolean loginFlag = false;
	
	/**
	 * 保存图片发送状态
	 * 
	 * @return
	 */
	/*public static int getImgSendState() {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 1)
				.getInt("YZX_IMGSENDSTATE", 0) : 0;
	}

	public static void saveImgSendState(int state) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putInt("YZX_IMGSENDSTATE", state).commit();
		}
	}
	*/
	public static String getVersionName(Context mContext) {
		return mContext != null ? (mContext.getSharedPreferences(
				PREFERENCE_NAME, 1).getString("YZX_VERSION_NAME", ""))
				: (YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
						.getString("YZX_VERSION_NAME", "") : "");
	}

	public static void saveVersionName(Context mContext, String versionName) {
		mContext.getSharedPreferences(PREFERENCE_NAME, 1).edit()
				.putString("YZX_VERSION_NAME", versionName).commit();
	}

	public static String getPackageName(Context mContext) {
		return mContext != null ? (mContext.getSharedPreferences(
				PREFERENCE_NAME, 1).getString("YZX_PACKAGE_NAME", ""))
				: (YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
						.getString("YZX_PACKAGE_NAME", "") : "");
	}

	public static void savePackageName(Context mContext, String packageName) {
		mContext.getSharedPreferences(PREFERENCE_NAME, 1).edit()
				.putString("YZX_PACKAGE_NAME", packageName).commit();
	}

	/*private static boolean logSwitch = true;

	public static void saveLogSwitch(Context mContext, boolean isSwitch) {
		logSwitch = isSwitch;
		if (mContext != null) {
			mContext.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		} else if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putBoolean("YZX_CLIENT_LOG_SWITCH", isSwitch).commit();
		}
	}

	public static boolean isLogSwitch() {
		return YzxTCPCore.getContext() != null ? (YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 1)
				.getBoolean("YZX_CLIENT_LOG_SWITCH", logSwitch)) : logSwitch;
	}*/

	/**
	 * 当前联接TCP的索引
	 */
	/*public static int getCurrentTcpIndex() {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 1)
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
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putInt("YZX_TCP_INDEX", index).commit();
		}
	}
*/
	private static ArrayList<String> csServiceAddress = new ArrayList<String>(); // CS服务器列表z

	public static void saveImServicesAddress(String ipaddr) {
		if (csServiceAddress.size() > 4) {
			csServiceAddress.remove(0);
		}
		csServiceAddress.add(ipaddr);
		String csAddrs = "";
		for (int i = 0; i < csServiceAddress.size(); i++) {
			csAddrs = csAddrs + "=" + csServiceAddress.get(i);
		}
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1)
					.edit()
					.putString("CS_ADDRESS_LIST",
							RC4Tools.encry_RC4_string(csAddrs)).commit();
		}
	}

	public static ArrayList<String> getImServiceAddress() {
		String csAddrs = YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("CS_ADDRESS_LIST", "")
				: "";
		if (csAddrs != null && csAddrs.length() > 0) {
			if (isEncryption(csAddrs)) {
				csAddrs = RC4Tools.decry_RC4(csAddrs);
			}
		}
		if (csServiceAddress.size() > 0) {
			csServiceAddress.clear();
		}
		String[] ipaddrs = csAddrs.split("=");
		for (int i = 0; i < ipaddrs.length; i++) {
			csServiceAddress.add(ipaddrs[i]);
		}
		return csServiceAddress;
	}

	/**
	 * 判断是否使用了RC4加密
	 * 
	 * @param input
	 * @return
	 */
	private static boolean isEncryption(String input) {
		boolean isEquals = true;
		char[] str = "ghijklmnopqrstuvwxyz!@#$%^&*()~,.<>?/:;{}[]|+=-_*"
				.toCharArray();
		for (char value : str) {
			isEquals = input.toLowerCase().contains(value + "");
			if (isEquals) {
				break;
			}
		}
		return !isEquals;
	}

	/**
	 * 保存iVal值
	 * 
	 * @return
	 */
	/*public static int getiVal(String userName) {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME+userName, 1)
				.getInt("YZX_VAL_INDEX", 0) : 0;
	}

	public static void saveiVal(int iVal, String userName) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME+userName, 1).edit()
					.putInt("YZX_VAL_INDEX", iVal).commit();
		}
	}*/

	/**
	 * 保存联系人iVal值
	 * 
	 * @return
	 */
	/*public static int getGroupiVal(String userName) {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME+userName, 1)
				.getInt("YZX_GROUPVAL_INDEX", 0) : 0;
	}

	public static void saveGroupiVal(int iVal, String userName) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME+userName, 1).edit()
					.putInt("YZX_GROUPVAL_INDEX", iVal).commit();
		}
	}*/
	
	/**
	 * 保存iUin值
	 * 
	 * @return
	 */
	/*public static int getiUin() {
		return YzxTCPCore.getContext() != null ? YzxIMCoreService
				.getInstance().getSharedPreferences(PREFERENCE_NAME, 1)
				.getInt("YZX_UIN_INDEX", 0) : 0;
	}

	public static void saveiUin(int iUin) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putInt("YZX_UIN_INDEX", iUin).commit();
		}
	}*/

	/**
	 * 保存username值
	 * 
	 * @return
	 */
	public static String getUserName() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_USERNAME_INDEX", "") : "";
	}

	public static void saveUserName(String tUsername) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_USERNAME_INDEX", tUsername).commit();
		}
	}

	/**
	 * 保存nickname值
	 * 
	 * @return
	 */
	public static String getNickName() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_NICKNAME_INDEX", "") : "";
	}

	public static void saveNickName(String tNickname) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_NICKNAME_INDEX", tNickname).commit();
		}
	}

	/*
	 * 保存发送消息的MAP
	 */
	private static Map<String, ChatMessage> msgMap = new HashMap<String, ChatMessage>();

	public static void mapSaveMsg(String key, ChatMessage msg) {
		msgMap.put(key, msg);
	}

	public static ChatMessage mapGetMsg(String key) {
		return msgMap.get(key);
	}

	public static void mapDelMsg(String key) {
		msgMap.remove(key);
	}

	public static void mapResetMsg(){
		msgMap.clear();
	}
	
	public static boolean mapIsContainsKey(String key){
		return msgMap.containsKey(key);
	}
	
	public static boolean mapFindMsg(ChatMessage msg){
		Iterator iter = msgMap.entrySet().iterator();  
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    ChatMessage val = (ChatMessage) entry.getValue();  
		    if(val.getMsgid().equals(msg.getMsgid())){
		    	return true;
		    }
		}  
		
		return false;
	}
	
	public static ChatMessage mapGetMsgById(String msgid){
		Iterator iter = msgMap.entrySet().iterator();  
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    ChatMessage val = (ChatMessage) entry.getValue();  
		    if(val.getMsgid().equals(msgid)){
		    	return val;
		    }
		} 
		
		return null;
	}
	
	public static void mapTest() {
		Iterator ite = msgMap.keySet().iterator();
		
		while (ite.hasNext()) {

			String key = (String) ite.next(); // key
			CustomLog.v("-->" + key + "==>"
					+ msgMap.get(key).getContent());

		}
	}

	/**
	 * 获取数据库名
	 * 
	 * @return
	 */
	public static String getDbName() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_DBNAME_INDEX", "") : "";
	}

	public static void saveDbName(String dbName) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_DBNAME_INDEX", dbName).commit();
		}
	}
	
	/**
	 * 保存用户登陆信息
	 * 
	 * @return
	 */
	public static String getLoginInfo() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_LoginInfo_INDEX", "") : "";
	}

	public static void saveLoginInfo(String info) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_LoginInfo_INDEX", info).commit();
		}
	}
	
	/**
	 * 保存用户登陆时间精确到日
	 * 
	 * @return
	 */
	public static String getLoginTime() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_LoginTime_INDEX", "") : "";
	}

	public static void saveLoginTimer(String time) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_LoginTime_INDEX", time).commit();
		}
	}
	
	
	/**
	 * 保存APPID值
	 * 
	 * @return
	 */
	public static String getUserAPPID() {
		return YzxTCPCore.getContext() != null ? YzxTCPCore.getContext().getSharedPreferences(PREFERENCE_NAME, 1)
				.getString("YZX_APPID_INDEX", "") : "";
	}
	public static void saveUserAPPID(String tUsername) {
		if (YzxTCPCore.getContext() != null) {
			YzxTCPCore.getContext()
					.getSharedPreferences(PREFERENCE_NAME, 1).edit()
					.putString("YZX_APPID_INDEX", tUsername).commit();
		}
	}
	/**
	 * 清除IMData数据
	 * 
	 */
	public static void clear(Context context){
		FileTools.deleteFile(new File("/data/data/"+context.getPackageName()+"/shared_prefs/"+PREFERENCE_NAME+".xml"));
	}
}
