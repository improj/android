package com.yzxIM.tools;

import com.yzxIM.listener.MessageListener;

public class DownloadTools {

	/**
	 * 下载附件
	 * @param fileUrl:远程URL
	 * @param filePaht：本地路径
	 * @param msgId:消息ID
	 * @return
	 */
	public static DownloadThread downloadFile(String fileUrl, String filePaht,String msgId,MessageListener fileListener) {
		DownloadThread download = new DownloadThread(fileUrl, filePaht, msgId, fileListener);
		download.startDownload();
		return download;
	}
}
