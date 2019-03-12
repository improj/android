package com.yzxtcp.tools.tcp.packet.common;

/**
 * 透传数据原料
 * @author zhuqian
 */
public abstract class UCSTransStock {

	public String targetId;
	
	//用户需要传送的数据源
	public abstract String onTranslate();
	
	/**
	 * @Description 透传视频通话预览图片数据，可以为空
	 * @return	视频通话预览图片透传数据
	 * @date 2017-2-23 上午9:37:17 
	 * @author xhb  
	 * @return String    返回类型
	 */
	public String onPreviewImgData() {
		return null;
	}
	
	
	
}
