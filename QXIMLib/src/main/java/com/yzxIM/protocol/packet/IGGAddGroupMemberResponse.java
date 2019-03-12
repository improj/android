package com.yzxIM.protocol.packet;

import java.util.List;

import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

/************************* 讨论组加人回应 *******************/
public class IGGAddGroupMemberResponse extends IGGBaseResponse {
	public int iMemberCount; // 已处理的成员数
	public String ptMemberList; // 已处理的成员列表 成员名字:状态
	public List<IGGMemberResp> memberList;// 已处理的成员列表
	public String iChatRoomId;// 讨论组ID

	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();

	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		if ((base_iRet == 0)&&(!iChatRoomId.equals("0"))) {
			// memberList = new ArrayList<IGGMemberResp>();
			String member[] = ptMemberList.split("==,,==");
			/*
			 * for(int i = 0; i < iMemberCount; i++){ String memContent[] =
			 * member[i].split("@@@"); IGGMemberResp mResp = new
			 * IGGMemberResp(); mResp.tMemberName = memContent[0];
			 * mResp.iMemberStatus = Integer.valueOf(memContent[1]);
			 * CustomLog.e("mResp"+i+"="+mResp.tMemberName+mResp.iMemberStatus);
			 * }
			 */

			StringBuilder discussionMembers = new StringBuilder();
			for (int i = 0; i < iMemberCount; i++) {
				String memContent[] = member[i].split("@@@");
				discussionMembers.append(memContent[0]);
				if (i < iMemberCount - 1) {
					discussionMembers.append(",");
				}
			}
			CustomLog.e("iChatRoomId=" + iChatRoomId);

			DBManager.getInstance().updateDiscussionMemlist(
					iChatRoomId, discussionMembers.toString(), iMemberCount,1);
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGAddMem, new UcsReason().setReason(0)
							.setMsg("添加讨论组成员成功"), null);

		} else {
			CustomLog.e("添加讨论组成员失败");
			imListenerManager.notifyDisGroupCallBack(
					IMListenerManager.DGAddMem, new UcsReason()
					.setReason(UcsErrorCode.IM_ERROR_ADDUSERFAIL)
							.setMsg(""), null);
		}
	}
}