/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Voice Quality Enhanced configuration
 */
public class VqeConfig {
	// Voice Echo Cancellation enabled, default true
	public boolean EcEnable;
	// Voice Auto Gain Control enabled, default true
	public boolean AgcEnable;
	// Voice Noise Suppression enabled, default true
    public boolean NsEnable;
    // Voice Auto Gain Control enabled on received side, default false
    public boolean AgcRxEnable;
    // Voice Noise Suppression enabled on received side, default false
    public boolean NsRxEnable;
    // AGC target level,value range:1---15, default value:6
    // typical value:3(high volume) 6(medium volume) 9(small volume)
    public int     AgcTargetDbfs;
    // AGC compressionGain ,value range:1---30,default value 9, 
	// typical value:12(high volume) 9(medium volume) 6(small volume)
	public int 	   AgcCompressionGaindB;
	// Enable dual microphone noise suppression
	public boolean DualMicNsEnable;          
	public int     AgcRxTargetDbfs;
	    // AGC compressionGain ,value range:1---30,default value 9, 
		// typical value:12(high volume) 9(medium volume) 6(small volume)
	public int 	   AgcRxCompressionGaindB;
	/**
	 * 
	 */
	public VqeConfig() {
		// TODO Auto-generated constructor stub
		EcEnable = true;
		AgcEnable = true;
		NsEnable = true;
		AgcRxEnable = false;
		NsRxEnable = false;
		AgcTargetDbfs = 6;
		AgcCompressionGaindB = 9;
		DualMicNsEnable = false;
	}

}
