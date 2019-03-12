package com.yzx.listenerInterface;

import com.yzxtcp.data.UcsReason;


/**
 * TCP连接监听器
 * 
 * @author xiaozhenhua
 * 
 */
public interface ConnectionListener {

	/**
	 * 登录成功
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午4:01:07
	 */
	public void onConnectionSuccessful();

	/**
	 * 登录失败
	 * 
	 * @param e
	 * @author: xiaozhenhua
	 * @data:2014-4-15 下午4:01:41
	 */
	public void onConnectionFailed(UcsReason reason);

}
