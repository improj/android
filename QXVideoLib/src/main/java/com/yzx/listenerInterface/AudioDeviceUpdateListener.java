package com.yzx.listenerInterface;

public interface AudioDeviceUpdateListener {

	/**
	 * 驱动适配回调
	 * 
	 * @author: xiaozhenhua
	 * @data:2015-9-11 上午10:05:03
	 */
	public void onAudioDeviceUpdate();

	/**
	 * CPS适配回调
	 * 
	 * @author: xiaozhenhua
	 * @data:2015-9-11 上午10:05:16
	 */
	public void onCpsConfigUpdate();
}
