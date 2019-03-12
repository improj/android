package com.yzxtcp.tools.tcp.packet;

import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.StringUtils;
// 登入返回
public class IGGAuthResponse extends IGGBaseResponse {
	public int iUin; // UIN
	public String tUserName=""; // 用户名
	public String tNickName=""; // 昵称
	public String tBindEmail="";
	public String tBindMobile="";
	public int iStatus;
	public byte[] sSessionKey = new byte[36]; // SESSION KEY
	public int tImgBufiLen;
	public byte[] tImgBufpcBuff=new byte[1024]; // 头像BUFF
	public int iNewVersion; // 新版本信息，0则表示已经是最新
	public int iRegType; // 注册类型
	public int iProfileFlag;
	public int iUserSex; // 性别（参照enMMSexType宏定义）
	public int iUserStatus; // 用户状态enUserStatus
	public String tFirstName="";
	public String tLastName="";
	public int iYear;
	public int iMonth;
	public int iDay;
	public int iIPCount;	// 短连接IP信息
	public String ptIPList=""; // 短连接IP信息
	public String tSafeUserName=""; // Safe用户名
	public String tClientNumber="";
	public String tAppid="";
	@Override 
	public void onMsgResponse() {
		if(base_iRet == 0){
			//登陆成功 保存uin 和用户名 供以后发送消息时使用
			UserData.saveiUin(iUin);
			UcsLoginResponse.clientNumber = tClientNumber;
			UcsLoginResponse.phone = tBindMobile;
			UcsLoginResponse.userid = tUserName;
			UcsLoginResponse.appid = tAppid;
			
			UserData.saveUserId(tUserName);
			
			//自动重登陆SSID为NULL
			if(StringUtils.isEmpty(UcsLoginResponse.SSID) == false){
				UserData.saveLoginToken(UcsLoginResponse.SSID);
			}
			UserData.saveAppid(tAppid);
			UserData.saveClientId(tClientNumber);
		}
	}
};