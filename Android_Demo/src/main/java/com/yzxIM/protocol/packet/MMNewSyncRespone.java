package com.yzxIM.protocol.packet;

import com.yzxIM.data.IMUserData;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

//消息同步应答
public class MMNewSyncRespone extends IGGBaseResponse {
	public int iRet; // 同步结果标记
	public int iContinueFlag; // 还需要同步的iSelector,0为不需要再 NewSync
	public int iStates; // 同步状态 定义与mysyncdef.h
	public String tReceiveContent;
	public String tGroupContent;
	public String tGroupName;
	public int iVal; // 序列号
	public int iValGroup; // 群组序列号
	public int iModyChatroomName = 201;

	private String[] msg;

	@Override
	public synchronized void onMsgResponse() {
		CustomLog.e("iVal==" + iVal);
		// 同步讨论组信息
		if (StringUtils.isEmpty(tGroupContent) == false) {
			msg = tGroupContent.split("\1\2\3");// 讨论组消息分隔符
			if (HandleMessageRespone.handleGroupMessage(msg)) {
				UserData.saveGroupiVal(iValGroup, IMUserData.getUserName());
			}
		}
		// 普通消息
		if (StringUtils.isEmpty(tReceiveContent) == false) {
			CustomLog.e("普通消息");
			msg = tReceiveContent.split("==,,==");
			for(String item : msg){
				CustomLog.v(item);
			}
			if (HandleMessageRespone.handleChatMessage(msg)) {
				UserData.saveiVal(iVal, IMUserData.getUserName());
			}
		}

		// 被踢出讨论组
		if (StringUtils.isEmpty(tGroupName) == false) {
			CustomLog.e("被踢讨论组名称:" + tGroupName);
			HandleMessageRespone.handleGroupSelfInfo(tGroupName);
			UserData.saveGroupiVal(iValGroup, IMUserData.getUserName());
		}

		if (iModyChatroomName != 201) {
			CustomLog.e("修改讨论组名字 result:" + iModyChatroomName);
			UcsReason reason = new UcsReason();
			if (iModyChatroomName == 0) {
				reason.setReason(iModyChatroomName).setMsg("修改讨论组名字成功");
			} else {
				reason.setReason(UcsErrorCode.IM_ERROR_MODIFYDISFAIL).setMsg(
						"修改讨论组名字失败");
			}

			IMListenerManager.getInstance().notifyDisGroupCallBack(
					IMListenerManager.DGModifyName, reason, null);
		}
	}

}
