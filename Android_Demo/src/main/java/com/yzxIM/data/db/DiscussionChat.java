package com.yzxIM.data.db;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;

public class DiscussionChat extends ChatMessage {
	private int _id;

	public DiscussionChat() {
		super();
		this.setCategoryId(CategoryId.DISCUSSION);
	}

	public DiscussionChat(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, int sendTime,
			int receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super(msgid, targetId, senderId, categoryId, isFromMyself, sendTime,
				receiveTime, msgType, content, readStatus, sendStatus, path, parentID);
	}


}
