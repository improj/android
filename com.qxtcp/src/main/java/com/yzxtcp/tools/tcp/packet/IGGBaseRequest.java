package com.yzxtcp.tools.tcp.packet;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.tcp.packet.iface.IUCSMessageRequest;

public abstract class IGGBaseRequest extends Object implements IUCSMessageRequest{
	public byte[] sSessionKey = new byte[36]; // SESSION KEY
	public int iUin; // UIN
	public String cDeviceID; // 设备ID
	public int iClientVersion; // 客户端版本号
	public byte[] sDeviceType = new byte[132]; // 设备类型
	public int iScene; // 场景标识符（参考enSceneStatus宏定义）
	public int iSeq;

	public IGGBaseRequest() {
		iUin = UserData.getiUin();
		this.iClientVersion = NetWorkTools.getCurrentNetWorkType(YzxTCPCore.getContext());
	}
	public void setClientVersion() {
//		Set<String> sdkSet = TCPListenerManager.getInstance().getcpRecvListener();
//		if(sdkSet != null){
//			for(String item : sdkSet){
//				if(item.equals(ITcpRecvListener.IMSDK)){
//					this.iClientVersion |= 0x01<< 8; 
//				}else if(item.equals(ITcpRecvListener.VOIPSDK)){
//					this.iClientVersion |= 0x01<< 9; 
//				}
//			}
//		}
	}
	public Object packet(int cmd, Object datatype, Object packbody, int uin, String deviceid) {
		return PacketSerialize.pack(cmd, datatype, packbody, uin, deviceid);
	}
	
}