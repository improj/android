package com.yzxIM.data.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.IMUserData;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.protocol.packet.PacketData.ENMMDataType;
import com.yzxtcp.UCSManager;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;

public class DBManager {
	// 聊天记录表的前缀
	public static final String SINGLE_TABLE_PREFIX = "singlechat_";
	public static final String GROUP_TABLE_PREFIX = "groupchat_";
	public static final String DISCUSSION_TABLE_PREFIX = "discussionchat_";
	public static final String BROADCAST_TABLE_PREFIX = "broadcast_";
	public static final String NONE_TABLE_PREFIX = "none_";
	
	public static final String USER_INFO_TABLENAME = "userInfo";
	public static final String GROUP_INFO_TABLENAME = "groupInfo";
	public static final String DISCUSSION_INFO_TABLENAME = "discussionInfo";
	public static final String CONVERSATION_TABLENAME = "conversation";
	public static final String VERSION_TABLENAME = "version";
	
	private static DBManager dbManager;
	private IMDBHelper helper;
	private SQLiteDatabase db;
//	private Context mContext;
	private String mDbName;

	private DBManager() {
		CustomLog.v("DBManager instance create ...");
//		mContext = YzxTCPCore.getContext();
		if ((mDbName = IMUserData.getDbName()).equals("") == false) {
			CustomLog.v("DBManager dbName exist ..." + mDbName);
			createDatabase(mDbName);
/*			UCSManager.setServiceListener(new IServiceListener() {
				@Override
				public void onServiceStart() {
				}

				@Override
				public void onServiceDestory() {
					closeDb();
				}
			});*/
		}
	}

	public static DBManager getInstance() {
		if (dbManager == null) {
			synchronized (IMManager.class) {
				if (dbManager == null) {
					dbManager = new DBManager();
				}
			}
		}
		return dbManager;
	}

	public void createDatabase(String dbName) {
		if (dbName == null) {
			CustomLog.e("createDatabase dbName is null");
			return;
		}
		mDbName = dbName;
		if (YzxTCPCore.getContext() == null) {
			throw new RuntimeException("createDatabase mContext is null");
		}
		helper = new IMDBHelper(YzxTCPCore.getContext(), dbName);
		if (helper == null) {
			throw new RuntimeException("createDatabase helper is null");
		}
		db = helper.getWritableDatabase(); // 创建数据库
		if (db == null) {
			throw new RuntimeException("createDatabase db is null");
		}
	}

