package com.yzxIM.protocol.packet;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/********************* 同步KEY检查 ****************************/
public class IGGNewSyncCheckRequest extends IGGBaseRequest {
	public int sync_iUin; // 用户uin
	public int iSyncKeyLen; // tSyncKeyBuf的长度
	public byte[] tSyncKeyBuf; // ToBuffer后的IGGSyncKey_t
	
	@Override
	public void onSendMessage() {
		// TODO Auto-generated method stub
		UCSManager.sendPacket(RequestCmd.REQ_NEW_SYNCCHK.ordinal(), this);
	}
	
	public IGGNewSyncCheckRequest(int iSyncKeyLen, byte[] tSyncKeyBuf) {
		// TODO Auto-generated constructor stub
		this.iSyncKeyLen = iSyncKeyLen;
		this.tSyncKeyBuf = tSyncKeyBuf;
	}
};
