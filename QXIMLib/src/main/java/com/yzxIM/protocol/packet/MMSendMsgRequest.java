package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.IMListenerManager;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;
import com.yzxtcp.tools.tcp.packet.PackContent;

public class MMSendMsgRequest extends IGGBaseRequest {

	public int iCount;// 发送的消息个数

	public String tFromUserName; // 发送方名字，
	// 优先填写AS账号userID，如果userid为空，则填写clientNumber
	public String tToUserName;// 接收方名字，APP设置。
	public int iType;// 消息类型
	public String pcContent;// 上下文
	public String pcClientMsgId;// 消息唯一标识
	public String pcMsgSource;// 消息内容
	public int iEmojiFlag;// 自定义表情标识
	public String extmsg; //扩展消息对象
	
	private PackContent packContent;
	private ChatMessage chatMessage;
	private DBManager dbManager = DBManager.getInstance();
	private String showContent;
	
	
	private static int msgSeq = 0;
	
	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	@Override
	public void onSendMessage() {
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		boolean isRetryMsg = false;
		if(CategoryId.NONE == chatMessage.getCategoryId()){
			chatMessage.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
			imListenerManager.notifiSendMsgListener(chatMessage);
			DBManager.getInstance().insertChatMsgToDB(chatMessage);
			return;
		}
		//插入发送消息到数据库
		if(chatMessage.getSendStatus() != ChatMessage.MSG_STATUS_RETRY){
			DBManager.getInstance().insertChatMsgToDB(chatMessage);
		}else{
			isRetryMsg = true;
			chatMessage.setSendStatus(ChatMessage.MSG_STATUS_INPROCESS);
			dbManager.updateMessageSendTimeAndStatus(chatMessage.getMsgid(), 
					chatMessage.getParentID(), 
					chatMessage.getCategoryId(),
					chatMessage.getSendStatus());
		}
		
		//根据消息类型调用发送方式
		MSGTYPE msgtype = chatMessage.getMsgType();
		switch (msgtype) {
		case MSG_DATA_TEXT:
			packContent = UCSManager.sendPacket(
					RequestCmd.REQ_SEND_MSG.ordinal(), this);
			if (packContent == null) {
				CustomLog.e("SEND message FAIL");
				notifyMsgNetError(chatMessage);
			}
			showContent = pcContent;
			break;
		case MSG_DATA_IMAGE:
			CustomLog.e("chatMessage.getPath():" + chatMessage.getPath());
			IGGUploadMsgImgRequest upImgRequest = new IGGUploadMsgImgRequest(
					tToUserName, chatMessage.getPath());
			packContent = UCSManager.sendPacket(600030, upImgRequest);
			if (packContent == null) {
				CustomLog.e("SEND IMAGE FAIL");
				notifyMsgNetError(chatMessage);
			}
			showContent = "[图片]";
			break;
		case MSG_DATA_VOICE:
			IGGUploadVoiceRequest upVoiceRequest = new IGGUploadVoiceRequest(
					tToUserName, chatMessage.getPath(),
					Integer.valueOf(chatMessage.getContent()));
			packContent = UCSManager.sendPacket(600035, upVoiceRequest);
			if (packContent == null) {
				CustomLog.e("SEND VOICE FAIL");
				notifyMsgNetError(chatMessage);
			}
			showContent = "[语音:" + pcContent + "秒]";
			break;
		case MSG_DATA_VIDEO:
			break;
		case MSG_DATA_LOCALMAP:
			extmsg = chatMessage.getExtMessage();
			CustomLog.d("map:"+extmsg);
			IGGSendLocationRequest locationMapRequest = new IGGSendLocationRequest(
					tToUserName, chatMessage.getContent(), extmsg);
			packContent = UCSManager.sendPacket(50, locationMapRequest);
			if (packContent == null) {
				CustomLog.e("SEND map FAIL");
				notifyMsgNetError(chatMessage);
			}
			showContent = "[位置]";
			break;
		case MSG_DATA_CUSTOMMSG:
			IGGSendCustomRequest customRequest = new IGGSendCustomRequest(
					tToUserName, chatMessage.getContent());
			packContent = UCSManager.sendPacket(51, customRequest);
			if (packContent == null) {
				CustomLog.e("SEND map FAIL");
				notifyMsgNetError(chatMessage);
			}
			showContent = "[自定义消息]";
			break;
		default:
			CustomLog.d("未知发送消息命令类型：" + chatMessage.getMsgType());
			return;
		}
		//开启发送定时器
		sendMessageTimerOut(chatMessage, 60000);
		//重发消息不更新会话
		if(isRetryMsg){
			isRetryMsg = false;
			CustomLog.d("重发消息不更新会话");
			return;
		}
		// 判断会话是否存在
		String targetID = chatMessage.getTargetId();
		if (dbManager.isConversationExisit(targetID) == false) {
			CustomLog.d("发送消息 会话不存在 创建会话表:"+targetID);
			String converTitle = "";
			if(chatMessage.getCategoryId() == CategoryId.DISCUSSION){
				DiscussionInfo di = dbManager.getDiscussionInfo(
						chatMessage.getParentID());
				if(di != null){
					converTitle = di.getDiscussionName();
				}else{
					converTitle = chatMessage.getTargetId();
					CustomLog.d("获取讨论组信息失败!!");
				}
			}else{
				converTitle = chatMessage.getTargetId();
			}
			CustomLog.d("发送消息converTitle:"+converTitle);
			ConversationInfo cinfo = dbManager.insertConversationToDb(targetID,
					chatMessage.getCategoryId(), showContent, converTitle, 0,
					chatMessage.getSendTime());
			if (cinfo != null) {
				recvCinfos.add(cinfo);
				/*imListenerManager.notifyICovListener(
						IMListenerManager.COVCreate, cinfo);*/
			} else {
				System.err.println("生成 cinfo 失败");
			}
		} else {
			dbManager.updateConversationLTimeAndDMsg(targetID,
					System.currentTimeMillis() + "", showContent,false);

			ConversationInfo cinfo = dbManager.getConversation(targetID);
			if (cinfo != null) {
				recvCinfos.add(cinfo);
				/*imListenerManager.notifyICovListener(
						IMListenerManager.COVUpdate, cinfo);*/
			}
		}
		
		if(recvCinfos.size() > 0){
			CustomLog.d("MMSendMsgRequest COVUpdate");
			imListenerManager.notifyICovListener(
				IMListenerManager.COVUpdate, recvCinfos);
		}
		
	}

