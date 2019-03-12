package com.yzxIM.protocol.packet;

import java.util.List;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/********************** 讨论组踢人请求 ***********************/
public class IGGDelGroupMemberRequest extends IGGBaseRequest {
	public int iMemberCount;// 踢出的成员数
	public String ptMemberList="";// 踢出的成员列表
	public String iChatRoomId;// 讨论组ID
	
	@Override
	public void onSendMessage() {
		// TODO Auto-generated method stub
		UCSManager.sendPacket(RequestCmd.REQ_DEL_GROUP_MEMBER.ordinal(), this);
	}
	
	public IGGDelGroupMemberRequest(String iChatRoomId, List<String> memberList) {
		// TODO Auto-generated constructor stub
		this.iChatRoomId = iChatRoomId;
		this.iMemberCount = memberList.size();
		int len = memberList.size();
		for(int i=0;i<len;i++){
			this.ptMemberList = this.ptMemberList+memberList.get(i);
			if(i != (len-1)){
				this.ptMemberList = this.ptMemberList+":";
			}
		}
	}
}
