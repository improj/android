package com.yzxtcp.tools.tcp.packet.common;

import android.text.TextUtils;

import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.ParserUtils;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.provider.IProvider;
import com.yzxtcp.tools.provider.ProviderHandler;
import com.yzxtcp.tools.provider.RequestProvider;
import com.yzxtcp.tools.tcp.packet.common.request.HasUnPackTransRequest;
import com.yzxtcp.tools.tcp.packet.common.request.UCSTransRequest;
import com.yzxtcp.tools.tcp.packet.common.request.HasUnPackTransRequest.HasUnPackTransResponse;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.UCSResponse;
import com.yzxtcp.tools.tcp.packet.factory.TransConetntFactory;
/**
 * 请求处理
 * @author zhuqian
 */
public class UCSRequestHandler {
	
	public void HandleRequest(int cmdId,byte[] data,int serviceId){
		if(cmdId == 4000){
			byte[] serviceHeadBytes = new byte[2];
			System.arraycopy(data, 0, serviceHeadBytes, 0, 2);
			TCPLog.e("op : "+data[2]+"，serviceId ： "+serviceId+"，serviceHead ："+ParserUtils.byteToShort(serviceHeadBytes));
			if(data[2] == 0x01){ // 接收响应
				byte[] ucsData = new byte[data.length - 3];
				TCPLog.d("ucsData:" + ucsData);
				System.arraycopy(data, 3, ucsData, 0, ucsData.length);
				HasUnPackTransRequest ucsTransRequest = (HasUnPackTransRequest) TransConetntFactory.obtain().createUCSTransRequest(ucsData);
				//发送收到回执
				HasUnPackTransResponse ucsResponse = (HasUnPackTransResponse) TransConetntFactory.obtain().createUCSResponse(ucsTransRequest.msgId, 0,serviceId);
				
				TCPServer.obtainTCPService().sendPacket(4000, ucsResponse.data);
				//接收到请求透传
				if(!TextUtils.isEmpty(ucsTransRequest.recvPreviewImgUrl) && !TextUtils.isEmpty(ucsTransRequest.recvPreviewImgCallid)) {
					// 如果是视频预览图片透传，则不上抛到上层
					if(TCPListenerManager.getInstance().getPerviewImgTransListener() != null) {
						TCPListenerManager.getInstance().getPerviewImgTransListener().onRecvTranslate(ucsTransRequest.recvPreviewImgCallid, ucsTransRequest.recvPreviewImgUrl);
					}
				} else if(ucsTransRequest.recvData != null) {
					TCPListenerManager.getInstance().notifyOnRecvTransUCSListener(ucsTransRequest.fromUserId,new String(ucsTransRequest.recvData), ucsTransRequest.recvPreviewImgCallid, ucsTransRequest.recvPreviewImgUrl);
				}
			}else if(data[2] == 0x02){ // 发送响应
				//透传响应
				UCSResponse ucsResponse = TransConetntFactory.obtain().createUCSResponse(cmdId, data);
				IProvider provider = ProviderHandler.getProvider(ucsResponse.msgId);
				if(provider != null){
					int result = 0;
					if(TextUtils.isEmpty(ucsResponse.result)){
						result = 0;
					}else{
						result = Integer.parseInt(ucsResponse.result);
					}
					UCSTransRequest.UCSTransResponse transResponse = (UCSTransRequest.UCSTransResponse)ucsResponse;

					((RequestProvider)provider).onSend(result, TextUtils.isEmpty(transResponse.ackString) ? "":transResponse.ackString);
				}
			}
		}
	}
}
