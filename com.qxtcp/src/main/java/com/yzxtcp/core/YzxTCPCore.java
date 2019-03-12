  
package com.yzxtcp.core;  

import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.CrashHandler;
import com.yzxtcp.tools.FileTools;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.TCPLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
/**
 * @Title YzxTCPCore   
 * @Description  主要针对网络变化状态监听
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-19 下午4:18:05
 */
public class YzxTCPCore {
	private static String className = "YzxTCPCore";
	private volatile static YzxTCPCore yzxTCPCore;
	private static Context mContext;
	public static final int STATUS_WIFI = 2;
	public static final int STATUS_MOBILE = 1;
	public static final int STATUS_ERROR = 0;
	//mStatus的修改第一时间同步到主线程   
	// 在Java中，对基本数据类型的变量的读取和赋值操作是原子性操作，即这些操作是不可被中断的，要么执行，要么不执行。
	// volatile 保证了可见性和有序性，所以mStatus字段是线程安全的
	private volatile int mStatus = STATUS_ERROR;
	
	private YzxTCPCore(Context context) {
		TCPLog.d(className + " onCreate ... ");
		mContext = context;
		crashHandle();
		TCPLog.v("mContext:" + mContext.toString());
		TCPLog.v("packageName: " + mContext.getPackageName());
		listenerNetworkStateBroadcast();
		setNetworkStateBroadcast();
		FileTools.createFolder();
	}

	private void listenerNetworkStateBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(receiver, intentFilter);
	} 

	private void setNetworkStateBroadcast() {
		// 应用程序内部广播LocalBroadcastManager，特点：安全，高效
		IntentFilter updateIntentFilter = new IntentFilter();
		updateIntentFilter.addAction("com.yzx.update.network.state");
		LocalBroadcastManager.getInstance(mContext).registerReceiver(updateReceiver, updateIntentFilter);
	}

	private void crashHandle() { 
		if (UCSManager.getCrashException(mContext)) {
			CrashHandler.getInstance().init();
			TCPLog.d("开启崩溃异常捕获...");
		} else {
			TCPLog.d("关闭崩溃异常捕获...");
		}
	}
	
	public static YzxTCPCore init(Context context) {
		if(yzxTCPCore == null) {
			synchronized (YzxTCPCore.class) {
				if(yzxTCPCore == null) {
					yzxTCPCore = new YzxTCPCore(context);
				}
			}
		}
		return yzxTCPCore;
	}
	
	private void checkNetWork(Context context) {
		//当前网络类型
		int temp_status = NetWorkTools.getCurrentNetWorkType(context);
		TCPLog.d("network type: " + temp_status + "local network mStatus: " + mStatus);
		if (temp_status != mStatus) {
			if (temp_status == 0) {
				TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(
								UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT).setMsg("网络断开"));
				// 断开连接
				TCPLog.d("STATUS_ERROR No network! Stop the heart beat!!");
				//断开tcp
				TCPServer.obtainTCPService().disconnect(null);
			} else {
				// 重连
				TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(
								UcsErrorCode.PUBLIC_ERROR_NETCONNECTED).setMsg("网络已连接"));
				TCPLog.d("The network type has been changed. Notify the service to re-connect");
				TCPServer.obtainTCPService().reconnect();
			}
		} else {
			// 网络没变化
			TCPLog.d("CONNECTIVITY_ACTION The network is same as before, do nothing!!");
		}
		mStatus = temp_status;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(intent.getAction(),
					ConnectivityManager.CONNECTIVITY_ACTION)) {
				TCPLog.d("coreService receiver network change ...");
				checkNetWork(context);
			}
		}
	};

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(TextUtils.equals(intent.getAction(), "com.yzx.update.network.state")) {	// 更新网络状态
//				setStatus(intent.getIntExtra("update_network_state", 0)); 
				TCPLog.d("handle set mStatus ："+ intent.getIntExtra("update_network_state", 0));
				YzxTCPCore.this.mStatus = intent.getIntExtra("update_network_state", 0);
			}
		}
	};
	
/*	private void setStatus(int status) {
		this.mStatus = status;
		TCPLog.d("handle set mStatus ："+status);
	}*/
	
	// TODO 这里返回为空，可能会出现空指针问题
	public static Context getContext() {
		return mContext;
	}
}
  
