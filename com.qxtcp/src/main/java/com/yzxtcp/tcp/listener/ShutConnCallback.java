package com.yzxtcp.tcp.listener;

/**
 * 断开TCP结果
 * @author zhuqian
 */
public abstract class ShutConnCallback extends TCPResultCallback{
	
	//断开完成
	public abstract void onShutConnFinish();

	@Override
	public void onResult(int t) {
		onShutConnFinish();
	}
}
