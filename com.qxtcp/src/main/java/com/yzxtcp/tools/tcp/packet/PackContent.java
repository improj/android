package com.yzxtcp.tools.tcp.packet;

public class PackContent {
	public int pack_size;//总的数据包大小
	public String pcClientMsgId="";//消息id
	public String tImgPackSize="";//图片包大小
	public byte[] pack_content=new byte[4096];//数据包内容
}
