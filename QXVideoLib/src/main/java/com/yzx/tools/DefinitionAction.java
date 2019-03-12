package com.yzx.tools;

public class DefinitionAction {
	
/*	//日志上报URL
	public static final String REPORT_URL = "http://ulog.ucpaas.com";
	//public static final String REPORT_URL = "http://113.31.89.144:8088";

	
	public static final String IM_URL = "https://im.ucpaas.com";
	public static final String IM_PORT = "8887";*/
	
	public static final String SDK_VERSION = "v1000.2.0.3_L.0";
	
	//日志上报URL
	public static final String REPORT_URL = "http://ulog.ucpaas.com";
	
	public static final String RESULT = "result";
	public final static String PREFERENCE_NAME = "yunzhixun_preference";
	
	//是否是收费版本，收费版本号类似："v1000.2.0.1_L.,0"，免费版本类似："v2.0.1.0"
	public static boolean isLicenseVersion() {
	    return SDK_VERSION.contains("_L");
	}
}
