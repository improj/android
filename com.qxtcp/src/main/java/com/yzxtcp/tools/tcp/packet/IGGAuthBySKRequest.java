package com.yzxtcp.tools.tcp.packet;

import com.yzxtcp.data.UserData;

public class IGGAuthBySKRequest extends IGGBaseRequest {

	@Override
	public void onSendMessage() {
	}
	public IGGAuthBySKRequest() {
		setClientVersion();
		this.iUin = UserData.getiUin();
	}
};