package com.yzx.api;

import java.util.ArrayList;

import android.content.Context;

import com.yzx.controller.VoipCore;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.VoipListenerManager;
import com.yzx.preference.UserData;
import com.yzx.tools.DefinitionAction;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;

public class UCSService {
	
	public static void addConnectionListener(ConnectionListener cl){
		VoipListenerManager.getInstance().addConnectionListener(cl);
	}
	
	public static ArrayList<ConnectionListener> getConnectionListener(){
		return VoipListenerManager.getInstance().getConnectionListener();
	}
	
	public static void removeConnectionListener(ConnectionListener cl){
		VoipListenerManager.getInstance().removeConnectionListener(cl);
	}
	
	/**
	 * @Description 初始化voip配置
	 * @param mContext	程序上下文
	 * @param isSwitch	是否开启logcat日志打印	
	 * @date 2016-9-30 下午5:15:20 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void init(Context mContext, boolean isSwitch){
		CustomLog.v("UCSService init() mContext:" + mContext);
		UCSManager.init(mContext);
		VoipCore.getInstance(mContext);
		com.yzxtcp.data.UserData.saveLogSwitch(mContext, isSwitch);
		CustomLog.v("voip sdk version:" + DefinitionAction.SDK_VERSION);
		licenseEnable(mContext);
	}
	
	/**
	 * @Description 设置license版本
	 * @param mContext	程序上下文	
	 * @date 2016-9-30 下午5:24:57 
	 * @author xhb  
	 * @return void    返回类型
	 */
	private static void licenseEnable(Context mContext) {
		//针对收费版本添加标志位，主叫在TCP SDK中使用
		if (!DefinitionAction.isLicenseVersion()) {
		    mContext.getSharedPreferences("YZX_VOIP_DEFAULT", 0).edit().putBoolean("TRANS_DATA_ENABLE", false).commit();
		} else {
			mContext.getSharedPreferences("YZX_VOIP_DEFAULT", 0).edit().putBoolean("TRANS_DATA_ENABLE", true).commit();
		}
	}
	
	/**
	 * @Description 断开连接和销毁组件
	 * @date 2016-9-30 下午5:24:16 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void uninit(){
		CustomLog.v("UCSService uninit()");
		UCSManager.disconnect();
		//销毁组件
		VoipCore.getInstance(null).uninit();
		
	}

	/**
	 * @Description 通过token连接服务器
	 * @param token	连接时需要的token参数
	 * @date 2016-9-30 下午5:17:16 
	 * @author xhb  
	 * @return void    返回类型
	 */
	@Deprecated
	public static void connect(String token){
		if(UCSManager.isConnect()){
			UCSManager.disconnect();
		}
		UCSManager.connect(token ,new ILoginListener() {
			
			@Override
			public void onLogin(UcsReason arg0) {}
		});
	}
	
	/**
	 * @Description 设置logcat日志开关
	 * @param mContext	程序上下文
	 * @param isOpenSdkLog	true：开；false：关	
	 * @date 2016-9-30 下午5:23:30 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public static void openSdkLog(Context mContext, boolean isOpenSdkLog){
		com.yzxtcp.data.UserData.saveLogSwitch(mContext, isOpenSdkLog);
	}
	
	/**
	 * @Description 	通过userID连接去服务器
	 * @param sid		主账号id
	 * @param sidPwd	主账号密码
	 * @param userId	子账号id
	 * @param userIdPwd	子账号密码
	 * @date 2016-9-30 下午5:20:36 
	 * @author xhb  
	 * @return void    返回类型
	 */
	@Deprecated
	public static void connect(String sid,String sidPwd,String userId,String userIdPwd){
		if(UCSManager.isConnect()){
			UCSManager.disconnect();
		}
		UCSManager.connect(sid, sidPwd, userId, userIdPwd, new ILoginListener() {
			
			@Override
			public void onLogin(UcsReason arg0) {
				
			}
		});
		UserData.saveClientPwd(VoipCore.getContext(), userIdPwd);
	}
	
	/**
	 * @Description TCP是否连接
	 * @return	true：连接；false：断开	
	 * @date 2016-9-30 下午5:22:07 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	@Deprecated
	public static boolean isConnected() {
		return UCSManager.isConnect();
	}
	
	/**
	 * @Description 获取VOIP SDK的版本号
	 * @return	VOIP SDK版本号
	 * @date 2016-9-30 下午5:22:48 
	 * @author xhb  
	 * @return String    返回类型
	 */
	public static String getSDKVersion(){
		return DefinitionAction.SDK_VERSION.substring(0, DefinitionAction.SDK_VERSION.lastIndexOf("."));
	}
	
}
