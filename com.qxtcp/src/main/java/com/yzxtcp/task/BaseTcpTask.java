package com.yzxtcp.task;

import com.yzxtcp.tcp.listener.TCPResultCallback;
/**
 * 带TCPResultCallback的tcp任务
 * @author zhuqian
 */
public abstract class BaseTcpTask<Result> extends TcpTask<Result>{
	public TCPResultCallback callback;
	/**
	 * 子类实现
	 * @return
	 */
	public abstract void setTcpCallback(TCPResultCallback callback);
	
	public BaseTcpTask() {
		super();
	}
	
	public void onPreExecute(){
		
	}
	/**
	 * TCP任务
	 * @author zhuqian
	 */
	public static class TCPResult{
		public TCPResult(){
			
		}
		//是否成功
		public boolean isSuccess;
		//错误码
		public int errorCode;
	}
}
