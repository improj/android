package com.yzxIM.listener;

/**
 * 播放完成监听器
 * 
 *
 */
public interface RecordListener {
	
	/**
	 * 播放完成
	 * 
	 */
	public void onFinishedPlayingVoice();
	
	/**
	 * 录音完成(最大60s)
	 * 
	 * @param duration:录音文件时长
	 */
	public void onFinishedRecordingVoice(int duration);
	
}
