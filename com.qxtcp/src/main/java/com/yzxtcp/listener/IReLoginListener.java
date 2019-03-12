package com.yzxtcp.listener;

import com.yzxtcp.data.UcsReason;

public interface IReLoginListener {
	/**
	 * 当登陆时返回 reason.getReason()==NET_ERROR_RECONNECTOK 表示登陆成功 否则失败
	 * 
	 * @author zhangbin
	 * @2015-7-20
	 * @param reason
	 *            返回值
	 */
	void onReLogin(UcsReason reason);
}
