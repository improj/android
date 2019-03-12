package com.yzxIM.protocol.packet;

import com.yzxtcp.tcp.ImageUploader;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGUploadMsgImgResponse extends IGGBaseResponse {
	public int iMsgId; // 服务端产生的MSGID
	public String pcClientMsgId; // 消息ID，客户端生成的
	public String pcFromUserName; // 发起者
	public String pcToUserName; // 接收者
	public int iTotalLen; // 总大小
	public int iStartPos; // 下一个包的起始位置
	public int iDataLen;
	public int iCreateTime; // 完整接收之后产生的时间戳
	@Override
	public void onMsgResponse() {
		CustomLog.e("上传图片返回：iMsgId == "+iMsgId+" base_iRet == "+base_iRet);
		if(iMsgId == 0 && base_iRet == 0){
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(true);
			}
			return ;
		}else if(base_iRet != 0){
//			UserData.saveImgSendState(1);// 发送失败
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(false);
			}
//			return;
		}
		HandleMessageRespone.onMsgRespone(iMsgId, pcClientMsgId, iCreateTime, base_iRet);
	}
}
