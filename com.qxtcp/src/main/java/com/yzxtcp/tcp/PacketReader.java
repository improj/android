package com.yzxtcp.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.IGGAuthBySKResponse;
import com.yzxtcp.tools.tcp.packet.IGGAuthResponse;
import com.yzxtcp.tools.tcp.packet.common.UCSRequestHandler;

/**
 * 
 * 消息读取类
 * 
 */
public class PacketReader {
	private static final int HENDLEN = 16;
	private static final int MAXLEN = 7340032; // 7M

	private DataInputStream reader;
	private Thread readerThread;
	protected boolean done = false; // 客户端是否已经断线
	private byte[] head = new byte[HENDLEN];
	private byte[] body = null;
	private byte[] dataBuf = null;
	private byte[] buf = new byte[1024];
	private UCSRequestHandler mHandler;

	public PacketReader(TcpConnection c) {
		reader = c.reader;
		// 将计时器和请求响应传递给Reader线程
		readerThread = new Thread(new ReaderRunnable(), "readerThread");
		// 标识为守护线程
		readerThread.setDaemon(true);
	}

	private class ReaderRunnable implements Runnable {
		@Override
		public void run() {
			TCPLog.d("PacketReader thread: " + Thread.currentThread().getName());
			// CustomLog.v("--------------START READER THREAD-----------------");
			while (!done) {
				try {
					// 读数据包长 阻塞式方法
					int length = reader.read(head); // 如果读取不到数据，就会阻塞在这里，直到读取数据后才往下面执行，如果读取的长度返回为-1，则流结束
					if (length == -1) {
						TCPLog.d(" Server close out，Reader return -1");
						break;
					}
					int packetLength = (int) ((int) (head[0] << 24) + (int) ((head[1] << 16) & 0x00ff0000)
							+ (int) ((head[2] << 8) & 0xff00) + (int) (head[3] & 0xff));
					// 包头长度
					short headerLength = (short) ((short) (head[4] << 8) + (short) head[5]);
					int bodyLength = packetLength - headerLength;
					int commandCode = (int) ((int) (head[8] << 24) + (int) ((head[9] << 16) & 0x00ff0000)
							+ (int) ((head[10] << 8) & 0xff00) + (int) (head[11] & 0xff));
					TCPLog.d("headerLength:" + headerLength + " bodyLength:" + bodyLength + " commandCode:"
							+ commandCode + " read length:" + length);

					int serviceId = 0;
					if (headerLength > 16) {
						// 透传协议版本
						byte[] headReset = new byte[headerLength - 16];
						reader.read(headReset);
						// 客户端id
						int cilentId = (int) ((int) (headReset[0] << 24) + (int) ((headReset[1] << 16) & 0x00ff0000)
								+ (int) ((headReset[2] << 8) & 0xff00) + (int) (headReset[3] & 0xff));
						serviceId = (int) ((int) (headReset[4] << 24) + (int) ((headReset[5] << 16) & 0x00ff0000)
								+ (int) ((headReset[6] << 8) & 0xff00) + (int) (headReset[7] & 0xff));
						TCPLog.d("trans data serviceId : " + serviceId + "，cilentId：" + cilentId);
					}
					// 当网络包大于7M 就丢弃
					if (MAXLEN < bodyLength) {
						TCPLog.e("error socketData");
						done = true;
						break;
					}

					if (bodyLength > 0) {// 读报文体
						body = new byte[bodyLength];

						int resdLength = 0;
						int rl = 0;
						while (resdLength < bodyLength) {
							int lastLength = bodyLength - resdLength;
							if (lastLength > 1024) {
								rl = reader.read(buf);
							} else {
								rl = reader.read(buf, 0, lastLength);
							}
							if (rl >= 0) {
								System.arraycopy(buf, 0, body, resdLength, rl);
								resdLength += rl;
							}
							rl = 0;
						}
						// 组装数据包
						dataBuf = new byte[packetLength];
						System.arraycopy(head, 0, dataBuf, 0, HENDLEN);
						System.arraycopy(body, 0, dataBuf, HENDLEN, bodyLength);

					}
					if (!done) {
						TCPLog.d("commandCode = " + commandCode);
						if (packetLength == 0) {// 心跳包返回
							TCPLog.d("RESPONSE PING  ... ");
							AlarmTools.stopBackTcpPing();
						} else {
							// 调用公共接口
							if (handleLoginResponse(commandCode, dataBuf)) {
								// 登陆响应
							} else if (isVoipResponse(commandCode)) {// 响应VOIP数据
								TCPListenerManager.getInstance().notifiTcpRecvListener(ITcpRecvListener.VOIPSDK,
										commandCode, dataBuf);
							} else if (isPublic(commandCode)) {
								// 公共数据，透传数据
								if (mHandler == null) {
									mHandler = new UCSRequestHandler();
								}
								mHandler.HandleRequest(commandCode, body, serviceId);
							} else {// 响应IM数据
								TCPListenerManager.getInstance().notifiTcpRecvListener(ITcpRecvListener.IMSDK,
										commandCode, dataBuf);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					TCPLog.d("READER CONDUIT EXCEPTION:" + e.toString());
				}
			}
			TCPLog.d("Reader done is " + done);
			if (done) {
				TCPLog.d("主动断开，Reader不用重连");
			} else {
				TCPLog.d("被动断开，Reader开启重连");
				TCPServer.obtainTCPService().reconnect();
			}
			done = true;
			TCPLog.d("Reader 结束");
		}
	}

	/**
	 * 是否是公共数据
	 * 
	 * @return
	 */
	public boolean isPublic(int cmdId) {
		return cmdId == 4000;
	}

	// 30052:响应上传视频预览图片
	private boolean isVoipResponse(int cmd) {
		return cmd == 2100 || cmd == 3000 || cmd == 30052;
	}

	private boolean handleLoginResponse(int cmd, byte[] buf) {
		if (cmd == 30001) {
			// 解析登录返回
			IGGAuthResponse loginResponse = new IGGAuthResponse();
			if (loginResponse != null) {
				loginResponse.uppacket(cmd, buf, loginResponse);
			}
			loginResponse.onMsgResponse();
			TCPServer.obtainTCPService().loginFinish(loginResponse);
			return true;
		} else if (cmd == 30002) {
			// 解析重登录返回
			IGGAuthBySKResponse reLoginResponse = new IGGAuthBySKResponse();
			if (reLoginResponse != null) {
				reLoginResponse.uppacket(cmd, buf, reLoginResponse);
			}
			reLoginResponse.onMsgResponse();
			TCPServer.obtainTCPService().loginFinish(reLoginResponse);
			return true;
		} else if (cmd == 10600100) {
			TCPLog.e("返回心跳成功");
			AlarmTools.stopBackTcpPing();
			return true;
		} else if (cmd == 600016 && buf.length == 20) {
			int iSelector = (int) ((int) (buf[16] << 24 & 0xff000000) + (int) ((buf[17] << 16) & 0x00ff0000)
					+ (int) ((buf[18] << 8) & 0xff00) + (int) (buf[19] & 0xff));
			if (iSelector == 0x2000) {
				TCPLog.e("iSelector=" + iSelector);
				AlarmTools.stopBackTcpPing();
				TCPListenerManager.getInstance().notifySdkStatus(
						new UcsReason().setReason(UcsErrorCode.NET_ERROR_KICKOUT).setMsg("服务器强制下线"));
				// Selector值:
				// 0x2000 表示踢线通知
				// 0x3000 表示切换Proxy连接
				UCSManager.disconnect();
				return true;
			} else if (iSelector == 0x3000) {
				TCPLog.d("切换proxy地址");
			}
		}
		return false;
	}

	public void startup() {
		readerThread.start();
	}

	public void shutdown() {
		// isNotifyDisconnect = isNotify;
		done = true;
		try {
			reader.close();
			reader = null;
			// readerThread.interrupt();
			// readerThread = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCPLog.d("PACKET_READER : " + done);
		}
	}

	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
}
