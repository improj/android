package com.yzxtcp;

import android.content.Context;
import android.text.TextUtils;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.listener.IReLoginListener;
import com.yzxtcp.listener.ISdkStatusListener;
import com.yzxtcp.listener.ITcpRecvListener;
import com.yzxtcp.listener.OnRecvPerviewImgTransListener;
import com.yzxtcp.listener.OnRecvTransUCSListener;
import com.yzxtcp.listener.OnSendTransRequestListener;
import com.yzxtcp.listener.TCPListenerManager;
import com.yzxtcp.tcp.TCPServer;
import com.yzxtcp.tcp.config.TCPConnectConfig;
import com.yzxtcp.tcp.identity.impl.CycleReconnectPlicy;
import com.yzxtcp.tcp.identity.impl.ProxyConnectPlicy;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.provider.ProviderHandler;
import com.yzxtcp.tools.provider.RequestProvider;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;
import com.yzxtcp.tools.tcp.packet.PackContent;
import com.yzxtcp.tools.tcp.packet.common.UCSTransStock;
import com.yzxtcp.tools.tcp.packet.common.request.IUCSRequest;
import com.yzxtcp.tools.tcp.packet.factory.TransConetntFactory;
import com.yzxtcp.tools.tcp.packet.login.ILoginParams;
import com.yzxtcp.tools.tcp.packet.login.LoginParams;

/**
 * TCP对外提供的公共管理库
 */
public class UCSManager {

	private UCSManager() {
	}

	/**
	 * @Description 初始化SDK
	 * @param mC
	 *            应用上下文
	 * @date 2016-9-7 上午9:53:53
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void init(Context mC) {
		if (mC == null) {
			TCPLog.e("init 参数错误!!!!");
			return;
		}
		TCPLog.v("mc:" + mC.toString());
		YzxTCPCore.init(mC.getApplicationContext());
		// 设置连接和重连策略
		TCPServer tcpServer = TCPServer.obtainTCPService();
		tcpServer.init(new TCPConnectConfig.Builder().setReconnectPlicy(new CycleReconnectPlicy(tcpServer))
				.setConnectPlicy(new ProxyConnectPlicy(tcpServer.getTcpConnection())).build());
	}

	/**
	 * @Description 通过token连接云平台
	 * @param loginToken
	 *            token字符串
	 * @param loginCallBack
	 *            登录回调
	 * @date 2016-9-7 上午10:28:46
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void connect(String loginToken, ILoginListener loginCallBack) {
		ILoginParams loginParams = saveLoginTokenParams(loginToken);
		if (checkLoginParams(loginCallBack, loginParams)) {
			connect(loginParams, loginCallBack);
		}
	}

	private static ILoginParams saveLoginTokenParams(String loginToken) {
		// 新建Token登录参数
		ILoginParams loginParams = new LoginParams(UcsLoginResponse.UCS_TokenLogin);
		loginParams.token = loginToken;
		return loginParams;
	}

	/**
	 * @Description 通过账号系统登陆
	 * @param sid
	 *            开发者账号
	 * @param sidPwd
	 *            开发者账号密码
	 * @param clientId
	 *            子账号
	 * @param clientPwd
	 *            子账号密码
	 * @param loginCallBack
	 *            登录回调
	 * @date 2016-9-7 上午10:32:03
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void connect(String sid, String sidPwd, String clientId, String clientPwd,
			ILoginListener loginCallBack) {
		ILoginParams loginParams = saveLoginClientParams(sid, sidPwd, clientId, clientPwd);
		if (checkLoginParams(loginCallBack, loginParams)) {
			connect(loginParams, loginCallBack);
		}
	}

	private static ILoginParams saveLoginClientParams(String sid, String sidPwd, String clientId, String clientPwd) {
		// 新建Client登录参数
		ILoginParams loginParams = new LoginParams(UcsLoginResponse.UCS_ClientLogin);
		loginParams.tDevSid = sid;
		loginParams.tDevPwd = sidPwd;
		loginParams.tAccount = clientId;
		loginParams.tPwd = clientPwd;
		return loginParams;
	}

	private static boolean checkLoginParams(ILoginListener loginCallBack, ILoginParams loginParams) {
		try {
			// 检查登录参数
			loginParams.checkParams();
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			if (loginCallBack != null) {
				loginCallBack.onLogin(new UcsReason().setReason(UcsErrorCode.PUBLIC_ERROR_PARAMETERERR).setMsg(
						e.getMessage()));
			}
			return false;
		}
	}

	/**
	 * @Description 连接服务器，仅限内部使用
	 * @param loginParams
	 *            登录参数
	 * @param loginCallBack
	 *            登录回调
	 * @date 2016-9-7 上午10:35:44
	 * @author xhb
	 * @return void 返回类型
	 */
	private static void connect(ILoginParams loginParams, ILoginListener loginCallBack) {
		TCPLog.v("UCSManager connect loginParams: loginType=" + loginParams.loginType + " SSID=" + loginParams.token
				+ " userid=" + loginParams.tDevSid);
		// 登录前先断开所有连接
		disconnect();
		saveLoginParams(loginParams);
		TCPServer.obtainTCPService().login(loginParams, loginCallBack);
	}

