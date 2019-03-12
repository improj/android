package com.yzxtcp.listener;

/**
 * @Title onRecvPerviewImgTransListener
 * @Description 视频预览图片透传监听回调
 * @Company yunzhixun
 * @author xhb
 * @date 2017-3-9 下午2:00:47
 */
public interface OnRecvPerviewImgTransListener {

	/**
	 * @Description 视频预览图片透传监听回调
	 * @param callid	通话的callid
	 * @param previewImgUrl	透传预览图片地址
	 * @date 2017-3-9 下午2:13:54 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public abstract void onRecvTranslate(String callid, String previewImgUrl);
}
