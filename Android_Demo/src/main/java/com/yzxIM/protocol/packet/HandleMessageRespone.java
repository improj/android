package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.os.Handler;
import android.text.TextUtils;

import com.JRMange;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.IMListenerManager;
import com.yzxIM.protocol.packet.PacketData.ENMMDataType;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxIM.tools.XmlUtils;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.FileTools;
import com.yzxtcp.tools.StringUtils;

public class HandleMessageRespone {
	private static ConversationInfo cinfo;

	private static DBManager dbManager = DBManager.getInstance();
	private static IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	private static IMManager imManager = IMManager.getInstance(null);
	
	//发送消息响应出来函数
	public static void onMsgRespone(int iMsgId, String pcClientMsgId, 
												int iCTime, int iRet) {
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		
		CustomLog.d("msgid:" + iMsgId + " pcClientMsgId" + pcClientMsgId);
		ChatMessage msg = IMUserData.mapGetMsg(pcClientMsgId);
		if (msg == null) {
			CustomLog.d("获取发送消息的 MSG失败!");
			return;
		}
		IMUserData.mapDelMsg(pcClientMsgId);
		// 接收到消息响应后取消定时器
		removeSendMsgTimeOut(msg);
		String sendTime = iCTime+"000";
		msg.setSendTime(Long.parseLong(sendTime));
		cinfo = dbManager.getConversation(msg.getParentID());
		if (iRet == 0) {
			msg.setSendStatus(ChatMessage.MSG_STATUS_SUCCESS);
		} else {
			msg.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
		}
		//更新会话时间
		if (cinfo != null) {
			cinfo.setLastTime(msg.getSendTime());
			dbManager.updateConversationLTimeAndDMsg(
					msg.getParentID(), msg.getSendTime() + "", 
					cinfo.getDraftMsg(), false);
			recvCinfos.add(cinfo);
			CustomLog.d("onMsgRespone COVUpdate");
			imListenerManager.notifyICovListener(
					IMListenerManager.COVUpdate, recvCinfos);
		}
		CustomLog.d("MsgID:" + msg.getMsgid()+"  sendTime:"+sendTime);
		// 更新消息状态
		IMListenerManager.getInstance().notifiSendMsgListener(msg);
		DBManager.getInstance()
				.updataMsgStatusAndTime(msg, msg.getMsgid());
	}
	//接收到消息处理函数
	@SuppressWarnings("finally")
	public static boolean handleChatMessage(String[] msg) {
		String targetId;
		ChatMessage chatMessage = null;
		List<ChatMessage> recvMsgs = new ArrayList<ChatMessage>();
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		ENMMDataType msgtype;
		int msgid = 0;
		int length = 0;
		String showMsg = "";
		boolean isok = false;
		
		dbManager.beginTransaction();
		try {
			for (int i = 0; i < msg.length; i++) {
				String dbMsg[] = msg[i].split("\1\2");
				// debugmsg(dbMsg);
				CategoryId categoryId = dbManager.getCategoryId(dbMsg[2]);
				targetId = dbManager.formatTargetID(dbMsg[2]);
				msgtype = ENMMDataType.valueof(Integer.valueOf(dbMsg[1]));
				CustomLog.d("new targetID:" + targetId + "  msgtype:"
						+ msgtype);

				chatMessage = dbManager.initChatMessage(dbMsg);
				if(chatMessage == null){
					CustomLog.d("未知会话类型");
					continue;
				}
				msgid = Integer.valueOf(dbMsg[0]);
				if (msgtype == ENMMDataType.MM_DATA_IMG) {// 图片消息
					String vPath = "";
					String vLength = "";
					try{
						String str = dbMsg[7].replace("&", "!!!!!!");
						Document document = XmlUtils.getXml(str);
						Element root = document.getDocumentElement();
						NodeList msgs = root.getChildNodes();
						Node imagemsg = msgs.item(0);
						vLength = imagemsg.getAttributes()
								.getNamedItem("length").getNodeValue();
						vPath = imagemsg.getAttributes()
								.getNamedItem("cdnbigimgurl").getNodeValue();
						vPath = vPath.replace("!!!!!!", "&");
					}catch (Exception e){
						CustomLog.e("图片error:"+e.getMessage());
					}
					chatMessage.setContent(dbMsg[10]); // 重新设置小图路径
					chatMessage.setPath(vPath);
					showMsg = "[图片]";
				} else if (msgtype == ENMMDataType.MM_DATA_VOICEMSG) {// 语音消息
					String vLength = "";
					String voicelength = "";
					String clientmsgid = "";
					try{
						Document document = XmlUtils.getXml(dbMsg[7]);
						Element root = document.getDocumentElement();
						NodeList msgs = root.getChildNodes();
						Node voicemsg = msgs.item(0);
						vLength = voicemsg.getAttributes()
								.getNamedItem("length").getNodeValue();
						voicelength = voicemsg.getAttributes()
								.getNamedItem("voicelength").getNodeValue();
						clientmsgid = voicemsg.getAttributes()
								.getNamedItem("clientmsgid").getNodeValue();
						
					}catch(Exception e){
						CustomLog.e("语音error:"+e.getMessage());
					}
					length = Integer.valueOf(vLength);
					chatMessage.setContent(voicelength); // 语音消息长度
					CustomLog.d("voicelength:" + voicelength);
					chatMessage.setSendStatus(ChatMessage.MSG_STATUS_INPROCESS);
					String filedir = chatMessage.getParentID();
					FileTools.createAudioFileName(filedir);
					showMsg = "[语音:"+voicelength+"秒]";
					JSONObject jpath = new JSONObject();
					jpath.put("msgid", clientmsgid);
					jpath.put("flen", length);
					chatMessage.setPath(jpath.toString());
					if(IMManager.getInstance(null).downloadVoice(chatMessage) == false){
						chatMessage.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
					}
				}else if (msgtype == ENMMDataType.MM_DATA_LOCATION){
					try {
						chatMessage.setContent(dbMsg[10]); // 重新缩略图路径
						chatMessage.setExtMessage(dbMsg[7]);
						showMsg = "[位置]";
					} catch (Exception e) {
						CustomLog.e("位置error:"+e.getMessage());
					}
					
				}else if(msgtype == ENMMDataType.MM_DATA_CUSTOMMSG){
					try {
						final JSONObject jsonObject = new JSONObject(chatMessage.getContent());
						String fileType = jsonObject.optString("ucsMsgType");
						if (fileType.equals(JRMange.selectSoundFlag)) {
							showMsg = "[通话]";
						}
						if (fileType.equals(JRMange.selectVideoChatFlag)) {
							showMsg = "[视频通话]";
						}
						if (fileType.equals(JRMange.sendVideoFlag)) {
							showMsg = "[视频]";
						}
						if (fileType.equals(JRMange.sendfileFlag)) {
							String fileName = jsonObject.optString("fileName");
							if (fileName.contains(".jpg") || fileName.contains(".JPG") || fileName.contains(".png") ||
									fileName.contains(".PNG") || fileName.contains(".jpeg") || fileName.contains(".JPEG")
									|| fileName.contains(".bmp") || fileName.contains(".BMP")) {
								showMsg = "[图片]";
							} else {
								showMsg = "[文件]";
							}
						}
					} catch (Exception e) {
						showMsg = "[自定义消息]";
					}
				}else{//未知消息按文本消息处理
					showMsg = chatMessage.getContent();
				}
				
				String name[] = dbMsg[5].split("\\+",2);
				if(name.length == 2){
					chatMessage.setSenderId(name[0]);
					chatMessage.setNickName(name[1]);
				}else{
					int a = dbMsg[5].indexOf("\\+");
					if(a >= 0){
						chatMessage.setSenderId(dbMsg[5].substring(0, a));
					}
					//没有昵称  NickName设置为senderID
					chatMessage.setNickName(chatMessage.getSenderId());
				}
				
				dbManager.insertChatMsgToDB(chatMessage);
				recvMsgs.add(chatMessage);
				
				// 判断会话是否存在
				if (dbManager.isConversationExisit(targetId) == false) {
					CustomLog.d("会话不存在 创建会话表 targetId:" + targetId);
					String converTitle = null;
					switch(categoryId){
					case PERSONAL:
						converTitle = chatMessage.getNickName();
						break;
					case DISCUSSION:
						DiscussionInfo di = dbManager.getDiscussionInfo(
												chatMessage.getParentID());
						if(di != null){
							converTitle = di.getDiscussionName();
						}
						break;
					case GROUP:
						converTitle = chatMessage.getParentID();
						break;
					case BROADCAST:
						converTitle = "服务器广播消息";
						break;
					case NONE:
						converTitle = "未知会话";
						break;
					 default:
						 converTitle = chatMessage.getSenderId();
						 break;
					}
					CustomLog.d("converTitle:"+converTitle);
					cinfo = dbManager.insertConversationToDb(targetId,
							categoryId, showMsg, converTitle, 1,chatMessage.getSendTime());
					/*if (cinfo != null) {
						imListenerManager.notifyICovListener(
								IMListenerManager.COVCreate, cinfo);
					} else {
						CustomLog.d("cinfo is null");
					}*/
					addToCinfoList(recvCinfos, cinfo);
				} else {
					dbManager.updateConversationLTimeAndDMsg(
							targetId, chatMessage.getSendTime() + "", showMsg, true);
					
					ConversationInfo cinfo = dbManager
							.getConversation(targetId);
					if(categoryId == CategoryId.PERSONAL&&
							!StringUtils.isEmpty(chatMessage.getNickName())){
						cinfo.setConversationTitle(chatMessage.getNickName());
					}
					/*if (cinfo != null) {
						imListenerManager.notifyICovListener(
								IMListenerManager.COVUpdate, cinfo);
					}*/
					addToCinfoList(recvCinfos, cinfo);
				}

			}
			dbManager.setTransactionSuccessful();
			imListenerManager.notifiRecvMsgListener(recvMsgs);
			CustomLog.d("收到"+recvCinfos.size()+"条会话的消息");
			imListenerManager.notifyICovListener(
					IMListenerManager.COVUpdate, recvCinfos);
			isok = true;
		}catch (Exception e) {
			isok = false;
			CustomLog.e("handleChatMessage:error   "+e.getMessage());
		}finally {
			dbManager.endTransaction();
			return isok;
		}
	}
	//讨论组信息处理函数
	@SuppressWarnings("finally")
	public static boolean handleGroupMessage(String[] msg) {
		boolean isok = false;
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		dbManager.beginTransaction();
		try{
		  for (int i = 0; i < msg.length; i++) {
			  if(TextUtils.isEmpty(msg[i])){
				  CustomLog.d("已经到达最后");
				  break;
			  }
			//讨论组信息+成员
			String msggroup[] = msg[i].split("\2\3\4");// 讨论组信息,,,,,讨论组成员信息
			//讨论组信息
			String groupMsg[] = msggroup[0].split("\3\4\5");
			// username:nickname:roommembercount
			DiscussionInfo di = new DiscussionInfo();
			String discussionId = dbManager.formatTargetID(groupMsg[0]);
			StringBuilder members = new StringBuilder();

			for (int j = 1; j < msggroup.length; j++) {
				String dbMsg[] = msggroup[j].split("\3\4\5");
				// memberName:nickName
				if (j == 1) {// 讨论组成员第一个为创建者
					di.setOwnerId(dbMsg[0]);
				}
				members.append(dbMsg[0]);
				if (j < msggroup.length - 1) {
					members.append(",");
				}
			}
			
			if (dbManager.isDiscussionInfoExisit(discussionId) == false) {
				CustomLog.d("讨论组信息不存在 创建discussionId:"+discussionId);
				di.setDiscussionId(discussionId);
				di.setDiscussionName(groupMsg[1]);
				di.setMemberCount(Integer.valueOf(groupMsg[2]));
				di.setCategoryId(CategoryId.DISCUSSION);
				di.setCreateTime(System.currentTimeMillis());
				//服务器可能返回成员为0 的讨论组信息，为了规避这种错误 这里加判断
				if(di.getMemberCount() == 0){
					CustomLog.e("讨论组:"+discussionId+"成员数为0");
					continue;
				}
				
				
				di.setDiscussionMembers(members.toString());
				// 添加讨论组信息到数据库
				dbManager.addDiscussionInfo(di);

			} else {
				dbManager.updateDiscussionMemlist(discussionId,
						members.toString(), Integer.valueOf(groupMsg[2]),2);
				dbManager.updateDiscussionName(discussionId, groupMsg[1]);
				dbManager.updateConversationTitle(discussionId, groupMsg[1]);
				
				if (null == imManager) {
					CustomLog.d("handleGroupMessage imManager is null");
					isok = false;
					break;
				}
			}
			
			cinfo = imManager.getConversation(discussionId);
			if (null != cinfo) {
				cinfo.setConversationTitle(groupMsg[1]);
				recvCinfos.add(cinfo);
				/*imListenerManager.notifyICovListener(
						IMListenerManager.COVUpdate, cinfo);*/
			}
			
			// 被动 创建不需要创建讨论组回调
		  }
		  dbManager.setTransactionSuccessful();
		  isok = true;
		}catch (Exception e) {
			e.printStackTrace();
			CustomLog.e("handleGroupMessage:error   "+e.getMessage());
			isok = false;
		}finally{
			dbManager.endTransaction();
			if(recvCinfos.size() > 0){
				CustomLog.d("handleGroupMessage COVUpdate");
				imListenerManager.notifyICovListener(
					IMListenerManager.COVUpdate, recvCinfos);
			}
			
			return isok;
		}
	}
	
