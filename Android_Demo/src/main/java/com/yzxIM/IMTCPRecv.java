package com.yzxIM;

import com.yzxIM.protocol.packet.IGGAddGroupMemberResponse;
import com.yzxIM.protocol.packet.IGGCreateGroupResponse;
import com.yzxIM.protocol.packet.IGGDelGroupMemberResponse;
import com.yzxIM.protocol.packet.IGGDownloadMsgImgResponse;
import com.yzxIM.protocol.packet.IGGDownloadVoiceResponse;
import com.yzxIM.protocol.packet.IGGNewInitResponse;
import com.yzxIM.protocol.packet.IGGQuitGroupResponse;
import com.yzxIM.protocol.packet.IGGUploadMsgImgResponse;
import com.yzxIM.protocol.packet.IGGUploadVoiceResponse;
import com.yzxIM.protocol.packet.MMNewSyncRequest;
import com.yzxIM.protocol.packet.MMNewSyncRespone;
import com.yzxIM.protocol.packet.MMSendMsgResponse;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;

public class IMTCPRecv implements ITcpRecvListener{

	@Override
	public void onRecvMessage(int cmd, byte[] buf) {
		CustomLog.e("IMTCPRecv onRecvMessage:"+cmd);
		switch (cmd) {
		case 30003:
			MMSendMsgResponse sendMsgResponse = new MMSendMsgResponse();
			sendMsgResponse = (MMSendMsgResponse) sendMsgResponse.uppacket(cmd, buf, sendMsgResponse);
			sendMsgResponse.onMsgResponse();
			break;
		case 10600010:
			IGGNewInitResponse newInitResponse = new IGGNewInitResponse(); 
			newInitResponse = (IGGNewInitResponse) newInitResponse.uppacket(cmd, buf, newInitResponse);
			if(StringUtils.isEmpty(newInitResponse.tGroupContent) == true 
					&& StringUtils.isEmpty(newInitResponse.tReceiveContent) == true
					&& StringUtils.isEmpty(newInitResponse.tGroupName) == true){
				CustomLog.e("没有新消息");
				break;
			}else{
				CustomLog.e("AAA:"+newInitResponse.tReceiveContent+newInitResponse.tReceiveContent.length());
				newInitResponse.onMsgResponse();
			}
			break;
		case 10600011:
			MMNewSyncRespone newSyncResponse = new MMNewSyncRespone();
			newSyncResponse = (MMNewSyncRespone) newSyncResponse.uppacket(cmd, buf, newSyncResponse);
			if(StringUtils.isEmpty(newSyncResponse.tGroupContent) == true 
					&& StringUtils.isEmpty(newSyncResponse.tReceiveContent) == true
					&& StringUtils.isEmpty(newSyncResponse.tGroupName) == true
					&& newSyncResponse.iModyChatroomName == 201){
				CustomLog.e("没有新消息");
				break;
			}else{
				CustomLog.e("AAA:"+newSyncResponse.tReceiveContent+newSyncResponse.tReceiveContent.length());
				newSyncResponse.onMsgResponse();
			}
			break;
		case 30051:
		case 30050:
		case 10600030:
			IGGUploadMsgImgResponse uploadMsg = new IGGUploadMsgImgResponse();
			uploadMsg = (IGGUploadMsgImgResponse) uploadMsg.uppacket(cmd, buf, uploadMsg);
			uploadMsg.onMsgResponse();
			CustomLog.e("AAA:"+uploadMsg.base_iRet);
			break;
		case 10600031:
			IGGDownloadMsgImgResponse downImgMsg = new IGGDownloadMsgImgResponse();
			downImgMsg = (IGGDownloadMsgImgResponse) downImgMsg.uppacket(cmd, buf, downImgMsg);
			downImgMsg.onMsgResponse();
			CustomLog.e("AAA:"+downImgMsg.base_iRet);
			break;
		case 10600035:
			IGGUploadVoiceResponse uploadVoiceMsg = new IGGUploadVoiceResponse();
			uploadVoiceMsg = (IGGUploadVoiceResponse) uploadVoiceMsg.uppacket(cmd, buf, uploadVoiceMsg);
			uploadVoiceMsg.onMsgResponse();
			CustomLog.e("AAA:"+uploadVoiceMsg.base_iRet);
			break;
		case 10600036:
			IGGDownloadVoiceResponse downVoiceMsg = new IGGDownloadVoiceResponse();
			downVoiceMsg = (IGGDownloadVoiceResponse) downVoiceMsg.uppacket(cmd, buf, downVoiceMsg);
			downVoiceMsg.onMsgResponse();
			CustomLog.e("AAA:"+downVoiceMsg.base_iRet);
			break;
		
		case 600016:
			if(buf.length==20){
				int iSelector = (int)((int) (buf[16] << 24 & 0xff000000) + (int) ((buf[17] << 16) & 0x00ff0000)
               		 + (int) ((buf[18] << 8) & 0xff00)+ (int) (buf[19] & 0xff));
				CustomLog.e("iSelector="+iSelector);
				switch (iSelector){
					case 2:
					case 4:
					case 6:
						MMNewSyncRequest newsyncRequest = new MMNewSyncRequest(
															iSelector,1,null,null);
						newsyncRequest.onSendMessage();
					break;
					/*case 8192: 
						IMListenerManager.getInstance().notifySdkStatus(
								new UcsReason()
								.setReason(UcsErrorCode.NET_ERROR_KICKOUT)
								.setMsg("服务器强制下线"));
					break;*/
				}
			}
			break;
		/*case 30002:
			IMListenerManager.getInstance()U.notifySdkStatus(
					new UcsReason().setReason(2).setMsg("长时间未登陆，请重新登陆"));
			break;*/
		case 30013:
			IGGCreateGroupResponse createGroup = new IGGCreateGroupResponse();
			createGroup=(IGGCreateGroupResponse) createGroup.uppacket(cmd, buf, createGroup);
			createGroup.onMsgResponse();
			break;
		case 30014:
			IGGAddGroupMemberResponse addMemGroup = new IGGAddGroupMemberResponse();
			addMemGroup=(IGGAddGroupMemberResponse) addMemGroup.uppacket(cmd, buf, addMemGroup);
			addMemGroup.onMsgResponse();
			break;
		case 30015:
			IGGDelGroupMemberResponse delMemGroup = new IGGDelGroupMemberResponse();
			delMemGroup=(IGGDelGroupMemberResponse) delMemGroup.uppacket(cmd, buf, delMemGroup);
			delMemGroup.onMsgResponse();
			break;
		case 30016:
			IGGQuitGroupResponse quitGroup = new IGGQuitGroupResponse();
			quitGroup=(IGGQuitGroupResponse) quitGroup.uppacket(cmd, buf, quitGroup);
			quitGroup.onMsgResponse();
			break;
		/*case 30050:
			IGGSendLocationResponse mapResponse = new IGGSendLocationResponse();
			mapResponse = (IGGSendLocationResponse) mapResponse.uppacket(cmd, buf, mapResponse);
			mapResponse.onMsgResponse();
			break;*/
		default:
			break;
		}
		
	}

	
}
