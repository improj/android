package com.yzxIM.listener;

import java.util.List;

import com.yzxIM.data.db.ChatMessage;

/**
 * 发送和接收消息回调
 *
 */
public interface MessageListener {
	/**发送消息回调
	 * @author zhangbin
	 * @2015-7-22
	 * @param message 发送的消息对象
	 * @descript:
	 */
	void onSendMsgRespone(ChatMessage message);
	/**有消息接收到时调用
	 * @author zhangbin
	 * @2015-7-22
	 * @param messages 接收到的消息列表
	 * @descript:
	 */
	void onReceiveMessage(List<ChatMessage> messages);
	/**
	 * 下载文件进度监听接口(下载文件)
	 * @param msgId 消息ID
	 * @param sizeProgrss:总件总大小
	 * @param currentProgress：当前下载大小
	 */
	public void onDownloadAttachedProgress(String msgId, String filePaht, int sizeProgrss, int currentProgress);
}
