/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Video encode configuration
 */
public class VideoEncodeConfig {
	// Encoded image width
	public int usWidth;
	// Encoded image height
	public int usHeight;
    // Video encoded start bitrate, unit: kbps
    public int usStartBitrate;
    // Video encoded max bitrate, unit: kbps
    public int usMaxBitrate;
    // Video encoded min bitrate, unit: kbps
    public int usMinBitrate;
    // Video encoded max frame rate
    public int usMaxFramerate;
    // 
    int usQpMax;
    // Video Encoder complexity, larger complexity needed higher CPU performance
    public int usComplexity; /* 0-3*/
	// Use hardware encoder or not
	boolean usIsUseHwEnc;
	
	boolean usFixedResolution; /* true false*/
	
	/**
	 * 
	 */
	public VideoEncodeConfig() {
		// TODO Auto-generated constructor stub
		usWidth = 640;
		usHeight = 480;
		usStartBitrate = 200;
		usMaxBitrate = 500;
		usMinBitrate = 30;
		usMaxFramerate = 15;
		usComplexity = 0;
		usIsUseHwEnc = false;
	}

}
