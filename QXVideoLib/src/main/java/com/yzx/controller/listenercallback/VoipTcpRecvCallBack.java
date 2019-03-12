  
package com.yzx.controller.listenercallback;  

import com.gl.softphone.UGoManager;
import com.yzx.protocol.packet.IGGUploadPreviewImgResponse;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipTcpRecvCallBack   
 * @Description  voip tcp 接收回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午5:05:47
 */
public class VoipTcpRecvCallBack implements ITcpRecvListener {

	@Override
	public void onRecvMessage(int cmd, byte[] message) {
		switch (cmd) {
		case 30052:	// 响应上传视频预览图片
			CustomLog.d("cmd:" + cmd);
			IGGUploadPreviewImgResponse uploadMsg = new IGGUploadPreviewImgResponse();
			uploadMsg = (IGGUploadPreviewImgResponse) uploadMsg.uppacket(cmd, message, uploadMsg);
			uploadMsg.onMsgResponse();
			break;
		case 2100: // VOIP数据
		case 3000:
			if (message != null && message.length > 0) {
				CustomLog.v( "UPDATE TCP MSG ... ");
				UGoManager.getInstance().pub_UGoTcpRecvMsg(message.length, message);
			}
			break;
		default:
			break;
		}
	}

}
  
