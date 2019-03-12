package com.yzxIM.protocol.packet;

import com.yzxtcp.tcp.ImageUploader;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

//上传语音应答
public class IGGUploadVoiceResponse extends IGGBaseResponse {
	public String pcFromUserName; // 发起端用户名
	public String pcToUserName; // 目标用户名
	public int iOffset; // 指定一下个包的位置
	public int iLength; // 当前包的BUFF长度
	public int iCreateTime; // 暂时不需要
	public String pcClientMsgId; // 客户端自定义的声音标识
	public int iMsgId; // 服务器创建的MsgId
	public int iVoiceLength; // 时间长度
	public int iEndFlag; // 结束标志（发送结束=1，否则=0）
	public int iCancelFlag; // 取消标志（取消发送=1，否则=0）
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		CustomLog.e("UploadVoiceResponse：iMsgId == "+iMsgId+" base_iRet == "+base_iRet);
		if(iMsgId == 0 && base_iRet == 0){
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(true);
			}
			return ;
		}else if(base_iRet != 0){
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(false);
			}
//			return;
		}
		
		HandleMessageRespone.onMsgRespone(iMsgId, pcClientMsgId, iCreateTime,base_iRet);
	}
}
