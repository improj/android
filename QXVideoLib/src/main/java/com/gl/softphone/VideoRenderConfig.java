/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Video Render configuration
 */
public class VideoRenderConfig {
	// Video remote render view
	public Object pWindowRemote;
	// Video local preview view
	public Object pWindowLocal;
    // Video remote view width
	public int	remoteWidth;
	// Video remote view height
	public int	remoteHeight;
	// Video render scale, 1: do scale on view, 0: full render on view 
	public int	renderMode;/*0:半全屏 1：不全屏 2：全部全屏 3：老版本不全屏*/
	
	/**
	 * 
	 */
	public VideoRenderConfig() {
		// TODO Auto-generated constructor stub
	}

}
