package com.yzxIM;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxIM.protocol.packet.IGGAddGroupMemberRequest;
import com.yzxIM.protocol.packet.IGGCreateGroupRequest;
import com.yzxIM.protocol.packet.IGGDelGroupMemberRequest;
import com.yzxIM.protocol.packet.IGGDownloadMsgImgRequest;
import com.yzxIM.protocol.packet.IGGDownloadVoiceRequest;
import com.yzxIM.protocol.packet.IGGQuitGroupRequest;
import com.yzxIM.protocol.packet.MMNewSyncRequest;
import com.yzxIM.protocol.packet.MMSendMsgRequest;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.iface.IUCSMessageRequest;

public class IMHandler extends Handler {
	
	private DBManager dbManager = DBManager.getInstance();
	private IMListenerManager imlManager = IMListenerManager.getInstance();
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Bundle data;
		String discussionID;
		ChatMessage chatMessage;
		int msgid = 0;
		int length = 0;
		String clientMsgID = null;
		RequestCmd cmd = RequestCmd.valueof(msg.what);

		CustomLog.d("cmd is " + cmd);

		switch (cmd) {
		/*case REQ_AUTH:
			data = msg.getData();
			String token = data.getString("token");
			byte[] imei = data.getByteArray("imei");

			IUCSMessageRequest authRequest = new IGGAuthRequest(token, imei);
			authRequest.onSendMessage();
			break;*/
		case REQ_SEND_MSG: //发送文字 语音 图片消息
			data = msg.getData();
			chatMessage = data.getParcelable("chatmsg");
			if(chatMessage == null)
			{
				CustomLog.e("获取发送消息失败！！！！");
				break;
			}
			IUCSMessageRequest sendMsgRequest = null;
			sendMsgRequest = new MMSendMsgRequest(chatMessage);
			
			sendMsgRequest.onSendMessage();
			break;
		/*case REQ_REAUTH:
			IUCSMessageRequest initRequest = new IGGAuthBySKRequest();
			initRequest.onSendMessage();
			break;*/
		case REQ_NEW_SYNC:
			data = msg.getData();
			discussionID = data.getString("discussionID");
			int seletor = data.getInt("seletor");
			int Scene = data.getInt("Scene");
			String chatroomTopic = data.getString("chatroomTopic");
			IUCSMessageRequest newsyncRequest = new MMNewSyncRequest(
					seletor,Scene, discussionID, chatroomTopic);
			newsyncRequest.onSendMessage();
			break;
		
		case REQ_DOWNLOAD_IMG:
			data = msg.getData();
			msgid = data.getInt("msgid");
			length = data.getInt("length");
			if(msgid == 0 || length == 0){
				CustomLog.e("REQ_DOWNLOAD_IMG 获取参数失败!!!");
				break;
			}
			IUCSMessageRequest updownImgRequest = new IGGDownloadMsgImgRequest(
					msgid, length, 0, 0);
			updownImgRequest.onSendMessage();
			break;
		
		case REQ_DOWNLOAD_VOICE:
			data = msg.getData();
			msgid = data.getInt("msgid");
			length = data.getInt("length");
			int offset = data.getInt("offset",0);
			if(msgid == 0 || length == 0){
				CustomLog.e("REQ_DOWNLOAD_VOICE 获取参数失败!!!");
				break;
			}
			clientMsgID = data.getString("clientMsgID");
			IUCSMessageRequest updownVoiceRequest = new IGGDownloadVoiceRequest(
					clientMsgID, msgid,	length, offset);
			CustomLog.e("IGGDownloadVoiceRequest 参数 clientMsgID:" + clientMsgID + " msgid:" + msgid + " length:" + length + " offset:" + offset);
			updownVoiceRequest.onSendMessage();
			break;
		case REQ_CREATE_GROUP:
			data = msg.getData();
			List<String> memberList = data.getStringArrayList("memberList");
			String discussionName = data.getString("discussionName");
			if(memberList == null || discussionName == null){
				CustomLog.e("REQ_CREATE_GROUP 获取参数失败!!!");
				break;
			}
			IUCSMessageRequest createGroupRequest = new IGGCreateGroupRequest(
					discussionName, memberList);
			createGroupRequest.onSendMessage();
			break;
		case REQ_ADD_GROUP_MEMBER:
			data = msg.getData();
			List<String> addmemberList = data.getStringArrayList("memberList");
			discussionID = data.getString("discussionID");
			if(addmemberList == null || discussionID == null){
				CustomLog.e("REQ_ADD_GROUP_MEMBER 获取参数失败!!!");
				break;
			}
			IUCSMessageRequest addMemberRequest = new IGGAddGroupMemberRequest(
					discussionID, addmemberList);
			addMemberRequest.onSendMessage();
			break;
		case REQ_DEL_GROUP_MEMBER:
			data = msg.getData();
			discussionID = data.getString("discussionID");
			List<String> delmemberList = data.getStringArrayList("memberList");
			if(delmemberList == null || discussionID == null){
				CustomLog.e("REQ_DEL_GROUP_MEMBER 获取参数失败!!!");
				break;
			}
			IUCSMessageRequest delMemberRequest = new IGGDelGroupMemberRequest(
					discussionID, delmemberList);
			delMemberRequest.onSendMessage();
			break;
		case REQ_QUIT_GROUP:
			data = msg.getData();
			discussionID = data.getString("discussionID");
			if(discussionID == null){
				CustomLog.e("REQ_QUIT_GROUP 获取参数失败!!!");
				break;
			}
			IUCSMessageRequest quitGroupRequest = new IGGQuitGroupRequest(discussionID);
			quitGroupRequest.onSendMessage();
			break;
			
		case REQ_SENDMSG_TIMEROUT:
			chatMessage = (ChatMessage) msg.obj;
			CustomLog.d("发送消息超时:"+chatMessage.getContent());
			chatMessage.setSendStatus(ChatMessage.MSG_STATUS_TIMEOUT);
			dbManager.updataMsgStatusAndMsgID(chatMessage, 
					chatMessage.getMsgid());
			//通知APP消息状态改变
			imlManager.notifiSendMsgListener(chatMessage);
			break;
		default:
			break;
		}
	}
}
