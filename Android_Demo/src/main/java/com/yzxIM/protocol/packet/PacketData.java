package com.yzxIM.protocol.packet;

/**
 * 通讯协议
 */
public class PacketData {
	public static enum RequestCmd {
		REQ_NONE,

		REQ_AUTH, /** 1< Auth 请求 登陆 */
		REQ_REAUTH, /** 2< ReAuth请求 使用SK */

		REQ_SEND_MSG, /** 3< SendMsg 请求 点对点发送消息 */
		REQ_NEW_SYNC, /** 4< New Sync 请求 点对点同步消息 */

		REQ_NEW_NOTIFY, /** 5< NewNotify 请求 服务端NOTIFY消息 */
		REQ_NEW_SYNCCHK, /** 6< NewSyncCheck 请求 同步有效KEY检查 */

		REQ_UPLOAD_VOICE, /** 7< UploadVoice 请求 上传语音 */
		REQ_DOWNLOAD_VOICE, /** 8< DownloadVoice 请求 下载语音 */

		REQ_UPLOAD_IMG, /** 9< UploadMsgImg 请求 上传图片 */
		REQ_DOWNLOAD_IMG, /** 10< DownloadMsgImg 下载图片请求 */

		REQ_UPLOAD_VIDEO, /** 11< UploadVideo 请求 上传视频 */
		REQ_DOWNLOAD_VIDEO, /** 12< DownloadVideo 请求 下载视频 */

		REQ_CREATE_GROUP, /** 13< CreateGroup 请求 创建讨论组 */

		REQ_ADD_GROUP_MEMBER, /** 14< AddGroupMember 请求 讨论组加人 */

		REQ_DEL_GROUP_MEMBER, /** 15< DelGroupMember 请求 讨论组踢人 */

		REQ_QUIT_GROUP,		/** 16< QuitGroup 请求 讨论组成员主动退出 */
		
		REQ_SEND_LOCALMAP, /** 17< SEND_LOCALMAP 请求 发送地图定位消息*/
		
		REQ_SENDMSG_TIMEROUT;   /** 18< SENDMSG_TIMEROUT 请求 发送消息超时*/
		
		public static RequestCmd valueof(int cmd){
			RequestCmd req = REQ_NONE;
			for (RequestCmd s : RequestCmd.values()) {
				if(s.ordinal() == cmd){
					return s;
				}
			}
			return req;
		}
	};

	public static enum ResponeCmd {
		RESP_NONE(30000), /** < 无响应 */

		RESP_AUTH(30001), /** < Auth 响应 */
		RESP_REAUTH(30002), /** < ReAuth响应 使用SK */

		RESP_SEND_MSG(30003), /** < SendMsg 响应 */
		RESP_NEW_SYNC(30004), /** < New Sync 响应 */

		RESP_NEW_SYNCCHK(30006), /** < NewSyncCheck 请求 */

		RESP_UPLOAD_VOICE(30007), /** < UploadVoice 响应 */
		RESP_DOWNLOAD_VOICE(30008), /** < DownloadVoice 响应 */

		RESP_UPLOAD_MSGIMG(30009), /** < UploadMsgImg 响应 */
		RESP_DOWNLOAD_MSGIMG(30010), /** < DownloadMsgImg 响应 */

		RESP_UPLOAD_VIDEO(30011), /** < UploadVideo 响应 */
		RESP_DOWNLOAD_VIDEO(30012), /** < DownloadVideo 响应 */

		RESP_CREATE_GROUP(30013), /** < CreateGroup 响应 */

		RESP_ADD_GROUP_MEMBER(30014), /** < AddGroupMember 响应 */

		RESP_DEL_GROUP_MEMBER(30015), /** < DelGroupMember 响应 */

		RESP_QUIT_GROUP(30016);		/** < QuitGroup 响应 */

		private int value = 30000;

