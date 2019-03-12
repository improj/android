package com.yzxIM.data.db;


/**
 * 自定义消息对象
 * 
 * @author zhuqian
 */
public class CustomMsg {
	private static final int MAX_CUSTOMLEN = 16384;// 最大16k

	/**
	 * 自定义消息内容
	 */
	private byte[] content;
	/**
	 * 自定义消息长度
	 */
	private int len;

	/**
	 * 构造自定义消息
	 * 
	 * @param content
	 * @param len
	 * @throws Exception
	 */
	public CustomMsg(byte[] content, int len)throws Exception{
		if(len > MAX_CUSTOMLEN){
			throw new Exception("len is lager than 16k");
		}
		this.content = content;
		this.len = len;
	}
	/**
	 * 获取自定义消息内容
	 * 
	 * @return
	 */
	public byte[] getContent() {
		return content;
	}
	/**
	 * 获取自定义消息长度
	 * 
	 * @return
	 */
	public int getLen() {
		return len;
	}
}
