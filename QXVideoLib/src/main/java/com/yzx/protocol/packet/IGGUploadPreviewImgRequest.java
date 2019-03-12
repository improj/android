package com.yzx.protocol.packet;  

import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

/**
 * @Title IGGUploadPreviewImgRequest   
 * @Description  上传预览图片请求 命令码：52
 * @Company yunzhixun  
 * @author xhb
 * @date 2017-2-17 下午2:26:19
 */
public class IGGUploadPreviewImgRequest extends IGGBaseRequest {
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
	public String pcImgDir; // 图片路径 ，这个图片路径一定要有
	
	@Override
	public void onSendMessage() {
		UCSManager.sendPacket(52, this);
	}
	
	public IGGUploadPreviewImgRequest(String pcImgDir) {
		CustomLog.d("pcImgDir:" + pcImgDir);
		this.pcFromUserName = UserData.getUserId();
		this.pcImgDir = pcImgDir;
		this.pcToUserName = "15219483291"; // 这个号码一定要带，协议规定的，这里就随机填一个号码  
	}

}
  
