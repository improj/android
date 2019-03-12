package com.yzxIM.data;

public enum MSGTYPE {
	/**
	 * 未知消息类型
	 */
	MSG_DATA_NONE,
	/**
	 * 文本消息
	 */
	MSG_DATA_TEXT,
	/**
	 * 图片消息
	 */
	MSG_DATA_IMAGE,
	/**
	 * 语音消息
	 */
	MSG_DATA_VOICE,
	/**
	 * 视频消息
	 */
	MSG_DATA_VIDEO,
	/**
	 * 地图定位消息
	 */
	MSG_DATA_LOCALMAP,
	/**
	 * 系统消息
	 */
	MSG_DATA_SYSTEM,
	/**
	 * 自定义消息
	 */
	MSG_DATA_CUSTOMMSG;
	
	/**通过消息类型对应的整型值获取MSGTYPE
	 * @author zhangbin
	 * @2015-7-22
	 * @param cmd 消息类型对应的整型值
	 * @return
	 * @descript:
	 */
	public static MSGTYPE valueof(int cmd){
		MSGTYPE req = MSG_DATA_NONE;
		for (MSGTYPE s : MSGTYPE.values()) {
			if(s.ordinal() == cmd){
				return s;
			}
		}
		return req;
	}}
