package com.yzxtcp.listener;

/**tcp消息接收接听器
 * 
 *
 */
public interface ITcpRecvListener {
	/**
	 * 监听类型为IM
	 */
	public static String IMSDK = "IMSDK";
	/**
	 * 监听类型为VOIP
	 */
	public static String VOIPSDK = "VOIPSDK";
	/**收到TCP消息时调用
	 * @author zhangbin
	 * @2015-7-20
	 * @param cmd 接收到的命令
	 * @param buf 返回BUF
	 */
	void onRecvMessage(int cmd, byte[] buf);
}
