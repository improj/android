package com.yzxtcp.tools.tcp.packet.common.request;

import com.yzxtcp.tools.ParserUtils;

/**
 * IUCSRequest包含请求和响应
 * 
 * @author zhuqian
 */
public abstract class IUCSRequest {
	// 协议版本，包头24字节
	protected static final int PACKAGE_HEAD_LEN = 24;

	// 数据(包头+包体)
	public byte[] data = null;
	// 包头数据
	protected byte[] head = null;
	// 包体数据
	protected byte[] body = null;
	// 包头
	public Header header;
	// 消息id
	public String msgId;

	/**
	 * 子类公用，序列化包头
	 */
	protected void onPackHead() {
		if (header != null) {
			head = new byte[24];
			header.headLength = (short) head.length;
			// 包长度 = (包头+包体长度)
			header.packLength = header.headLength + body.length;
			// 获取子类传递的包头
			header.commCode = header.obtainCommId();
			// 获得包长
			byte[] packLenBytes = ParserUtils.intToByte(header.packLength);
			// 获得包头长
			byte[] packHeadLenBytes = ParserUtils
					.shortToByte(header.headLength);
			// 获得协议版本
			byte[] protocolBytes = ParserUtils
					.shortToByte(header.protocolVersion);
			// 获得命令号
			byte[] cmdBytes = ParserUtils.intToByte(header.commCode);
			// 获得序列号
			byte[] sequenceBytes = ParserUtils.intToByte(header.sequence);
			// 获得客户端id
			byte[] clientIdBytes = ParserUtils.intToByte(header.clientId);
			// 获得业务服务器id
			byte[] serviceIdBytes = ParserUtils.intToByte(header.serviceId);

			int resetLen = 0;
			// 拷贝到头
			System.arraycopy(packLenBytes, 0, head, resetLen,
					packLenBytes.length);
			resetLen += packLenBytes.length;
			System.arraycopy(packHeadLenBytes, 0, head, resetLen,
					packHeadLenBytes.length);
			resetLen += packHeadLenBytes.length;
			System.arraycopy(protocolBytes, 0, head, resetLen,
					protocolBytes.length);
			resetLen += protocolBytes.length;
			System.arraycopy(cmdBytes, 0, head, resetLen, cmdBytes.length);
			resetLen += cmdBytes.length;
			System.arraycopy(sequenceBytes, 0, head, resetLen,
					sequenceBytes.length);
			resetLen += sequenceBytes.length;
			System.arraycopy(clientIdBytes, 0, head, resetLen,
					clientIdBytes.length);
			resetLen += clientIdBytes.length;
			System.arraycopy(serviceIdBytes, 0, head, resetLen,
					serviceIdBytes.length);
		}
	}

	/**
	 * 组包体方法(具体子类实现)
	 * 
	 * @return 加密后的结果
	 */
	protected abstract void onPack(byte[] packJson);

	public abstract static class UCSResponse {
		public String msgId;
		// 发送结果
		public String result;

		public String getMsgId() {
			return msgId;
		}

		public void setMsgId(String msgId) {
			this.msgId = msgId;
		}

		/**
		 * 解包方法(具体子类实现)
		 * 
		 * @param data
		 */
		public abstract void onUnPack(byte[] data);
	}

	/**
	 * 数据包头(详情请见协议)
	 * 
	 * @author zhuqian
	 */
	public abstract static class Header {
		// 数据总长度
		public int packLength;
		// 包头长度
		public short headLength;
		// 协议版本
		public short protocolVersion;
		// 命令码
		public int commCode;
		// 序列号
		public int sequence;
		// 客户端id
		public int clientId;
		// 业务服务器id
		public int serviceId;

		/**
		 * 子类必须设置commId
		 * 
		 * @return
		 */
		public abstract int obtainCommId();
	}

	/**
	 * 发送TCP数据包回调
	 * 
	 * @author zhuqian
	 */
	public static abstract class OnSendUCSRequestListener {
		public abstract void onSend(int sendResult, IUCSRequest content);
	}

	/**
	 * 接收TCP数据包回调
	 * 
	 * @author zhuqian
	 */
	public static abstract class OnReceiveUCSListener {
		/**
		 * 收到数据回调
		 * 
		 * @param cmdId
		 *            命令码
		 * @param data
		 *            包体数据
		 * @param serviceId
		 *            业务服务器id
		 */
		public abstract void onReceive(int cmdId, byte[] data, int serviceId);
	}

	/**
	 * 发送TCP内容错误码
	 * 
	 * @author zhuqian
	 */
	public static class SendErrorCode {
		// 发送成功
		public static final int SEND_SUCCESS = 0;
		// 对方不在线
		public static final int NO_ONLINE = 1;
		// 消息不可达
		public static final int MESSAGE_NO_ARRIVED = 2;
		// 发送超时
		public static final int SEND_TIMEOUT = 3; // 有可能是对方不在线或者对方回复了，服务器没有收到 发送了透传请求 没收到对方回复就返回3
		// 对方版本不支持
		public static final int VERSION_NO_SUPPORT = 4;
		// 上一条消息超时
		public static final int OLD_MSG_TIMEOUT = 5;
		// TCP已经断开
		public static final int TCP_NO_CONNECTION = 6;
		// 发送的数据为Null
		public static final int CONTENT_NULL_DATA = 7;
		// 加密content失败
		public static final int ENCODE_CONTENT_FAIL = 8;
		// 传递的data超过500
		public static final int DATA_TOO_LARGE = 9;
		// 用户不存在
		public static final int ERROR_USER_NO_EXISTS = 11;
	}
}
