package com.yzxIM.protocol.packet;

import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;


public class MMSendMsgResponse extends IGGBaseResponse {
	public int iCount;// =发送成功的消息个数
	public int iRet; // 消息处理结果 0为成功
	public String tFromUserName; // 发送方名字
	public String tToUserName;// 接收方名字
	public int iMsgId;// 此消息的Index编号
	public String pcClientMsgId;// 消息唯一标识
	public int iCreateTime;// 创建时间
	public int iType; // 消息类型
	
	@Override
	public void onMsgResponse() {
		int res = iRet==0&&base_iRet==0 ? 0:-1;
		CustomLog.e("res:"+res+"  imsgid:"+iMsgId+" pcClientMsgId ="+pcClientMsgId);
		if(iMsgId == 0 && res == 0){
			CustomLog.e("iMsgId == 0 && res == 0");
			return ;
		}
		HandleMessageRespone.onMsgRespone(iMsgId, pcClientMsgId, iCreateTime,res);
	}
}