package com.yzxtcp.tools.tcp.packet.factory;

import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
/**
 * 抽象的解析工厂
 * @author zhuqian
 */
public abstract class IUCSFactory {
	/**
	 * 创建数据包
	 * @param cmdId 命令码
	 * @return
	 */
	protected abstract IUCSRequest createUCSRequest(int cmdId);
	
	/**
	 * 创建协议包头
	 * @param request
	 * @param serviceId
	 * @return
	 */
	protected abstract IUCSRequest.Header createUCSHead(IUCSRequest request,int serviceId);
	
	/**
	 * 创建解密好的响应
	 * @param cmdId 命令码
	 * @param data 包体数据
	 * @return
	 */
	protected abstract IUCSRequest.UCSResponse createUCSResponse(int cmdId,byte[] data);
	
	/**
	 * 生产msgId
	 * @return
	 */
	protected abstract String generateMsgId();
}
