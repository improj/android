package com.yzxIM.data.db;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;

public class NoneChat extends ChatMessage{
	private int _id;

	public NoneChat() {
		// TODO Auto-generated constructor stub
		super();
		this.setCategoryId(CategoryId.NONE);
	}
	
	public NoneChat(String groupID) {
		// TODO Auto-generated constructor stub
		this.setCategoryId(CategoryId.NONE);
		this.setParentID(groupID);
	}

	public NoneChat(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, int sendTime,
			int receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super(msgid, targetId, senderId, categoryId, isFromMyself, sendTime,
				receiveTime, msgType, content, readStatus, sendStatus, path, parentID);
	}
}
