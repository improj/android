/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description UGo control configuration
 */
public class UGoConfig {
	// RC4 encrypt enabled, default false
	public boolean rc4Enabled;
	// IM3.0 protocol compress enabled
	public boolean compressEnabled;
    // IM3.0 protocol enabled, false would use IM2.0
    public boolean tlvEnabled;
    // IPv6 enabled for address specific at SDP connection field 
    boolean ipv6Enabled;
    // Customer service version enabled
    boolean csrvEnabled;
    // Video call enabled, 0: disabled, 1: enabled, 2: enabled with ringing preview
    public int videoEnabled;
    // Client platform, 0x01: PC, 0x02: IOS, 0x04: Android
    public int platform;
    // Account type, 0 and 1 for im3.0, 2 for old JSON(IM2.0) protocol
    public int atype;
    // network type. 0x00: unavailable, 0x01: WIFI, 0x02: 2G, 0x04:3G, 0x08: 4G
    public int netType;
    // Client number
    public String uid;
    // UserId for im3.0
    public String userid;
    // Client register phone number
    public String phone;
    // Client brand
    public String brand;
    // User Nickname
    public String nickName;
    // Client local IP address
    public String localAddr;
    
	/**
	 * 
	 */
	public UGoConfig() {
		// TODO Auto-generated constructor stub
		rc4Enabled = false;
		ipv6Enabled = false;
		csrvEnabled = false;
		platform = 0x04;
	}

}