	// 开启事务
	public void beginTransaction() {
		db.beginTransaction();
	}

	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}

	// 结束事务
	public void endTransaction() {
		db.endTransaction();
	}

	public void closeDb() {
		if (db != null) {
			db.close();
			db = null;
		}
		if (helper != null) {
			helper.close();
			helper = null;
		}
		CustomLog.d("关闭数据库");
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param tableName 表名
	 * @descript:创建群组聊天信息表
	 */
	public void createTableGroupChat(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			CustomLog.e("createTableGroupChat 参数错误 ");
			return;
		}
		if (helper == null) {
			CustomLog.e("helper is null");
			return;
		}
		if (db == null) {
			CustomLog.e("db is null");
			return;
		}
		helper.createTableGroupChat(db, tableName);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param tableName 表名
	 * @descript:创建讨论组聊天信息表
	 */
	public void createDiscussionChat(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			CustomLog.e("createDiscussionChat 参数错误 ");
			return;
		}
		if (helper == null) {
			CustomLog.e("helper is null");
			return;
		}
		if (db == null) {
			CustomLog.e("db is null");
			return;
		}
		helper.createDiscussionChat(db, tableName);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param tableName 表名
	 * @descript:创建单聊聊天信息表
	 */
	public void createSignalChat(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			CustomLog.e("createSignalChat 参数错误 ");
			return;
		}
		if (helper == null) {
			CustomLog.e("helper is null");
			return;
		}
		if (db == null) {
			CustomLog.e("db is null");
			return;
		}
		helper.createSignalChat(db, tableName);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建系统广播会话聊天信息表
	 */
	public void createBroadcastChat(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			CustomLog.e("createBroadcastChat 参数错误 ");
			return;
		}
		if (helper == null) {
			CustomLog.e("helper is null");
			return;
		}
		if (db == null) {
			CustomLog.e("db is null");
			return;
		}
		helper.createBroadCastChat(db, tableName);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建未知聊天信息表
	 */
	public void createNoneChat(String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			CustomLog.e("createNoneChat 参数错误 ");
			return;
		}
		if (helper == null) {
			CustomLog.e("helper is null");
			return;
		}
		if (db == null) {
			CustomLog.e("db is null");
			return;
		}
		helper.createNoneChat(db, tableName);
	}
	
	// 创建数据表结束

	// 添加表信息开始
	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param tableName 表名
	 * @param userInfo 用户信息
	 * @descript:添加用户信息资料到用户信息表
	 */
	public void addUserInfo(String tableName, UserInfo userInfo) {
		if (StringUtils.isEmpty(tableName) || userInfo == null) {
			CustomLog.e("addUserInfo 参数错误 ");
			return;
		}
		db.execSQL("INSERT INTO " + tableName + " VALUES(null, ?, ?, ?,?,?,?)",
				new Object[] { userInfo.getUserId(), userInfo.getUserName(),
						userInfo.getCategoryId(), userInfo.getPortrailUrl(),
						userInfo.getUpdateTime(), userInfo.getUserSettings() });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName 表名
	 * @param groupInfo 群组信息
	 * @descript:
	 */
	public void addGroupInfo(String tableName, GroupInfo groupInfo) {
		if (StringUtils.isEmpty(tableName) || groupInfo == null) {
			CustomLog.e("addGroupInfo 参数错误 ");
			return;
		}
		db.execSQL("INSERT INTO " + tableName + " VALUES(null, ?, ?, ?,?)",
				new Object[] { groupInfo.getGroupId(),
						groupInfo.getGroupName(), groupInfo.getCategoryId(),
						groupInfo.getUpdateTime() });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName 表名
	 * @param discussionInfo 讨论组信息
	 * @descript:插入相应的讨论组信息到表中
	 */
	public void addDiscussionInfo(DiscussionInfo discussionInfo) {
		if (discussionInfo == null) {
			CustomLog.e("addDiscussionInfo 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO " + DISCUSSION_INFO_TABLENAME
						+ " VALUES(null, ?, ?, ?,?, ?, ? ,?, ?)",
				new Object[] { discussionInfo.getDiscussionId(),
						discussionInfo.getDiscussionName(),
						discussionInfo.getCategoryId().ordinal(),
						discussionInfo.getMemberCount(),
						discussionInfo.getOwnerId(),
						discussionInfo.getDiscussionMembers(),
						discussionInfo.getDisscussionSettings(),
						discussionInfo.getCreateTime() + "" });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName 表名
	 * @param versionInfo 版本信息
	 * @descript 插入相应的版本信息到表中
	 */
	public void addVersionInfo(String tableName, VersionInfo versionInfo) {
		if (StringUtils.isEmpty(tableName) || versionInfo == null) {
			CustomLog.e("addUserInfo 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO " + tableName + " VALUES(null, ?, ?, ?)",
				new Object[] { versionInfo.getDatabaseVersion(),
						versionInfo.getSdkVersion(),
						versionInfo.getCreateTime() });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName 表名
	 * @param conversationInfo 会话信息列表
	 * @descript 插入相应的会话信息到表中
	 */
	public void addConversationInfo(ConversationInfo conversationInfo) {
		if (conversationInfo == null) {
			CustomLog.e("addConversationInfo 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO " + CONVERSATION_TABLENAME
						+ " VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { conversationInfo.getTargetId(),
						conversationInfo.getConversationTitle(),
						conversationInfo.getCategoryId().ordinal(),
						conversationInfo.getDraftMsg(),
						conversationInfo.getIsTop(),
						conversationInfo.getLastTime() + "",
						conversationInfo.getTopTime() + "",
						conversationInfo.getMsgUnRead() });

	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName
	 * @param groupChat 群聊天信息
	 * @descript 插入相应的群组信息到表中
	 */
	public void addGroupChat(String tableName, GroupChat groupChat) {
		if (StringUtils.isEmpty(tableName) || groupChat == null) {
			CustomLog.e("addGroupChat 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO "
						+ tableName
						+ " VALUES(null, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
				new Object[] { groupChat.getMsgid(), groupChat.getTargetId(),
						groupChat.getSenderId(), groupChat.getNickName(),
						groupChat.getCategoryId().ordinal(),
						groupChat.getIsFromMyself(),
						groupChat.getSendTime() + "",
						groupChat.getReceiveTime() + "",
						groupChat.getMsgType().ordinal(),
						groupChat.getContent(), groupChat.getReadStatus(),
						groupChat.getSendStatus(), groupChat.getPath(),
						groupChat.getParentID(), groupChat.getExtMessage(),
						groupChat.getExtMessage2() });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName
	 * @param discussionChat 讨论组聊天信息
	 * @descript 插入相应的讨论组信息到表中
	 */
	public void addDiscussionChat(String tableName,
			DiscussionChat discussionChat) {
		if (StringUtils.isEmpty(tableName) || discussionChat == null) {
			CustomLog.e("addDiscussionChat 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO "
						+ tableName
						+ " VALUES(null, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
				new Object[] { discussionChat.getMsgid(),
						discussionChat.getTargetId(),
						discussionChat.getSenderId(),
						discussionChat.getNickName(),
						discussionChat.getCategoryId().ordinal(),
						discussionChat.getIsFromMyself(),
						discussionChat.getSendTime() + "",
						discussionChat.getReceiveTime() + "",
						discussionChat.getMsgType().ordinal(),
						discussionChat.getContent(),
						discussionChat.getReadStatus(),
						discussionChat.getSendStatus(),
						discussionChat.getPath(), discussionChat.getParentID(),
						discussionChat.getExtMessage(),
						discussionChat.getExtMessage2() });
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName
	 * @param SingleChat 单聊信息
	 * @descript 插入相应的单聊信息到表中
	 */
	public void addSingleChat(String tableName, SingleChat singleChat) {
		if (StringUtils.isEmpty(tableName) || singleChat == null) {
			CustomLog.e("addSingleChat 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO "
						+ tableName
						+ " VALUES(null, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
				new Object[] { singleChat.getMsgid(), singleChat.getTargetId(),
						singleChat.getSenderId(), singleChat.getNickName(),
						singleChat.getCategoryId().ordinal(),
						singleChat.getIsFromMyself(),
						singleChat.getSendTime() + "",
						singleChat.getReceiveTime() + "",
						singleChat.getMsgType().ordinal(),
						singleChat.getContent(), singleChat.getReadStatus(),
						singleChat.getSendStatus(), singleChat.getPath(),
						singleChat.getParentID(), singleChat.getExtMessage(),
						singleChat.getExtMessage2() });

	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName
	 * @param BroadCastChat 广播信息
	 * @descript 插入相应的广播信息到表中
	 */
	public void addBroadcastChat(String tableName, BroadCastChat broadCastChat) {
		if (StringUtils.isEmpty(tableName) || broadCastChat == null) {
			CustomLog.e("addBroadcastChat 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO "
						+ tableName
						+ " VALUES(null, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
				new Object[] { broadCastChat.getMsgid(),
						broadCastChat.getTargetId(),
						broadCastChat.getSenderId(),
						broadCastChat.getNickName(),
						broadCastChat.getCategoryId().ordinal(),
						broadCastChat.getIsFromMyself(),
						broadCastChat.getSendTime() + "",
						broadCastChat.getReceiveTime() + "",
						broadCastChat.getMsgType().ordinal(),
						broadCastChat.getContent(),
						broadCastChat.getReadStatus(),
						broadCastChat.getSendStatus(), broadCastChat.getPath(),
						broadCastChat.getParentID(),
						broadCastChat.getExtMessage(),
						broadCastChat.getExtMessage2() });

	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param tableName
	 * @param noneChat 未知会话消息
	 * @descript 插入相应的未知信息到表中
	 */
	public void addNoneChat(String tableName, NoneChat noneChat) {
		if (StringUtils.isEmpty(tableName) || noneChat == null) {
			CustomLog.e("addNoneChat 参数错误 ");
			return;
		}
		db.execSQL(
				"INSERT INTO "
						+ tableName
						+ " VALUES(null, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
				new Object[] { noneChat.getMsgid(), noneChat.getTargetId(),
						noneChat.getSenderId(), noneChat.getNickName(),
						noneChat.getCategoryId().ordinal(),
						noneChat.getIsFromMyself(),
						noneChat.getSendTime() + "",
						noneChat.getReceiveTime() + "",
						noneChat.getMsgType().ordinal(), noneChat.getContent(),
						noneChat.getReadStatus(), noneChat.getSendStatus(),
						noneChat.getPath(), noneChat.getParentID(),
						noneChat.getExtMessage(), noneChat.getExtMessage2() });

	}

	// 添加类容到表结构结束

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @param c 当前会话的CURSOR
	 * @return 表名
	 * @throws Exception
	 * @descript:获取当前会话的对应的表名
	 */
	public String getConversationTableName(Cursor c) {
		String targetId = c.getString(c
				.getColumnIndex(IMDB.Conversation.COLUMN_TARGETID));
		int categoryId = c.getInt(c
				.getColumnIndex(IMDB.Conversation.COLUMN_CATEGORYID));
		CategoryId cId = CategoryId.valueof(categoryId);
		String tableName = null;

		switch (cId) {
		case PERSONAL:
			tableName = DBManager.SINGLE_TABLE_PREFIX + targetId;
			break;
		case GROUP:
			tableName = DBManager.GROUP_TABLE_PREFIX + targetId;
			break;
		case DISCUSSION:
			tableName = DBManager.DISCUSSION_TABLE_PREFIX + targetId;
			break;
		case BROADCAST:
			tableName = DBManager.BROADCAST_TABLE_PREFIX + targetId;
			break;
		case NONE:
			tableName = DBManager.NONE_TABLE_PREFIX + targetId;
			break;
		default:
			try {
				throw new Exception("getConversationTableName CategoryId 错误:"
						+ categoryId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return tableName;
	}

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @param chatMessage
	 * @return 返回表名
	 * @descript:获取消息所存储的表名
	 */
	public String getChatMsgTableName(String targetID, CategoryId categoryID) {

		String tableName = null;
		// CategoryId categoryId = categoryID;

		switch (categoryID) {
		case PERSONAL:
			tableName = DBManager.SINGLE_TABLE_PREFIX + targetID;
			break;
		case GROUP:
			tableName = DBManager.GROUP_TABLE_PREFIX + targetID;
			break;
		case DISCUSSION:
			tableName = DBManager.DISCUSSION_TABLE_PREFIX + targetID;
			break;
		case BROADCAST:
			tableName = DBManager.BROADCAST_TABLE_PREFIX + targetID;
			break;
		case NONE:
			tableName = DBManager.NONE_TABLE_PREFIX + targetID;
			break;
		default:
			break;
		}
		return tableName;
	}

	public Cursor dbQuery(String sql, String[] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}

	/**
	 * @author zhangbin
	 * @2015-5-21
	 * @param targetId 
	 * @return
	 * @descript:targetID 对应的会话是否存在
	 */
	public boolean isConversationExisit(String targetId) {
		String sql = "select " + IMDB.Conversation.COLUMN_TARGETID + " from "
				+ CONVERSATION_TABLENAME + " where " + IMDB.Conversation.COLUMN_TARGETID
				+ " like ?";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, new String[] { targetId });
			if (cursor != null && cursor.moveToFirst()) {
				if (cursor.isNull(0)) {
					return false;
				} else {
					CustomLog.d("已存在会话:" + targetId);
					return true;
				}
			} else {
				return false;
			}
		} finally {
			cursor.close();
			cursor = null;
		}
	}

	/**
	 * @author zhangbin
	 * @2015-8-24
	 * @@param targetId 讨论组ID
	 * @@return
	 * @descript:判断讨论组信息是否已存在
	 */
	public boolean isDiscussionInfoExisit(String targetId) {
		String sql = "select " + IMDB.DiscussionInfo.COLUMN_DISCUSSIONID + " from "
				+ DISCUSSION_INFO_TABLENAME + " where "
				+ IMDB.DiscussionInfo.COLUMN_DISCUSSIONID + " like ?";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, new String[] { targetId });
			if (cursor != null && cursor.moveToFirst()) {
				if (cursor.isNull(0)) {
					return false;
				} else {
					CustomLog.v("讨论组信息已存在");
					return true;
				}
			} else {
				return false;
			}
		} finally {
			cursor.close();
		}
	}

	public CategoryId getCategoryId(String targetId) {

		if (targetId.endsWith("@chatroom")) {
			return CategoryId.GROUP;
		} else if (targetId.endsWith("@group")) {
			return CategoryId.DISCUSSION;
		} else if (targetId.endsWith("@broadcast")) {
			return CategoryId.BROADCAST;
		} else if (targetId.indexOf("@") >= 0) {
			return CategoryId.NONE;
		} else {
			return CategoryId.PERSONAL;
		}
	}

	/**
	 * @author zhangbin
	 * @2015-5-21
	 * @param targetID
	 * @return
	 * @descript:去掉讨论组 或群 targetid的后缀
	 */
	public String formatTargetID(String targetID) {
		String newTargetID = null;
		int a = targetID.indexOf("@");

		if (a < 0) {
			newTargetID = targetID;
		} else {
			newTargetID = targetID.substring(0, a);
		}
		/*
		 * CategoryId categoryId = dbManager.getCategoryId(targetID); switch
		 * (categoryId) { case GROUP: newTargetID = targetID.substring(0,
		 * targetID.length() - GROUP_ENDWITH.length()); break; case DISCUSSION:
		 * newTargetID = targetID.substring(0, targetID.length() -
		 * DISCUSSION_ENDWITH.length()); break; case BROADCAST: newTargetID =
		 * targetID.substring(0, targetID.length() -
		 * BROADCAST_ENDWITH.length()); break; case NONE: int a =
		 * targetID.indexOf("@"); newTargetID = targetID.substring(0,a); break;
		 * default: newTargetID = targetID; break; }
		 */

		return newTargetID;
	}

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @@param dbMsg
	 * @@return
	 * @descript:接收到聊天消息时 初始化消息对象
	 */
	public ChatMessage initChatMessage(JSONObject jsonMsg) {
		CategoryId categoryId;
		ChatMessage chatMessage = null;
		try {
			categoryId = dbManager.getCategoryId(jsonMsg.getString("fid"));
			String name[] = jsonMsg.getString("source").split("\\+");
			String sendId = name.length > 1 ? name[1] : name[0];

			int msgtype = Integer.valueOf(jsonMsg.getInt("type"));

			switch (categoryId) {
			case PERSONAL:
				chatMessage = new SingleChat();
				break;
			case GROUP:
				chatMessage = new GroupChat();
				break;
			case DISCUSSION:
				chatMessage = new DiscussionChat();
				break;
			}

			chatMessage.setMsgid("" + jsonMsg.getInt("id"));
			chatMessage.setTargetId(formatTargetID(jsonMsg.getString("tid")));
			chatMessage.setSenderId(sendId);
			chatMessage.setParentID(formatTargetID(jsonMsg.getString("fid")));
			chatMessage.setCategoryId(categoryId);
			chatMessage.setIsFromMyself(false);
			chatMessage.setSendTime(Long.parseLong(jsonMsg.getString("time")));
			chatMessage.setReceiveTime(System.currentTimeMillis());
			if (msgtype == 1)
				chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT);
			else if (msgtype == 3) {
				chatMessage.setMsgType(MSGTYPE.MSG_DATA_IMAGE);
			} else if (msgtype == 34) {
				chatMessage.setMsgType(MSGTYPE.MSG_DATA_VOICE);
			} else if (msgtype == 10000) {
				chatMessage.setMsgType(MSGTYPE.MSG_DATA_SYSTEM);
			} else {
				System.err.println("initChatMessage unkonw msgtype");
				chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT);
			}
			chatMessage.setContent(jsonMsg.getString("content"));
			chatMessage.setReadStatus(ChatMessage.MSG_STATUS_UNREAD);
			chatMessage.setSendStatus(ChatMessage.MSG_STATUS_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return chatMessage;
	}

	public ChatMessage initChatMessage(String[] dbMsg) {
		CategoryId categoryId = dbManager.getCategoryId(dbMsg[2]);
		/*
		 * String name[] = dbMsg[5].split("\\+"); String sendId = name.length>1?
		 * name[1]:name[0];
		 */

		int msgtype = Integer.valueOf(dbMsg[1]);
		ChatMessage chatMessage = null;
		switch (categoryId) {
		case PERSONAL:
			chatMessage = new SingleChat();
			break;
		case GROUP:
			chatMessage = new GroupChat();
			break;
		case DISCUSSION:
			chatMessage = new DiscussionChat();
			break;
		case BROADCAST:
			chatMessage = new BroadCastChat();
			break;
		case NONE:
			chatMessage = new NoneChat();
			break;
		default:
			return null;
		}
		chatMessage.setMsgid(dbMsg[0]);
		chatMessage.setTargetId(formatTargetID(dbMsg[3]));
		// chatMessage.setSenderId(formatTargetID(dbMsg[2]).getBytes());
		chatMessage.setSenderId(dbMsg[5]);
		chatMessage.setParentID(formatTargetID(dbMsg[2]));
		chatMessage.setCategoryId(categoryId);
		chatMessage.setIsFromMyself(false);
		chatMessage.setSendTime(Long.parseLong(dbMsg[4]));
		// chatMessage.setReceiveTime(System.currentTimeMillis());
		chatMessage.setReceiveTime(0);
		chatMessage.setContent(dbMsg[7]);
		if (msgtype == ENMMDataType.MM_DATA_TEXT.value())
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT);
		else if (msgtype == ENMMDataType.MM_DATA_IMG.value()) {
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_IMAGE);
		} else if (msgtype == ENMMDataType.MM_DATA_VOICEMSG.value()) {
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_VOICE);
		} else if (msgtype == ENMMDataType.MM_DATA_LOCATION.value()) {
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_LOCALMAP);
		} else if (msgtype == ENMMDataType.MM_DATA_CUSTOMMSG.value()) {
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_CUSTOMMSG);
		} else if (msgtype == ENMMDataType.MM_DATA_SYS.value()) {
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_SYSTEM);
		} else {
			System.err.println("initChatMessage unkonw msgtype");
			chatMessage.setMsgType(MSGTYPE.MSG_DATA_TEXT);
			chatMessage.setContent("当前版本不支持查看此消息，请升级最新版本。");
		}

		chatMessage.setReadStatus(ChatMessage.MSG_STATUS_UNREAD);
		chatMessage.setSendStatus(ChatMessage.MSG_STATUS_SUCCESS);
		return chatMessage;
	}

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @@param chatMessage
	 * @descript:插入聊天消息到数据库
	 */
	public void insertChatMsgToDB(ChatMessage chatMessage) {

		String tableName;
		CategoryId categoryId = chatMessage.getCategoryId();
		switch (categoryId) {
		case PERSONAL:
			SingleChat singleChat;
			tableName = DBManager.SINGLE_TABLE_PREFIX
					+ chatMessage.getParentID();
			CustomLog.v("创建单聊信息表:" + tableName);
			dbManager.createSignalChat(tableName);
			CustomLog.v("插入单聊信息");
			singleChat = (SingleChat) chatMessage;
			dbManager.addSingleChat(tableName, singleChat);
			break;
		case GROUP:
			GroupChat groupChat;
			tableName = DBManager.GROUP_TABLE_PREFIX
					+ chatMessage.getParentID();

			dbManager.createTableGroupChat(tableName);
			CustomLog.v("插入群聊信息");
			groupChat = (GroupChat) chatMessage;
			dbManager.addGroupChat(tableName, groupChat);
			break;
		case DISCUSSION:
			DiscussionChat discussionChat;
			tableName = DBManager.DISCUSSION_TABLE_PREFIX
					+ chatMessage.getParentID();

			dbManager.createDiscussionChat(tableName);
			CustomLog.v("插入讨论组信息");
			discussionChat = (DiscussionChat) chatMessage;
			dbManager.addDiscussionChat(tableName, discussionChat);
			break;
		case BROADCAST:
			BroadCastChat broadCastChat;
			tableName = DBManager.BROADCAST_TABLE_PREFIX
					+ chatMessage.getParentID();
			dbManager.createBroadcastChat(tableName);
			CustomLog.v("插入广播信息");
			broadCastChat = (BroadCastChat) chatMessage;
			dbManager.addBroadcastChat(tableName, broadCastChat);
			break;
		case NONE:
			NoneChat noneChat;
			tableName = DBManager.NONE_TABLE_PREFIX + chatMessage.getParentID();
			dbManager.createNoneChat(tableName);
			CustomLog.v("插入未知会话信息");
			noneChat = (NoneChat) chatMessage;
			dbManager.addNoneChat(tableName, noneChat);
			break;
		default:
			break;
		}

	}

	/**
	 * @author zhangbin
	 * @2015-5-21
	 * @@param targetId
	 * @@param categoryId
	 * @@param content
	 * @param msgUnRead
	 *            未读消息数
	 * @param lastTime
	 *            会话接收或发生消息的时间
	 * @@return
	 * @descript:插入会话信息到数据库
	 */
	public ConversationInfo insertConversationToDb(String targetId,
			CategoryId categoryId, String content, String title, int msgUnRead,
			long lastTime) {
		String cTitle;
		if (StringUtils.isEmpty(title)) {
			cTitle = targetId;
		} else {
			cTitle = title;
		}
		ConversationInfo cvInfo = new ConversationInfo(targetId, cTitle,
				categoryId, content, false, lastTime, 0, msgUnRead);

		addConversationInfo(cvInfo);
		updateDraftMsg(targetId, content);
		// debugConversationTable();
		return cvInfo;
	}

	public void debugConversationTable() {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * From " + CONVERSATION_TABLENAME,null);
			while (cursor.moveToNext()) {
				ConversationInfo cinfo = getDBConversationInfo(cursor);
				CustomLog.v("targetid:" + cinfo.getTargetId());
			}
		} finally {
			if(cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

	}

//	public void insertUserInfo() {
//		// UserInfo
//	}

	// 更新表信息

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @@param chatMessage
	 * @@param oldMsgID
	 * @@return
	 * @descript:更新消息状态和 msgid
	 */
	public int updataMsgStatusAndMsgID(ChatMessage chatMessage, String oldMsgID) {
		String targetID = chatMessage.getParentID();
		String tableName = getChatMsgTableName(targetID,
				chatMessage.getCategoryId());
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return -1;
		}
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_SENDSTATUS, chatMessage.getSendStatus());
		cv.put(IMDB.Chat.COLUMN_MSGID, chatMessage.getMsgid());
		CustomLog.v("更新发送消息状态:" + chatMessage.getSendStatus()
				+ "    tableName:" + tableName);
		// debugDB(tableName, chatMessage);

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { oldMsgID });

	}

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @param chatMessage
	 * @param time
	 * @return
	 * @descript:更新消息状态和 msgid
	 */
	public int updataMsgStatusAndTime(ChatMessage chatMessage, String oldMsgID) {
		String targetID = chatMessage.getParentID();
		String tableName = getChatMsgTableName(targetID,
				chatMessage.getCategoryId());
		if (tabbleIsExist(tableName) == false) {
			CustomLog.e(tableName + " 表不存在");
			return -1;
		}
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_SENDSTATUS, chatMessage.getSendStatus());
		cv.put(IMDB.Chat.COLUMN_SENDTIME, chatMessage.getSendTime());

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { oldMsgID });

	}

	/**
	 * @author zhangbin
	 * @2015-12-10
	 * @param chatMessage
	 *            要更新的消息对象
	 * @param content
	 *            新消息内容
	 * @return
	 * @descript:更新消息内容
	 */
	public int updataMsgContent(ChatMessage chatMessage, String content) {
		String targetID = chatMessage.getParentID();
		String tableName = getChatMsgTableName(targetID,
				chatMessage.getCategoryId());
		if (tabbleIsExist(tableName) == false) {
			CustomLog.e(tableName + " 表不存在");
			return -1;
		}
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_CONTENT, content);
		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { new String(chatMessage.getMsgid()) });

	}

	/**
	 * @author zhangbin
	 * @2015-5-14
	 * @@param chatMessage
	 * @@param oldMsgID
	 * @@return
	 * @descript:更新消息状态和 msgid
	 */
	public int updataVoiceMsg(ChatMessage chatMessage) {
		String targetID = chatMessage.getParentID();
		String tableName = getChatMsgTableName(targetID,
				chatMessage.getCategoryId());
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return -1;
		}
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_SENDSTATUS, chatMessage.getSendStatus());
		cv.put(IMDB.Chat.COLUMN_PATH, chatMessage.getPath());

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { chatMessage.getMsgid() });

	}

	private void debugDB(String tableName, ChatMessage msg) {
		CustomLog.v("debug tableName:" + tableName);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from " + tableName, null);
			if (cursor == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v("cursor pos:" + cursor.getPosition());
				CustomLog.v("cursor count:" + cursor.getCount());
			}
			
			while (cursor.moveToNext()) {
				CustomLog.v("msgid:"
						+ cursor.getString(cursor
								.getColumnIndex(IMDB.Chat.COLUMN_MSGID)));
				CustomLog.v("sendstatus:"
						+ cursor.getInt(cursor
								.getColumnIndex(IMDB.Chat.COLUMN_SENDSTATUS)));
			}
		} finally {
			if(cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

	}

	/**
	 * @author zhangbin
	 * @2015-5-20
	 * @@param discussionID
	 * @@param discussionMembers
	 * @@param action 1=增加成员 0=删除成员 2=替换讨论组成员
	 * @@return
	 * @descript:更新讨论组成员列表
	 */
	public int updateDiscussionMemlist(String discussionID,
			String discussionMembers, int memcount, int action) {
		Cursor cursor = null;
		String mems = "";
		try {
			cursor = db.rawQuery("select * from " + DISCUSSION_INFO_TABLENAME
					+ " where " + IMDB.DiscussionInfo.COLUMN_DISCUSSIONID + " like ?",
					new String[] { discussionID });
			if (cursor == null || cursor.moveToNext() == false) {
				CustomLog.v("更新成员信息时 未找到讨论组ID:" + discussionID);
				return 0;
			}
			mems = cursor.getString(cursor
					.getColumnIndex(IMDB.DiscussionInfo.COLUMN_DISCUSSIONMEMBERS));
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		CustomLog.d("updateDiscussionMemlist first mem:" + mems);
		if (action == 1) {// 增加讨论组成员
			mems += ",";
			mems += discussionMembers;
		} else if (action == 0) { // 删除指定成员
			mems = membersDelmem(mems, discussionMembers);
		} else {
			mems = discussionMembers;
		}
		CustomLog.d("updateDiscussionMemlist after mem:" + mems);
		String ownerID = mems.split(",")[0];
		ContentValues cv = new ContentValues();
		cv.put(IMDB.DiscussionInfo.COLUMN_DISCUSSIONMEMBERS, mems);
		cv.put(IMDB.DiscussionInfo.COLUMN_MEMBERCOUNT, memcount);
		cv.put(IMDB.DiscussionInfo.COLUMN_OWNERID, ownerID);

		String[] args = { discussionID };
		return db.update(DISCUSSION_INFO_TABLENAME, cv, IMDB.DiscussionInfo.COLUMN_DISCUSSIONID
				+ " like ?", args);
	}

	/**
	 * @author zhangbin
	 * @2015-5-20
	 * @@param discussionID
	 * @@param discussionName
	 * @@return
	 * @descript:更新讨论组名称
	 */
	public int updateDiscussionName(String discussionID, String discussionName) {
		ContentValues cv = new ContentValues();
		cv.put(IMDB.DiscussionInfo.COLUMN_DISCUSSIONNAME, discussionName);

		String[] args = { discussionID };
		return db.update(DISCUSSION_INFO_TABLENAME, cv, IMDB.DiscussionInfo.COLUMN_DISCUSSIONID
				+ " like ?", args);
	}

	/**
	 * @author zhangbin
	 * @2015-5-23
	 * @@param targetID
	 * @@param lastTime
	 * @@param draftMsg
	 * @@return
	 * @descript:更新会话的LastTime 和 draftMsg msgUnRead(新增)
	 */
	public int updateConversationLTimeAndDMsg(String targetID, String lastTime,
			String draftMsg, boolean isUpMsgUnRead) {

		// updateConversationMsgUnRead(targetID, msgUnRead);
		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Conversation.COLUMN_LASTTIME, lastTime);
		cv.put(IMDB.Conversation.COLUMN_DRAFTMSG, draftMsg);
		if (isUpMsgUnRead) {
			int msgUnRead = getConversationMsgUnRead(targetID);
			msgUnRead++;
			cv.put(IMDB.Conversation.COLUMN_MSGUNREAD, msgUnRead);
		}

		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * 更新会话未读消息
	 * 
	 * @author zhangbin
	 * @2015-10-28
	 * @param targetID
	 *            会话ID
	 * @return
	 * @descript:
	 */
	public int updateConversationMsgUnRead(String targetID) {
		ContentValues cv = new ContentValues();
		int msgUnRead = getConversationMsgUnRead(targetID);
		msgUnRead++;
		cv.put(IMDB.Conversation.COLUMN_MSGUNREAD, msgUnRead);
		CustomLog.v("msgUnRead:" + msgUnRead);
		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * @author zhangbin
	 * @2015-5-23
	 * @param targetID
	 * @param title
	 * @return
	 * @descript:更新会话标题
	 */
	public int updateConversationTitle(String targetID, String title) {
		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Conversation.COLUMN_CONVERSATIONTITLE, title);

		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * @author zhangbin
	 * @2015-5-23
	 * @@param targetID
	 * @@param title
	 * @@return
	 * @descript:更新会话草稿信息
	 */
	public int updateDraftMsg(String targetID, String draftMsg) {

		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Conversation.COLUMN_DRAFTMSG, draftMsg);

		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * @author zhangbin
	 * @2015-6-15
	 * @@param targetID
	 * @@param isTop
	 * @@return
	 * @descript:更新会话是否置顶参数
	 */
	public int updateConversationisTop(String targetID, boolean isTop) {

		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Conversation.COLUMN_ISTOP, isTop);

		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * @author zhangbin
	 * @2015-6-18
	 * @@param targetID
	 * @@param msgUnRead
	 * @@return
	 * @descript:更新未读消息
	 */
	public int updateConversationMsgUnRead(String targetID, int msgUnRead) {

		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Conversation.COLUMN_MSGUNREAD, msgUnRead);

		return db.update(CONVERSATION_TABLENAME, cv, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	/**
	 * @author zhangbin
	 * @2015-6-18
	 * @@param targetID
	 * @@param msgUnRead
	 * @@return
	 * @descript:更新未读消息
	 */
	public int updateMessageReadStatus(String msgID, String targetID,
			CategoryId categoryID, int msgUnRead) {
		String tableName = getChatMsgTableName(targetID, categoryID);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return 0;
		}
		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_READSTATUS, msgUnRead);

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { msgID });
	}

	/**
	 * @author zhangbin
	 * @2015-6-18
	 * @@param targetID
	 * @@param msgUnRead
	 * @@return
	 * @descript:更新未读消息
	 */
	public int updateMessageSendStatus(String msgID, String targetID,
			CategoryId categoryID, int sendStatus) {
		String tableName = getChatMsgTableName(targetID, categoryID);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return 0;
		}
		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_SENDSTATUS, sendStatus);

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { msgID });
	}

	/**
	 * @author zhangbin
	 * @2015-6-18
	 * @@param targetID
	 * @@param msgUnRead
	 * @@return
	 * @descript:更新未读消息
	 */
	public int updateMessageSendTimeAndStatus(String msgID, String targetID,
			CategoryId categoryID, int sendStatus) {
		String tableName = getChatMsgTableName(targetID, categoryID);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return 0;
		}
		// 更新数据
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_SENDTIME, System.currentTimeMillis());
		cv.put(IMDB.Chat.COLUMN_SENDSTATUS, sendStatus);

		return db.update(tableName, cv, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { msgID });
	}

	/**
	 * @author zhangbin
	 * @2015-5-21
	 * @@param oldString
	 * @@param delString
	 * @@return
	 * @descript:从现有成员中删除直指定成员
	 */
	public String membersDelmem(String oldString, String delString) {
		if (StringUtils.isEmpty(delString) || StringUtils.isEmpty(oldString)) {
			CustomLog.e("membersDelmem 参数错误!!!");
			return oldString;
		}
		String[] oldStringsArray = oldString.split(",");
		String[] delStringsArray = delString.split(",");
		StringBuilder membs = new StringBuilder();

		for (int i = 0; i < oldStringsArray.length; i++) {
			for (int j = 0; j < delStringsArray.length; j++) {
				if (oldStringsArray[i] != null
						&& oldStringsArray[i].equals(delStringsArray[j])) {
					oldStringsArray[i] = null;
				}
			}
		}

		for (int i = 0; i < oldStringsArray.length; i++) {
			if (oldStringsArray[i] != null) {
				membs.append(oldStringsArray[i]);
				if (i < oldStringsArray.length - 1) {
					membs.append(",");
				}
			} else if (i == oldStringsArray.length - 1) {
				if (membs.length() > 0) {
					membs.deleteCharAt(membs.length() - 1);
				}

			}

		}

		return membs.toString();
	}

	// 删除表信息
	public int delDiscussionInfo(String discussionID) {

		return db.delete(DISCUSSION_INFO_TABLENAME, IMDB.DiscussionInfo.COLUMN_DISCUSSIONID
				+ " like ?", new String[] { discussionID });
	}

	public int delConversationInfo(String targetID) {

		return db.delete(CONVERSATION_TABLENAME, IMDB.Conversation.COLUMN_TARGETID
				+ " like ?", new String[] { targetID });
	}

	public int clearAllConversations() {
		return db.delete(CONVERSATION_TABLENAME, null, null);
	}

	private ConversationInfo getDBConversationInfo(Cursor c) {
		boolean istop = c.getInt(c.getColumnIndex(IMDB.Conversation.COLUMN_ISTOP)) == 0 ? false
				: true;
		ConversationInfo ci = new ConversationInfo();
		ci.setTargetId(c.getString(c.getColumnIndex(IMDB.Conversation.COLUMN_TARGETID)));
		ci.setConversationTitle(c.getString(c
				.getColumnIndex(IMDB.Conversation.COLUMN_CONVERSATIONTITLE)));
		ci.setCategoryId(c.getInt(c.getColumnIndex(IMDB.Conversation.COLUMN_CATEGORYID)));

		ci.setDraftMsg(c.getString(c.getColumnIndex(IMDB.Conversation.COLUMN_DRAFTMSG)));
		ci.setIsTop(istop);
		ci.setLastTime(Long.parseLong(c.getString(c
				.getColumnIndex(IMDB.Conversation.COLUMN_LASTTIME))));
		ci.setTopTime(Long.parseLong(c.getString(c
				.getColumnIndex(IMDB.Conversation.COLUMN_TOPTIME))));
		ci.setMsgUnRead(c.getInt(c.getColumnIndex(IMDB.Conversation.COLUMN_MSGUNREAD)));
		return ci;

	}

	public void clearMessages(String targetId, CategoryId categoryId) {
		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return;
		}
		// db.execSQL("DELETE FROM "+tableName);
		db.delete(tableName, null, null);
	}

	/**
	 * @author zhangbin
	 * @2015-5-12
	 * @@param msgid 需要删除消息的MSGID
	 * @@return 删除是否成功
	 * @descript:删除指定MSGID的消息记录
	 */
	public boolean deleteMessages(String targetId, CategoryId categoryId,
			List<ChatMessage> msgs) {
		String tableName = getChatMsgTableName(targetId, categoryId);

		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return false;
		}

		for (ChatMessage chatMessage : msgs) {
			deleteChatMessage(tableName, chatMessage);
		}

		return true;
	}

	private void deleteChatMessage(String tableName, ChatMessage msg) {
		db.delete(tableName, IMDB.Chat.COLUMN_MSGID + " like ?",
				new String[] { msg.getMsgid() });
	}

	/**
	 * @author zhangbin
	 * @2015-5-13 @ 无
	 * @descript:清除消息未读状态
	 */
	public int clearMessagesUnreadStatus(String targetId, CategoryId categoryId) {
		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return 0;
		}
		ContentValues cv = new ContentValues();
		cv.put(IMDB.Chat.COLUMN_READSTATUS, ChatMessage.MSG_STATUS_READED);

		return db.update(tableName, cv, IMDB.Chat.COLUMN_READSTATUS + " = ?",
				new String[] { "" + ChatMessage.MSG_STATUS_UNREAD });
	}

	/**
	 * @author zhangbin
	 * @2015-6-15
	 * @@return
	 * @descript:获取置顶的会话
	 */
	public String getDraftMsg(String targetId) {
		String draftMsg = "";
		Cursor c = null;
		if (null == db) {
			CustomLog.d("getConversationListTop db is null");
			return "";
		}
		try {
			c = db.rawQuery("SELECT " + IMDB.Conversation.COLUMN_DRAFTMSG + " FROM  "
					+ CONVERSATION_TABLENAME + " where " + IMDB.Conversation.COLUMN_TARGETID
					+ " like ?", new String[] { targetId });
			if (c != null && c.moveToNext() == true) {
				draftMsg = c.getString(c.getColumnIndex(IMDB.Conversation.COLUMN_DRAFTMSG));
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		return draftMsg;
	}

	/**
	 * @author zhangbin
	 * @2015-6-15
	 * @@return
	 * @descript:获取置顶的会话
	 */
	private List<ConversationInfo> getConversationListTop(int isTop) {
		List<ConversationInfo> cinfos = new ArrayList<ConversationInfo>();
		Cursor c = null;

		if (null == db) {
			CustomLog.d("getConversationListTop db is null");
			return cinfos;
		}

		try {
			c = db.rawQuery("SELECT * FROM  " + CONVERSATION_TABLENAME
					+ " WHERE " + IMDB.Conversation.COLUMN_ISTOP + " = " + isTop
					+ " ORDER BY " + IMDB.Conversation.COLUMN_LASTTIME + " DESC", null);
			if (c == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v("isTop:" + isTop + "  rows:" + c.getCount()
						+ "条会话记录");
			}
			while (c.moveToNext()) {
				ConversationInfo ci = getDBConversationInfo(c);
				cinfos.add(ci);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}

		return cinfos;
	}

	/**
	 * 
	 * @author zhangbin
	 * @2015-5-11
	 * @@param c
	 * @@return
	 * @descript:
	 */
	public List<ConversationInfo> getConversationList() {
		List<ConversationInfo> cinfos = new ArrayList<ConversationInfo>();
		cinfos = getConversationListTop(1);
		cinfos.addAll(getConversationListTop(0));

		return cinfos;
	}

	/**
	 * @author zhangbin
	 * @2015-6-1
	 * @@param categroy
	 * @@return
	 * @descript:根据会话种类获取会话列表
	 */
	public List<ConversationInfo> getConversation(CategoryId categroy) {
		List<ConversationInfo> cinfos = new ArrayList<ConversationInfo>();
		Cursor c = null;
		try {
			c = db.rawQuery(
					"SELECT * FROM  " + CONVERSATION_TABLENAME + " WHERE "
							+ IMDB.Conversation.COLUMN_CATEGORYID + " = "
							+ categroy.ordinal() + " ORDER BY "
							+ IMDB.Conversation.COLUMN_LASTTIME + " DESC", null);
			if (c == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v("categroy:" + categroy + " " + c.getCount()
						+ "条讨论组记录");
			}

			while (c.moveToNext()) {
				ConversationInfo ci = getDBConversationInfo(c);
				cinfos.add(ci);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		return cinfos;
	}

	public ConversationInfo getConversation(String targetId) {
		ConversationInfo ci = null;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM  " + CONVERSATION_TABLENAME
					+ " where " + IMDB.Conversation.COLUMN_TARGETID + " like ?",
					new String[] { targetId });

			if (cursor != null && cursor.moveToNext() == true) {
				ci = getDBConversationInfo(cursor);
				CustomLog.v("getconversation:" + ci.getTargetId());
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return ci;
	}

	public int getConversationMsgUnRead(String targetId) {
		int msgUnRead = 0;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT " + IMDB.Conversation.COLUMN_MSGUNREAD + " FROM  "
					+ CONVERSATION_TABLENAME + " where " + IMDB.Conversation.COLUMN_TARGETID
					+ " like ?", new String[] { targetId });

			if (cursor != null && cursor.moveToNext() == true) {
				msgUnRead = cursor.getInt(cursor
						.getColumnIndex(IMDB.Conversation.COLUMN_MSGUNREAD));
			}

		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		// getConversationMsgUnReadAll();
		return msgUnRead;
	}

	public int getConversationMsgUnReadAll() {
		int msgUnReadAll = 0;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT " + IMDB.Conversation.COLUMN_MSGUNREAD + " FROM  "
					+ CONVERSATION_TABLENAME, null);
			while (cursor != null && cursor.moveToNext() == true) {
				msgUnReadAll += cursor.getInt(cursor
						.getColumnIndex(IMDB.Conversation.COLUMN_MSGUNREAD));
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return msgUnReadAll;
	}

	public long getConversationLastTime(String targetId) {
		long lastTime = 0;
		ConversationInfo ci = getConversation(targetId);
		if (ci != null) {
			lastTime = ci.getLastTime();
		}
		return lastTime;
	}

	private DiscussionInfo getDBDiscussionInfo(Cursor cursor) {

		DiscussionInfo di = new DiscussionInfo();

		di.setDiscussionId(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_DISCUSSIONID)));
		di.setDiscussionName(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_DISCUSSIONNAME)));
		di.setCategoryId(CategoryId.DISCUSSION.ordinal());
		di.setMemberCount(cursor.getInt(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_MEMBERCOUNT)));
		di.setOwnerId(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_OWNERID)));
		di.setDiscussionMembers(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_DISCUSSIONMEMBERS)));
		di.setDisscussionSettings(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_DISCUSSIONSETTING)));
		di.setCreateTime(Long.parseLong(cursor.getString(cursor
				.getColumnIndex(IMDB.DiscussionInfo.COLUMN_CREATETIME))));
		return di;

	}

	public DiscussionInfo getDiscussionInfo(String discussionID) {
		DiscussionInfo di = null;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM  " + DISCUSSION_INFO_TABLENAME
					+ " where " + IMDB.DiscussionInfo.COLUMN_DISCUSSIONID + " like ?",
					new String[] { discussionID });

			if (cursor != null && cursor.moveToNext() == true) {
				di = getDBDiscussionInfo(cursor);
				CustomLog.v("getconversation:" + di.getDiscussionId());
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return di;
	}

	public List<DiscussionInfo> getAllDiscussionInfos() {
		List<DiscussionInfo> dicinfos = new ArrayList<DiscussionInfo>();
		Cursor c = null;
		try {
			c = db.rawQuery("SELECT * FROM  " + DISCUSSION_INFO_TABLENAME, null);
			if (c == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v(c.getCount() + "条讨论组记录");
			}

			while (c.moveToNext()) {
				DiscussionInfo di = getDBDiscussionInfo(c);
				dicinfos.add(di);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		return dicinfos;
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @@param c 单曲会话的CURSOR
	 * @@return 未读消息数
	 * @descript:获取指定会话的未读消息数
	 */
	public int getUnreadCount(String targetId, CategoryId categoryId) {
		String tableName = getChatMsgTableName(targetId, categoryId);
		Cursor chatC = null;
		int unReadCount = 0;
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return 0;
		}

		try {
			chatC = db.rawQuery("SELECT * FROM " + tableName
					+ " WHERE readStatus = ?", new String[] { ""
					+ ChatMessage.MSG_STATUS_UNREAD });
			unReadCount = chatC.getCount();
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (chatC != null) {
				chatC.close();
				chatC = null;
			}
		}
		return unReadCount;
	}

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @@param c
	 * @@return
	 * @descript:获取所有未读消息数量
	 */
	/*
	 * public int getTotalUnreadCount(Cursor c) { int unReadCount = 0; while
	 * (c.moveToNext()) { unReadCount += getUnreadCount(c); }
	 * 
	 * return unReadCount;
	 * 
	 * }
	 */

	/**
	 * @author zhangbin
	 * @2015-4-27
	 * @@param c
	 * @@return
	 * @descript:获取某一会话的草稿消息
	 */
	public String getTextMessageDraft(Cursor c) {
		String draftMsg = null;

		draftMsg = c.getString(c.getColumnIndex(IMDB.Conversation.COLUMN_DRAFTMSG));
		return draftMsg;
	}

	/**
	 * 
	 * @author zhangbin
	 * @2015-5-15
	 * @@param chatC
	 * @@return
	 * @descript:从数据库中解析出ChatMessage对象
	 */
	private ChatMessage getChatMessageFromDB(Cursor chatC) {
		ChatMessage msg;
		int category = chatC
				.getInt(chatC.getColumnIndex(IMDB.Chat.COLUMN_CATEGORYID));
		CategoryId categoryId = CategoryId.valueof(category);

		String msgid = chatC.getString(chatC.getColumnIndex(IMDB.Chat.COLUMN_MSGID));
		// 查询发送队列里是否有该消息
		msg = IMUserData.mapGetMsgById(msgid);
		if (msg == null) {
			switch (categoryId) {
			case PERSONAL:
				msg = new SingleChat();
				break;
			case GROUP:
				msg = new GroupChat();
				break;
			case DISCUSSION:
				msg = new DiscussionChat();
				break;
			case BROADCAST:
				msg = new BroadCastChat();
				break;
			case NONE:
				msg = new NoneChat();
				break;
			default:
				CustomLog.v("getChatMessageFromDB 未知的消息类型");
				return null;
			}
		}

		msg.setId(chatC.getInt(chatC.getColumnIndex(IMDB.Chat.COLUMN_ID)));
		msg.setMsgid(msgid);
		msg.setTargetId(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_TARGETID)));
		msg.setSenderId(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_SENDERID)));
		msg.setNickName(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_NICKNAME)));
		msg.setCategoryId(category);
		int derect = chatC
				.getInt(chatC.getColumnIndex(IMDB.Chat.COLUMN_ISFROMMYSELF));
		boolean isFromMyself = derect == 0 ? false : true;
		msg.setIsFromMyself(isFromMyself);
		msg.setSendTime(Long.parseLong(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_SENDTIME))));
		msg.setReceiveTime(Long.parseLong(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_RECEIVETIME))));
		msg.setMsgType(chatC.getInt(chatC.getColumnIndex(IMDB.Chat.COLUMN_MSGTYPE)));
		msg.setContent(chatC.getString(chatC.getColumnIndex(IMDB.Chat.COLUMN_CONTENT)));
		msg.setReadStatus(chatC.getInt(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_READSTATUS)));
		int sendStatus = chatC.getInt(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_SENDSTATUS));
		if (sendStatus == ChatMessage.MSG_STATUS_INPROCESS
				&& IMUserData.mapFindMsg(msg) == false) {
			sendStatus = ChatMessage.MSG_STATUS_NETERROR;
		}
		msg.setSendStatus(sendStatus);
		msg.setPath(chatC.getString(chatC.getColumnIndex(IMDB.Chat.COLUMN_PATH)));
		msg.setParentID(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_PARENTID)));

		/*
		 * String name[] = msg.getSenderId().split("\\+",2); if(name.length ==
		 * 2){ msg.setSenderId(name[0]); msg.setNickName(name[1]); }
		 */

		msg.setExtMessage(chatC.getString(chatC
				.getColumnIndex(IMDB.Chat.COLUMN_EXTMSG)));

		return msg;
	}

	public List<ChatMessage> getMessagesFromStatus(String targetId,
			CategoryId categoryId, int status) {
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		Cursor c = null;
		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			CustomLog.e(tableName + " 表不存在");
			return msgs;
		}
		try {
			c = db.rawQuery("SELECT * FROM  " + tableName + " WHERE "
					+ IMDB.Chat.COLUMN_SENDSTATUS + " = " + status, null);
			if (c == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v("status:" + status + " " + c.getCount()
						+ "条消息");
			}

			while (c.moveToNext()) {
				ChatMessage ci = getChatMessageFromDB(c);
				msgs.add(ci);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			c.close();
			c = null;
		}
		return msgs;
	}

	public ChatMessage getMessageFromMsgid(String targetId,
			CategoryId categoryId, String msgID) {

		ChatMessage msg = null;
		Cursor c = null;

		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			CustomLog.e(tableName + " 表不存在");
			return msg;
		}
		try {
			c = db.rawQuery("SELECT * FROM  " + tableName + " WHERE "
					+ IMDB.Chat.COLUMN_MSGID + " = " + msgID, null);
			if (c == null) {
				CustomLog.v("cursor is null");
			} else {
				CustomLog.v("status:" + msgID + " " + c.getCount()
						+ "条消息");
			}

			if (c.moveToNext()) {
				msg = getChatMessageFromDB(c);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}

		return msg;

	}

	/**
	 * @author zhangbin
	 * @2015-4-28
	 * @@param c
	 * @@return
	 * @descript:获取某一会话的最新一条消息
	 */
	public List<ChatMessage> getLatestMessages(String targetId,
			CategoryId categoryId, int startPos, int count) {
		if (startPos < 0 || count < 0) {
			return null;
		}
		List<ChatMessage> lastMsgs = new ArrayList<ChatMessage>();
		Cursor chatC = null;

		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return lastMsgs;
		}

		try {
			chatC = db.rawQuery("SELECT * FROM " + tableName
					+ " ORDER BY _id DESC", null);

			if (chatC.moveToNext()) {
				int chatNums = 0;
				if (chatC.getCount() - startPos > 0) {
					chatNums = Math.min(chatC.getCount() - startPos, count);
					CustomLog.v("chatNums:" + chatNums);
					chatC.moveToPosition(startPos);
				} else {
					return lastMsgs;
				}

				for (int i = 0; i < chatNums; i++, chatC.moveToNext()) {
					ChatMessage msg = getChatMessageFromDB(chatC);
					if (msg != null)
						lastMsgs.add(i, msg);
				}
				Collections.sort(lastMsgs, new SortById());
			}
		} catch (Exception e) {
			// TODO: handle exception
			CustomLog.e(e.getMessage());
		} finally {
			if (chatC != null) {
				chatC.close();
				chatC = null;
			}
		}

		return lastMsgs;
	}

	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			ChatMessage s1 = (ChatMessage) o1;
			ChatMessage s2 = (ChatMessage) o2;
			long times1 = Math.max(s1.getSendTime(), s1.getReceiveTime());
			long times2 = Math.max(s2.getSendTime(), s2.getReceiveTime());
			if (times1 > times2)
				return 1;
			else
				return -1;
		}
	}

	class SortById implements Comparator {
		public int compare(Object o1, Object o2) {
			ChatMessage s1 = (ChatMessage) o1;
			ChatMessage s2 = (ChatMessage) o2;

			if (s1.getId() > s2.getId())
				return 1;
			else
				return -1;
		}
	}

	/**
	 * @author zhangbin
	 * @2015-5-12
	 * @@param startPos 开始的消息地址 0 表示最老的一条消息
	 * @@param count 想获取的消息的条数
	 * @@return 所有获取到的消息的列表
	 * @descript:获取历史消息记录 以startPos开始， count结束的所有消息。
	 */
	public List<ChatMessage> getHistroyMessages(String targetId,
			CategoryId categoryId, int startPos, int count) {
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		if (startPos < 0 || count < 0) {
			return msgs;
		}

		Cursor chatC = null;
		String tableName = getChatMsgTableName(targetId, categoryId);
		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return msgs;
		}

		try {
			chatC = db.rawQuery("SELECT * FROM " + tableName, null);

			int chatNums = 0;
			if (chatC.getCount() - startPos > 0) {
				chatNums = Math.min(chatC.getCount() - startPos, count);
				CustomLog.v("chatNums:" + chatNums);
				chatC.moveToPosition(startPos - 1);
			} else {
				return msgs;
			}

			if (chatC.moveToNext()) {

				for (int i = 0; i < chatNums; i++, chatC.moveToNext()) {
					ChatMessage msg = getChatMessageFromDB(chatC);
					if (msg != null)
						msgs.add(msg);
				}
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (chatC != null) {
				chatC.close();
				chatC = null;
			}
		}

		return msgs;
	}

	public List<ChatMessage> getAllMessages(String targetId,
			CategoryId categoryId) {
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();

		String tableName = getChatMsgTableName(targetId, categoryId);
		Cursor chatC = null;

		if (tabbleIsExist(tableName) == false) {
			System.err.println(tableName + " 表不存在");
			return msgs;
		}
		try {
			chatC = db.rawQuery("SELECT * FROM " + tableName, null);
			if (chatC == null) {
				CustomLog.v("chatC is null ");
				return msgs;
			} else {
				CustomLog.v("rows:" + chatC.getCount() + "条聊天记录");
			}
			while (chatC.moveToNext()) {
				ChatMessage msg = getChatMessageFromDB(chatC);
				if (msg != null)
					msgs.add(msg);
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (chatC != null) {
				chatC.close();
				chatC = null;
			}
		}
		return msgs;
	}

	private boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;

		try {
			String sql = "select count(*) as c from sqlite_master "
					+ "where type ='table' and name ='" + tableName.trim()
					+ "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			CustomLog.e(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return result;
	}
}