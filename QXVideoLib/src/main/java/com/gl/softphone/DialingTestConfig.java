/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description 
 */
public class DialingTestConfig {
	int isConfCall;         /* normal call or conference call */
    int callRole;           /* call role, caller or called */
    int callCnt;            /* number of calls */
    int callTime;           /* the duration time per call */
    int callInterval;       /* interval time in every call */
    int mode;
    int videoEnable;
    int ucalltype;          /* uxin calltype: 0:normal call 1:uxin liaoyiliao*/
    String uid;
    String phone;
    
	/**
	 * 
	 */
	public DialingTestConfig() {
		// TODO Auto-generated constructor stub
	}

}
