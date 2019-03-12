package com.yzx.http.net;

import android.content.Context;
import android.text.TextUtils;


public class InterfaceUrl {
	
	public static final String URL = "url";

	//cps.kaixuantx.com:9997
	//测试环境地址
	//public static String GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//获取适配参数
	//public static String GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//获取智能适配列表
	//public static String POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//异常适配参数上报
	//public static String POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//成功适配参数上报
	
	//线上环境地址
	public static String GET_PARAMETER = "http://cps.kaixuantx.com:9997/v2/get_audiodevice?";//获取适配参数
	public static String GET_PARAMETER_LIST = "http://cps.kaixuantx.com:9997/v2/get_adlist?";//获取智能适配列表
	public static String POST_PARAMETER_ADEXCEPTION = "http://cps.kaixuantx.com:9997/v2/post_adexception?";//异常适配参数上报
	public static String POST_PARAMETER_ADSUCCESS = "http://cps.kaixuantx.com:9997/v2/post_adsuccess?";//成功适配参数上报
	public static String RTPP_URL_ADDRESS="http://cps.kaixuantx.com:9997/v2/getrtpplist?";
	/** 
	 * 配置CPS Url到测试环境
	 * 
	 */  
	public static void initUrlToTest(Context context){
	    
	    String cpsAddressAndPort = context.getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_CPSADDRESS", "");
	    if(!TextUtils.isEmpty(cpsAddressAndPort)) {
	        GET_PARAMETER = "http://" + cpsAddressAndPort + "/v2/get_audiodevice?";//获取适配参数
            GET_PARAMETER_LIST = "http://" + cpsAddressAndPort + "/v2/get_adlist?";//获取智能适配列表
            POST_PARAMETER_ADEXCEPTION = "http://" + cpsAddressAndPort + "/v2/post_adexception?";//异常适配参数上报
            POST_PARAMETER_ADSUCCESS = "http://" + cpsAddressAndPort + "/v2/post_adsuccess?";//成功适配参数上报
            RTPP_URL_ADDRESS="http://" + cpsAddressAndPort + "/v2/getrtpplist?";   // 获取Rtpp列表
	    }
	        
	    /*
		if(isTest){
			GET_PARAMETER = "http://113.31.89.144:9997/v2/get_audiodevice?";//获取适配参数
			//GET_PARAMETER = "http://121.201.55.70:9997/v2/get_audiodevice?";//获取适配参数
			GET_PARAMETER_LIST = "http://113.31.89.144:9997/v2/get_adlist?";//获取智能适配列表
			//GET_PARAMETER_LIST = "http://121.201.55.70:9997/v2/get_adlist?";//获取智能适配列表
			POST_PARAMETER_ADEXCEPTION = "http://113.31.89.144:9997/v2/post_adexception?";//异常适配参数上报
			//POST_PARAMETER_ADEXCEPTION = "http://121.201.55.70:9997/v2/post_adexception?";//异常适配参数上报
			POST_PARAMETER_ADSUCCESS = "http://113.31.89.144:9997/v2/post_adsuccess?";//成功适配参数上报
			//POST_PARAMETER_ADSUCCESS = "http://121.201.55.70:9997/v2/post_adsuccess?";//成功适配参数上报
			RTPP_URL_ADDRESS="http://113.31.89.144:9997/v2/getrtpplist?";	// 获取Rtpp列表
			//RTPP_URL_ADDRESS="http://121.201.55.70:9997/v2/getrtpplist?";	// 获取Rtpp列表
		}else {
			GET_PARAMETER = "http://cps.kaixuantx.com:9997/v2/get_audiodevice?";//获取适配参数
			GET_PARAMETER_LIST = "http://cps.kaixuantx.com:9997/v2/get_adlist?";//获取智能适配列表
			POST_PARAMETER_ADEXCEPTION = "http://cps.kaixuantx.com:9997/v2/post_adexception?";//异常适配参数上报
			POST_PARAMETER_ADSUCCESS = "http://cps.kaixuantx.com:9997/v2/post_adsuccess?";//成功适配参数上报
			RTPP_URL_ADDRESS="http://cps.kaixuantx.com:9997/v2/getrtpplist?";	// 获取Rtpp列表
		}
		*/
	}
}
