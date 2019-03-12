/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description RTP configuration
 */
public class RtpConfig {
	// Voice RTP packets received timeout. range: 0 ~ 60s
	public int rtpTimeout;
    //低码流标志，打开 ：true， 关闭: false,仅在使用私有协议时有效，当网络较差(2g)时打开（g729 60ms，silk 5k 60ms）
	//低码流标志打开时，动态负载默认关闭
    public boolean fixLowPayloadEnabled;
    
	/**
	 * 
	 */
	public RtpConfig() {
		// TODO Auto-generated constructor stub
		rtpTimeout = 45;
		fixLowPayloadEnabled = false;
	}

}
