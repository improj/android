/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-14
 * @description Rtpp server config
 */
public class RtppSrvConfig {

	public int delay;
	public int lost;
	public String ipString;
	
	/**
	 * Rtpp server config
	 */
	public RtppSrvConfig() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construct Rtpp server config with network information
	 * @param delay network delay(ms) between rtpp server and client
	 * @param lost network lost(%) between rtpp server and client
	 * @param ipString rtpp network ip address
	 */
	public RtppSrvConfig(int delay, int lost, String ipString) {
		this.delay = delay;
		this.lost = lost;
		this.ipString = ipString;
	}
}