	private static void saveLoginParams(ILoginParams loginParams) {
		// 用于获取proxy时的请求参数
		UcsLoginResponse.loginType = loginParams.loginType;
		UcsLoginResponse.SSID = loginParams.token;
		UcsLoginResponse.userid = loginParams.tDevSid;
	}

	/**
	 * @Description 断开与云平台的连接
	 * @date 2016-9-7 上午10:40:10
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void disconnect() {
		TCPServer.obtainTCPService().loginOut();
	}

	/**
	 * @Description 获取TCP连接状态和登录状态
	 * @return true：连接成功；false：连接失败
	 * @date 2016-9-7 下午2:49:47
	 * @author xhb
	 * @return boolean 返回类型
	 */
	public static boolean isConnect() {
		return TCPServer.obtainTCPService().isConnect();
	}

	/**
	 * @Description 发送VOIP数据包
	 * @param cmd
	 *            VOIP命令范围在101-200,10101-10200
	 * @param voipBuf
	 *            VOIP数据包
	 * @return true：发送成功；false：发送失败
	 * @date 2016-9-7 下午2:52:11
	 * @author xhb
	 * @return boolean 返回类型
	 */
	public static boolean sendPacket(int cmd, byte[] voipBuf) {
		if (checkVoipPacketParams(cmd, voipBuf)) {
			TCPLog.d("send VOIP Packet...");
			return TCPServer.obtainTCPService().sendPacket(cmd, voipBuf);
		} else {
			return false;
		}
	}

	private static boolean checkVoipPacketParams(int cmd, byte[] voipBuf) {
		if (!((cmd >= 101 && cmd <= 200) || (cmd >= 10101 && cmd <= 10200)) || voipBuf == null) {
			CustomLog.e("sendPacket voip 参数错误!!!");
			return false;
		}
		if (voipBuf.length > 40960) {
			CustomLog.e("sendPacket voip voipBuf.length > 40960!");
			return false;
		}
		return true;
	}

	/**
	 * @Description 发送IM消息包
	 * @param cmd
	 *            IM命令码
	 * @param request
	 *            IM请求参数
	 * @return 序列化后的包
	 * @date 2016-9-7 下午2:57:42
	 * @author xhb
	 * @return PackContent 返回类型
	 */
	public static PackContent sendPacket(int cmd, IGGBaseRequest request) {
		if (checkImPacketParams(request)) {
			return TCPServer.obtainTCPService().sendPacket(cmd, request);
		} else {
			return null;
		}
	}

	private static boolean checkImPacketParams(IGGBaseRequest request) {
		if (request == null) {
			CustomLog.e("sendPacketIM 参数错误!!!");
			return false;
		}
		return true;
	}

