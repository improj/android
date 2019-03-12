package com.yzxIM.listener;

import com.yzxIM.data.db.DiscussionInfo;
import com.yzxtcp.data.UcsReason;

/**
 * 讨论组消息回调
 *
 */
public interface DiscussionGroupCallBack {
	/**创建讨论组回调
	 * @author zhangbin
	 * @2015-7-22
	 * @param reason 成功与否返回的错误码和原因
	 * @param dInfo 创建成功是返回的讨论组信息对象
	 * @descript:
	 */
	void onCreateDiscussion(UcsReason reason, DiscussionInfo dInfo);  
	/**讨论组加人回调
	 * @author zhangbin
	 * @2015-7-22
	 * @param reason 成功与否返回的错误码和原因
	 * @descript:
	 */
	void onDiscussionAddMember(UcsReason reason);
	/**讨论组踢人
	 * @author zhangbin
	 * @2015-7-22
	 * @param reason 成功与否返回的错误码和原因
	 * @descript:
	 */
	void onDiscussionDelMember(UcsReason reason); //讨论组踢人回调
	/**退出讨论组
	 * @author zhangbin
	 * @2015-7-22
	 * @param reason 成功与否返回的错误码和原因
	 * @descript:
	 */
	void onQuiteDiscussion(UcsReason reason);    //退出讨论组回调
	
	/**修改讨论组名称
	 * @author zhangbin
	 * @2015-7-22
	 * @param reason 成功与否返回的错误码和原因
	 * @descript:
	 */
	void onModifyDiscussionName(UcsReason reason);    //修改讨论组名称回调
}
