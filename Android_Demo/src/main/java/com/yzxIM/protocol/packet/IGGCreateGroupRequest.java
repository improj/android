package com.yzxIM.protocol.packet;

import java.util.List;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/************************* 创建讨论组请求 **********************/
public class IGGCreateGroupRequest extends IGGBaseRequest{
	public String tIntroDuce; // 介绍
	public int iMemberCount; // 创建讨论组时的成员数(不包括创建者本身)
	public String ptMemberList="";// 讨论组成员列表(不包含创建者本身)
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(RequestCmd.REQ_CREATE_GROUP.ordinal(), this);
	}
	
	public IGGCreateGroupRequest(String tIntroDuce, List<String> memberList) {
		this.tIntroDuce = tIntroDuce;
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
