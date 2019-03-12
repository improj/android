package com.yzxtcp.tools.tcp.packet;

public class IGGAuthBySKResponse extends IGGBaseResponse {
	public int iUin;							//UIN
	public int iNewVersion;						//SESSION KEY
	public String pcUserName;						//UserName
	public int iIPCount;
	public int iRet;
	public String ptIPList;
	
	@Override
	public void onMsgResponse() {
	}
		
};