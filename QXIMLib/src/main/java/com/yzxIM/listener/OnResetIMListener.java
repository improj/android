package com.yzxIM.listener;

import com.yzxtcp.data.UcsReason;

/**
 * 重置IM回调，包括删除文件，数据库和缓存
 * 
 * @author zhuqian
 */
public abstract class OnResetIMListener {
	// 缓存清除成功
	public static final int RESET_SUCCESS = 0;
	// 数据库删除失败
	public static final int IO_DATABASE_ERROR = 1;
	// 文件删除失败
	public static final int IO_FILE_ERROR = 2;
	// 缓存清除失败
	public static final int IO_CACHE_ERROR = 3;
	// 重置被拒绝
	public static final int RESET_REFUSE = 4;

	public abstract void onResetResult(UcsReason reason);
}
