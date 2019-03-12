package com.yzxtcp.listener;

import com.yzxtcp.data.UcsReason;

/**
 * 登录回调接口
 * 
 */
public interface ILoginListener {
		/**当登陆时返回 reason.getReason()==UcsErrorCode.NET_ERROR_CONNECTOK表示登陆成功 否则失败
		 * @param reason 返回值
		 */
		void onLogin(UcsReason reason);
}
