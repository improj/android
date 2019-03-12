package com.yzxIM.data.db;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;

public class BroadCastChat extends ChatMessage {
	private int _id;

	public BroadCastChat() {
		// TODO Auto-generated constructor stub
		super();
		this.setCategoryId(CategoryId.BROADCAST);
	}

	public BroadCastChat(String groupID) {
		// TODO Auto-generated constructor stub
		this.setCategoryId(CategoryId.BROADCAST);
		this.setParentID(groupID);
	}

	public BroadCastChat(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, int sendTime,
			int receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super(msgid, targetId, senderId, categoryId, isFromMyself, sendTime,
				receiveTime, msgType, content, readStatus, sendStatus, path,
				parentID);
	}
}
