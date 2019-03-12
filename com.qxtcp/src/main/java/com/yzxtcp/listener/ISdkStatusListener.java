package com.yzxtcp.listener;

import com.yzxtcp.data.UcsReason;

/**
 * SDK状态回调接口
 */
public interface ISdkStatusListener {
	/**
	 * sdk状态改变是回调
	 * 
	 */
	void onSdkStatus(UcsReason reason);
}
