package com.yzxtcp.task.factory;

import java.util.HashMap;
import java.util.Map;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.task.BaseTcpTask;
import com.yzxtcp.task.BaseTcpTask.TCPResult;
import com.yzxtcp.task.TcpTask;
import com.yzxtcp.tcp.listener.TCPResultCallback;
import android.os.Handler;
import android.os.Looper;

/**
 * TCP任务工厂接口
 * 
 * @author zhuqian
 */
public abstract class ITcpTaskFactory {

	// 连接任务
	public static final String CONNECT_TASK = "com.yzx.tcp.connect";
	// 重连任务
	public static final String RECONNECT_TASK = "com.yzx.tcp.reconnect";
	// 断开任务
	public static final String DISCONNECT_TASK = "com.yzx.tcp.disconnect";
	// 正在执行的tcp任务
	public Map<String, TcpTask<TCPResult>> tcpTasks;

	protected Handler mainHandler;

	public ITcpTaskFactory() {
		mainHandler = new Handler(Looper.getMainLooper());
		tcpTasks = new HashMap<String, TcpTask<TCPResult>>();
	}

	/**
	 * 在主线程创建并执行tcp任务
	 * 
	 * @param taskName
	 */
	public void executeTcpTask(final String taskName,
			final TCPResultCallback callback) {
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				BaseTcpTask<TCPResult> tcpTask = createTcpTask(taskName);
				if (tcpTask != null) {
					tcpTask.setTcpCallback(callback);
					executeTask(taskName, tcpTask);
				} else {
					// 通知重连失败
					//TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTFAIL).setMsg("TCP 连接失败"));
				}
			}
		});
	}

	/**
	 * 执行TCP任务，并添加到任务列表
	 * 
	 * @param taskName
	 *            任务名称
	 * @param task
	 *            具体任务
	 */
	private void executeTask(String taskName, TcpTask<TCPResult> task) {
		tcpTasks.put(taskName, task);
		task.execute();
	}
	/**
	 * 移除任务
	 * @param taskName
	 * @return
	 */
	public TcpTask<TCPResult> removeTask(String taskName){
		return tcpTasks.remove(taskName);
	}

	/**
	 * 根据任务名创建对应任务
	 * 
	 * @param taskName
	 */
	protected abstract BaseTcpTask<TCPResult> createTcpTask(String taskName);

	/**
	 * 取消对应任务
	 * 
	 * @param taskName
	 */
	public abstract void cancelTcpTask(String taskName,TcpTask<TCPResult> tcpTask);

}
