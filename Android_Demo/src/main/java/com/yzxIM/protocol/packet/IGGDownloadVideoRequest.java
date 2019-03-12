package com.yzxIM.protocol.packet;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//下载视频请求
public class IGGDownloadVideoRequest extends IGGBaseRequest {
	public int iMsgId; // 从服务器得到的msgid
	public int iTotalLen;// 视频总大小（第一个包填0）
	public int iStartPos;// 起始位置（第一个包填0）
	public int iNetworkEnv;// 网络环境
	public int iMxPackSize;// 客户端指定最大包大小//暂时不起作用
	
	@Override
	public void onSendMessage() {
		// TODO Auto-generated method stub
		UCSManager.sendPacket(RequestCmd.REQ_DOWNLOAD_VIDEO.ordinal(), this);
	}
	
	public IGGDownloadVideoRequest(int iMsgId, int iTotalLen, int iStartPos, int iNetworkEnv, int iMxPackSize) {
		// TODO Auto-generated constructor stub
		this.iMsgId = iMsgId;
		this.iTotalLen = iTotalLen;
		this.iStartPos = iStartPos;
		this.iNetworkEnv = iNetworkEnv;
		this.iMxPackSize = iMxPackSize;
	}
}
