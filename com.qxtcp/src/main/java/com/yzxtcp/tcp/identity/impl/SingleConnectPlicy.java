package com.yzxtcp.tcp.identity.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import com.yzxtcp.tcp.TcpConnection;
import com.yzxtcp.tcp.identity.IConnectPlicy;
import com.yzxtcp.tools.TCPLog;

/**
 * 单连接TCP策略
 * 
 * @author zhuqian
 */
public class SingleConnectPlicy extends IConnectPlicy{
	private TcpConnection tcpConnection;
	public SingleConnectPlicy(TcpConnection tcpConnection){
		this.tcpConnection = tcpConnection;
	}

	@Override
	public boolean connectPlicy(String ip) {
		try {
			String[] address = ip.split(":");
			tcpConnection.connection(address[0],
					Integer.parseInt(address[1]));
			TCPLog.d("connect finish");
			if (tcpConnection.isConnection()) {
				return true;
			}else{
				tcpConnection.shutdown();
			}
		} catch (Exception e) {
			tcpConnection.shutdown();
			if(e instanceof UnknownHostException){
				TCPLog.d("TcpConnection UnknownHostException:" + e.toString());
			}else if(e instanceof IOException){
				TCPLog.d("TcpConnection IOException:" + e.toString());
			}else{
				TCPLog.d("TcpConnection Exception:" + e.toString());
			}
			//通知重连
		}
		return false;
	}
}
