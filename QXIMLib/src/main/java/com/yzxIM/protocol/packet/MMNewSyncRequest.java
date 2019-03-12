package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//消息同步请求
public class MMNewSyncRequest extends IGGBaseRequest {
	public int iVal;		//消息其实序列号
	public int iValGroup;		//群组信息同步序列号
	public int iSelector; // 选择同步的内容 
	public int sync_iScene; // 同步的场景，定义于mmsyncdef.h 1：同步消息和讨论组信息 7:修改标题
	public String chatroomId;
	public String chatroomTopic="";
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(600011, this); 
	}
	
	public MMNewSyncRequest(int iSelector, int sync_iScene, 
			String discussionID, String chatroomTopic) {
		iVal = UserData.getiVal(IMUserData.getUserName());
		iValGroup = UserData.getGroupiVal(IMUserData.getUserName());
		this.iSelector = iSelector;
		this.sync_iScene = sync_iScene;
		this.chatroomId = discussionID;
		this.chatroomTopic = chatroomTopic == null?"":chatroomTopic;
	}
};
