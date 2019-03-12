/**
 * Copyright (c) 2017 ~ 2018 The KQcky project authors. All Rights Reserved.
 */
package com.gl.softphone;

import java.util.ArrayList;


/**
 * @author vinton
 * @description Group dialing configuration
 */
public class GroupDialingConfig {
	// If video call
	public boolean videoEnable;
	public boolean isSeqCall;
	public boolean recordEnable;
	public String userData;
	public ArrayList<CalleeInfo> calleeList;
	
	/**
	 * 
	 */
	public GroupDialingConfig() {
		// TODO Auto-generated constructor stub
		videoEnable = false;
		isSeqCall = false;
		recordEnable = false;
		calleeList = new ArrayList<CalleeInfo>();
	}
	
	public class CalleeInfo {
		// Dialing mode, see "call mode" in UGoAPIParam
		int callMode;
		String toUserId;
		String toPhone;
		
		public CalleeInfo(int callMode, String toUserId, String toPhone) {
			// TODO Auto-generated constructor stub
			this.callMode =  callMode;
			this.toUserId = toUserId;
			this.toPhone = toPhone;
		}
	}	
}
