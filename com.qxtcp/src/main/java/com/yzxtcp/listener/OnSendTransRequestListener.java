package com.yzxtcp.listener;

import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.OnSendUCSRequestListener;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.SendErrorCode;
/**
 * 透传数据回调
 * 
 * @author zhuqian
 */
public abstract class OnSendTransRequestListener extends OnSendUCSRequestListener {
	public abstract void onSuccess(String msgId, String ackData);

	public abstract void onError(int errorCode, String msgId);

	@Override
	public void onSend(int sendResult, IUCSRequest content) {
		onSend(sendResult, "", content);
	}
	
	public void onSend(int sendResult, String ackData, IUCSRequest content) {
		if (sendResult == SendErrorCode.SEND_SUCCESS) {
			// 发送成功
			onSuccess(content.msgId, ackData);
		} else {
			// 发送失败
			onError(sendResult, content.msgId);
		}
	}
}