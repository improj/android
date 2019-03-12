package com.yzxtcp.tools.tcp.packet.factory;

import java.util.Random;
import java.util.UUID;

import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.encrypt.Md5Generator;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
import com.yzxtcp.tools.tcp.packet.common.request.UCSTransRequest;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.Header;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.UCSResponse;

/**
 * 基础的解析工厂
 * 
 * @author zhuqian
 */
public abstract class BaseUCSFactory extends IUCSFactory {

	@Override
	protected Header createUCSHead(IUCSRequest request, int serviceId) {
		return null;
	}

	@Override
	protected IUCSRequest createUCSRequest(int cmdId) {
		IUCSRequest request= null;
		if(cmdId == 4000){
			request = new UCSTransRequest();
		}
		return request;
	}

	@Override
	protected UCSResponse createUCSResponse(int cmdId, byte[] data) {
		UCSResponse response = null;
		if (cmdId == 4000) {
			// 透传响应
			response = new UCSTransRequest.UCSTransResponse();
			response.onUnPack(data);
		}
		return response;
	}

	/**
	 * 生成唯一的msgId
	 */
	@Override
	protected String generateMsgId() {
		// md5(uuid+随机码)
		String uuid = UUID.randomUUID().toString();
		Random r = new Random();
		String romId = String.valueOf(r.nextInt(89999) + 10000);
		String msgId = Md5Generator.generate(uuid + "_" + romId);
		return msgId;
	}
}
