/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description ICE configuration
 */
public class IceConfig {
	// ICE enabled
	public boolean iceEnabled;
	// IPv6 enabled
    boolean ipv6Enabled;
    // STUN server address with port
    public String stunServer;
    
	/**
	 * 
	 */
	public IceConfig() {
		// TODO Auto-generated constructor stub
		iceEnabled = false;
		ipv6Enabled = false;		
	}

}
