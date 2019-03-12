package com.yzxIM.data;

public enum CategoryId {
	
	/**
	 * 未知会话类型
	 */
	NONE,
	/**
	 * 单聊会话
	 */
	PERSONAL, 
	/**
	 * 群聊会话
	 */
	GROUP, 
	/**
	 * 讨论组会话
	 */
	DISCUSSION,
	/**
	 * 系统广播会话
	 */
	BROADCAST;
	/**通过CategoryId的整型值获取CategoryId
	 * @author zhangbin
	 * @2015-7-22
	 * @param id CategoryId 对应的整型值
	 * @return
	 * @descript:
	 */
	public static CategoryId valueof(int id) {
		CategoryId cid = PERSONAL;
		for (CategoryId s : CategoryId.values()) {
			if (s.ordinal() == id) {
				return s;
			}
		}
		return cid;
	}
}
