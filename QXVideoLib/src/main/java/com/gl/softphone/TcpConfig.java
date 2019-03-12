/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description TCP connection configuration, for internal testing
 */
public class TcpConfig {
	// enabled internal TCP connection
	boolean tcpEnabled;
	// TCP server address
    String tcpSrvAddr;
    
	/**
	 * 
	 */
	public TcpConfig() {
		// TODO Auto-generated constructor stub
		tcpEnabled = false;
	}

}
