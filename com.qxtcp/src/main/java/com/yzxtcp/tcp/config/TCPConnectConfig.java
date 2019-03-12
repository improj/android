package com.yzxtcp.tcp.config;

import com.yzxtcp.tcp.identity.IConnectPlicy;
import com.yzxtcp.tcp.identity.IReconnectPlicy;
/**
 * TCP连接配置
 * 
 * @author zhuqian
 */
public class TCPConnectConfig {
	//连接策略
	public IConnectPlicy connectPlicy;
	//重连策略
	public IReconnectPlicy reconnectPlicy;
	
	private TCPConnectConfig(Builder build) {
		this.connectPlicy = build.connectPlicy;
		this.reconnectPlicy = build.reconnectPlicy;
	}
	
	/**
	 * Builder模式
	 * 
	 * @author zhuqian
	 */
	public static class Builder{
		IConnectPlicy connectPlicy;
		IReconnectPlicy reconnectPlicy;
		
		public Builder setConnectPlicy(IConnectPlicy connectPlicy) {
			this.connectPlicy = connectPlicy;
			return this;
		}

		public Builder setReconnectPlicy(IReconnectPlicy reconnectPlicy) {
			this.reconnectPlicy = reconnectPlicy;
			return this;
		}
		
		public TCPConnectConfig build() {
			TCPConnectConfig config = new TCPConnectConfig(this);
			return config;
		}
	}
}
