package com.yzx.tools;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class DevicesAddressTools {
	
	
	/**
	 * 获取本机的mac地址
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午3:36:36
	 */
	public static String getDevicesMacAddress(Context mContext){
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi != null ? wifi.getConnectionInfo() : null;
		if (info != null) {
			String macString = info.getMacAddress();
			return macString != null && macString.length() > 0 ? macString :"";
		} else {
			return "";
		}
	}
	
	/**
	 * 获取设备IMEI
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:29:31
	 */
	public static String getDevicesImei(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei != null && imei.length() > 0 ? imei : "";
	}

	/**
	 * 获取设备IMSI
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-17 下午2:29:44
	 */
	public static String getDevicesImsi(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi != null && imsi.length() > 0 ? imsi : "";
	}

}
