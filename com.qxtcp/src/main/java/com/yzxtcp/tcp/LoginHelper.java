package com.yzxtcp.tcp;

import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.IGGAuthBySKRequest;
import com.yzxtcp.tools.tcp.packet.IGGAuthBySKResponse;
import com.yzxtcp.tools.tcp.packet.IGGAuthRequest;
import com.yzxtcp.tools.tcp.packet.IGGAuthResponse;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;
import com.yzxtcp.tools.tcp.packet.PacketSerialize;
import com.yzxtcp.tools.tcp.packet.login.ILoginParams;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
/**
 * 登录帮手
 * 
 * @author zhuqian
 */
public class LoginHelper {
	private OnLoginListener listener;
	//登录超时
	public static final int LOGIN_TIME_OUT = 202;
	//登录完成
	public static final int LOGIN_FINISH = 200;
	//登录类型
	public static final int LOGIN  = 0;
	//重登录类型
	public static final int RELOGIN  = 1;
	//登录超时时间
	private static final int LOGIN_TIME_OUT_SECOND = 15*1000;
	private Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN_TIME_OUT:
				TCPLog.d("login time out... type ："+msg.obj);
				if(listener != null){
					listener.onFail((Integer) msg.obj,UcsErrorCode.NET_ERROR_CONNECTTIMEOUT);
				}
				//断开连接
				TCPServer.obtainTCPService().disconnect(null);
				break;
			case LOGIN_FINISH:
				IGGBaseResponse response = (IGGBaseResponse) msg.obj;
				int type = -1;
				if(response instanceof IGGAuthResponse){
					TCPLog.d("LoginHelper login finish...");
					type = LOGIN;
				}else if(response instanceof IGGAuthBySKResponse){
					TCPLog.d("LoginHelper relogin finish...");
					type = RELOGIN;
				}
				if(listener != null){
					TCPLog.v("response.base_iRet " + response.base_iRet);
					if(response.base_iRet == 0){
						listener.onSuccess(type);
					}else{
						//断开连接
						TCPServer.obtainTCPService().disconnect(null);
						listener.onFail(type,UcsErrorCode.NET_ERROR_TOKENERROR);
					}
				}
				break;
			default:
				break;
			}
		};
	};
	/**
	 * 登录
	 * @param listener
	 */
	public void login(ILoginParams loginParams,OnLoginListener listener){
		this.listener = listener;
		//发送登录包
		IGGAuthRequest authRequest = new IGGAuthRequest();
		//填充登录包
		loginParams.fillAuthRequest(authRequest);
		TCPServer.obtainTCPService().sendPacket(PacketSerialize.REQ_AUTH,authRequest);
		Message msg = mHandler.obtainMessage();
		msg.what = LOGIN_TIME_OUT;
		msg.obj = LOGIN;
		//登录超时
		mHandler.sendMessageDelayed(msg,LOGIN_TIME_OUT_SECOND);
	}
	/**
	 * 重登录
	 * @param listener
	 */
	public void relogin(OnLoginListener listener){
		this.listener = listener;
		//发送重登录包
		TCPServer.obtainTCPService().sendPacket(PacketSerialize.REQ_REAUTH,new IGGAuthBySKRequest());
		Message msg = mHandler.obtainMessage();
		msg.what = LOGIN_TIME_OUT;
		msg.obj = RELOGIN;
		//登录超时
		mHandler.sendMessageDelayed(msg,LOGIN_TIME_OUT_SECOND);
	}
	
	/**
	 * 登录完成
	 * @param response
	 */
	public void finish(IGGBaseResponse response){
		//移除
		mHandler.removeMessages(LOGIN_TIME_OUT);
//		mHandler.removeCallbacksAndMessages(null);
		Message msg = mHandler.obtainMessage();
		msg.what = LOGIN_FINISH;
		msg.obj = response;
		msg.sendToTarget();
	}
	/**
	 * 取消登录回调
	 */
	public void cancelLogin(){
		//移除
		if(mHandler.hasMessages(LOGIN_TIME_OUT)){
			TCPLog.d("current has login... login code : "+ LOGIN_TIME_OUT);
		}
//		mHandler.removeCallbacksAndMessages(null);
		mHandler.removeMessages(LOGIN_TIME_OUT);
	}
	public interface OnLoginListener{
		void onSuccess(int type);
		void onFail(int type,int errorCode);
	}
}
