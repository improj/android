package com.yzxIM.protocol.packet;

import java.util.ArrayList;
import java.util.List;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

/************************* 创建讨论组回应 **********************/
public class IGGCreateGroupResponse extends IGGBaseResponse {
	public String tIntroDuce;// 讨论组的主题
	public String tPYInitial;// 主题的拼音
	public String tQuanPin;// 讨论组主题的全拼
	public int iMemberCount;// 讨论组成员数
	public String ptMemberList; // 讨论组成员列表 成员名字:状态
	public List<IGGMemberResp> memberList; // 讨论组成员列表 成员名字:状态
	public String iChatRoomId;// 讨论组ID
	public byte[] tImgBuf;// 暂不使用
	public String pcBigHeadImgUrl;// 暂不使用
	public String pcSmallHeadImgUrl;// 暂不使用
	public int iMaxMemberCount;
	
	private IMListenerManager imListenerManager = IMListenerManager
			.getInstance();
	
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		List<ConversationInfo> recvCinfos = new ArrayList<ConversationInfo>();
		
		if((base_iRet == 0)&&(!iChatRoomId.equals("0"))){
			CustomLog.d("onMsgResponse 创建讨论组成功");
			memberList = new ArrayList<IGGMemberResp>();
			DBManager dbManager = DBManager.getInstance();
			String member[] = ptMemberList.split("==,,==");
			
			StringBuilder discussionMembers = new StringBuilder();
			discussionMembers.append(IMUserData.getUserName());
			discussionMembers.append(",");
			for(int i = 0;i < iMemberCount; i++){
				String memContent[] = member[i].split("@@@");
				discussionMembers.append(memContent[0]);
				if(i < iMemberCount-1){
					discussionMembers.append(",");
				}
			}
			CustomLog.d("discussionMembers:"+discussionMembers);
			CustomLog.e("iChatRoomId="+iChatRoomId);
			
			
			DiscussionInfo discussionInfo = new DiscussionInfo(iChatRoomId, 
					tIntroDuce, CategoryId.DISCUSSION, iMemberCount+1,
					IMUserData.getUserName(),discussionMembers.toString(), 
					null, System.currentTimeMillis());
			
			//添加讨论组信息到数据库
			DBManager.getInstance().addDiscussionInfo(discussionInfo);
			
			
			//创建新的会话
			ConversationInfo cinfo = dbManager.insertConversationToDb(iChatRoomId,
					CategoryId.DISCUSSION, "",
					discussionInfo.getDiscussionName(),1,discussionInfo.getCreateTime());
			recvCinfos.add(cinfo);
//			imListenerManager.notifyICovListener(IMListenerManager.COVCreate, cinfo);
			imListenerManager.notifyICovListener(IMListenerManager.COVUpdate, recvCinfos);
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGCreate, 
					new UcsReason().setReason(0).setMsg("创建讨论组成功"), discussionInfo);
			
		}else{
			CustomLog.e("创建讨论组失败");
			imListenerManager.notifyDisGroupCallBack(IMListenerManager.DGCreate, 
					new UcsReason()
			.setReason(UcsErrorCode.IM_ERROR_CREATEDISFAIL)
			.setMsg("创建讨论组失败"), null);
		}
		
	}
}
