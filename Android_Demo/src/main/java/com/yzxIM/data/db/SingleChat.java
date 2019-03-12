package com.yzxIM.data.db;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;

public class SingleChat extends ChatMessage {
	private int _id;

	public SingleChat() {
		super();
		this.setCategoryId(CategoryId.PERSONAL);
	}

	public SingleChat(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, long sendTime,
			long receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super(msgid, targetId, senderId, categoryId, isFromMyself, sendTime,
				receiveTime, msgType, content, readStatus, sendStatus, path, parentID);
	}

}
