package com.yzxtcp.tools.tcp.packet.common.request;

import org.json.JSONObject;
import android.text.TextUtils;
import com.yzxtcp.tools.CustomLog;

/**
 * 透传请求
 * 
 * @author zhuqian
 */
public class UCSTransRequest extends BaseUCSRequest {
	// 应用id
	public String appId;
	// 发送id
	public String fromUserId;
	// 接收id
	public String toUserId;

	// 用户需要透传的数据
	public byte[] sendData;
	
	// 下面两个字段针对视频预览图片的透传
	// 需要发送的callid
	public String callid;
	// 需要发送的预览图片url
	public String url;
	
	@Override
	protected int requestCommId() {
		return 4000;
	}

	/**
	 * 序列化带json格式协议
	 * @param jsonStr
	 */
	public void pack() {
		String jsonStr = onRequestJsonStr();
		if(TextUtils.isEmpty(jsonStr)){//子类没有传递json字符串
			// 用户要发送的数据
			JSONObject obj = null;
			try {
				obj = new JSONObject();
				if (!TextUtils.isEmpty(appId)) {
					obj.put("appid", appId);
				} 
				if (!TextUtils.isEmpty(msgId)) {
					obj.put("msgid", msgId);
				}
				if (!TextUtils.isEmpty(fromUserId)) {
					obj.put("fuserid", fromUserId);
				}
				if (!TextUtils.isEmpty(toUserId)) {
					obj.put("tuserid", toUserId);
				}
				if (sendData != null && sendData.length > 0) {
					obj.put("data", new String(sendData));
				}
				if(!TextUtils.isEmpty(callid) && !TextUtils.isEmpty(url)) {
					// 携带视频预览图片透传数据
					JSONObject previewData = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("callid", callid);
					data.put("url", url);
					previewData.put("previewData",data);
					obj.put("ucpdata", previewData);
				}
				
				CustomLog.e("trans request: " + obj.toString());
				onPack(obj.toString().getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				CustomLog.e("UCSTransRequest init json fail...");
			}
		}else{
			//直接将子类传递的字符串序列化
			onPack(onRequestJsonStr().getBytes());
		}
	}
	/**
	 * 子类可以自行设置要发送的请求字符串
	 * @return
	 */
	protected String onRequestJsonStr(){
		return null;
	}
	/**
	 * 透传数据序列化
	 */
	@Override
	public void onPack(byte[] bodyBytes) {
		//拷贝业务包头和包体
		short bodyHeadLen = 3;
		byte[] bodyHeadBytes = new byte[2];
		// 高位在前
		bodyHeadBytes[0] = (byte) ((bodyHeadLen >>> 8) & 0xff);
		bodyHeadBytes[1] = (byte) ((bodyHeadLen >>> 0) & 0x00ff);
		byte[] opBytes = new byte[1];
		opBytes[0] = onRequestOp();
		int resetLen = 0;
		body = new byte[bodyBytes.length + 3];
		System.arraycopy(bodyHeadBytes, 0, body, resetLen, bodyHeadBytes.length);
		resetLen += bodyHeadBytes.length;
		System.arraycopy(opBytes, 0, body, resetLen, opBytes.length);
		resetLen += opBytes.length;
		System.arraycopy(bodyBytes, 0, body, resetLen, bodyBytes.length);
		super.onPack(bodyBytes);
	}
	/**
	 * 请求码(子类可以覆写)，默认为0x01;
	 * @return
	 */
	protected byte onRequestOp(){
		return 0x01;
	}

	/**
	 * 透传响应
	 * 
	 * @author zhuqian
	 */
	public static class UCSTransResponse extends UCSResponse {
		public String ackString = ""; //透传响应携带的字段\
		public String previewImgUrl = ""; // 透传响应，视频预览图片地址数据
		public String previewImgCallid = ""; // 透传响应，视频预览图片callid数据
		@Override
		public void onUnPack(byte[] data) {
			byte[] hasData = new byte[data.length - 3];
			// 过滤头三个字节
			System.arraycopy(data, 3, hasData, 0, hasData.length);
			try {
				JSONObject obj = new JSONObject(new String(hasData));
				if (obj.has("msgid")) {
					msgId = obj.getString("msgid");
				}
				if (obj.has("errcode")) {
					result = obj.getString("errcode");
				}
				if (obj.has("ackdata")) {
					ackString = obj.getString("ackdata");
				}
				// 解析视频预览图片透传数据
				if(obj.has("ucpdata")) {
					previewImgUrl = obj.getJSONObject("ucpdata").getJSONObject("previewData").getString("url");
					previewImgCallid = obj.getJSONObject("ucpdata").getJSONObject("previewData").getString("callid");
				}
				CustomLog.e("解析透传返回成功 errcode:" + result + " ackString:" + ackString + " previewImgUrl:" + previewImgUrl + " previewImgCallid:" + previewImgCallid);
			} catch (Exception e) {
				e.printStackTrace();
				CustomLog.e("解析透传返回失败");
			}
		}
	}
}
