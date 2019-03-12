package com.yzxtcp.task.factory;

import com.yzxtcp.task.BaseTcpTask;
import com.yzxtcp.task.ConnectTask;
import com.yzxtcp.task.DisconnectTask;
import com.yzxtcp.task.ReconnectTask;
import com.yzxtcp.task.TcpTask;
import com.yzxtcp.task.BaseTcpTask.TCPResult;
import com.yzxtcp.tcp.AlarmTools;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tools.TCPLog;
import android.text.TextUtils;

/**
 * TCP任务工厂，用于创建TCP任务
 * @author zhuqian
 */
public class TCPTaskFactory extends ITcpTaskFactory{
	
	private TCPServer tcpServer;
	
	public TCPTaskFactory(TCPServer tcpServer){
		super();
		this.tcpServer = tcpServer;
	}

	@Override
	protected BaseTcpTask<TCPResult> createTcpTask(String taskName) {
		if(TextUtils.isEmpty(taskName)){
			return null;
		}
		TCPLog.d("tcpServer loginFlag: " + tcpServer.loginFlag);
		if(RECONNECT_TASK.equals(taskName) && !tcpServer.loginFlag){
			AlarmTools.stopAll();
			TCPLog.d("当前没有登录成功，不需要重连");
			return null;
		}
		BaseTcpTask<TCPResult> tcpTask = checkConnect();
		if(taskName.equals(CONNECT_TASK)){
			//连接任务
			if(tcpTask != null){
				TCPLog.d("has tcpTask ："+tcpTask.toString()+"running... 不需要创建连接任务...");
				return null;
			}
			tcpTask = new ConnectTask<TCPResult>(tcpServer);
		}else if(taskName.equals(RECONNECT_TASK)){
			//重连任务
			if(tcpTask != null){
				TCPLog.d("has tcpTask ："+tcpTask.toString()+"running... 不需要创建连接任务...");
				return null;
			}
			tcpTask = new ReconnectTask<TCPResult>(tcpServer);
		}else if(taskName.equals(DISCONNECT_TASK)){
			//断开任务
			if(tcpTask != null){
				TCPLog.d("has tcpTask ："+tcpTask.toString()+"running... cancel："+DISCONNECT_TASK);
				if(tcpTask instanceof ConnectTask){
					cancelTcpTask(CONNECT_TASK,tcpTask);
				}else if(tcpTask instanceof ReconnectTask){
					cancelTcpTask(RECONNECT_TASK,tcpTask);
				}
			}
			tcpTask = new DisconnectTask<TCPResult>(tcpServer);
		}
		return tcpTask;
	}

	@Override
	public void cancelTcpTask(String taskName,TcpTask<TCPResult> tcpTask) {
		if(tcpTask != null){
			tcpTask.cancel(true);
			TCPLog.d("remove tcpTask : "+removeTask(taskName));
		}
	}
	
	/**
	 * 检查是否有正在连接的任务
	 */
	private BaseTcpTask<TCPResult> checkConnect() {
		if (tcpTasks.containsKey(CONNECT_TASK)) {
			// 有正在连接的任务
			TCPLog.d("tcpTasks has ： " + CONNECT_TASK);
			return (BaseTcpTask<TCPResult>) tcpTasks.get(CONNECT_TASK);
		}
		if (tcpTasks.containsKey(RECONNECT_TASK)) {
			// 有正在连接的任务
			TCPLog.d("tcpTasks has ： " + RECONNECT_TASK);
			return (BaseTcpTask<TCPResult>) tcpTasks.get(RECONNECT_TASK);
		}
		return null;
	}
}
