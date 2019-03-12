  
package com.yzx.controller.listenercallback;  

import com.forwarddevelopmenttools.ErrorCode;
import com.forwarddevelopmenttools.ProvidersForwardingListener;
import com.yzx.controller.VoipCore;
import com.yzx.listenerInterface.VoipListenerManager;
import com.yzx.preference.UserData;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipProvidersForwardingCallBack   
 * @Description  国际漫游回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午5:24:29
 */
public class VoipProvidersForwardingCallBack implements ProvidersForwardingListener {

	@Override
	public void onCallForwardingIndicatorChanged(ErrorCode errorCode) {
		CustomLog.v("SET_FORWARDING:"+errorCode.getMsg()+"    "+errorCode.getReason());
		UserData.saveForwarding(VoipCore.getContext(),errorCode.getReason() == 300400);
//		if(UCSCall.getForwardListener() != null){
//			UCSCall.getForwardListener().onCallForwardingIndicatorChanged(new UcsReason(errorCode.getReason()).setMsg(errorCode.getMsg()));
//		}
		if(VoipListenerManager.getInstance().getForwardingListener() != null) {
			VoipListenerManager.getInstance().getForwardingListener()
				.onCallForwardingIndicatorChanged(new UcsReason(errorCode.getReason()).setMsg(errorCode.getMsg()));
		} 
		
	}

}
  
