/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Peer to peer Stream config on local area network testing
 */
public class PeerConnectionConfig {
	// RTP payload type
	int playload;
	// Remote peer IP address
    String remoteIP;
    // Remote peer port
    int remotePort;
    // Local port to bind
    int localPort;
    // if use external udp transport, default false
    boolean exTransportEnable;
    
	/**
	 * 
	 */
	public PeerConnectionConfig() {
		// TODO Auto-generated constructor stub
	}

}
