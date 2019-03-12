package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//下载消息图片请求 
public class IGGDownloadMsgImgRequest extends IGGBaseRequest {
	public int iMsgId; // 服务端产生的MSGID
	public String pcFromUserName; // 发起者
	public int iTotalLen; // 总长度（第一个包为0）
	public int iStartPos; // 起始位置（第一个为0）
	public int iDataLen; // 用不到，填0
	public int iCompressType; // 是否需要原图

	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(600031, this);
	}

	public IGGDownloadMsgImgRequest(int iMsgId, int iTotalLen, int iStartPos,
			int iCompressType) {
		this.pcFromUserName = IMUserData.getUserName();
		this.iMsgId = iMsgId;
		this.iTotalLen = iTotalLen;
		this.iStartPos = iStartPos;
		this.iCompressType = iCompressType;
	}
}
