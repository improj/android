package com.yzxtcp.tcp.identity.impl;

import org.json.JSONArray;
import org.json.JSONException;
import com.yzxtcp.tcp.TcpConnection;
import com.yzxtcp.tcp.identity.IConnectPlicy;
import com.yzxtcp.tools.CpsUtils;
import com.yzxtcp.tools.TCPLog;

/**
 * 以Proxy的方式连接TCP策略
 * 
 * @author zhuqian
 */
public class ProxyConnectPlicy extends IConnectPlicy {
	private TcpConnection tcpConnection;

	public ProxyConnectPlicy(TcpConnection tcpConnection) {
		this.tcpConnection = tcpConnection;
	}

	@Override
	public boolean connectPlicy(String proxyIp) {
		// 判断本地是否存有PROXY IP
		if (!CpsUtils.hasLocalProxyIP()) {
			TCPLog.d("connectPlicy：未找到ProxyIP");
			return false;
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(proxyIp);
		} catch (Exception e) {
			e.printStackTrace();
			TCPLog.d("connectPlicy：解析json失败, " + e.getMessage());
			return false;
		}
		// 遍历保存的PROXY IP
		for (int i = 0; i < jsonArray.length(); i++) {
			int mRetryCnt = 0;
			String ip = "";
			try {
				ip = jsonArray.getString(i);
			} catch (JSONException e) {
				TCPLog.d("connectPlicy：jsonArray.getString("+i+") fail continue.");
				e.printStackTrace();
				continue;
			}
			String[] address = ip.split(":");
			if(address.length != 2) {	//规避服务器返回的地址有问题
				continue;
			} 
			// 每个IP重试两次
			while (mRetryCnt < 2) {
				try {
					this.tcpConnection.connection(address[0],
							Integer.parseInt(address[1]));
					CpsUtils.replaceIPtoFirst(ip);// 将连接成功的IP放到第一个PROXYIP
					return true;
				} catch (Exception e) {// tcp异常，需要重试
					mRetryCnt++;
					TCPLog.d("connectToProxy:" + e.getMessage());
					this.tcpConnection.shutdown();
					// 最后一次连接仍然异常，异常Throw到外部处理
					if (jsonArray.length() - 1 == i && mRetryCnt == 2) {
						return false;
					}
				}
			}
		}
		
		return false;
	}

}
