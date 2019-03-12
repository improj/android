package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

public class IGGQuitGroupResponse extends IGGBaseResponse {
	public String iChatRoomId; // 讨论组ID
	public String tUserName; // 退出的用户名

	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();

	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		DBManager dbManager = DBManager.getInstance();
		CustomLog.d("iChatRoomId:"+iChatRoomId);
		if ((base_iRet == 0)&&(!iChatRoomId.equals("0"))) {
			// UserData.delDiscussionID(iChatRoomId.getBytes());
			// 删除讨论组信息
			dbManager.delDiscussionInfo(iChatRoomId);
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGQuit, new UcsReason().setReason(0)
							.setMsg("退出讨论组成功"), null);
			// 删除会话信息

			ConversationInfo cinfo = dbManager.getConversation(iChatRoomId);

			if (cinfo != null) {
				dbManager.delConversationInfo(iChatRoomId);
				recvCinfos.add(cinfo);
				imListenerManager.notifyICovListener(IMListenerManager.COVDel, recvCinfos);
			}
			dbManager.debugConversationTable();
		} else {
			CustomLog.e("退出讨论组失败");
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGQuit, new UcsReason()
					.setReason(UcsErrorCode.IM_ERROR_QUITDISFAIL)
							.setMsg("退出讨论组失败"), null);
		}
	}
}
