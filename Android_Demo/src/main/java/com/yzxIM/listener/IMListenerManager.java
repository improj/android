package com.yzxIM.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

public class IMListenerManager {

	private HashMap<String, MessageListener> msgListMap = new HashMap<String, MessageListener>();
	private List<MessageListener> msgListeners = new ArrayList<MessageListener>();
	private DiscussionGroupCallBack discussionGroupCallBack;
	private IConversationListener icvListener;
//	private ISdkStatusListener iSdkStatusListener;
	// 讨论组消息回调
	public static final int DGCreate = 1;
	public static final int DGAddMem = 2;
	public static final int DGDelMem = 3;
	public static final int DGQuit = 4;
	public static final int DGModifyName = 5;
	// 会话消息回调
	public static final int COVCreate = 6;
	public static final int COVDel = 7;
	public static final int COVUpdate = 8;
	public static IMListenerManager imListenerManager;

	public static IMListenerManager getInstance() {
		if (imListenerManager == null) {
			synchronized (IMListenerManager.class) {
				if (imListenerManager == null) {
					imListenerManager = new IMListenerManager();
				}
			}
		}

		return imListenerManager;
	}

	// 发送文本消息监听
	public void setSendMsgListener(MessageListener cl) {
		String className = cl.getClass().toString();
		if(msgListMap.containsKey(className)){
			MessageListener mcl = msgListMap.get(className);
			delSendMsgListenerList(mcl);
			CustomLog.d("setSendMsgListener:class  == "+cl.getClass()+"已存在，删除");
		}
		msgListMap.put(className, cl);
		synchronized (msgListeners) {
			msgListeners.add(cl);
		}
		
	}

	public void delSendMsgListenerList(MessageListener cl) {
		synchronized (msgListeners){
			msgListeners.remove(cl);
			msgListMap.remove(cl.getClass().toString());
		}
	}

	public void delAllSendMsgListenerList() {
		synchronized (msgListeners){
			msgListeners.clear();
		}
	}

	public void notifiSendMsgListener(ChatMessage msg) {
		synchronized (msgListeners){
			for (MessageListener cl : msgListeners) {
				cl.onSendMsgRespone(msg);
			}
		}
	}

	public void notifiRecvMsgListener(List<ChatMessage> msg) {
		synchronized (msgListeners){
			for (MessageListener cl : msgListeners) {
				cl.onReceiveMessage(msg);
			}
		}
	}

	public void setDiscussionGroup(DiscussionGroupCallBack callBack) {
		discussionGroupCallBack = callBack;
	}

	public void notifyDisGroupCallBack(int type, UcsReason reason,
			DiscussionInfo dInfo) {
		if (discussionGroupCallBack == null) {
			System.err.println("discussionGroupCallBack is null");
			return;
		}

		switch (type) {
		case DGCreate:
			discussionGroupCallBack.onCreateDiscussion(reason, dInfo);
			break;
		case DGAddMem:
			discussionGroupCallBack.onDiscussionAddMember(reason);
			break;
		case DGDelMem:
			discussionGroupCallBack.onDiscussionDelMember(reason);
			break;
		case DGQuit:
			discussionGroupCallBack.onQuiteDiscussion(reason);
			break;
		case DGModifyName:
			discussionGroupCallBack.onModifyDiscussionName(reason);
		default:
			break;
		}
	}

	public void setConversationListener(IConversationListener listener) {
		this.icvListener = listener;
	}


	public void notifyICovListener(int type, List<ConversationInfo> cinfos){
		if(icvListener == null){
			System.err.println("IConversationListener is null");
			return;
		}
		switch(type){
		case COVCreate://以后不再提供会话创建回调
//			icvListener.onCreateConversation(cinfo);
			break;
		case COVDel:
			icvListener.onDeleteConversation(cinfos.get(0));
			break;
		case COVUpdate:
			icvListener.onUpdateConversation(cinfos);
			break;
		}
	}

	/*public void notifyICovListener(int type,  ConversationInfo cinfo){
		if(icvListener == null){
			System.err.println("IConversationListener is null");
			return;
		}
		switch(type){
		case COVCreate://以后不再提供会话创建回调
//			icvListener.onCreateConversation(cinfo);
			break;
		case COVDel:
			icvListener.onDeleteConversation(cinfo);
			break;
		case COVUpdate:
			icvListener.onUpdateConversation(cinfo);
			break;
		}
	}*/
	
}
