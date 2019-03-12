package com.yzxtcp.tcp.identity;


/**
 * 连接策略
 * @author zhuqian
 */
public abstract class IConnectPlicy {

	/**
	 * 连接TCP
	 */
	public abstract boolean connectPlicy(String ip);
}
