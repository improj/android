package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

/********************** 讨论组踢人回应 ***********************/
public class IGGDelGroupMemberResponse extends IGGBaseResponse {
	public int iMemberCount;// 踢出的成员数
	public String ptMemberList; // 踢出的成员列表
	public List<String> memberList = new ArrayList<String>(); // 踢出的成员列表
	public String iChatRoomId;// 讨论组ID

	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();

	@Override
	public void onMsgResponse() {
		if ((base_iRet == 0)&&(!iChatRoomId.equals("0"))) {
			String member[] = ptMemberList.split("@@@");
			for (int i = 0; i < iMemberCount; i++) {
				memberList.add(member[i]);
				CustomLog.e("member=" + member[i]);
			}
			CustomLog.e("iChatRoomId=" + iChatRoomId);

			// String discussionID = String.valueOf(iChatRoomId);
			String members = StringUtils.listToString(memberList);
			CustomLog.d("delgroupmembers:" + members);
			DBManager.getInstance().updateDiscussionMemlist(
					iChatRoomId, members, memberList.size(),0);
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGDelMem, new UcsReason().setReason(0)
							.setMsg("删除讨论组成员成功"), null);

		} else {
			CustomLog.e("删除讨论组成员失败");
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGDelMem, new UcsReason()
					.setReason(UcsErrorCode.IM_ERROR_DELUSERFAIL)
							.setMsg("删除讨论组成员失败"), null);

		}
	}
}
