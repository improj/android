package com.yzx.http.net;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gl.softphone.CodecConfig;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.preference.UserData;
import com.yzx.tools.CpuTools;
import com.yzx.tools.DefinitionAction;
import com.yzx.tools.NotifyAudioDeviceUpdate;
import com.yzxtcp.tools.CustomLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceUtil;

public class SharedPreferencesUtils {

	private static final String FILE_NAME = DefinitionAction.PREFERENCE_NAME;

	public static String String = "String";

	public static String Integer = "Integer";

	public static String Boolean = "Boolean";

	public static String Float = "Float";

	public static String Long = "Long";

	public static void setParam(Context context, String key, Object object) {
		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if (String.equals(type)) {
			editor.putString(key, (String) object);
		} else if (Integer.equals(type)) {
			editor.putInt(key, (Integer) object);
		} else if (Boolean.equals(type)) {
			editor.putBoolean(key, (Boolean) object);
		} else if (Float.equals(type)) {
			editor.putFloat(key, (Float) object);
		} else if (Long.equals(type)) {
			editor.putLong(key, (Long) object);
		}
		editor.commit();
		NotifyAudioDeviceUpdate.notifyAudioDevicesUpdate(key);
		
	}

	public static Object getParam(Context context, String key, Object defaultObject) {
		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE); // 数据缓存文件

		if (String.equals(type)) {
			return sp.getString(key, "");
		} else if (Integer.equals(type)) {
			return sp.getInt(key, 0);
		} else if (Boolean.equals(type)) {
			return sp.getBoolean(key, false);
		} else if (Float.equals(type)) {
			return sp.getFloat(key, 0);
		} else if (Long.equals(type)) {
			return sp.getLong(key, 0);
		}

		return null;
	}

	/**
	 * P2P探测使能开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:12:09
	 */
	public static boolean isIceEnable(Context mContext){
		boolean isIceenable = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_ICEENABLE", -1) >= 0){
			isIceenable = sp.getInt("YZX_ICEENABLE", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("iceenable")){
						sp.edit().putInt("YZX_ICEENABLE", jsObj.getInt("iceenable")).commit();
						isIceenable = jsObj.getInt("iceenable") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return isIceenable;
	}
	
	/**
	 * 日志上报使能开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:19:46
	 */
	public static boolean isLogReportEnable(Context mContext){
		boolean logreport = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_LOG_REPORT", -1) >= 0){
			logreport = sp.getInt("YZX_LOG_REPORT", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("logreport")){
						sp.edit().putInt("YZX_LOG_REPORT", jsObj.getInt("logreport")).commit();
						logreport = jsObj.getInt("logreport") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return logreport;
	}
	
	/**
	 * 驱动自动适配开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:12:29
	 */
	public static boolean isAutoAdapterEnable(Context mContext){
		boolean autoadapter = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_AUTO_ADAPTER_ENABLE", -1) >= 0){
			autoadapter = sp.getInt("YZX_AUTO_ADAPTER_ENABLE", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("autoadapter")){
						sp.edit().putInt("YZX_AUTO_ADAPTER_ENABLE", jsObj.getInt("autoadapter")).commit();
						autoadapter = jsObj.getInt("autoadapter") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				autoadapter = false;
			}
		}
		return autoadapter;
	}
	
	/**
	 * 音频FEC使能开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:13:04
	 */
	public static boolean isAutoFecEnable(Context mContext){
		boolean audiofec = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_AUTO_FEC_ENABLE", -1) >= 0){
			audiofec = sp.getInt("YZX_AUTO_FEC_ENABLE", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			//CustomLog.v("permission:"+permission);
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("audiofec")){
						sp.edit().putInt("YZX_AUTO_FEN_ENABLE", jsObj.getInt("audiofec")).commit();
						audiofec = jsObj.getInt("audiofec") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return audiofec;
	}
	
	/**
	 * 语音质量监控使能开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:13:13
	 */
	public static boolean isVpmEnable(Context mContext){
		boolean vqmenable = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_AUTO_VPM_ENABLE", -1) >= 0){
			vqmenable = sp.getInt("YZX_AUTO_VPM_ENABLE", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("vqmenable")){
						sp.edit().putInt("YZX_AUTO_VPM_ENABLE", jsObj.getInt("vqmenable")).commit();
						vqmenable = jsObj.getInt("vqmenable") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return vqmenable;
	}
	
	/**
	 * Rtp压缩使能开关
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-3 上午10:14:00
	 */
	public static boolean isPrtpEnable(Context mContext){
		boolean prtpenable = false;
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		if(sp.getInt("YZX_AUTO_PRTP_ENABLE", -1) >= 0){
			prtpenable = sp.getInt("YZX_AUTO_PRTP_ENABLE", -1) == 1;
		}else{ 
			String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
			if(permission.length() > 0){
				try {
					JSONObject jsObj = new JSONObject(permission);
					if(jsObj.has("prtpenable")){
						sp.edit().putInt("YZX_AUTO_PRTP_ENABLE", jsObj.getInt("prtpenable")).commit();
						prtpenable = jsObj.getInt("prtpenable") == 1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return prtpenable;
	}
	
	/**
	 * @Description 将CPS参数由字符串转化为数组  
	 * @param str CPS参数由字符串
	 * @param arr 接收转化结果的数组
	 * @date 2016年2月26日 下午4:46:28 
	 * @author zhj  
	 * @return void    返回类型
	 */
	private static void fromStr2Array(String str,int arr[][]) {
	    //str = "1,1,-1:5,5,5:5,5,5";//str格式示例
	    
	    String strArray[] = str.split(":");
	    for (int i = 0; i < 3; i++) {
	        String paraArray[] = strArray[i].split(",");
	        for (int j = 0; j < 3; j++) {
	            arr[i][j] =  java.lang.Integer.parseInt(paraArray[j]);
	        }
	    }
	}
}
