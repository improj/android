package com.yzxIM.protocol.packet;

import java.util.List;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/************************* 讨论组加人请求 *******************/
public class IGGAddGroupMemberRequest extends IGGBaseRequest {
	public int iMemberCount; // 邀请加入的成员数
	public String ptMemberList="";// 邀请加入的成员列表
	public String iChatRoomId;// 讨论组ID
	
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(RequestCmd.REQ_ADD_GROUP_MEMBER.ordinal(), this);
	}
	
	/**
	 * @author zhangbin
	 * @2015-4-21
	 * @@param discussionId
	 * @@param userIdList
	 * @@return
	 * @descript:
	 */
	public  IGGAddGroupMemberRequest(String iChatRoomId,
			List<String> memberList) {
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
