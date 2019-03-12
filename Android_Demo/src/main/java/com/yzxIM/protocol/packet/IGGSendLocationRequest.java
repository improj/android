package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

public class IGGSendLocationRequest extends IGGBaseRequest{
	//格式JSON字符串{“coordinate”:“xx”,
	//“lat”:“139.468888”,“lng”:“35.426666”，“address”:“日本东京”}
	//coordinate坐标/lat经度/lng纬度/address地址
	public String pcClientMsgId;
	public String pcFromUserName;  //发起者
	public String pcToUserName;	//接收者
	public String pcContent;	//地理位置信息,
	public int iTotalLen;		//图片总大小,地图的缩略图
	public int iStartPos;		//起始位置
	public byte[] tData;		//BUFF（数据+数据长度）
	public int iMsgType;		//消息类型，枚举值：MM_DATA_LOCATION：48
	public String pcMD5;		//图片MD5
	public String pcImgDir;     //地图地址

	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(50, this);
	}

	public IGGSendLocationRequest(String pcToUserName, String imgPath,String mapMsg) {
		this.pcFromUserName = IMUserData.getUserName();
		this.pcToUserName = pcToUserName;
		this.pcImgDir = imgPath;
		this.pcContent = mapMsg;
	}

}
