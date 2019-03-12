  
package com.yzx.controller.listenercallback;  


import android.content.Intent;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSService;
import com.yzx.controller.LoginHandler;
import com.yzx.controller.VoipCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.IReLoginListener;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipReloginCallBack   
 * @Description  voip重登录回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午4:47:15
 */
public class VoipReloginCallBack extends LoginHandler implements IReLoginListener {

	@Override
	public void onReLogin(UcsReason reason) {
		CustomLog.v("video sdk onReLogin reason: "+reason.getReason()+"   msg:"+reason.getMsg());
		if(reason.getReason() == UcsErrorCode.NET_ERROR_RECONNECTOK){
			report();
		    //重连成功后通知组件，以触发路由更新
			UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_RECONNECTED);
		}else{
			switchErrorCode(reason);
		}
	}

}
  
