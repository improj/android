package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;

//上传语音请求
public class IGGUploadVoiceRequest extends IGGBaseRequest {
	public String pcFromUserName; // 发起端用户名
	public String pcToUserName; // 目标用户名
	public int iOffset; // 起始位置，第一个包填0
	public int iLength; // 当前包的BUFF长度
	public String pcClientMsgId; // 客户端自定义的声音标识
	public int iMsgId; // 第一个包填0，后续的包填写服务器返回的ID
	public int iVoiceLength; // 时间长度
	public byte[] tData; // 语音BUFF
	public int iEndFlag; // 结束标志（发送结束=1，否则=0）
	public int iCancelFlag; // 取消标志（取消发送=1，否则=0）
	public String pcMsgSource; // 该字段不填
	public int iVoiceFormat; // 声音格式
	public int iUICreateTime; // 时间戳
	public int iForwardFlag; // 填0
	public String pcVoiceDir; // 填0
	
	@Override
	public void onSendMessage() {
		//TcpTools.sendPacket(RequestCmd.REQ_UPLOAD_VOICE.ordinal(), this);
		UCSManager.sendPacket(600035, this);
	}
	
	public IGGUploadVoiceRequest(String pcToUserName, String pcVoiceDir, int voiceLen) {
		// TODO Auto-generated constructor stub
		this.pcFromUserName = IMUserData.getUserName();
		this.pcToUserName = pcToUserName;
		this.pcVoiceDir = pcVoiceDir;
		this.iVoiceLength = voiceLen;
	}
}
