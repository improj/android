package com.yzxtcp.tcp;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.task.factory.ITcpTaskFactory;
import com.yzxtcp.task.factory.TCPTaskFactory;
import com.yzxtcp.tcp.LoginHelper.OnLoginListener;
import com.yzxtcp.tcp.config.TCPConnectConfig;
import com.yzxtcp.tcp.identity.IReconnectPlicy;
import com.yzxtcp.tcp.listener.ConnectCallback;
import com.yzxtcp.tcp.listener.ShutConnCallback;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;
import com.yzxtcp.tools.tcp.packet.IGGBaseResponse;
import com.yzxtcp.tools.tcp.packet.PackContent;
import com.yzxtcp.tools.tcp.packet.login.ILoginParams;

/**
 * TCP服务类
 * 
 * @author zhuqian
 */
public class TCPServer implements OnLoginListener {
	private volatile static TCPServer mInstance;

	public final ITcpTaskFactory tcpFactory;
	//是否登录成功
	public volatile boolean loginFlag;

	public TCPManager tcpManager;
	
	private LoginHelper loginHelper;
	private IReconnectPlicy reconnPlicy;
	private TCPConnectConfig config;
	
	private ILoginListener mLoginListener;
	private ILoginParams loginParams;
	public static TCPServer obtainTCPService() {
		if (mInstance == null) {
			synchronized (TCPServer.class) {
				if (mInstance == null) {
					mInstance = new TCPServer();
				}
			}
		}
		return mInstance;
	}
	/**
	 * 初始化
	 * @param config
	 */
	public void init(TCPConnectConfig config){
		this.config = config;
		//检查配置
		checkConfig();
	}
	/**
	 * 检查TCPConnectConfig
	 */
	private void checkConfig(){
		//是否是错误的配置
		boolean isErrorConfig = false;
		if(this.config == null){
			isErrorConfig = true;
		}
		if(this.config.connectPlicy == null){
			isErrorConfig = true;
		}else if(this.config.reconnectPlicy == null){
			isErrorConfig = true;
		}
		if(isErrorConfig){
			throw new RuntimeException("TCPConnectConfig is error... is null or ?...");
		}
		//设置
		this.reconnPlicy = this.config.reconnectPlicy;
		tcpManager.setConnectPlicy(this.config.connectPlicy);
		TCPLog.d("init success ...");
	}
	private TCPServer() {
		tcpManager = new TCPManager(new TcpConnection());
		tcpFactory = new TCPTaskFactory(this);
	}
	/**
	 * token登录
	 * @param token
	 * @param loginListener
	 */
	public void login(final ILoginParams loginParams,ILoginListener loginListener){
		if(!NetWorkTools.isNetWorkConnect(YzxTCPCore.getContext())){
			loginListener.onLogin(new UcsReason(UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT));
			return;
		}
		this.loginParams = loginParams;
		this.mLoginListener = loginListener;
		connect(new ConnectCallback() {
			@Override
			public void onSuccess() {
				TCPLog.d("TCPService 连接成功返回");
				// 暂存数据
				UcsLoginResponse.loginType = loginParams.loginType;
				UcsLoginResponse.SSID = loginParams.getAuthPwd();
				loginHelper = new LoginHelper();
				loginHelper.login(loginParams,TCPServer.this);
			}
			@Override
			public void onFail(UcsReason reason) {
				TCPLog.d("TCPService 连接返回失败");
				loginFlag = false;
				if (mLoginListener != null) {
					mLoginListener.onLogin(reason);
					mLoginListener = null;
				}
			}
		});
	}
	/**
	 * 登出
	 */
	public void loginOut(){
//		if(isConnect()) { // 登出时直接断开连接
			disconnect(new ShutConnCallback() {
				@Override
				public void onShutConnFinish() {
					loginFlag = false;
//				UserData.clearLoginToken();
				}
			});
//		}
	}
	public void connect(){
		connect(null);
	}
	/**
	 * 连接
	 */
	public void connect(ConnectCallback callback) {
		cancelReconn();
		cancelLogin();
	    // 执行
		if (tcpFactory != null) {
			tcpFactory.executeTcpTask(ITcpTaskFactory.CONNECT_TASK,callback);
		}
	}

	/**
	 * 重连
	 */
	public void reconnect() {
		cancelReconn();
		cancelLogin();
		executeReconnectTask();
	}
	
