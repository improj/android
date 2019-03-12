package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//上传消息图片请求 
public class IGGUploadMsgImgRequest extends IGGBaseRequest {
	public String pcClientMsgId; // 消息ID，格式参考如下
	public String pcFromUserName; // 发起者
	public String pcToUserName; // 接收者
	public int iTotalLen; // 总大小
	public int iStartPos; // 起始位置
	public int iDataLen; // 用不到不填
	public byte[] tData; // BUFF（数据+数据长度）
	public int iMsgType; // 消息图片的类型
	public String pcMsgSource;
	public int iCompressType; // 是否是原图发送
	public int iNetType; // 暂不用
	public int iPhotoFrom; // 暂不用
	public String pcMediaId; // 暂不用
	public String pcCDNBigImgUrl; // CDN 原图URL
	public String pcCDNMidImgUrl; // CND 大图URL
	public String pcAESKey; // CND 公钥
	public int iEncryVer; // CND 相关
	public int iCDNBigImgSize; // 上传CDN 的原图大小
	public int iCDNMidImgSize; // 上传CDN 的大图大小
	public String pcMD5; // 图片MD5
	public String pcImgDir; // 图片路径
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(600030, this);
		//TcpTools.sendPacket(RequestCmd.REQ_UPLOAD_IMG.ordinal(), this);
	}
	
	public IGGUploadMsgImgRequest(String pcToUserName, String pcImgDir) {
		this.pcFromUserName = IMUserData.getUserName();
		this.pcToUserName = pcToUserName;
		this.pcImgDir = pcImgDir;
	}

}
