  
package com.yzx.controller.listenercallback;  

import com.yzx.controller.UGoSetConfig;
import com.yzx.listenerInterface.AudioDeviceUpdateListener;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipAudioDeviceUpdateCallBack   
 * @Description  设备更新回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午5:51:00
 */
public class VoipAudioDeviceUpdateCallBack implements AudioDeviceUpdateListener {

	@Override
	public void onAudioDeviceUpdate() {}
	
	@Override
	public void onCpsConfigUpdate() {
		CustomLog.v("about onCpsConfigupdate callback....");
		UGoSetConfig.setConfig();
	}
	
}
  
