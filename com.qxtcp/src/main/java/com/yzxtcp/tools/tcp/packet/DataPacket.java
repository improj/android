package com.yzxtcp.tools.tcp.packet;

import java.util.Random;
/**
 * 协议基类包
 * @author xiaozhenhua
 *
 */
public abstract class DataPacket {

	public static int packetLength = -1; //数据包长度
	public int bodyLength = -1; //数体长度
	public int protocolVerson = -1; //协议版本
	public int commandCode = -1;//命令号
	public int sequenceCode = -1;//序列号
	public byte[] buf = new byte[40960+16];
	
	public void createVoipPacketHead(){
		Random rand =new Random(5000);
		int seq;
		seq=rand.nextInt(10000);
		
		buf = new byte[40960+16];
		int ver = 16;
		buf[0] = (byte) ((ver >> 24) & 0xff);
		buf[1] = (byte) ((ver >> 16) & 0xff);
		buf[2] = (byte) ((ver >> 8) & 0xff);
		buf[3] = (byte) (ver & 0xff);
		ver = 0x01;
		buf[4] = (byte) ((ver >> 24) & 0xff);
		buf[5] = (byte) ((ver >> 16) & 0xff);
		buf[6] = (byte) ((ver >> 8) & 0xff);
		buf[7] = (byte) (ver & 0xff);
		ver = 101;
		buf[8] = (byte) ((ver >> 24) & 0xff);
		buf[9] = (byte) ((ver >> 16) & 0xff);
		buf[10] = (byte) ((ver >> 8) & 0xff);
		buf[11] = (byte) (ver & 0xff);
		ver = seq;
		buf[12] = (byte) ((ver >> 24) & 0xff);
		buf[13] = (byte) ((ver >> 16) & 0xff);
		buf[14] = (byte) ((ver >> 8) & 0xff);
		buf[15] = (byte) (ver & 0xff);
	}
	
}
