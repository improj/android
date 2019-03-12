package com.yzxtcp.tcp.listener;


/**
 * TCP结果回调，都在主线程中进行
 * @author zhuqian
 */
public abstract class TCPResultCallback {
	
	public static final int RESULT_OK = 0;

	public abstract void onResult(int t);
}
