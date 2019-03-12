package com.yzxtcp.data;

import org.json.JSONException;
import org.json.JSONObject;


public class UcsLoginResponse {
	/**
	 * 	老用户账号密码登陆
	 */
	public static final int UCS_ClientLogin = 1;
	/** 
	 * token登陆 
	 */
	public static final int UCS_TokenLogin = 2;
	
	/**
	 * 登陆类型 1=老用户， 2=新用户TOKEN登陆
	 */
	public static int loginType = 2;
	/**
	 * 用户子账号
	 */
	public static String clientNumber="";
	/**
	 * 用户账号
	 */
	public static String userid = "";
	/**
	 * 用户账号绑定的手机号码
	 */
	public static String phone = "";
	/**
	 * SSID旧用户登陆的时候为ClientPwd 新用户为token
	 */
	public static String SSID = "";
	
	/**
	 * 应用ID
	 */
	public static String appid = "";
	
	public static String toStringResponse() {
		JSONObject json = new JSONObject();
		try {
			json.put("loginType", loginType);
			json.put("clientNumber", clientNumber);
			json.put("userid", userid);
			json.put("phone", phone);
			json.put("SSID", SSID);
			json.put("appid", appid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}