	private void executeReconnectTask() {
		// 创建连接任务并执行
		if (tcpFactory != null) {
			tcpFactory.executeTcpTask(ITcpTaskFactory.RECONNECT_TASK,new ConnectCallback() {
				@Override
				public void onSuccess() {
					//重连成功，准备重新登录
					loginHelper = new LoginHelper();
					loginHelper.relogin(TCPServer.this);
					cancelReconn();
				}
				@Override
				public void onFail(UcsReason reason) {
					// 通知重连失败
					TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTFAIL).setMsg("TCP 连接失败"));
					//启动重连
					if(reconnPlicy != null){
						reconnPlicy.reconn();
					}
				}
			});
		}
	}
	
	/**
	 * @Description 循环重连	
	 * @date 2016-5-25 上午9:23:56 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public void CycleReconnect() {
		cancelLogin();
		executeReconnectTask();
	}
	
//	/*
//	 * 取消重连和登录
//	 */
//	private void cancelConn() {
//		cancelReconn();
//		cancelLogin();
//	}
	private void cancelLogin() {
		//取消登录
		if(loginHelper != null){
			loginHelper.cancelLogin();
		}
	}
	
	private void cancelReconn() {
		//取消重连
		if(reconnPlicy != null){
			reconnPlicy.cancelReconn();
		}
	}
	
	/**
	 * 断开连接
	 */
	public void disconnect(ShutConnCallback callback) {
		cancelReconn();
		cancelLogin();
		if (tcpFactory != null) {
			tcpFactory.executeTcpTask(ITcpTaskFactory.DISCONNECT_TASK,callback);
		}
	}
	/**
	 * 发送IM数据包
	 * @param cmd 命令码
	 * @param request 请求包
	 * @return
	 */
	public PackContent sendPacket(int cmd, IGGBaseRequest request) {
		return tcpManager.sendPacket(cmd, request);
	}
	/**
	 * 发送Voip数据包
	 * @param cmd
	 * @param voipBuf
	 * @return
	 */
	public boolean sendPacket(int cmd, byte[] voipBuf){
		return tcpManager.sendPacket(cmd, voipBuf);
	}

	@Override
	public void onSuccess(int type) {
		if(type == LoginHelper.LOGIN){
			loginSuccess();
		}else if(type == LoginHelper.RELOGIN){
			reLoginSuccess();
		}
	}
	
	/**
	 * 登录成功
	 */
	private void loginSuccess(){
		loginFlag = true;
		if (mLoginListener != null) {
			//通知连接成功
			mLoginListener.onLogin(new UcsReason(UcsErrorCode.NET_ERROR_CONNECTOK));
			mLoginListener = null;
		}
		//保存Token
		loginParams.saveLoginParams();
		// 开始心跳
		AlarmTools.startAlarm(0);
		//通知开发者连接TCP成功
		TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTOK).setMsg("TCP 连接成功"));
		// 通知登录成功
		TCPListenerManager.getInstance().notifiLoginListener(new UcsReason().setReason(UcsErrorCode.NET_ERROR_CONNECTOK).setMsg(UcsLoginResponse.toStringResponse()));
	}
	/**
	 * 重登录成功
	 */
	private void reLoginSuccess(){
		// 开启心跳
		AlarmTools.startAlarm(0);
		// 提示tcp连接成功
		TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTOK).setMsg("TCP 连接成功"));
		// 通知im重连成功
		TCPListenerManager.getInstance().notifiReLoginListener(new UcsReason().setReason(UcsErrorCode.NET_ERROR_RECONNECTOK).setMsg("重登陆成功"));
	}
	/**
	 * 登录失败
	 */
	private void loginFail(int errorCode){
		loginFlag = false;
		if (mLoginListener != null) {
			mLoginListener.onLogin(new UcsReason(errorCode));
			mLoginListener = null;
		}
	}
	/**
	 * 重登录失败
	 * @param errorCode
	 */
	private void reLoginFail(int errorCode){
		// 通知重连失败
		TCPListenerManager.getInstance().notifySdkStatus(new UcsReason().setReason(UcsErrorCode.NET_ERROR_TCPCONNECTFAIL).setMsg("TCP 连接失败"));
	}
	/**
	 * 登录完成相应
	 * @param response
	 */
	public void loginFinish(IGGBaseResponse response){
		if(loginHelper != null){
			loginHelper.finish(response);
		}
	}
	@Override
	public void onFail(int type, int errorCode) {
		if(type == LoginHelper.LOGIN){
			loginFail(errorCode);
		}else if(type == LoginHelper.RELOGIN){
			reLoginFail(errorCode);
		}
	}
	/**
	 * 是否连接成功(连接+登录成功)
	 * 
	 * @return
	 */
	public boolean isConnect(){
		return tcpManager.isConnect() && loginFlag;
	}
	/**
	 * 获取TcpConnection
	 * @return
	 */
	public TcpConnection getTcpConnection(){
		return tcpManager.getTcpConnection();
	}
	public ImageUploader getImageUploader(String iClientMsgId){
		return tcpManager.getImageUploader(iClientMsgId);
	}
}
