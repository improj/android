package com.yzxtcp.data;


/**
 * 错误码
 * 
 * @author zhuqian
 */
public class UcsErrorCode {
	/**
	 * 连接服务器失败
	 */
	public final static int NET_ERROR_CONNECTFAIL = 300100;
	/**
	 * 连接超时
	 */
	public final static int NET_ERROR_CONNECTTIMEOUT = 300101;
	/**
	 * 强制踢线
	 */
	public final static int NET_ERROR_KICKOUT = 300102;
	/**
	 * 无效的token 或 与appid不匹配
	 */
	public final static int NET_ERROR_TOKENERROR = 300103;
	/**
	 * 用户不存在
	 */
	public final static int NET_ERROR_USERUNKNOWN = 300104;
	/**
	 * 密码错误
	 */
	public final static int NET_ERROR_PASSWORDERROR = 300105;
	/**
	 * 重新连接成功
	 */
	public final static int NET_ERROR_RECONNECTOK = 300106;
	/**
	 * 连接服务器成功
	 */
	public final static int NET_ERROR_CONNECTOK = 300107;
	/**
	 * TCP 连接成功
	 */
	public final static int NET_ERROR_TCPCONNECTOK = 300108;
	/**
	 * TCP 连接失败
	 */
	public final static int NET_ERROR_TCPCONNECTFAIL = 300109;
	/**
	 * TCP 连接中
	 */
	public final static int NET_ERROR_TCPCONNECTING = 300110;
	/**
	 * 获取CPS失败
	 */
	public final static int NET_ERROR_GET_CPS = 300111;
	
	
	/**
	 * 无效的消息(为null)
	 */
	public final static int IM_ERROR_INVALIDMSG = 300300;
	/**
	 * 无效的群组
	 */
	public final static int IM_ERROR_INVALIDGROUP = 300301;
	/**
	 * 无效的讨论组
	 */
	public final static int IM_ERROR_INVALIDDISSCUSSION = 300302;
	/**
	 * 修改讨论组名字失败
	 */
	public final static int IM_ERROR_MODIFYDISFAIL = 300303;
	/**
	 * 创建讨论组失败
	 */
	public final static int IM_ERROR_CREATEDISFAIL = 300304;
	/**
	 * 用户不在群组内
	 */
	public final static int IM_ERROR_USERNOTINGROUP = 300305;
	/**
	 * 用户不在讨论组内
	 */
	public final static int IM_ERROR_USERNOTINDIS = 300306;
	/**
	 * 删除成员失败
	 */
	public final static int IM_ERROR_DELUSERFAIL = 300307;
	/**
	 * 邀请成员失败
	 */
	public final static int IM_ERROR_ADDUSERFAIL = 300308;
	/**
	 * 文件上传失败
	 */
	public final static int IM_ERROR_UPLOADFILEFAIL = 300309;
	/**
	 * 文件下载失败
	 */
	public final static int IM_ERROR_DOWNLOADFILEFAIL = 300310;
	/**
	 * 录音时间过短
	 */
	public final static int IM_ERROR_RECORDTOOSHORT = 300311;
	/**
	 * 文件格式不支持
	 */
	public final static int IM_ERROR_INVALIDFILEFORMAT = 300312;
	/**
	 * 消息内容过长
	 */
	public final static int IM_ERROR_MSGTOOLONG = 300313;
	/**
	 * 读取本地数据库消息列表失败
	 */
	public static int IM_ERROR_READDBFAIL = 300314;
	/**
	 * 消息写入本地数据库失败
	 */
	public final static int IM_ERROR_WRITEDBFAIL = 300315;
	/**
	 * 退出讨论组失败
	 */
	public final static int IM_ERROR_QUITDISFAIL = 300316;
	
	/**
	 * 参数错误
	 */
	public final static int PUBLIC_ERROR_PARAMETERERR = 300600;
	/**
	 * 消息格式错误
	 */
	public final static int PUBLIC_ERROR_MSGFORMATERR = 300601;
	/**
	 * 网络未连接
	 */
	public final static int PUBLIC_ERROR_NETUNCONNECT = 300602;
	/**
	 * 初始化加载失败(包括数据库加载)
	 */
	public final static int PUBLIC_ERROR_INITFAIL = 300603;
	/**
	 * 操作过于频繁
	 */
	public final static int PUBLIC_ERROR_OPERATIONFREQUENT = 300604;
	/**
	 * 网络已连接
	 */
	public final static int PUBLIC_ERROR_NETCONNECTED = 300605;
}