	public MMSendMsgRequest(ChatMessage chatMessage) {
		if(chatMessage.getSendStatus() != ChatMessage.MSG_STATUS_RETRY){
			initChatMessage(chatMessage);
		}

		tFromUserName = IMUserData.getUserName();
		switch (chatMessage.getCategoryId()) {
		case PERSONAL:
			this.tToUserName = chatMessage.getTargetId();
			break;
		case GROUP:
			this.tToUserName = chatMessage.getTargetId()
					+ "@chatroom";
			break;
		case DISCUSSION:
			this.tToUserName = chatMessage.getTargetId() 
					+ "@group";
			break;
		default: //未知的消息直接报发送错误
			
			break;
		}

		this.pcContent = chatMessage.getContent();
		this.chatMessage = chatMessage;
		
	}

	private void initChatMessage(ChatMessage chatMessage) {
		msgSeq++;
		String msgid = "ms" + System.currentTimeMillis() + msgSeq;

		chatMessage.setMsgid(msgid);
		chatMessage.setSenderId(IMUserData.getUserName());

		chatMessage.setFromMyself(true);
		chatMessage.setSendTime(System.currentTimeMillis());
		chatMessage.setReceiveTime(0);
		chatMessage.setParentID(chatMessage.getTargetId());
		chatMessage.setReadStatus(ChatMessage.MSG_STATUS_READED);
		chatMessage.setSendStatus(ChatMessage.MSG_STATUS_INPROCESS);
	}
	
	private void sendMessageTimerOut(ChatMessage imMsg, long timeOut){
		if(packContent != null){
			Handler handler = IMManager.getInstance(null).getMsgHandler();
			if(handler == null || imMsg == null){
				CustomLog.e("sendMessageTimerOut error!!!");
				return ;
			}
			
			Message msg = handler.obtainMessage(RequestCmd.REQ_SENDMSG_TIMEROUT.ordinal());
			msg.obj = imMsg;
			handler.sendMessageDelayed(msg, timeOut);
			
			IMUserData.mapSaveMsg(packContent.pcClientMsgId, chatMessage);

			CustomLog.d("send pcClientMsgId:"
					+ packContent.pcClientMsgId + "sendmsgid:"
					+ chatMessage.getMsgid());
		}
	}
	
	private void notifyMsgNetError(ChatMessage chatMessage){
		chatMessage.setSendStatus(ChatMessage.MSG_STATUS_NETERROR);
		imListenerManager.notifiSendMsgListener(chatMessage);
		dbManager.updataMsgStatusAndMsgID(chatMessage, 
					chatMessage.getMsgid());
	}
}
