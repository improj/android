package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGNewInitResponse extends IGGBaseResponse {
	public int iRet; // 同步结果标记
	public int iContinueFlag; // 还需要同步的iSelector,0为不需要再 NewSync
	public int iStates; // 同步状态 定义与mysyncdef.h
	public String tReceiveContent;
	public String tGroupContent;
	public String tGroupName;
	public int iVal; // 序列号
	public int iValGroup; // 群组序列号

	private String[] msg ;
	
	/*
	 * dbMsg[0]= msgid 1=msgtype 2=fromUsername 3=toUsername 4 createTime
	 * 5=msgSource 6 PushContent 7=Content
	 */

	@Override
	public synchronized void onMsgResponse() {
		CustomLog.e("iVal==" + iVal + ",iValGroup:" + iValGroup);
		//同步讨论组信息
		if (StringUtils.isEmpty(tGroupContent) == false) {
			CustomLog.e("讨论组消息:  " + tGroupContent);
			msg = tGroupContent.split("\1\2\3");//讨论组消息分隔符
			CustomLog.e("msg.size = "+msg.length);
			if(HandleMessageRespone.handleGroupMessage(msg)){
				UserData.saveGroupiVal(iValGroup, IMUserData.getUserName());
			}
			
		}
		//普通消息
		if (StringUtils.isEmpty(tReceiveContent) == false) {
			CustomLog.d("普通消息");
			msg = tReceiveContent.split("==,,==");
			if(HandleMessageRespone.handleChatMessage(msg)){
				UserData.saveiVal(iVal, IMUserData.getUserName());
			}
			
		}
		
		if (StringUtils.isEmpty(tGroupName) == false) {
			CustomLog.d("被踢讨论组名称:"+tGroupName);
			HandleMessageRespone.handleGroupSelfInfo(tGroupName);
		}
		
		CustomLog.v("iContinueFlag:" + iContinueFlag);
		if(iContinueFlag!=0){
			new IGGNewInitRequest().onSendMessage();
		}
	}
}
