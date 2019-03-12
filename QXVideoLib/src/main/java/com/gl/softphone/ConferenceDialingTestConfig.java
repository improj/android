/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description 
 */
public class ConferenceDialingTestConfig {
	int isConfCall;         /* normal call or conference call */
    int testFileIdx;        /* auto test play file index */
    int refFileIdx;         /* auto test reference file index, for caller */
    int callRole;           /* call role, caller or called */
    int callCnt;            /* number of calls */
    int callTime;           /* the duration time per call */
    int callInterval;       /* interval time in every call */
    int userNum;
    String userAttr;
    String roomName;
    String roomPwd;
    String remark;
    
	/**
	 * 
	 */
	public ConferenceDialingTestConfig() {
		// TODO Auto-generated constructor stub
	}

}
