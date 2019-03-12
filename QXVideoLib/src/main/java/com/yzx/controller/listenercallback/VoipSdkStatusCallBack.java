  
package com.yzx.controller.listenercallback;  

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSService;
import com.yzx.controller.TimerHandler;
import com.yzx.controller.VoipCore;
import com.yzx.http.net.InterfaceUrl;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.tools.CallLogTools;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ISdkStatusListener;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title VoipSdkStatusCallBack   
 * @Description  voip sdk状态回调类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午4:59:13
 */
public class VoipSdkStatusCallBack implements ISdkStatusListener {
//	private String callDelayId;     //延迟通话的通话ID
	@Override
	public void onSdkStatus(UcsReason reason) {
		CustomLog.v("voice sdk status: "+reason.getReason()+"    "+reason.getMsg());
		for(ConnectionListener cl:UCSService.getConnectionListener()){
			switch(reason.getReason()){
			case UcsErrorCode.NET_ERROR_KICKOUT:
			  //被踢线后如果当前有通话则结束通话
                CustomLog.v("onSdkStatus() NET_ERROR_KICKOUT");
                String currentCallID = UCSCall.getCurrentCallId();
                if (currentCallID != null && currentCallID.length() > 0)
                {
                	UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
                    CustomLog.v("NET_ERROR_KICKOUT, hangup by oneself");
                }
				cl.onConnectionFailed(new UcsReason(300207).setMsg("forced offline server"));
				break;
			case UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT:
				//cl.onConnectionFailed(new UcsReason(UCSCall.NOT_NETWORK).setMsg("net status has disConnected"));
				//UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_DISCONNECTED);//不主动通知组件TCP断开，否则组件会立即断开通话
				//如果有正在进行的通话，10秒后再结束
				TimerHandler.getInstance().startCallDelayTimer();
//				String callID = UCSCall.getCurrentCallId();
//                if (callID != null && callID.length() > 0 && (callDelayId == null || callDelayId.length() == 0)) {
//                    callDelayId = callID;
//                }
				break;
//			case UcsErrorCode.NET_ERROR_RECONNECTOK:
//			    //重连成功后通知组件，以触发路由更新
//				UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_RECONNECTED);
//				break;
			case UcsErrorCode.NET_ERROR_TCPCONNECTOK:
				CustomLog.v("TCP 连接操作成功...");
				TimerHandler.getInstance().stopCallDelayTimer();
				InterfaceUrl.initUrlToTest(VoipCore.getContext());
				UGoManager.getInstance().pub_UGoTcpUpdateState(UGoAPIParam.eUGo_TCP_CONNECTED);
				//启动日志上传
				new Thread(new Runnable() {
					@Override
					public void run() {
						CallLogTools.launchUploadCalllog(null);
					}}).start();
				break;
			}
		}
	
	}
	

}
  
