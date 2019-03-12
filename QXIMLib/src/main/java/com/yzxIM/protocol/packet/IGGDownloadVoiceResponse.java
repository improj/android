package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yzxIM.IMManager;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

//下载语音应答
public class IGGDownloadVoiceResponse extends IGGBaseResponse {
	public int iMsgId; // 从服务器得到的msgId
	public int iOffset; // 起始位置
	public int iLength; // 实际发送的长度
	public int iVoiceLength; // 播放长度
	public String pcClientMsgId; // 从服务器得到的ClientMsgId
	public byte[] tData; // 语音BUFF
	public int iEndFlag; // 结束标志
	public int iCancelFlag; // 取消标志
	public String voiceDir; // 保存路径
	
	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		DBManager dbManager = DBManager.getInstance();
		ChatMessage msg = IMUserData.mapGetMsg(iMsgId + "");
		if(msg == null){
			CustomLog.e("IGGDownloadMsgImgResponse 获取发送消息 MSG 失败");
			return;
		}else if (iEndFlag == 1)
		{
			IMUserData.mapDelMsg(iMsgId+"");
			// 接收到消息响应后取消定时器
			Handler handler = IMManager.getInstance(null).getMsgHandler();
			handler.removeMessages(RequestCmd.REQ_SENDMSG_TIMEROUT.ordinal(), msg);
		}

		if (base_iRet == 0 && iEndFlag == 1) {
			
			msg.setPath(voiceDir);
			msg.setMsgType(MSGTYPE.MSG_DATA_VOICE);
//			List<ChatMessage> recvMsgs = new ArrayList<ChatMessage>();
			msg.setSendStatus(ChatMessage.MSG_STATUS_SUCCESS);
			IMListenerManager.getInstance().notifiSendMsgListener(msg);
			DBManager.getInstance().updataVoiceMsg(msg);
			
			CustomLog.d("voiceDir:"+voiceDir);
			// 判断会话是否存在
			String targetId = msg.getParentID();
			if (dbManager.isConversationExisit(targetId) == false) {
				CustomLog.d("会话不存在 创建会话表 targetId:"+targetId);
				
				ConversationInfo cinfo = dbManager.insertConversationToDb(
						targetId, msg.getCategoryId(),
						"[语音:"+msg.getContent()+"秒]",null, 1,msg.getSendTime());
				if (cinfo != null) {
					recvCinfos.add(cinfo);
					/*imListenerManager.notifyICovListener(
							IMListenerManager.COVCreate, cinfo);*/
				} else {
					CustomLog.d("cinfo is null");
				}

			} else {
				CustomLog.d("会话已存在");
				//通知会话有更新 如果语音之后已经有消息接收，就不需要更新
				long lastTime = dbManager.getConversationLastTime(targetId);
				CustomLog.d("lastTime:"+lastTime+" recvTime:"+ msg.getSendTime());
				if(lastTime > msg.getSendTime()) //会话被别的消息更新了
				{
//					dbManager.updateConversationMsgUnRead(targetId);
				}else{
					int num = dbManager.updateConversationLTimeAndDMsg(
							targetId, msg.getSendTime()+"","[语音:"+msg.getContent()+"秒]",false);
					CustomLog.d("更新语音会话消息num:"+num+"个");
					ConversationInfo cinfo = dbManager.getConversation(targetId);
					if(cinfo != null){
						recvCinfos.add(cinfo);
//						imListenerManager.notifyICovListener(IMListenerManager.COVUpdate, cinfo);
					}
				}
				
			}
			/*dbManager.insertChatMsgToDB(msg);
			recvMsgs.add(msg);
			IMListenerManager.getInstance().notifiRecvMsgListener(recvMsgs);*/
			
		} 
		else if (base_iRet == 0 && iEndFlag == 0) {
			int lengthFollowup = 0;//再次请求的长度
			JSONObject jpath;
			try {
				jpath = new JSONObject(msg.getPath());
				if(jpath.has("flen")){
					lengthFollowup =  jpath.getInt("flen");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle data = new Bundle();
			data.putInt("msgid", iMsgId);
			data.putInt("length", lengthFollowup);
			data.putString("clientMsgID", pcClientMsgId);
			data.putInt("offset", iOffset + iLength);//请求起始位置
			Message msgFollowup = IMManager.getInstance(null).getMsgHandler().obtainMessage(RequestCmd.REQ_DOWNLOAD_VOICE.ordinal());
			msgFollowup.setData(data);
			IMManager.getInstance(null).getMsgHandler().sendMessage(msgFollowup);
		}
		else {
			CustomLog.e("下载语音失败 base_iRet =" + base_iRet + " iEndFlag" + iEndFlag);
			CustomLog.e("下载语音失败 tErrMsg=" + tErrMsg);
			msg.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
			IMListenerManager.getInstance().notifiSendMsgListener(msg);
			DBManager.getInstance()
					.updataMsgStatusAndMsgID(msg, msg.getMsgid());
			
			ConversationInfo cinfo = dbManager.getConversation(msg.getParentID());
			if(cinfo != null){
				recvCinfos.add(cinfo);
			}
		}
		if(recvCinfos.size() > 0){
			CustomLog.d("IGGDownloadVoiceResponse COVUpdate");
			imListenerManager.notifyICovListener(IMListenerManager.COVUpdate, recvCinfos);
		}
		
	}
}
