/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Video image enhance process
 */
public class VideoImageProcess {
	// Video deflickering on capture image 
	boolean deflickingEnable;
	// Video denoising on capture image
    boolean denoisingEnable;
    // Video enhancement on remote image
    boolean enhancementEnable;
    
	/**
	 * 
	 */
	public VideoImageProcess() {
		// TODO Auto-generated constructor stub
		deflickingEnable = false;
		denoisingEnable = false;
		enhancementEnable = false;
	}

}
