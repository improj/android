/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-14
 * @description MediaEngine control configuration 
 */
public class MediaConfig {
	// Real time protocol type, 0: RTP 1: PRTP
	public int realTimeType;
	// Voice e-model enabled, default true
    public boolean emodelEnabled;
    // Voice fec enabled, default false
    public boolean fecEnabled;
    // Voice VanderMander Fec enabled, default false
    public boolean vdmFecEnabled;
    // Voice RTP packet encrypt enabled, default false
    public boolean rtpEncEnabled;
	// External pcm media processing, default false
    public boolean extMediaProcEnabled;
    // support external audio transport, default false
    public boolean extAudioTransEnabled;
    // Media UDP support IPv6 enabled, default false
    public boolean IPv6Enabled;
	// Video fec enabled, default false
    public boolean vieFecEnabled;
    // Video nack enabled, default false
    public boolean vieNackEnabled;
    
	/**
	 * Construct
	 */
	public MediaConfig() {
		// TODO Auto-generated constructor stub
		realTimeType = 0;
		emodelEnabled = true;
		fecEnabled = false;
		vdmFecEnabled = false;
		rtpEncEnabled = false;
		extMediaProcEnabled = false;
		extAudioTransEnabled = false;
		IPv6Enabled = false;
		vieFecEnabled = false;
		vieNackEnabled = false;
	}
}
