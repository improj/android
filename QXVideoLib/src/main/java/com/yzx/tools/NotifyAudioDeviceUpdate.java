package com.yzx.tools;

import org.webrtc.voiceengine.AudioDeviceUtil;

import com.yzx.listenerInterface.AudioDeviceUpdateListener;
import com.yzxtcp.tools.CustomLog;

public class NotifyAudioDeviceUpdate {

	public static AudioDeviceUpdateListener audioDeviceUpdateListener;
	
	public static void setAudioDeviceUpdateListener(AudioDeviceUpdateListener adul){
		audioDeviceUpdateListener = adul;
	}
	
	public static void notifyAudioDevicesUpdate(String key){
		if (audioDeviceUpdateListener != null) {
			if (key.equals(AudioDeviceUtil.PERMISSION_KEY)) {
				CustomLog.v("1 call back cps Config update....");
				audioDeviceUpdateListener.onCpsConfigUpdate();
			} else if (key.equals(AudioDeviceUtil.PARAM_KEY)) {
				CustomLog.v("2 call back audio device update....");
				audioDeviceUpdateListener.onAudioDeviceUpdate();
			}
		}
	}
	
}
