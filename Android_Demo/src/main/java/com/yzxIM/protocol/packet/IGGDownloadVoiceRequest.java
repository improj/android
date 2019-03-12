package com.yzxIM.protocol.packet;

import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//下载语音请求 
public class IGGDownloadVoiceRequest extends IGGBaseRequest {
	public int iMsgId; // 从服务器得到的msgId
	public int iOffset; // 起始位置
	public int iLength; // 请求长度
	public String pcClientMsgId; // 从服务器得到的ClientMsgId
	
	@Override
	public void onSendMessage() {
		//TcpTools.sendPacket(RequestCmd.REQ_DOWNLOAD_VOICE.ordinal(), this);
		UCSManager.sendPacket(600036, this);
	}
	
	public IGGDownloadVoiceRequest(String pcClientMsgId,
			int iMsgId, int iLength, int iOffset) {
		
		// TODO Auto-generated constructor stub
		this.pcClientMsgId = pcClientMsgId;
		this.iMsgId = iMsgId;
		this.iLength = iLength;
		this.iOffset = iOffset;
	}
}