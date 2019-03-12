package com.yzxtcp.tools;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


/**
 * device id生成器
 * @author zhuqian
 *
 */
public class DeviceGenerator {
	
	public String obtainDevice(Context context){
		//计算业务码
//		int serviceCode = 0;
//		Set<String> sdkSet = TCPListenerManager.getInstance().getcpRecvListener();
//		if(sdkSet != null){
//			for(String item : sdkSet){
//				if(item.equals(ITcpRecvListener.IMSDK)){
//					serviceCode |= 0x01<< 0; 
//				}else if(item.equals(ITcpRecvListener.VOIPSDK)){
//					serviceCode |= 0x01<< 1;
//				}
//			}
//		}
		//网络码
//		int netCode = NetWorkTools.getCurrentNetWorkType(context);
		//设备码，截取十位
		String deviceCode = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		TCPLog.v("deviceCode:" +deviceCode);
		if(TextUtils.isEmpty(deviceCode)){
			deviceCode = "0123456789abcde";
		}
//		deviceCode = deviceCode.substring(0, 12);
//		return String.format("%c%c%s", netCode,serviceCode,deviceCode);
		return deviceCode;
		//兼容线上环境
//		return "ndroid";
	}

}
