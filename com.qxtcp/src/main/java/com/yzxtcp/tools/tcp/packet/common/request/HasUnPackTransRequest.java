package com.yzxtcp.tools.tcp.packet.common.request;

import org.json.JSONObject;

import android.text.TextUtils;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.TCPLog;
/**
 * 带反序列化的透传请求
 * 
 * @author zhuqian
 */
public class HasUnPackTransRequest extends UCSTransRequest {

	// 传递给开发者的数据
	public byte[] recvData;
	
	public String recvPreviewImgUrl = ""; // 透传响应，视频预览图片地址数据
	public String recvPreviewImgCallid = ""; // 透传响应，视频预览图片callid数据

	/**
	 * 解析包
	 * 
	 * @param data
	 *            包体
	 */
	public void onUnPack(byte[] data) {
		try {
			JSONObject obj = new JSONObject(new String(data));
			TCPLog.d("unPack:" + obj.toString());
			if (obj.has("msgid")) {
				msgId = obj.getString("msgid");
			}
			if (obj.has("appid")) {
				appId = obj.getString("appid");
			}
			if (obj.has("fuserid")) {
				fromUserId = obj.getString("fuserid");
			}
			if (obj.has("tuserid")) {
				toUserId = obj.getString("tuserid");
			}
			if (obj.has("data")) {
				String dataStr = obj.getString("data");
				recvData = dataStr.getBytes();
			}
			// 解析透传数据
			if(obj.has("ucpdata")) {
				recvPreviewImgUrl = obj.getJSONObject("ucpdata").getJSONObject("previewData").getString("url");
				recvPreviewImgCallid = obj.getJSONObject("ucpdata").getJSONObject("previewData").getString("callid");
			}
		} catch (Exception e) {
			CustomLog.e("HasUnPackTransRequest onUnPack parser Json fail...");
		}
	}

	/**
	 * 带序列化的透传请求
	 * 
	 * @author zhuqian
	 */
	public static class HasUnPackTransResponse extends UCSTransRequest {
		private String errorCode;
		
		private int serviceId;

		public HasUnPackTransResponse(int errorCode) {
			this.errorCode = String.valueOf(errorCode);
		}
		
		public void setServiceId(int serviceId){
			this.serviceId = serviceId;
		}
		
		/**
		 * 传递serviceId给父类
		 */
		@Override
		protected int onRequestHeaderServiceId() {
			return this.serviceId;
		}
		
		/**
		 * 接收返回(op = 0x02)
		 * @return
		 */
		@Override
		protected byte onRequestOp() {
			return 0x02;
		}
		
		
		@Override
		protected String onRequestJsonStr() {
			// 用户要发送的数据
			JSONObject obj = null;
			try {
				obj = new JSONObject();
				if (!TextUtils.isEmpty(msgId)) {
					obj.put("msgid", msgId);
				}
				if (!TextUtils.isEmpty(msgId)) {
					obj.put("errcode", errorCode);
				}
				//响应中加入透传字段
				String ackStr = YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0).getString("TRANS_ACK_DATA", "");
				obj.put("ackdata", ackStr);
			} catch (Exception e) {
				e.printStackTrace();
				CustomLog.e("HasUnPackTransResponse init json fail...");
				return null;
			}
			return obj == null ? "" : obj.toString();
		}
	}
}
