package com.yzxtcp.tools.tcp.packet.common.request;


/**
 * 基础的序列化(创建Head)
 * @author zhuqian
 */
public abstract class BaseUCSRequest extends IUCSRequest {
	/**
	 * 先初始化Head信息
	 */
	@Override
	protected void onPack(byte[] packJson) {
		header = new Header() {
			@Override
			public int obtainCommId() {
				return requestCommId();
			}
		};
		//初始化Head信息
		header.sequence = onRequestHeaderSequence();
		header.clientId = onRequestHeaderClientId();
		header.serviceId = onRequestHeaderServiceId();
		header.protocolVersion = onRequestHeaderProtocolVersion();
		onPackHead();
		// 拷贝包头
		int resetLen = 0;
		data = new byte[body.length + head.length];
		// 拷贝包头+包体
		System.arraycopy(head, 0, data, resetLen, head.length);
		resetLen += head.length;
		System.arraycopy(body, 0, data, resetLen, body.length);
	}
	/**
	 * 父类初始化sequence，子类可以选择性覆写
	 * @return
	 */
	protected int onRequestHeaderSequence(){
		return 0;
	}
	/**
	 * 父类初始化clientId，子类可以选择性覆写
	 * @return
	 */
	protected int onRequestHeaderClientId(){
		return 0;
	}
	/**
	 * 父类初始化serviceId，子类可以选择性覆写
	 * @return
	 */
	protected int onRequestHeaderServiceId(){
		return 0;
	}
	/**
	 * 父类初始化protocolVersion，子类可以选择性覆写
	 * @return
	 */
	protected short onRequestHeaderProtocolVersion(){
		return 0x01;
	}
	/**
	 * 子类复写
	 * @return 命令码
	 */
	protected abstract int requestCommId();
}
