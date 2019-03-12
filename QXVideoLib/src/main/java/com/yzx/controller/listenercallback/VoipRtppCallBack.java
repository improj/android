  
package com.yzx.controller.listenercallback;  

import com.yzx.controller.VoipCore;
import com.yzx.listenerInterface.RtppListener;
import com.yzx.tools.RtppConfigTools;

/**
 * @Title VoipRtppCallBack   
 * @Description  voip rtpp回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午5:35:53
 */
public class VoipRtppCallBack implements RtppListener {

	@Override
	public void onRtpp() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				RtppConfigTools.pingRtpp(VoipCore.getContext());
			}
		}).start();
	}

}
  
