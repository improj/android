package com.yzxtcp.tools.provider;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yzxtcp.listener.OnSendTransRequestListener;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest.OnSendUCSRequestListener;
/**
 * 请求业务类
 * @author zhuqian
 */
public class RequestProvider implements IProvider{
	private IUCSRequest request;
	
	private OnSendUCSRequestListener sendTransContentListener;
	
	private static final int SENDTRANSCONTENT_TIMEOUT = 202;
	private static final int SENDTRANSCONTENT_FINISH = 200;
	
	
	private Handler mHandler;
	public RequestProvider setSendTransContentListener(
			OnSendUCSRequestListener sendTransContentListener) {
		this.sendTransContentListener = sendTransContentListener;
		return this;
	}
	public RequestProvider(IUCSRequest request){
		this.request = request;
		mHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SENDTRANSCONTENT_TIMEOUT:
					//发送超时
					if(sendTransContentListener != null){
						((OnSendTransRequestListener)sendTransContentListener).onSend(IUCSRequest.SendErrorCode.SEND_TIMEOUT, "", RequestProvider.this.request);
					}
					CustomLog.e("SENDTRANSCONTENT_TIMEOUT...");
					break;
				case SENDTRANSCONTENT_FINISH:
					//发送完毕，回调透传监听
					if(sendTransContentListener != null){
						((OnSendTransRequestListener)sendTransContentListener).onSend(msg.arg1, (String)(msg.obj), RequestProvider.this.request);
					}
					CustomLog.e("SENDTRANSCONTENT_FINISH...");
					break;
				default:
					break;
				}
				//移除TransContentProvider
				if(ProviderHandler.removeProvider(RequestProvider.this.request.msgId) != null){
					CustomLog.i("TransContentProvider remove success...");
				}else{
					CustomLog.e("TransContentProvider remove fail...");
				}
			}
		};
	}
	/**
	 * 发送失败返回false，发送成功返回true
	 */
	@Override
	public boolean send() {
		if(TCPServer.obtainTCPService().sendPacket(request.header.commCode, request.data)){
			CustomLog.i("发送透传数据成功,启动超时定时器...");
			//60秒之后超时
			mHandler.sendEmptyMessageDelayed(SENDTRANSCONTENT_TIMEOUT, SEND_TIMEOUT);
			return true;
		}else{
			CustomLog.e("Tcp已经断开，发送透传数据失败...");
			onSend(IUCSRequest.SendErrorCode.TCP_NO_CONNECTION);
			return false;
		}
	}
	@Override
	public void onSend(int errorCode) {
		onSend(errorCode, "");
	}
	
	public void onSend(int errorCode, String ackData) {
		//主线程回调事件
		Message msg = mHandler.obtainMessage();
		msg.what = SENDTRANSCONTENT_FINISH;
		msg.arg1 = errorCode;
		msg.obj = ackData;
		mHandler.sendMessage(msg);
		//移除超时回调
		mHandler.removeMessages(SENDTRANSCONTENT_TIMEOUT);
	}
}
