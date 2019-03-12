package com.yzxtcp.tools.tcp.packet.login;

import android.text.TextUtils;

import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.DeviceGenerator;
import com.yzxtcp.tools.tcp.packet.IGGAuthRequest;
/**
 * 登录参数封装
 * 
 * @author zhuqian
 */
public abstract class ILoginParams {
	//登录类型，1表示老账户登录、2表示token登录
	public int loginType;
	protected DeviceGenerator deviceGenerator;
	//token登录方式需要的token
	public String token = "";
	public String tAccount = ""; // 账号
	public String tPwd = ""; // 密码（需要MD5），如果是FB用户则填写TOKEN（不需要MD5）
	public String tDevSid = ""; // 开发者id
	public String tDevPwd = ""; // 开发者token
	public ILoginParams(int loginType){
		this.loginType = loginType;
		this.deviceGenerator = new DeviceGenerator();
	}
	//填充IGGAuthRequest数据包
	public abstract void fillAuthRequest(IGGAuthRequest authRequest);
	
	/**
	 * 保存登录参数，loginType==1保存clientPwd，loginType==2保存token
	 */
	public void saveLoginParams(){
		if(loginType == UcsLoginResponse.UCS_TokenLogin){
			UserData.saveLoginToken(token);
		}else if(loginType == UcsLoginResponse.UCS_ClientLogin){
			UserData.saveLoginToken(tPwd);
		}
	}
	/**
	 * 检验参数
	 */
	public void checkParams() throws IllegalArgumentException{
		if(loginType == UcsLoginResponse.UCS_TokenLogin){
			//token登录
			if(TextUtils.isEmpty(token)){
				throw new IllegalArgumentException("token不能为空");
			}
		}else if(loginType == UcsLoginResponse.UCS_ClientLogin){
			if(TextUtils.isEmpty(tDevSid) || TextUtils.isEmpty(tDevPwd) ||
					TextUtils.isEmpty(tAccount) || TextUtils.isEmpty(tPwd)){
				throw new IllegalArgumentException("login params不能为空");
			}
		}
	}
	/**
	 * 获取登录秘钥
	 * @return
	 */
	public String getAuthPwd(){
		if(loginType == UcsLoginResponse.UCS_TokenLogin){
			return token;
		}else if(loginType == UcsLoginResponse.UCS_ClientLogin){
			return tPwd;
		}
		return "";
	}
}
