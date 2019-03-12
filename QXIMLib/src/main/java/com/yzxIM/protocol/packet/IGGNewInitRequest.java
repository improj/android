package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

public class IGGNewInitRequest extends IGGBaseRequest{

	public String pcUserName;
	public int iVal;
	public int iValGroup;
	public int iKey;
	public String pcLanguage;
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(600010, this);
	}

	public IGGNewInitRequest() {
		iVal = UserData.getiVal(IMUserData.getUserName());
		iValGroup = UserData.getGroupiVal(IMUserData.getUserName());
		pcUserName = IMUserData.getUserName();
	}
}
