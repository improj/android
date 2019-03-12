/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date: 2016-12-14
 * @description: Audio/Video Codec config
 */
public class CodecConfig {

	public int pltype;
    public String plname;
    public boolean enabled;
    
    public CodecConfig() {
    	// TODO Auto-generated constructor stub
    }
    
	/**
	 * @pltype codec payload type
	 * @plname codec payload name
	 * @enabled enabled/disabled codec
	 */
	public CodecConfig(int pltype, String plname, boolean enabled) {
		this.pltype = pltype;
		this.plname = plname;
		this.enabled = enabled;		
	}
}
