package com.yzxIM.data.db;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;

public class GroupChat extends ChatMessage {

	private int _id;

	public GroupChat() {
		super();
		this.setCategoryId(CategoryId.GROUP);
	}
	
	public GroupChat(String groupID) {
		// TODO Auto-generated constructor stub
		this.setCategoryId(CategoryId.GROUP);
		this.setParentID(groupID);
	}

	public GroupChat(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, int sendTime,
			int receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super(msgid, targetId, senderId, categoryId, isFromMyself, sendTime,
				receiveTime, msgType, content, readStatus, sendStatus, path, parentID);
	}

}
