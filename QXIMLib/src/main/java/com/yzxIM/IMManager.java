package com.yzxIM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.bither.util.NativeUtil;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.data.db.UserInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxIM.listener.IConversationListener;
import com.yzxIM.listener.IMListenerManager;
import com.yzxIM.listener.MessageListener;
import com.yzxIM.listener.OnResetIMListener;
import com.yzxIM.listener.RecordListener;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxIM.tools.ConversationSortByTime;
import com.yzxIM.tools.DownloadThread;
import com.yzxIM.tools.DownloadTools;
import com.yzxIM.tools.RecordingTools;
import com.yzxIM.tools.CacheManager;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ISdkStatusListener;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;

/**IM 接口管理类
 * 
 * 所有IM接口通过此类提供
 */
public class IMManager {
	private static IMManager imManager;
	private IMHandler handler;
	private DBManager dbManager;
	
	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	private IMManager(Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() { 	// 循环处理消息 
				Looper.prepare();
				handler = new IMHandler();
				Looper.loop();
			}
		}, "handleMessageThread").start();

		dbManager = DBManager.getInstance();

		imListenerManager = IMListenerManager.getInstance();
		
		IMUserData.mapResetMsg();
		//安装TCP消息回调接口
		UCSManager.setTcpRecvListener(ITcpRecvListener.IMSDK, new IMTCPRecv());
		UCSManager.setLoginListener(new IMLoginCallBack());
		UCSManager.setReLoginListener(new IMReLoginCallBack());
	}

	/**
	 * 获取IMManager 对象
	 * 
	 * @param context 应用上下文
	 * @return  IMManager 对象
	 */
	public static IMManager getInstance(Context context) {
		if (imManager == null &&context != null) {
			synchronized (IMManager.class) {
				if (imManager == null) {
					imManager = new IMManager(context);
				}
			}
		}

		return imManager;
	}

	private void createAndSendMessage(int msgWhat, Bundle data) {
		Message msg = handler.obtainMessage(msgWhat);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	/**
	 * 获取SDK版本号
	 * 
	 * @return SDK版本字符串
	 */
	public String getSDKVersion() {
		String version = "3.0.4.6";

		return version;
	}

	/**
	 * 发送一条消息
	 * 
	 * @param msg 消息对象
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean sendmessage(ChatMessage msg) {
		if (msg == null ||
				!msg.isValidMessage()) {
			CustomLog.e("发送消息不合法");
			msg.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
			IMListenerManager.getInstance().notifiSendMsgListener(msg);
			return false;
		}
		msg.sendMessage(handler);
		return true;
	}
	
	public Handler getMsgHandler(){
		return handler;
	}
	
	/**
	 * 创建讨论组
	 * 
	 * @param GroupName 讨论组名字
	 * @param memberList 讨论组成员列表
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean createDiscussionGroup(String discussionName,
			List<String> memberList) {
		if (StringUtils.isEmpty(discussionName)
				|| memberList == null
				|| memberList.size() < 1) {
			CustomLog.e("discussionName 或者 memberList为null");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGCreate,
					new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR)
					.setMsg("discussionName 或者 memberList为null"), null);
			return false;
		}
		Bundle data = new Bundle();
		data.putString("discussionName", discussionName);
		data.putStringArrayList("memberList", (ArrayList<String>) memberList);

		createAndSendMessage(RequestCmd.REQ_CREATE_GROUP.ordinal(), data);
		return true;
	}

	/**
	 * 添加讨论组成员
	 * 
	 * @param discussionID 讨论组ID
	 * @param memberList 将添加的成员列表
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean addDiscussionGroupMember(String discussionID,
			List<String> memberList) {
		if (StringUtils.isEmpty(discussionID) == true 
				|| memberList == null
				|| memberList.size() < 1) {
			CustomLog.e("discussionID 或者 memberList为null");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGAddMem,
					new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR)
					.setMsg("discussionName 或者 memberList为null"), null);
			return false;
		}
		Bundle data = new Bundle();
		data.putString("discussionID", discussionID);
		data.putStringArrayList("memberList", (ArrayList<String>) memberList);

		createAndSendMessage(RequestCmd.REQ_ADD_GROUP_MEMBER.ordinal(), data);

		return true;
	}

	/**
	 * 删除讨论组成员
	 * 
	 * @param discussionID 讨论组ID
	 * @param memberList 将删除的成员列表
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean delDiscussionGroupMember(String discussionID,
			List<String> memberList) {
		if (StringUtils.isEmpty(discussionID) == true 
				|| memberList == null
				|| memberList.size() < 1) {
			CustomLog.e("discussionID 或者 memberList为null");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGDelMem,
					new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR)
					.setMsg("discussionName 或者 memberList为null"), null);
			return false;
		}
		Bundle data = new Bundle();
		data.putString("discussionID", discussionID);
		data.putStringArrayList("memberList", (ArrayList<String>) memberList);

		createAndSendMessage(RequestCmd.REQ_DEL_GROUP_MEMBER.ordinal(), data);

		return true;
	}

	/**
	 * 退出讨论组
	 * 
	 * @param discussionID 讨论组ID
	 * @param tUserName 退出用户的用户名
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean quitDiscussionGroup(String discussionID) {
		if (StringUtils.isEmpty(discussionID) == true) {
			CustomLog.e("discussionID 错误");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGQuit,
					new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR)
					.setMsg("discussionName 或者 memberList为null"), null);
			return false;
		}
		Bundle data = new Bundle();
		data.putString("discussionID", discussionID);

		createAndSendMessage(RequestCmd.REQ_QUIT_GROUP.ordinal(), data);

		return true;
	}

	/**
	 * 修改讨论组名称
	 * 
	 * @param discussionID 讨论组ID号
	 * @param title 讨论组标题名称
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean modifyDiscussionTitle(String discussionID, String title){
		if (StringUtils.isEmpty(discussionID) == true ||
				StringUtils.isEmpty(title) == true) {
			CustomLog.e("discussionID 错误 或 title is null");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGModifyName,
					new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR)
					.setMsg("discussionID 或者 title为null"), null);
			return false;
		}
		Bundle data = new Bundle();
		data.putString("discussionID", discussionID);
		data.putInt("seletor", 4);
		data.putInt("Scene", 7);
		data.putString("chatroomTopic", title);
		
		createAndSendMessage(RequestCmd.REQ_NEW_SYNC.ordinal(), data);
		return true;
	}
	
	public void newSynMessage(){
		Bundle data = new Bundle();
		data.putString("discussionID", "");
		data.putInt("seletor", 6);
		data.putInt("Scene", 1);
		data.putString("chatroomTopic", "");
		createAndSendMessage(RequestCmd.REQ_NEW_SYNC.ordinal(), data);
	}
	/**
	 * 下载语音
	 * 
	 * @param chatMessage 语音消息对象
	 * @return 命令调用是否成功(主要是校验参数)
	 * @descript:下载语音
	 */
	public boolean downloadVoice(ChatMessage chatMessage){
		if(StringUtils.isEmpty(chatMessage.getMsgid())||
				StringUtils.isEmpty(chatMessage.getPath())){
			CustomLog.e("downloadVoice 参数错误");
			return false;
		}
		if(IMUserData.mapGetMsgById(chatMessage.getMsgid()) != null){
			CustomLog.e("voice is downing");
			return false;
		}
		try {
			JSONObject jpath  = new JSONObject(chatMessage.getPath());
			if(jpath.has("msgid")&&jpath.has("flen")){
				IMUserData.mapSaveMsg(chatMessage.getMsgid(), chatMessage);
				Bundle data = new Bundle();
				data.putInt("msgid", Integer.valueOf(chatMessage.getMsgid()));
				data.putInt("length", jpath.getInt("flen"));
				data.putString("clientMsgID", jpath.getString("msgid"));
				data.putInt("offset", 0);//第一个请求起始位置为0
				createAndSendMessage(RequestCmd.REQ_DOWNLOAD_VOICE.ordinal(), data);
				
				Message msg = handler.obtainMessage(RequestCmd.REQ_SENDMSG_TIMEROUT.ordinal());
				msg.obj = chatMessage;
				handler.sendMessageDelayed(msg, 60000);
				return true;
			}else{
				return false;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 获取所有会话列表
	 * 
	 * @return 会话列表
	 */
	public List<ConversationInfo> getConversationList() {
		return dbManager.getConversationList();
	}

	/**
	 * 获取指定targetId的会话记录
	 * 
	 * @param targetId 会话id
	 * @return 指定targetId的会话记录
	 */
	public ConversationInfo getConversation(String targetId) {
		if(StringUtils.isEmpty(targetId)){
			CustomLog.e("getConversation 参数错误 ");
			return null;
		}
		return DBManager.getInstance().getConversation(targetId);
	}

	
	/**
	 * 获取指定会话类型的会话列表
	 * 
	 * @param categroy 会话类型(单聊，群聊，讨论组)
	 * @return 会话列表
	 */
	public List<ConversationInfo> getConversationList(CategoryId categroy){
		if(categroy!=CategoryId.NONE){
			return DBManager.getInstance().getConversation(categroy);
		}else{
			CustomLog.e("getConversationList 参数错误 category:"+categroy);
			return new ArrayList<ConversationInfo>();
		}
		
	}
	
	/**
	 * 排序会话列表 主要用于置顶功能排序
	 * 
	 * @param cinfos 需要排序的会话列表
	 * @return 按时间排序后的列表 
	 */
	@SuppressWarnings("unchecked")
	public void sortConversationList(List<ConversationInfo> cinfos){
		if(cinfos == null  || cinfos.size()<2){
			CustomLog.e("sortConversationList 参数错误!!!");
			return ;
		}
		Collections.sort(cinfos, new ConversationSortByTime());
	}
	
	/*public int getTotalUnreadCount(byte[] targetId) {
		int unReadCount = 0;

		return unReadCount;

	}*/

	
	/**
	 * 清除所有会话列表
	 * 
	 * @return 清除的会话记录数
	 */
	public int clearAllConversations() {
		return dbManager.clearAllConversations();
	}

	/**
	 * 删除指定会话
	 * 
	 * @param cinfo 要删除的会话对象
	 * @return 返回成功删除会话的条数,0表示删除失败
	 */
	public int delConversationInfo(ConversationInfo cinfo){
		if(cinfo == null){
			return 0;
		}
		return cinfo.delConversationInfo();
	}
	/**
	 * 获取指定用户名的用户信息 暂时未用
	 * 
	 * @param userName 用户名
	 * @return 用户信息
	 */
	public UserInfo getUserInfo(String userID) {
		if (StringUtils.isEmpty(userID)) {
			CustomLog.e("userID 参数错误");
			return null;
		}
		UserInfo userInfo = null;

		return userInfo;
	}

	/**
	 * 清空指定会话的所有聊天记录
	 * 
	 * @param cinfo 会话的对象
	 * @return 命令调用是否成功(主要是校验参数)
	 */
	public boolean clearMessages(ConversationInfo cinfo) {
		if(cinfo == null){
			CustomLog.e("clearMessages 参数错误!!!");
			return false;
		}
		cinfo.clearMessages();
		return true;
	}

	/**
	 * 获取当前会话指定索引的消息列表。
	 *
	 * @param cinfo          会话对象
	 * @param startPos     从最新消息开始索引，0表示最新，依次递增消息越早。
	 * @param count        要获取的消息数量。
	 * @return                  查询到的消息列表。
	 */
	public List<ChatMessage> getLastestMessages(ConversationInfo cinfo,
			int startPos, int count) {
		if(cinfo == null || startPos < 0 || count < 0){
			CustomLog.e("getLastestMessages 参数错误!!!");
			return new ArrayList<ChatMessage>();
		}
		return cinfo.getLastestMessages(startPos, count);
	}

	/**
	 * 获取当前会话指定索引的消息列表。
	 *
	 * @param cinfo          会话对象
	 * @param startPos     从最早消息开始索引，0表示最早，依次递增消息越新。
	 * @param count        要获取的消息数量。
	 * @return                  查询到的消息列表。
	 */
	public List<ChatMessage> getHistroyMessages(ConversationInfo cinfo,
			int startPos, int count) {
		if(cinfo == null || startPos < 0 || count < 0){
			CustomLog.e("getHistroyMessages 参数错误!!!");
			return new ArrayList<ChatMessage>();
		}
		return cinfo.getHistroyMessages(startPos, count);
	}

	/**
	 * 获取当前会话所有消息。
	 *
	 * @param cinfo          会话对象
	 * @return             查询到的消息列表。
	 */
	public List<ChatMessage> getAllMessage(ConversationInfo cinfo) {
		if(cinfo == null){
			CustomLog.e("getAllMessage 参数错误!!!");
			return new ArrayList<ChatMessage>();
		}
		return cinfo.getAllMessage();
	}

	/**
	 *  获取指定状态的所有消息
	 *  
	 * @param cinfo          会话对象
	 * @param status         消息状态
	 * @return                   状态为status所指状态的所有消息
	 */
	public List<ChatMessage> getMessagesFromStatus(ConversationInfo cinfo,int status){
		if (cinfo == null) {
			CustomLog.e("getMessagesFromStatus 参数错误");
			return null;
		}
		return cinfo.getMessagesFromStatus(status);
	}
	
	/**
	 * 获取指定会话，指定消息id的消息
	 * 
	 * @param cinfo          会话对象
	 * @param msgId        消息ID
	 * @return                   对应的消息对象
	 */
	public ChatMessage getMessageFromMsgid(ConversationInfo cinfo, String msgId) {
		if (cinfo == null) {
			CustomLog.e("getMessageFromMsgid 参数错误");
			return null;
		}
		ChatMessage msg = null;
		msg = cinfo.getMessageFromMsgid(msgId);
		return msg;
	}

	/**
	 * 删除某会话的指定N条消息
	 * 
	 * @param cinfo          会话对象
	 * @param msgs          需删除的消息列表
	 * @return                   命令调用是否成功(主要是校验参数)
	 */
	public boolean deleteMessages(ConversationInfo cinfo, List<ChatMessage> msgs) {
		if(cinfo == null ||
				msgs == null || msgs.size() == 0){
			CustomLog.e("deleteMessages 参数错误!!!");
			return false;
		}
		cinfo.deleteMessages(msgs);
		return true;
	}

	/**
	 * 获取指定会话的未读消息数
	 * 
	 * @param cinfo          会话对象
	 * @return                   指定会话的未读消息数
	 */
	public int getUnreadCount(ConversationInfo cinfo) {
		if(cinfo == null){
			CustomLog.e("getUnreadCount 参数错误!!!");
			return 0;
		}
		return cinfo.getUnreadCount();
	}

	/**
	 * 获取所有会话的未读消息数
	 * 
	 * @return 所有的未读消息数
	 */
	public int getUnreadCountAll(){
		return dbManager.getConversationMsgUnReadAll();
	}
	/**
	 * 清除指定会话的消息未读状态
	 * 
	 */
	public void clearMessagesUnreadStatus(ConversationInfo cinfo) {
		if(cinfo == null){
			CustomLog.e("clearMessagesUnreadStatus 参数错误!!!");
			return ;
		}
		cinfo.clearMessagesUnreadStatus();
	}
	/**
	 * 开始录音。
	 *
	 * @param savePath              录音文件保存路径，不能为空。
	 * @param recordListener      录音回调事件，不能为空。
	 */
	public  boolean startVoiceRecord(String filePath,RecordListener recordListener){
		if(StringUtils.isEmpty(filePath) || null == recordListener){
			CustomLog.e("startVoiceRecord 参数错误!!!");
			return false;
		}
		//E:/java/adt-bundle-windows-x86_64-20140702/platforms/android-22/android.jar
		return RecordingTools.getInstance().startVoiceRecord(filePath,recordListener);
	}
	
	/**
	 * 停止录音。
	 *
	 * 停止录音后，sdk会回调RecordListener的onFinishedRecordingVoice方法。
	 * 一般录音时长不超过60秒，开发者应该在60秒前调用stopVoiceRecord()停止录音。
	 */
	public  void stopVoiceRecord(){
		RecordingTools.getInstance().stopVoiceRecord();
	}
	
	/**
	 * 开始播放录音。
	 *
	 * @param filePath                 录音文件路径，不能为空。
	 * @param recordListener      录音回调事件，不能为空。
	 */
	public  void startPlayerVoice(String filePath,RecordListener recordListener){
		if(StringUtils.isEmpty(filePath) || null == recordListener){
			CustomLog.e("startPlayerVoice 参数错误!!!");
			return ;
		}
		RecordingTools.getInstance().startPlayerVoice(filePath, recordListener);
	}
	
	/**
	 * 下载附件
	 * 
	 * @param fileUrl:远程文件URL
	 * @param filePaht：本地文件路径
	 * @param msgId:消息ID
	 * @param fileListener：下载监听器
	 */
	public static DownloadThread downloadAttached(String fileUrl, String filePath,String msgId,MessageListener fileListener){
		if(StringUtils.isEmpty(fileUrl) 
				||StringUtils.isEmpty(filePath)
				||StringUtils.isEmpty(msgId)
				||fileListener == null  ){
			CustomLog.e("downloadAttached 参数错误!!!");
			return null;
		}
		return DownloadTools.downloadFile(fileUrl, filePath,msgId,fileListener);
	}
	
	/**
	 * 压缩图片并保存。
	 *
	 * @param src                     要压缩的图片源。
	 * @param quality                 压缩的图片质量(0-100)，100表示不压缩。
	 * @param fileName                压缩后图片保存路径。
	*/
	public static void compressBitmap(Bitmap bit, int quality, String fileName) {
		NativeUtil.compressBitmap(bit, quality, fileName, true);
	}
	
	/**
	 * 停止播放录音。
	 *
	 */
	public  void stopPlayerVoice(){
		RecordingTools.getInstance().stopPlayerVoice();
	}
	
	/**
	 * 获取指定讨论组id的讨论组信息
	 * 
	 * @param discussionID    讨论组ID号
	 * @return                         讨论组信息对象
	 */
	public DiscussionInfo getDiscussionInfo(String discussionID){
		if(discussionID == null){
			CustomLog.e("getDiscussionInfo 参数错误!!!");
			return null;
		}
		return dbManager.getDiscussionInfo(discussionID);
	}
	
	/**
	 * 获取所有讨论组信息
	 * 
	 * @return 讨论组信息列表
	 */
	public List<DiscussionInfo> getAllDiscussionInfos(){
		return dbManager.getAllDiscussionInfos();
	}
	//各种监听器的设置接口
	/**
	 * 设置消息收发监听器
	 * 
	 * @param cl 消息监听器 包括发送监听和接收监听
	 */
	public void setSendMsgListener(MessageListener cl){
		if(cl == null){
			CustomLog.e("setSendMsgListener 参数错误!!!");
			return ;
		}
		imListenerManager.setSendMsgListener(cl);
	}
	
	/**
	 * 移除消息收发监听器
	 * 
	 * @param cl            消息监听器 包括发送监听和接收监听
	 */
	public void removeSendMsgListener(MessageListener cl){
		if(cl == null){
			CustomLog.e("setSendMsgListener 参数错误!!!");
			return ;
		}
		imListenerManager.delSendMsgListenerList(cl);
	}
	/**
	 * 设置讨论组回调
	 * 
	 * @param callBack       讨论组回调包括创建 加人 踢人 退出监听
	 * @descript:
	 */
	public void setDiscussionGroup(DiscussionGroupCallBack callBack) {
		if(callBack == null){
			CustomLog.e("setDiscussionGroup 参数错误!!!");
			return ;
		}
		imListenerManager.setDiscussionGroup(callBack);
	}
	
	/**
	 * 设置会话状态监听器
	 * 
	 * @param listener 会话状态监听 包括创建，删除，更新
	 */
	public void setConversationListener(IConversationListener listener) {
		if(listener == null){
			CustomLog.e("setConversationListener 参数错误!!!");
			return ;
		}
		imListenerManager.setConversationListener(listener);
	}
	
	/**
	 * 设置SDK状态监听器
	 * 
	 * @param listener SDK状态监听
	 */
	public void setISdkStatusListener(ISdkStatusListener listener) {
		if(listener == null){
			CustomLog.e("setISdkStatusListener 参数错误!!!");
			return ;
		}
		UCSManager.setISdkStatusListener(listener);
	}
	/**
	 * 清除当前登录用户所有消息
	 * 
	 * @param onResetIMListener
	 */
	public void resetIM(OnResetIMListener onResetIMListener){
		String userId = IMUserData.getUserName();
		UcsReason uscReason = new UcsReason();
		if(TextUtils.isEmpty(userId)){
			if(onResetIMListener != null){
				uscReason.setReason(OnResetIMListener.RESET_REFUSE).setMsg("重置被拒绝");
				onResetIMListener.onResetResult(uscReason);
			}
			return;
		}
		//断开连接
		UCSManager.disconnect();
		//删除数据库
		CacheManager.obtain(YzxTCPCore.getContext()).deleteIMDatabases();
		//清除TCP、IM的缓存
		CacheManager.obtain(YzxTCPCore.getContext()).deleteIMSPF();
		//删除文件
		CacheManager.obtain(YzxTCPCore.getContext()).deleteSDCardCache();
		if(onResetIMListener != null){
			uscReason.setReason(OnResetIMListener.RESET_SUCCESS).setMsg("reset success...");
			onResetIMListener.onResetResult(uscReason);
		}
	}
}