	//自己被讨论组踢出
	public static void handleGroupSelfInfo(String targetID) {
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		String msgs[] = targetID.split(":" , 2);
		String discussionID = dbManager.formatTargetID(msgs[0]);
		ConversationInfo cinfo = imManager.getConversation(discussionID);
		String title = null;
		if(msgs.length >= 2){
			title = msgs[1];
		}else{
			title = discussionID;
		}
		if (cinfo != null) {
			dbManager.updateConversationTitle(discussionID, title);
			recvCinfos.add(cinfo);
			CustomLog.d("handleGroupSelfInfo COVUpdate");
			imListenerManager.notifyICovListener(
					IMListenerManager.COVUpdate, recvCinfos);
		}
		dbManager.delDiscussionInfo(discussionID);
	}
	
	
	//移除发送超时定时器
	private static void removeSendMsgTimeOut(ChatMessage msg){
		Handler handler = IMManager.getInstance(null).getMsgHandler();
		handler.removeMessages(RequestCmd.REQ_SENDMSG_TIMEROUT.ordinal(), msg);
		CustomLog.d("取消发送消息定时器");
	}
	
	
	/**
	 * @author zhangbin
	 * @2016-1-5
	 * @param cinfos 保存的会话列表
	 * @param cinfo 新加入的会话
	 * @descript:去冗， 当会话里已有该会话就去掉之前的加入新的
	 */
	private static void addToCinfoList(List<ConversationInfo> cinfos,
							ConversationInfo cinfo){
		boolean isFind = false;
		
		for(int i=0; i < cinfos.size(); i++){
			if(cinfos.get(i).getTargetId().equals(cinfo.getTargetId())){
				cinfos.remove(i);
				cinfos.add(cinfo);
				isFind = true;
				break;
			}
		}
		
		if(isFind == false){
			cinfos.add(cinfo);
		}
	}
}
