package com.yzxIM.data.db;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.yzxIM.data.CategoryId;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.FileTools;
import com.yzxtcp.tools.StringUtils;


/**
 * 会话类
 * 
 * @author zhuqian
 */
public class ConversationInfo implements Serializable{

	private static final long serialVersionUID = 2969169874448402107L;
	private int _id;
	private String targetId;  //单聊id,群组id,讨论组id
	private String conversationTitle; //会话标题
	private int categoryId;   //会话分类：1个人，2群组，3讨论组
	private String draftMsg="";  //草稿或者是最近的消息
	private boolean isTop;	//是否置顶
	private long lastTime;   //最后消息时间 
	private long topTime;	//置顶多久，默认是NULL
	private int msgUnRead;  //会话未读消息总数
	
	public ConversationInfo() {
	}
	
	public ConversationInfo(String targetId, String conversationTitle,
			CategoryId categoryId, String draftMsg, Boolean isTop, long lastTime,
			long topTime, int msgUnRead) {
		super();
		this.targetId = targetId;
		this.conversationTitle = conversationTitle;
		this.categoryId = categoryId.ordinal();
		this.draftMsg = draftMsg;
		this.isTop = isTop;
		this.lastTime = lastTime;
		this.topTime = topTime;
		this.msgUnRead = msgUnRead;
	}

	/**
	 * 获取targetId
	 * 
	 * @return
	 */
	public String getTargetId() {
		return  targetId;
	}

	/**
	 * 设置targetId
	 * 
	 * @param targetId
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	/**
	 * 获取会话title
	 * 
	 * @return
	 */
	public String getConversationTitle() {
		return conversationTitle;
	}
	/**
	 * 设置会话title
	 * 
	 * @param conversationTitle
	 */
	public void setConversationTitle(String conversationTitle) {
		this.conversationTitle = conversationTitle;
		if(StringUtils.isEmpty(getTargetId()) == false &&
				StringUtils.isEmpty(conversationTitle) == false){
			DBManager.getInstance().updateConversationTitle(
					getTargetId(), conversationTitle);
		}
		
	}

	/**
	 * 获取会话类型
	 * 
	 * @return
	 */
	public CategoryId getCategoryId() {
		return CategoryId.valueof(categoryId);
	}

	/**
	 * 设置会话类型
	 * 
	 * @param categoryId
	 */
	public void setCategoryId(CategoryId categoryId) {
		this.categoryId = categoryId.ordinal();
	}
	/**
	 * 设置会话类型
	 * 
	 * @param categoryId
	 */
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * 获取会话草稿
	 * 
	 * @return
	 */
	public String getDraftMsg() {
		if(StringUtils.isEmpty(getTargetId()) == false &&
				StringUtils.isEmpty(conversationTitle) == false){
			draftMsg = DBManager.getInstance().getDraftMsg(targetId);
		}
		return draftMsg;
	}

