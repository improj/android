package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/************************ 讨论组成员退出请求 ************************/
public class IGGQuitGroupRequest extends IGGBaseRequest {
	public String iChatRoomId; // 讨论组ID
	public String tUserName; // 退出的用户名
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(RequestCmd.REQ_QUIT_GROUP.ordinal(), this);
	}
	
	public IGGQuitGroupRequest(String iChatRoomId) {
		this.iChatRoomId = iChatRoomId;
		this.tUserName = IMUserData.getUserName();
	}
}

