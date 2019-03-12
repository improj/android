package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGDownloadMsgImgResponse extends IGGBaseResponse {
	public int iMsgId; // 服务端产生的MSGID
	public String pcFromUserName; // 发起者
	public String pcToUserName; // 接收者
	public int iTotalLen; // 总大小
	public int iStartPos; // 起始位置（第一个为0）
	public String imageDir; // 图片路径

	
	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		if (base_iRet == 0) {
			
			ChatMessage msg = IMUserData.mapGetMsg(iMsgId + "");
			if(msg == null){
				CustomLog.e("IGGDownloadMsgImgResponse 获取发送消息 MSG 失败");
				return;
			}
			
			DBManager dbManager = DBManager.getInstance();
			
			msg.setPath(imageDir);
			List<ChatMessage> recvMsgs = new ArrayList<ChatMessage>();
			CustomLog.d("imageDir:"+imageDir);
			// 判断会话是否存在
			String targetId = msg.getSenderId();
			if (dbManager.isConversationExisit(targetId) == false) {
				CustomLog.d("会话不存在 创建会话表 targetId:"+targetId);
				ConversationInfo cinfo = dbManager.insertConversationToDb(
						targetId, msg.getCategoryId(),
						"图片",null, 1,msg.getSendTime());
				if (cinfo != null) {
					recvCinfos.add(cinfo);
//					imListenerManager.notifyICovListener(IMListenerManager.COVCreate, cinfo);
				} else {
					CustomLog.d("cinfo is null");
				}

			} else {
				CustomLog.d("会话已存在");
				int num = dbManager.updateConversationLTimeAndDMsg(
						targetId, msg.getSendTime()+"","图片",true);
				CustomLog.d("更新图片会话消息num:"+num+"个");
				//通知会话有更新
				ConversationInfo cinfo = dbManager.getConversation(targetId);
				if(cinfo != null){
					recvCinfos.add(cinfo);
//					imListenerManager.notifyICovListener(IMListenerManager.COVUpdate, cinfo);
				}
			}
			if(recvCinfos.size() > 0){
				imListenerManager.notifyICovListener(IMListenerManager.COVUpdate, recvCinfos);
			}
			
			dbManager.insertChatMsgToDB(msg);
			recvMsgs.add(msg);
			IMListenerManager.getInstance().notifiRecvMsgListener(recvMsgs);
		} else {
			CustomLog.e("下载图片失败 tErrMsg=" + tErrMsg);
		}

	}
}