	/**
	 * 设置会话草稿
	 * 
	 * @param draftMsg
	 */
	public void setDraftMsg(String draftMsg) {
		this.draftMsg = draftMsg;
		if(StringUtils.isEmpty(getTargetId()) == false &&
				StringUtils.isEmpty(conversationTitle) == false){
			DBManager.getInstance().updateDraftMsg(
					getTargetId(), draftMsg);
		}
	}
	/**
	 * 会话是否置顶
	 * 
	 * @return
	 */
	public Boolean getIsTop() {
		return isTop;
	}
	/**
	 * 设置会话指定
	 * 
	 * @param isTop
	 */
	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
		if(StringUtils.isEmpty(getTargetId()) == false){
			DBManager.getInstance().updateConversationisTop(getTargetId(), isTop);
		}
	}
	/**
	 * 获取会话最后更新时间
	 * 
	 * @return
	 */
	public long getLastTime() {
		return lastTime;
	}
	/**
	 * 设置会话最后更新时间
	 * @param lastTime
	 */
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * 获取置顶时间
	 * 
	 * @return
	 */
	public long getTopTime() {
		return topTime;
	}
	/**
	 * 设置置顶时间
	 * 
	 * @param topTime
	 */
	public void setTopTime(long topTime) {
		this.topTime = topTime;
	}
	
	/**
	 * 获取当前会话未读消息数
	 * 
	 * @return
	 */
	public int getMsgUnRead() {
		return msgUnRead;
	}
	/**
	 * 设置当前会话未读消息数
	 * 
	 * @param msgUnRead
	 */
	public void setMsgUnRead(int msgUnRead) {
		this.msgUnRead = msgUnRead;
	}

	/**
	 * 清空聊天记录
	 * 
	 * @return
	 */
	public boolean clearMessages(){
		DBManager.getInstance().clearMessages(getTargetId(), getCategoryId());
		new Thread( new Runnable() {
			@Override
			public void run() {
				File file = new File("/sdcard/yunzhixun/image/"+targetId);
				FileTools.deleteFile(file);
				file = new File("/sdcard/yunzhixun/voice/"+targetId);
				FileTools.deleteFile(file);
			}
		}).start();
		return true;
	}
	
	/**
	 * 获取指定状态的所有消息
	 * 
	 */
	public List<ChatMessage> getMessagesFromStatus(int status){
		return DBManager.getInstance().getMessagesFromStatus(
				getTargetId(), getCategoryId(),status);
	}
	/**
	 * 获取最新消息，以startPos开始， count结束的所有消息。
	 * 
	 * @param startPos 最新消息开始索引位置  0表示最新的一条消息
	 * @param count 想获取的消息条数
	 * @return 获取到的消息
	 */
	public List<ChatMessage> getLastestMessages(int startPos, int count){
		
		return DBManager.getInstance().getLatestMessages(
				getTargetId(), getCategoryId(), startPos, count);
	}
	
	/**
	 * 获取历史消息记录 以startPos开始， count结束的所有消息。
	 * 
	 * @param startPos 开始的消息地址  0 表示最老的一条消息
	 * @param count 想获取的消息的条数   
	 * @return 所有获取到的消息的列表
	 */
	public List<ChatMessage> getHistroyMessages(int startPos, int count){
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		
		return DBManager.getInstance().getHistroyMessages(
				getTargetId(), getCategoryId(), startPos, count);
	}
	
	/**
	 * 获取当前会话的所有聊天记录
	 * 
	 */
	public List<ChatMessage> getAllMessage(){
		
		return DBManager.getInstance().getAllMessages(getTargetId(), getCategoryId());
	}
	
	
	/**
	 * 获取指定消息ID的消息对象
	 * 
	 */
	public ChatMessage getMessageFromMsgid(String msgId){
		return DBManager.getInstance().getMessageFromMsgid(
				getTargetId(), getCategoryId(),msgId);
	}
	
	/**
	 * 删除指定MSGID的消息记录
	 * 
	 */
	public boolean deleteMessages(List<ChatMessage> msgs){
		if(msgs == null || msgs.size() == 0){
			CustomLog.e("deleteMessages 参数错误!!!");
			return false;
		}
		return DBManager.getInstance().deleteMessages(getTargetId(), getCategoryId(), msgs);
	}
	
	
	/**
	 * 获取某targetId所对应的聊天的未读消息数
	 * 
	 */
	public int getUnreadCount() {
		return DBManager.getInstance().getConversationMsgUnRead(targetId);
//		return DBManager.getInstance().getUnreadCount(targetId, categoryId);
	}
	
	/**
	 * 清除当前会话的未读消息状态
	 * 
	 */
	public void clearMessagesUnreadStatus(){
		setMsgUnRead(0);
		DBManager.getInstance().updateConversationMsgUnRead(targetId, 0);
//		DBManager.getInstance().clearMessagesUnreadStatus(targetId, categoryId);
	}
	
	/**
	 * 删除指定会话
	 * 
	 */
	public int delConversationInfo(){
		return DBManager.getInstance().delConversationInfo(targetId);
	}
}