	/**
	 * @Description 设置TCP接收消息监听器
	 * @param listenerKey
	 *            接收消息组件类型 ITcpRecvListener.IMSDK/VOIPSDK
	 * @param listener
	 *            接收消息监听器
	 * @date 2016-9-7 下午2:59:59
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setTcpRecvListener(String listenerKey, ITcpRecvListener listener) {
		if (checkTcpRecvListenerParams(listenerKey, listener)) {
			TCPListenerManager.getInstance().setTcpRecvListener(listenerKey, listener);
		}
	}

	private static boolean checkTcpRecvListenerParams(String listenerKey, ITcpRecvListener listener) {
		if (StringUtils.isEmpty(listenerKey)
				|| (!listenerKey.equals(ITcpRecvListener.IMSDK) && !listenerKey.equals(ITcpRecvListener.VOIPSDK))
				|| listener == null) {
			CustomLog.e("setTcpRecvListener 参数错误!!!");
			return false;
		}
		return true;
	}

	/**
	 * @Description 安装登录回调接口
	 * @param listener
	 *            登录监听器
	 * @date 2016-9-7 下午3:05:57
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setLoginListener(ILoginListener listener) {
		if (null == listener) {
			CustomLog.e("setLoginListener 参数错误!!!");
			return;
		}
		TCPListenerManager.getInstance().setLoginListener(listener);
	}

	/**
	 * @Description 移除登录监听器
	 * @param listener
	 *            登录监听器
	 * @date 2016-9-7 下午3:06:58
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void removeLoginListener(ILoginListener listener) {
		if (null == listener) {
			CustomLog.e("removeLoginListener 参数错误!!!");
			return;
		}
		TCPListenerManager.getInstance().delLoginListenerList(listener);
	}

	/**
	 * @Description 安装重登录回调接口
	 * @param listener
	 *            重登录监听器
	 * @date 2016-9-7 下午3:08:33
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setReLoginListener(IReLoginListener listener) {
		if (null == listener) {
			CustomLog.e("setReLoginListener 参数错误!!!");
			return;
		}
		TCPListenerManager.getInstance().setReLoginListener(listener);
	}

	/**
	 * @Description 安装监控服务状态的回调 服务开启或者关闭
	 * @param listener
	 *            服务状态监听器
	 * @date 2016-9-7 下午3:11:58
	 * @author xhb
	 * @return void 返回类型
	 */
	// public static void setServiceListener(IServiceListener listener) {
	// if (null == listener) {
	// CustomLog.e("setServiceDestoryListener 参数错误!!!");
	// return;
	// }
	// TCPListenerManager.getInstance().setServiceListener(listener);
	// }

	/**
	 * @Description 设置SDK状态监听器
	 * @param listener
	 *            SDK状态监听
	 * @date 2016-9-7 下午3:13:24
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setISdkStatusListener(ISdkStatusListener listener) {
		if (listener == null) {
			CustomLog.e("setISdkStatusListener 参数错误!!!");
			return;
		}
		TCPListenerManager.getInstance().setISdkStatusListener(listener);
	}

	/**
	 * @Description 移除SDK状态监听
	 * @param listener
	 *            要移除的SDK监听器
	 * @date 2016-9-7 下午3:14:14
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void removeISdkStatusListener(ISdkStatusListener listener) {
		if (listener == null) {
			CustomLog.e("removeISdkStatusListener 参数错误!!!");
			return;
		}
		TCPListenerManager.getInstance().delISdkStatusListener(listener);
	}

	/**
	 * @Description 是否开启崩溃异常捕获
	 * @param context
	 *            程序上下文
	 * @param crash
	 *            true:开启，false:不开启
	 * @date 2016-9-7 下午3:15:14
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setCrashException(Context context, boolean crash) {
		if (context != null) {
			context.getSharedPreferences("yzxTCP", 0).edit()
					.putBoolean("YZX_CRASH_EXCEPTION", crash).commit();
		}
	}

	/**
	 * @Description 获取是否开启崩溃异常捕获状态(有些客户不想让我们开启崩溃异常捕获，所以增加此接口，让用户自己去设置，默认是开启的)
	 * @param context
	 *            程序上下文
	 * @return true:开启，false:不开启
	 * @date 2016-9-7 下午3:16:34
	 * @author xhb
	 * @return boolean 返回类型
	 */
	public static boolean getCrashException(Context context) {
		if (context != null) {
			return context.getSharedPreferences("yzxTCP", 0).getBoolean(
					"YZX_CRASH_EXCEPTION", true);
		} else {
			return true;
		}
	}

