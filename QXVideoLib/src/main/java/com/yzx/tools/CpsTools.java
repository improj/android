package com.yzx.tools;

import org.json.JSONException;
import org.json.JSONObject;

import org.webrtc.voiceengine.AudioDeviceUtil;

import android.content.Context;

import com.yzx.http.net.InterfaceConst;
import com.yzx.http.net.SharedPreferencesUtils;


public class CpsTools {
	
	/**
	 * 默认配置策略权限信息
	 * 
	 * @author: lion
	 * @throws JSONException 
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void setDynamicPolicyEnable(boolean enable){
		AudioDeviceUtil.getInstance().setDynamicPolicyEnable(enable);
	}
	
	/**
	 * @Description 设置是否要底层不做音频录制
	 * @param enable 底层不做音频录制使能开关
	 * @return void    返回类型 
	 * @date 2017年6月20日 下午4:25:47 
	 * @author zhj
	 */
	public static void setNoRecordEnable(boolean enable){
		AudioDeviceUtil.getInstance().setNoRecordEnable(enable);
	}
	
	/**
	 * @Description 设置是否要底层不做音频播放
	 * @param enable 底层不做音频播放使能开关
	 * @return void    返回类型 
	 * @date 2017年6月20日 下午4:27:22 
	 * @author zhj
	 */
	public static void setNoTrackEnable(boolean enable){
		AudioDeviceUtil.getInstance().setNoTrackEnable(enable);
	}
	
	/**
	 * 默认配置策略权限信息
	 * 
	 * @author: lion
	 * @throws JSONException 
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void setCpsDefPermission(Context mContext){
		String permission = (String)SharedPreferencesUtils.getParam(mContext, AudioDeviceUtil.getPermissionKey(), "");
		if(permission.length() <= 0){
			JSONObject jsObj;
			try {
				jsObj = new JSONObject();
				jsObj.put("iceenable", 0);
				jsObj.put("audiofec", 0);
				jsObj.put("logreport", 0);
				jsObj.put("vqmenable", 0);
				jsObj.put("autoadapter", 1);
				jsObj.put("prtpenable", 0);
				//CustomLog.v("setCpsDefPermission:"+jsObj.toString());
				setDynamicPolicyEnable(true);//default DynamicPolicy disabled
				AudioDeviceUtil.getInstance().setPermissionParam(mContext, jsObj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 配置android驱动适配信息
	 * 
	 * @author: lion
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void setCpsAudioAdapterParam(Context mContext){
		AudioDeviceUtil.getInstance().setAudioDeviceParam(mContext);
	}
	
	/**
	 * 获取策略权限信息
	 * 
	 * @author: lion
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void getCpsParam(Context mContext, boolean isFastGet){
	
		AudioDeviceUtil.getInstance().AakTaskload(mContext, InterfaceConst.getParameter, isFastGet);
	}

	/**
	 * 获取音频驱动智能适配信息
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void getCpsAdListParam(Context mContext){
		AudioDeviceUtil.getInstance().AakTaskload(mContext, InterfaceConst.getParameter_list, false);
	}

	/**
	 * 上报音频驱动适配成功信息
	 * 
	 * @author: lion
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void postCpsAndroidDeviceParam(Context mContext){
		AudioDeviceUtil.getInstance().AakTaskload(mContext, InterfaceConst.postadsuccess, false);
	}

	/**
	 * 上报音频驱动适配异常信息
	 * 
	 * @author: lion
	 * @data:2014-6-16 上午11:30:36
	 */
	public static void postCpsAdExceptionParam(Context mContext){
		AudioDeviceUtil.getInstance().AakTaskload(mContext, InterfaceConst.postadexception, false);
	}

	
	/**
	 * @author zhangbin
	 * @2016-2-18
	 * @param context
	 * @return
	 * @descript:获取CPS下发的参数，JSON格式
	 */
	public static String getCpsParamterDebug(Context context){
		return (String)SharedPreferencesUtils.getParam(context, AudioDeviceUtil.getPermissionKey(), "");
	}
}

