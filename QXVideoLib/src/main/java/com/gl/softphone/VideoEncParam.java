package com.gl.softphone;

public class VideoEncParam {
	
	/*private static VideoEncParam videoEncParam;
	
	public static VideoEncParam getInstance(){
		if(videoEncParam == null){
			videoEncParam = new VideoEncParam();
		}
		return videoEncParam;
	}*/
	
	
	public String ucPlName[];
	public int ucPlType[];
	public int enable[];
	public int bitrates[][];
    public int presets[][];
    public int fps[][];
	public int usNum;
	public int usWidth;
	public int usHeight;
	public int usStartBitrate;
	public int usMaxBitrate;
	public int usMinBitrate;
	public int ucmaxFramerate;
	public int ucQpMax;
	public int ucComplexity; /* 0-4 */
	public int ucFirstSelectedPt; /* 0-4 */
	public boolean ucFixedResolution; /* true false*/
}