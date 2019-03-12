package com.yzxtcp.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.DataPacket;

/**
 * 消息写入类
 *
 */
public class PacketWriter {
	private Queue<DataPacket> queue = new LinkedList<DataPacket>();
	private Thread writerThread;
	protected boolean done = false; // 客户端是否以经断线
	private OutputStream writer;
	public PacketWriter(TcpConnection c) {
		done = false;
		writer = c.writer;
		writerThread = new Thread("writerThread") {
			@Override
			public void run() {
				TCPLog.d("PacketWriter thread: " + Thread.currentThread().getName());
				writePackets(this);
			}
		};
		// 标识为守护线程
		writerThread.setDaemon(true);
	}

	/**
	 * 
	 * 发送数据包
	 * 
	 * @param thisThread
	 */
	private void writePackets(Thread thisThread) {
		while (!done && thisThread == writerThread) {
			DataPacket packet = nextPacket();
			if (packet != null) {
				try {
					writer.write(packet.buf);
					writer.flush();
				} catch (IOException e) {
					if(done){
						TCPLog.d("主动断开，Writer不用重连");
					}else{
						TCPLog.d("被动断开，Writer开启重连");
						TCPServer.obtainTCPService().reconnect();
					}
					done = true;
					e.printStackTrace();
				}
			}
		}
		TCPLog.d("PacketWriter done : " + done);
		queue.clear();
	}

	public void sendPacket(DataPacket packet) {
		if (packet != null) {
			synchronized (queue) {
				if (!done) {
					queue.offer(packet);
					queue.notifyAll();
					TCPLog.d("PacketWriter sendPacket " + "线程 " + Thread.currentThread().getName() + " 调用了object.notifyAll()");
				}
			}
			TCPLog.d("PacketWriter sendPacket " + "线程 " + Thread.currentThread().getName() + " 释放了锁");
		}else{
			CustomLog.e("packet == null");
		}
	}

	/**
	 * 
	 * 获取下一个要发送的数据包
	 * 
	 * @return
	 */
	private DataPacket nextPacket() {
		DataPacket packet = null;
		try{
			synchronized (queue) {
				while (!done && (packet = queue.poll()) == null) {
					try {
						TCPLog.d("线程 " + Thread.currentThread().getName() + " wait ...");
						queue.wait(); // 线程的执行被挂起，对象上的锁被释放
						TCPLog.d("线程 " + Thread.currentThread().getName() + " 获取到了锁");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return packet;
	}

	public void startup() {
		writerThread.start();
	}

	public void shutdown() {
		//isNotifyDisconnect = isNotify;
		synchronized (queue) {
			done = true;
			queue.notifyAll();
			TCPLog.d("PacketWriter shutdown " + "线程 " + Thread.currentThread().getName() + " 调用了object.notify()");
		}
		TCPLog.d("PacketWriter shutdown " + "线程 " + Thread.currentThread().getName() + " 释放了锁");
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			CustomLog.v( "PACKET_WRITE : " + done);
		}
	}
}
