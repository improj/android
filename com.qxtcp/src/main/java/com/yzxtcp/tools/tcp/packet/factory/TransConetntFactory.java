package com.yzxtcp.tools.tcp.packet.factory;

import android.text.TextUtils;

import com.yzxtcp.data.UserData;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.common.request.HasUnPackTransRequest;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
import com.yzxtcp.tools.tcp.packet.common.request.UCSTransRequest;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.UCSResponse;
import com.yzxtcp.tools.tcp.packet.common.UCSTransStock;
/**
 * 透传数据生产工厂(单例)
 * @author zhuqian
 */
public class TransConetntFactory extends BaseUCSFactory {
	
	private static TransConetntFactory mInstance;
	public static TransConetntFactory obtain(){
		if(mInstance == null){
			synchronized (TransConetntFactory.class) {
				if(mInstance == null){
					mInstance = new TransConetntFactory();
				}
			}
		}
		return mInstance;
	}
	private TransConetntFactory(){
		
	}

	@Override
	public IUCSRequest createUCSRequest(int cmdId) {
		return super.createUCSRequest(cmdId);
	}

	/**
	 * 创建IUCSTransRequest请求
	 * @param stock 开发者传入UCSTransStock参数
	 * @return
	 */
	public IUCSRequest createUCSTransRequest(final UCSTransStock stock) {
		UCSTransRequest request = (UCSTransRequest) createUCSRequest(4000);
		//初始化
		request.msgId = generateMsgId();
		request.fromUserId = UserData.getUserId();
		request.toUserId = stock.targetId;
		request.appId = UserData.getAppid();
		//设置要发送的数据
		request.sendData = stock.onTranslate().getBytes();
		TCPLog.v("previewImgData:" + stock.onPreviewImgData());
		if(!TextUtils.isEmpty(stock.onPreviewImgData())) { // 判断发送的视频预览图片地址是否为空,格式 callid@@@pathfile
			String[] previewData = stock.onPreviewImgData().split("@@@");
			if(previewData.length == 2) {
				request.callid = previewData[0];
				request.url = previewData[1];
			}
		}
		//序列化
		request.pack();
		return request;
	}
	
	/**
	 * sdk生成IUCSRequest
	 * @param data 传入body数据
	 * @return
	 */
	public UCSTransRequest createUCSTransRequest(byte[] data) {
		HasUnPackTransRequest request = new HasUnPackTransRequest();
		//序列化
		request.onUnPack(data);
		return request;
	}
	@Override
	public UCSResponse createUCSResponse(int cmdId, byte[] data) {
		return super.createUCSResponse(cmdId, data);
	}
	public UCSTransRequest createUCSResponse(String msgId,int errorCode,int serviceId) {
		HasUnPackTransRequest.HasUnPackTransResponse reponse = new HasUnPackTransRequest.HasUnPackTransResponse(errorCode);
		reponse.msgId = msgId;
		reponse.setServiceId(serviceId);
		reponse.pack();
		return reponse;
	}
}
