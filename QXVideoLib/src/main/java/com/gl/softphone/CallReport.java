/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-16
 * @description Get Voice quality evaluation by e-model 
 * and the call session info after every call end
 */
public class CallReport {
	// Voice MOS value
	public EmodelValue emodelMos;
	// Voice RTT value
	public EmodelValue emodelRtt;
    // Voice lost rate
	public EmodelValue emodelLost;
    // Voice JitterBuffer
	public EmodelValue emodelJitter;
    // Voice Delay
	public EmodelValue emodelDelay;
    // Call session information
	public SessionInfo sessionInfo;
	
	/**
	 * 
	 */
	public CallReport() {
		// TODO Auto-generated constructor stub
		emodelMos = new EmodelValue();
		emodelRtt = new EmodelValue();
		emodelLost = new EmodelValue();
		emodelJitter = new EmodelValue();
		emodelDelay = new EmodelValue();
		sessionInfo = new SessionInfo();
	}

	/** 
	 * @author vinton
	 * @date 2016-12-16
	 * @description e-model value
	 */
	public class EmodelValue {
		// Average value
        public double average;
        // Minimum value
        public double min;
        // Maximum value
        public double max;
	}
	
	/**
	 * @author vinton
	 * @date 2016-12-16
	 * @description the call session info
	 */
	public class SessionInfo {
		// CallId of the call
		public String strCallId;
    	// Address of the RTPP server
    	public String strMgw;
    	// Address of VoIP signaling server
    	public String strSgw;
    	// Voice codec of the call
    	public String strCodec;
    	// Call mode, 0: Free, 1: Direct 
    	public int callMode;
    	// RTP transmit mode, 0: RTPP, 1: P2P 
    	public int transMode;
    	// Time between connecting and ringing event
    	public int connTime;
    	// Call State, eg. single pass
    	public int callState;
    	// Call role, 0: caller, 1: called
    	public int callRole;
    	// Port of the RTPP server
    	public int mgwPort;
    	// Rtp packets for voice channel  had send
    	public int pktSnd;
    	// Rtp packets for voice channel  had received
    	public int pktRecv;
    	// Reason of single-pass state
    	public int singlePassRsn;
    	// Microphone muted state when call end
    	public boolean isMuted;
    }
}
