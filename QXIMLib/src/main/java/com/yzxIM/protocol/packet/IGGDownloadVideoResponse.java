package com.yzxIM.protocol.packet;

import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;

//下载视频回应
public class IGGDownloadVideoResponse extends IGGBaseResponse {
	public int iMsgId;// 从服务器得到的msgid
	public int iTotalLen;// 视频数据总大小
	public int iStartPos;// 起始位置（第一个包填0）
	public byte[] tData;// 视频数据（数据+长度）
	@Override
	public void onMsgResponse() {
		// TODO Auto-generated method stub
		
	}
}

