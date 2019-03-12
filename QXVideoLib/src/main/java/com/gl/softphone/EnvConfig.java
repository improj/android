/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description environment configuration, current not used
 */
public class EnvConfig {
	// open or close Speaker, default false
	public boolean status;
	// network type: 0:2G; 1:3G; 2:wifi; 3:4G; the other value is invalid
    public int networktype;
    // dialog scene: 0: general mode; else other: meeting mode
	public int dialogScene;
	
	/**
	 * 
	 */
	public EnvConfig() {
		// TODO Auto-generated constructor stub
		status = false;
		networktype = 2;
		dialogScene = 0;
	}

}
