package com.yzxtcp.tools.tcp.packet;

/**
 * 通讯协议
 */
public class PacketSerialize {

	public static final int REQ_AUTH = 1;
	public static final int REQ_REAUTH = 2;
	public static final int RESP_AUTH = 30001;
	public static final int RESP_REAUTH = 30002;

	public static native Object pack(int cmd, Object datatype, Object packbody,
			int uin, String deviceid);

	public static native Object uppack(int cmd, byte[] buffer, Object datatype);

	static {
		System.loadLibrary("pack");
	}

}
