package com.yzxtcp.tcp.identity;


/**
 * TCP重连策略
 * @author zhuqian
 */
public abstract class IReconnectPlicy {

	/**
	 * 重连
	 */
	public abstract void reconn();
	
	/**
	 * 取消重连
	 */
	public abstract void cancelReconn();
}
