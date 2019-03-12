/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description 
 */
public class DialingConfig {
	// Called userid or client number
	public String uid;
	// Called phone number
	public String phone;
	// Through data to called
	public String userData;
	// Dialing mode, see "call mode" in UGoAPIParam
	public int callMode;
	// If video call
	public boolean videoEnable;
	// uxin calltype: 0:normal call 1:uxin liaoyiliao
	public int uCallType;
	/**
	 * 
	 */
	public DialingConfig() {
		// TODO Auto-generated constructor stub
		uCallType = 0;
	}

}
