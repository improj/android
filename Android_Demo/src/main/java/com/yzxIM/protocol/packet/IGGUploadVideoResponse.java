package com.yzxIM.protocol.packet;

import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGUploadVideoResponse extends IGGBaseResponse {
	public String pcClientMsgId;// 客户端定义的标识符
	public int iMsgId; // 服务端产生的一个msgId
	public int iThumbStartPos;// 下一包的起始位置
	public int iVideoStartPos;// 下一包的起始位置
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		
	}
}
