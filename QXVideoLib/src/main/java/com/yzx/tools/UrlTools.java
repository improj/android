package com.yzx.tools;

public class UrlTools {

	
	/**
	 * 获取URL内指定字段值
	 * @param url,optionName
	 * @return value
	 * @author: xiaozhenhua
	 * @data:2014-4-10 上午11:15:30
	 */
	public static String getUrlOptionValue(String url,String optionName) {
		
		String ovalue = "";

		if(url == null || optionName == null)
			return "";

		int bf = url.indexOf(optionName);
		if (bf < 0)
			return "";

		String tmpStr = url.substring(bf);
		bf = tmpStr.indexOf('&');
		if (bf < 0) {
			ovalue = tmpStr.substring(optionName.length());
		}else{
			ovalue = tmpStr.substring(optionName.length(),bf);
		}
		
		return ovalue;
	}
}
