  
package com.yzx.controller.listenercallback;  

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yzx.api.UCSService;
import com.yzx.controller.LoginHandler;
import com.yzx.controller.VoipCore;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.preference.UserData;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipLoginCallBack   
 * @Description  voip登录回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午4:26:44
 */
public class VoipLoginCallBack extends LoginHandler implements ILoginListener {

	@Override
	public void onLogin(UcsReason reason) {
		CustomLog.v("video sdk onLogin reason: "+reason.getReason()+"   MSG:"+reason.getMsg());
		if(reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK){
			parseString(VoipCore.getContext(), reason.getMsg());
			for(ConnectionListener cl : UCSService.getConnectionListener()){
				cl.onConnectionSuccessful();
			}
			report();
		}else{
			switchErrorCode(reason);
		}
	}
	
	private void parseString(Context mContext, String result) {
		try {
			JSONObject json = new JSONObject(result);
			CustomLog.v("login result:" + json.toString());
			if (json.has("loginType")) {
				CustomLog.v("loginType:" + json.getInt("loginType"));
			}
			
			if (json.has("clientNumber")) {
				UserData.saveClientNumber(mContext, json.getString("clientNumber"));
				CustomLog.v("onLogin parseString clientNumber:" + json.getString("clientNumber"));
			}
			
			if (json.has("userid")) {
				UserData.saveUserId(mContext, json.getString("userid"));
			}
			
			if (json.has("phone")) {
				UserData.savePhoneNumber(mContext, json.getString("phone"));
			}
			
			if (json.has("SSID")) {
				UserData.saveImSsid(mContext, json.getString("SSID"));
			}
			
			if (json.has("appid")){
				UserData.saveAppid(mContext, json.getString("appid"));
			}
		} catch (JSONException e) {
			 e.printStackTrace();
		}
	}
	

}
  
