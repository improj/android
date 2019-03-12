package com.yzxtcp.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.DataPacket;

public class TcpConnection {
	
	private Socket socket;
	protected PacketReader packetReader;
	protected PacketWriter packetWriter;
	protected DataInputStream reader;
	protected OutputStream writer;
	protected int port ;
	protected String host;
	
	/**
	 * 建立SOCKET连接
	 * @param host:SOCKET连接地址
	 * @param port：SOCKET端口
	 */
	public void connection(String host, int port)throws Exception {
		try {
			TCPLog.d("host :"+host+" is Reachabled ："+isReachable(host) + " port:" +  port);
			//初始化socket
			socket = new Socket();
			//连接socket
			socket.connect(new InetSocketAddress(host,port),20000);
			TCPLog.d("初始化socket成功");
		}finally {
			//初始化Reader和Writer
			if (isConnection()) {
				TCPLog.d("启动Reader和Writer ip = "+host+"，port = "+port);
				initReaderAndWriter();
				packetWriter = new PacketWriter(this);
				packetReader = new PacketReader(this);
				this.port = port;
				this.host = host;
				startup();
			}
		}
	}
	/**
	 * 返回地址是否可用
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private boolean isReachable(String host) throws IOException{
		int timeout = 5000;
		boolean status = false;
		status = InetAddress.getByName(host).isReachable(timeout);
		return status;
	}
	public int getPort() {
		return this.port;
	}

	public String getHost() {
		return (this.host != null && this.host.length() > 0) ? this.host : "";
	}
	
	public boolean isConnection() {
		return socket != null ? socket.isConnected() : false;
	}

	/**
	 * 初始化网络数据读写对像
	 * 
	 */
	private void initReaderAndWriter() {
		try {
			reader = new DataInputStream(socket.getInputStream());
			writer = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送数据包
	 * @param packet
	 */
	public void sendPacket(final DataPacket packet) {
		if (packetWriter != null) {
			packetWriter.sendPacket(packet);
		}else{
			CustomLog.e("packetWriter == null");
		}
	}

	/**
	 * 启动网络套接字读写流
	 * 
	 */
	private void startup() {
		if (packetReader != null) {
			packetReader.startup();
		}
		if (packetWriter != null) {
			packetWriter.startup();
		}
	}

	/**
	 * 网络连接关闭时，回收所有网络对像
	 * 
	 */
	public void shutdown() {
		try {
			//销毁Reader和Writer
			if (packetReader != null) {
				packetReader.shutdown();
				packetReader = null;
			}
			if (packetWriter != null) {
				packetWriter.shutdown();
				packetWriter = null;
			}
			//销毁socket
			if (socket != null) {
				socket.close();
				TCPLog.d("SOCKET CLOSE ...");
			}
		} catch (Exception e) {
			TCPLog.d("SOCKET CLOSE Message ： "+e.toString());
		}finally{
			socket = null;
		}
	}
}
