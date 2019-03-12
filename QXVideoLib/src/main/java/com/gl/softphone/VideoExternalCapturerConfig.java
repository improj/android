/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Video external capturer configuration
 */
public class VideoExternalCapturerConfig {
	public static final int VIE_EXTERNAL_CAPTURE_FORMAT_I420 = 0;
	public static final int VIE_EXTERNAL_CAPTURE_FORMAT_H264 = 1;
	
	// if use external camera capturer
	boolean useExternalCapturer;
	// external capture image format, 0: I420, 1: H264
	int     externalFormat;
	
	/**
	 * 
	 */
	public VideoExternalCapturerConfig() {
		// TODO Auto-generated constructor stub
		useExternalCapturer = false;
		externalFormat = VIE_EXTERNAL_CAPTURE_FORMAT_H264;
	}

}
