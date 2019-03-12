package com.yzxtcp.listener;

import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.OnReceiveUCSListener;


/**
 * 收到透传回调
 * @author zhuqian
 */
public abstract class OnRecvTransUCSListener extends OnReceiveUCSListener{
	/**
	 * 开发者回调
	 * @param fromUserId 发送方userId
	 * @param data 透传的数据
	 * @param callid 通话的callid
	 * @param previewImgUrl 视频通话时的预览图片地址
	 */
	public abstract void onRecvTranslate(String fromUserId,String data, String callid, String previewImgUrl);
	@Override
	public void onReceive(int cmdId, byte[] data, int serviceId) {
		//nothing to do
	}
}
