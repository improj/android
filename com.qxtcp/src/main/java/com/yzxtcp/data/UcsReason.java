package com.yzxtcp.data;

/**返回值类
 * 
 *
 */
public class UcsReason {

	/**
	 * 返回消息错误码
	 */
	private int reason;
	/**
	 * 返回原因
	 */
	private String msg;

	/**获取返回消息错误码
	 * @author zhangbin
	 * @2015-7-21
	 * @return 错误码
	 */
	public int getReason() {
		return reason;
	}

	public UcsReason(){
		
	}
	
	public UcsReason(int reason){
		this.reason = reason;
	}
	/**设置错误码
	 * @param reason 错误码
	 */
	public UcsReason setReason(int reason) {
		this.reason = reason;
		return this;
	}

	/**获取错误返回原因
	 * @author zhangbin
	 * @2015-7-21
	 * @return 返回原因
	 */
	public String getMsg() {
		return (msg != null && msg.length() > 0) ? msg : "";
	}

	/**设置错误返回原因
	 * @author zhangbin
	 * @2015-7-21
	 * @param msg 错误原因
	 */
	public UcsReason setMsg(String msg) {
		this.msg = msg;
		return this;
	}
}
