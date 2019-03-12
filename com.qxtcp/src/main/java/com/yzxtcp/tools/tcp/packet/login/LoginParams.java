package com.yzxtcp.tools.tcp.packet.login;

import android.text.TextUtils;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.tools.tcp.packet.IGGAuthRequest;
/**
 * 基本的Login参数，需要填充deviceId
 * 
 * @author zhuqian
 */
public class LoginParams extends ILoginParams{

	public LoginParams(int loginType) {
		super(loginType);
	}

	@Override
	public void fillAuthRequest(IGGAuthRequest authRequest){
		authRequest.cDeviceID = deviceGenerator.obtainDevice(YzxTCPCore.getContext());
		
		if(loginType == UcsLoginResponse.UCS_TokenLogin){
			//token登录
			if(TextUtils.isEmpty(token)){
				throw new IllegalArgumentException("token不能为空");
			}
			authRequest.tLoginToken = token;
		}else if(loginType == UcsLoginResponse.UCS_ClientLogin){
			if(TextUtils.isEmpty(tDevSid) || TextUtils.isEmpty(tDevPwd) ||
					TextUtils.isEmpty(tAccount) || TextUtils.isEmpty(tPwd)){
				throw new IllegalArgumentException("login params不能为空");
			}
			authRequest.tDevSid = tDevSid;
			authRequest.tDevPwd = tDevPwd;
			authRequest.tAccount = tAccount;
			authRequest.tPwd = tPwd;
		}
	}
}
