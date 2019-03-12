package com.yzxtcp.tools.tcp.packet;

import com.yzxtcp.tools.tcp.packet.iface.IUCSMessageResponse;

public abstract class IGGBaseResponse extends Object implements IUCSMessageResponse{
	public int base_iRet;
	public String tErrMsg="";
	public int base_iSeq;
	
	//讨论组消息分隔符
	public Object uppacket(int cmd, byte[] buffer, Object datatype) {
		return PacketSerialize.uppack(cmd, buffer, datatype);
	}
};
