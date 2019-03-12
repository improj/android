package com.yzxtcp.tools.provider;

/**
 * 发送提供者接口
 * @author zhuqian
 */
public interface IProvider {
	
	public static final int SEND_TIMEOUT = 60 * 1000;
	//发送
	boolean send();
	//发送响应
	void onSend(int errorCode);
}
