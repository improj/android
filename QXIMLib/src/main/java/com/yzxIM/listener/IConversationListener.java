package com.yzxIM.listener;

import java.util.List;

import com.yzxIM.data.db.ConversationInfo;

/**
 * 会话状态监听接口
 *
 */
public interface IConversationListener {
	/**有会话创建时调用
	 * @author zhangbin
	 * @2015-7-22
	 * @param cinfo 会话对象
	 * @descript:
	 */
	@Deprecated
	void onCreateConversation(ConversationInfo cinfo); //创建会话成功
	/**有会话删除时调用
	 * @author zhangbin
	 * @2015-7-22
	 * @param cinfo 会话对象
	 * @descript:
	 */
	void onDeleteConversation(ConversationInfo cinfo); //删除会话成功
	/**会话有更新时调用
	 * @author zhangbin
	 * @2015-7-22
	 * @param cinfo 会话对象
	 * @descript:
	 */
	@Deprecated
	void onUpdateConversation(ConversationInfo cinfo);
	/**会话有更新时调用
	 * @author zhangbin
	 * @2016-1-4
	 * @param cinfos 会话对象列表
	 * @descript:
	 */
	void onUpdateConversation(List<ConversationInfo> cinfos);
}
