package com.yzxtcp.tcp.listener;

import com.yzxtcp.data.UcsReason;
/**
 * 连接回调
 * @author zhuqian
 */
public abstract class ConnectCallback extends TCPResultCallback{
	//成功回调
	public abstract void onSuccess();
	//失败回调
	public abstract void onFail(UcsReason reason);
	@Override
	public void onResult(int t) {
		if(t == RESULT_OK){
			onSuccess();
		}else{
			onFail(new UcsReason().setReason(t));
		}
	}
}
