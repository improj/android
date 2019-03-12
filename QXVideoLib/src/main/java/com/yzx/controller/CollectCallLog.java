package com.yzx.controller;  

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;

import com.yzx.preference.UserData;
import com.yzx.tools.CallLogTools;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title CollectCallLog   
 * @Description  收集拨打电话日志信息
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-23 上午11:32:47
 */
public class CollectCallLog {
	private static int upSPCount = 0; //上行单通计数
	private static int downSPCount = 0; //下行单通计数
	private static StringBuilder mStringBuilder = null;
	
	public static int getUpSPCount() {
		return upSPCount;
	}

	public static void setUpSPCount(int upSPCount) {
		CollectCallLog.upSPCount = upSPCount;
	}

	public static int getDownSPCount() {
		return downSPCount;
	}

	public static void setDownSPCount(int downSPCount) {
		CollectCallLog.downSPCount = downSPCount;
	}

	/**
	 * @author zhangbin
	 * @2016-1-27
	 * @descript:sdk 拨打电话流程日志上报
	 */
	public static void sdkPhoneReport(final String callID){
		TimerHandler.getInstance().setCalllogCount(0);
		upSPCount = 0;
		downSPCount = 0;
		TimerHandler.getInstance().setCalllogLastMessage(null);
		final String log=buildLog(callID, mStringBuilder);
		new Thread(new Runnable() {
			@Override
			public void run() {
//				String result = null;
//				int respCode = 0;
//				String respMsg = null;
				CallLogTools.saveLog2File(callID, log);
				CustomLog.v("sdkPhoneReport is going to launch launchUploadCalllog");
				CallLogTools.launchUploadCalllog(callID + ".txt");
				CustomLog.v("sdkPhoneReport thread ends");
			}
		}).start();

	}
	
	public synchronized static void setSDKPhoneMsg(String msg){
	    if (mStringBuilder != null && mStringBuilder.length() > CallLogTools.CALLLOG_MAX_SIZE) { 
	        return;
	    }
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String time = format.format(new Date(System.currentTimeMillis()));
		if(initStringBuilder(false)){
			/*if(TextUtils.isEmpty(mStringBuffer)&&
					msg.contains("CALLID") &&msg.contains("CALL_LOG")){
					如果组件上报LOG时mStringBuffer已经置为null则单独上报
				                 否则和其它日志一起上报					
				  reportTraceCallback(mStringBuffer, msg, time);
					return;
			}*/
			mStringBuilder.append(time+"|");
			msg = msg.replaceAll("\r\n", "|");
			mStringBuilder.append(msg+"|");	
		}
	}
	
	public synchronized static String buildLog(String callId, 
			StringBuilder builder){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(new Date(System.currentTimeMillis()));
		StringBuilder log = new StringBuilder();
		int callType = UserData.getCallType();
		String businessType = null;
		if(callType == 3){//视频电话
			businessType = "VFC";
		}else if(callType == 6){
			businessType = "AFC";
		}
		else if (callType == 4){
			businessType = "ADC";
		}
		if(TextUtils.isEmpty(callId)){
			callId = "-";
		}
		log.append("CALLLOG\t"+time+"\t"+businessType
				+"\tSDK\tANDROID\t"+callId+"\t")
				.append(builder);
		initStringBuilder(true);//清除日志缓存
		return log.toString();
	}
	
	/**
	 * @author zhangbin
	 * @2016-3-10
	 * @param isReset 是否需要设置为null
	 * @return true mStringBuilder!=null
	 * @descript:
	 */
	public synchronized static boolean initStringBuilder(boolean isReset){
		if(isReset){
			mStringBuilder = null;
			return false;
		}else{
			if (mStringBuilder == null) {
				mStringBuilder = new StringBuilder();
				return true;
			}
		 }
		return true;
	 }
}
  