	/**
	 * @Description 给指定用户发送tcp透传数据。
	 * @param targetId
	 *            对方userId
	 * @param stock
	 *            待发送的内容
	 * @param sendListener
	 *            透传回调监听
	 * @date 2016-9-7 下午3:17:28
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void sendTransData(String targetId, UCSTransStock stock, OnSendTransRequestListener sendListener) {
		if (YzxTCPCore.getContext() == null
				|| !YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0)
						.getBoolean("TRANS_DATA_ENABLE", true)) {
			return;
		}
		if (checkTransParams(targetId, stock, sendListener)) {
			stock.targetId = targetId;
			if (UCSManager.isConnect()) { // 发送透传请求前判断是否连接成功，如果未连接成功直接返回
				// 创建请求
				IUCSRequest transRequest = TransConetntFactory.obtain().createUCSTransRequest(stock);
				RequestProvider provider = new RequestProvider(transRequest);
				provider.setSendTransContentListener(sendListener);
				// 发送成功，添加定时器
				if (provider.send()) {
					ProviderHandler.addProvider(transRequest.msgId, provider);
				}
			} else {
				sendListener.onError(IUCSRequest.SendErrorCode.TCP_NO_CONNECTION, "");
			}
		}
	}

	private static boolean checkTransParams(String targetId, UCSTransStock stock,
			OnSendTransRequestListener sendListener) {
		if (sendListener == null) {
			TCPLog.e("透传回调为空");
			return false;
		}

		if (TextUtils.isEmpty(targetId)) {
			TCPLog.e("透传targetId为空" + "targetId=" + targetId);
			sendListener.onError(IUCSRequest.SendErrorCode.ERROR_USER_NO_EXISTS, "");
			return false;
		}

		if (stock == null || TextUtils.isEmpty(stock.onTranslate())) {
			TCPLog.e("透传数据为空...");
			sendListener.onError(IUCSRequest.SendErrorCode.CONTENT_NULL_DATA, "");
			return false;
		}

		if (stock.onTranslate().getBytes().length > 500) {
			// 传递的data不能超过500
			TCPLog.e("透传数据超长...");
			sendListener.onError(IUCSRequest.SendErrorCode.DATA_TOO_LARGE, "");
			return false;
		}
		return true;
	}

	/**
	 * @Description 设置透传响应数据
	 * @param ackString
	 *            透传响应数据
	 * @return true:设置成功，false:设置失败
	 * @date 2016-9-7 下午3:21:10
	 * @author xhb
	 * @return boolean 返回类型
	 */
	public static boolean setTransAckData(String ackString) {
		if (ackString == null)
			return false;
		if (ackString.length() > 500)
			return false;
		if (YzxTCPCore.getContext() == null
				|| !YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0)
						.getBoolean("TRANS_DATA_ENABLE", true)) {
			return false;
		}
		YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0).edit()
				.putString("TRANS_ACK_DATA", ackString).commit();
		return true;
	}

	/**
	 * @Description 设置透传消息监听
	 * @param listener
	 *            透传消息监听器
	 * @date 2016-9-7 下午3:24:44
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setOnRecvTransUCSListener(OnRecvTransUCSListener listener) {
		if (YzxTCPCore.getContext() == null
				|| !YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0)
						.getBoolean("TRANS_DATA_ENABLE", true)) {
			return;
		}

		TCPListenerManager.getInstance().setOnRecvTransUCSListener(listener);
	}

	/**
	 * @Description 移除透传消息监听
	 * @param listener
	 *            需要移除的透传监听器
	 * @date 2016-9-7 下午3:25:29
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void removeOnRecvTransUCSListener(OnRecvTransUCSListener listener) {
		if (YzxTCPCore.getContext() == null
				|| !YzxTCPCore.getContext().getSharedPreferences("YZX_VOIP_DEFAULT", 0)
						.getBoolean("TRANS_DATA_ENABLE", true)) {
			return;
		}

		TCPListenerManager.getInstance().removeOnRecvTransUCSListener(listener);
	}

	/**
	 * @Description 设置视频预览图片透传监听
	 * @param perviewImgTransListener
	 *            视频预览图片透传监听
	 * @date 2017-3-9 下午2:11:36
	 * @author xhb
	 * @return void 返回类型
	 */
	public static void setPerviewImgTransListener(OnRecvPerviewImgTransListener perviewImgTransListener) {
		TCPListenerManager.getInstance().setPerviewImgTransListener(perviewImgTransListener);
	}
}