		private ResponeCmd(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
		
		public static ResponeCmd valueof(int cmd){
			ResponeCmd req = RESP_NONE;
			for (ResponeCmd s : ResponeCmd.values()) {
				if(s.value() == cmd){
					return s;
				}
			}
			return req;
		}
	}
	
	
	public static enum  ENMMDataType
	{
		MM_DATA_NONE(0),
		MM_DATA_TEXT(1), // 文本类型
		//MM_DATA_HTML=2, // 未使用,已废弃
		MM_DATA_IMG(3), // 图片类型
		MM_DATA_PRIVATEMSG_(11), // 私信文本
		//MM_DATA_PRIVATEMSG_HTML=12, // 未使用,已废弃
		MM_DATA_PRIVATEMSG_IMG(13), // 私信图片
		MM_DATA_CHATROOMMSG_TEXT(21), // 被客户端误用，兼容 
		//MM_DATA_CHATROOMMSG_HTML=22, // 未使用,已废弃
		MM_DATA_CHATROOMMSG_IMG(23),  // 被客户端误用，兼容
		//MM_DATA_EMAILMSG_TEXT=31,    // 未使用,已废弃
		//MM_DATA_EMAILMSG_HTML=32,    // 未使用,已废弃
		//MM_DATA_EMAILMSG_IMG=33,     // 未使用,已废弃
		MM_DATA_VOICEMSG(34),  // 语音类型
		MM_DATA_PUSHMAIL(35), // pushmail类型
		MM_DATA_QMSG(36), // QQ离线消息文本
		MM_DATA_VERIFYMSG(37), // 好友验证类型
		MM_DATA_PUSHSYSTEMMSG(38), // 广告消息类型
		MM_DATA_QQLIXIANMSG_IMG(39), // QQ离线消息图片
		MM_DATA_POSSIBLEFRIEND_MSG(40), // 好友推荐类型
		MM_DATA_PUSHSOFTWARE(41), // 精品软件推荐类型
		MM_DATA_SHARECARD(42), // 名片分享类型
		MM_DATA_VIDEO(43), // 视频类型
		MM_DATA_VIDEO_IPHONE_EXPORT(44), // 转发视频类型
		MM_DATA_GMAIL_PUSHMAIL(45), // Gmail pushmail类型
		MM_DATA_EMPTY(46), //客户端要求，占坑
		MM_DATA_EMOJI(47), // 自定义表情类型
		MM_DATA_LOCATION(48), // 位置消息类型
		MM_DATA_CUSTOMMSG(49), // 用户自定义消息
		MM_DATA_VOIP_INVITE(50), //voipinvite
		MM_DATA_APPMSG(51), // AppMsg
		MM_DATA_WEIBOPUSH(52), // Weibo Push( MMReader )
		MM_DATA_WEBWXVOIPNOTIFY(53), // webwx voip notify
	    MM_DATA_CHATROOMVERIFYMSG(54), //申请加入群验证消息
		MM_DATA_BRAND_QA_ASK(55), //公众平台问答 提问
		MM_DATA_TALK_SYSMSG(56), //对讲模式的系统消息
		MM_DATA_BRAND_QA_MSG(57), //公众平台问答 普通消息
		MM_DATA_OPEN_SUBSCRIBE(58), //开放平台 订阅消息
		MM_DATA_OPEN_REPORT(59), //开放平台 报告消息
		MM_DATA_OPEN_LINK(60), //开放平台 报告消息
		MM_DATA_DEL_CONTACT(61), // 删除好友消息
		MM_DATA_VERIFY_CHATMEMBERMSG(62), // 群成员验证类型
		MM_DATA_INVITE_CHATMEMBERMSG(63), // 邀请群成员类型
		MM_DATA_LBSMATCH_SUCCESS(64), // 配对成功
		MM_DATA_CHATROOMINFO_UPDATE(65), // 群资料修改成功
		MM_DATA_CHATROOM_BEKICKOUT(66), // 被移出群
		MM_DATA_CHATROOM_CLOSE(67), //群解散
		MM_DATA_BIND_PROMTP(68), // 新加入的第三方用户推荐给老用户
		MM_DATA_SYSCMD_IPXX(9998), // 用于查询问题通知客户端上传日志
		MM_DATA_SYSNOTICE(9999), // 系统通知类型
		MM_DATA_SYS(10000), // 系统消息，出现在会话中间
	    MM_DATA_SYSCMD_XML(10001),  //系统命令XML消息，客户端只处理，不显示
	    MM_DATA_SYSCMD_NEWXML(10002);  //系统命令XML消息，客户端只处理，不显示
	    
	    
	    
	    private int value;

	    private ENMMDataType(int value) {
			// TODO Auto-generated constructor stub
			this.value = value;
		}
		
	    public int value(){
	    	return this.value;
	    }
	    
		public static ENMMDataType valueof(int myType){
			ENMMDataType type = MM_DATA_NONE;
			
			for(ENMMDataType s : ENMMDataType.values()){
				if(s.value() == myType){
					return s;
				}
			}
			return type;
		}
	}

	enum IGGNewSyncSelector {
		MM_NEWSYNC_PROFILE(0x1), // 个人主页
		MM_NEWSYNC_MSG(0x2), // 消息
		MM_NEWSYNC_WXCONTACT(0x4), // 微信好友
		MM_NEWSYNC_SNSSYNCKEY(0x100); // 朋友圈

		private int value = 0x1;

		IGGNewSyncSelector(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
		
	};
	
}
