package com.yzxtcp.tools.tcp.packet;


//登入请求
public class IGGAuthRequest extends IGGBaseRequest {
	public String tAccount = ""; // 账号
	public String tPwd = ""; // 密码（需要MD5），如果是FB用户则填写TOKEN（不需要MD5）
	public String pcTimeZone = ""; // 时区
	public String pcLanguage = ""; // 设备语言
	public String pcAuthTicket = "";
	public String pcRealCountry = ""; // 国家
	public String tRandomEncryKey = ""; // 客户端产生的随机加密密钥
	public String tDevSid = ""; // 开发者id
	public String tDevPwd = ""; // 开发者token
	public String tLoginToken = ""; // 客户端填写tLoginToken

	@Override
	public void onSendMessage() {
		//nothing to do
	}
	public IGGAuthRequest() {
		setClientVersion();
		// 登录iUin必须置为0
		this.iUin = 0;
	}
};
