package com.yzx.protocol.packet;  

import android.text.TextUtils;

import com.yzx.controller.TimerHandler;
import com.yzx.listenerInterface.VoipListenerManager;
import com.yzx.preference.UserData;
import com.yzxtcp.tcp.ImageUploader;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

/**
 * @Title IGGUploadPreviewImgResponse   
 * @Description  上传预览图片响应 响应码：30052
 * @Company yunzhixun  
 * @author xhb
 * @date 2017-2-17 下午2:27:22
 */
public class IGGUploadPreviewImgResponse extends IGGBaseResponse {
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
		CustomLog.d("上传视频预览图片返回：iMsgId == "+iMsgId+" base_iRet == "+base_iRet + " pcClientMsgId == " + pcClientMsgId);
		if(iMsgId == 0 && base_iRet == 0){
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(true);
			}
			if(!TextUtils.isEmpty(tErrMsg)) { // 有数据并且base_iRet=0，则代表发送成功
				// 取消监听
				CustomLog.d("tErrMsg:" + tErrMsg);
				TimerHandler.getInstance().stopVideoPreviewTimer();
				UserData.setPreviewImgUrl(tErrMsg);
				if(VoipListenerManager.getInstance().getPreviewImgUrlListener() != null) {
					VoipListenerManager.getInstance().getPreviewImgUrlListener().callback();
					VoipListenerManager.getInstance().setPreviewImgUrlListener(null); // 设置为空
				}
			}
			return ;
		}else if(base_iRet != 0){ 
			ImageUploader imageUploader = TCPServer.obtainTCPService().getImageUploader(pcClientMsgId);
			if(imageUploader != null){
				imageUploader.notifySendNext(false);
			}
			// 如果上传失败了，也进行拨打电话。
			TimerHandler.getInstance().stopVideoPreviewTimer();
			if(VoipListenerManager.getInstance().getPreviewImgUrlListener() != null) {
				VoipListenerManager.getInstance().getPreviewImgUrlListener().callback();
				VoipListenerManager.getInstance().setPreviewImgUrlListener(null); // 设置为空
			}
		}
	}

}
  
