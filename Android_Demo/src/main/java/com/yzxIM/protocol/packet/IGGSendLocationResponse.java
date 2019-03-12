package com.yzxIM.protocol.packet;

import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGSendLocationResponse extends IGGBaseResponse {
	public int iMsgId; // 服务端产生的MSGID
	public String pcClientMsgId; // 消息ID，格式参考如下
	public String pcFromUserName; // 发起者
	public String pcToUserName; // 接收者
	public int iTotalLen; // 总大小
	public int iStartPos; // 下一个包的起始位置
	public int iDataLen;
	public int iCreateTime; // 完整接收之后产生的时间戳

	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub

	}

}
