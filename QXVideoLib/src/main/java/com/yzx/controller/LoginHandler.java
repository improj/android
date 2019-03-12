package com.yzx.controller;  

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSService;
import com.yzx.controller.listenercallback.VoipRtppCallBack;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.ReportListener;
import com.yzx.preference.UserData;
import com.yzx.tools.CpsTools;
import com.yzx.tools.DevicesReportTools;
import com.yzx.tools.FileTools;
import com.yzx.tools.RtppConfigTools;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title LoginHandler   
 * @Description  登录处理类，包括登录和重登录
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-26 下午3:28:15
 */
public abstract class LoginHandler {
	
	public void report(){
	    //获取参数并设置组件
        new Thread(new Runnable() {
            @Override
            public void run() {
                //ServiceConfigTools.getCsAddress(UserData.getAccountSid());//CS地址暂时写死
                RtppConfigTools.getRtppAndStunList(VoipCore.getContext(),new VoipRtppCallBack());
                //获取参数成功后设置组件
                UGoSetConfig.setConfig();

                CpsTools.getCpsParam(VoipCore.getContext(), false);
            }
        }).start();
        
		//上报设备信息
		if(DevicesReportTools.isReportDevicesInfo(VoipCore.getContext())){
			DevicesReportTools.reportDevicesInfo(VoipCore.getContext(), UserData.getUserId(VoipCore.getContext()), new ReportListener() {
				@Override
				public void onReportResult(int code, String result) {
					if(code== 0){
						DevicesReportTools.saveReportDevicesInfo(VoipCore.getContext(),false);
						CustomLog.v("REPORT DEVICES SUCCESS ... ");
					}else{
						CustomLog.v("REPORT DEVICES FAILUER ... ");
						CustomLog.v(code+":"+result);
					}
				}
			});
		}
		//异常日志上报
		crashReport();
	}
	
	/**
	 * @author zhangbin
	 * @2016-1-27
	 * @descript:错误日志上报
	 */
	private void crashReport(){
		final String crashFile = com.yzxtcp.data.UserData.getCrashFile();
		if(!TextUtils.isEmpty(crashFile)){
			new Thread(new Runnable() {
				@Override
				public void run() {
					String result = null;
					int respCode = 0;
					String respMsg = null;
					
					//调用上传之前清除上传crash文件标识，上传失败不检查
					com.yzxtcp.data.UserData.saveCrash("");
					result = FileTools.uploadCrashFile(VoipCore.getContext(), crashFile, 
							FileTools.CRASH_URL);
					if(TextUtils.isEmpty(result)){
//						CustomLog.d("上报crashFile fail");
					}else{
						try {
							JSONObject obj = new JSONObject(result);
							if(obj.has("respCode")){
								respCode = obj.getInt("respCode");
							}
							if(obj.has("respMsg")){
								respMsg = obj.getString("respMsg");
							}
							if(respCode == 0){
//								CustomLog.d("上传crash成功");
							}else{
								/*try {
									CustomLog.d("上传crash失败:"+respCode+"  "+URLDecoder.decode(respMsg, "gb2312"));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}*/
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				}
			}).start();
			
		}
	}
	
	protected synchronized void switchErrorCode(UcsReason reason){ 
		for(ConnectionListener cl:UCSService.getConnectionListener()){
			switch(reason.getReason()){
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_CONNECTFAIL:
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_CONNECTTIMEOUT:
				cl.onConnectionFailed(new UcsReason(300001).setMsg(reason.getMsg() + "[" + reason.getReason() + "]"));
				break;
//			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_KICKOUT:
//			    //被踢线后如果当前有通话则结束通话
//			    CustomLog.v("switchErrorCode() NET_ERROR_KICKOUT");
//			    String currentCallID = UCSCall.getCurrentCallId();
//			    if (currentCallID != null && currentCallID.length() > 0)
//			    {
//			    	UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
//	                CustomLog.v("NET_ERROR_KICKOUT, hangup by oneself"); 
//			    }
//				cl.onConnectionFailed(new UcsReason(300207).setMsg(reason.getMsg() + "[" + reason.getReason() + "]"));
//				break;
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_TOKENERROR:
				cl.onConnectionFailed(new UcsReason(300017).setMsg(reason.getMsg() + "[" + reason.getReason() + "]"));
				break;
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_USERUNKNOWN:
				cl.onConnectionFailed(new UcsReason(300014).setMsg(reason.getMsg() + "[" + reason.getReason() + "]"));
				break;
			case com.yzxtcp.data.UcsErrorCode.PUBLIC_ERROR_PARAMETERERR:
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_PASSWORDERROR:
				cl.onConnectionFailed(new UcsReason(300009).setMsg(reason.getMsg() + "[" + reason.getReason() + "]"));
				break;
			case com.yzxtcp.data.UcsErrorCode.NET_ERROR_RECONNECTOK:
				CustomLog.v("重新连接服务器");
				//cl.onConnectionSuccessful();
				break;
			case com.yzxtcp.data.UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT:
				//cl.onConnectionFailed(new UcsReason().setMsg(reason.getMsg()));
				break;
			}
		}
	}
}
  
