package com.yzxIM.protocol.packet;

import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//上传视频文件
public class IGGUploadVideoRequest extends IGGBaseRequest {
	public String pcClientMsgId;// 客户端定义的标识符
	public String pcFromUserName;// 发送端用户名
	public String pcToUserName;// 接收端用户名
	public int iThumbTotalLen;// 缩略图总大小
	public int iThumbStartPos;// 缩略图起始位置
	public byte[] tThumbData;// 缩略图数据
	public int iVideoTotalLen;// 视频数据总大小
	public int iVideoStartPos;// 视频数据起始位置
	public byte[] tVideoData;// 视频数据
	public int iPlayLength;// 不填
	public int iNetworkEnv;// 不填
	public int iCameraType;// 不填
	public int iFuncFlag;// 不填
	public String pcMsgSource;// 不填
	public String pcCDNVideoUrl;// 视频数据CDN URL
	public String pcAESKey;// CDN KEY
	public int iEncryVer;// CDN相关
	
	@Override
	public void onSendMessage() {
		// TODO Auto-generated method stub
		UCSManager.sendPacket(RequestCmd.REQ_UPLOAD_VIDEO.ordinal(), this);
	}
	
	public IGGUploadVideoRequest(String pcClientMsgId, String pcFromUserName, String pcToUserName,
			int iThumbTotalLen, int iThumbStartPos) {
		// TODO Auto-generated constructor stub
		this.pcClientMsgId = pcClientMsgId;
		this.pcFromUserName = pcFromUserName;
		this.pcToUserName = pcToUserName;
		this.iThumbTotalLen = iThumbTotalLen;
		this.iThumbStartPos = iThumbStartPos;
	}
}
