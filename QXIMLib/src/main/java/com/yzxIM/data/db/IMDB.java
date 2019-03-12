package com.yzxIM.data.db;

/**
 * @Title IMDB
 * @Description 数据库表，字段描述
 * @Company yunzhixun
 * @author xhb
 * @date 2016-6-6 下午2:29:34
 */
public interface IMDB {

	/**
	 * @Title UserInfo
	 * @Description 用户信息表
	 * @Company yunzhixun
	 * @author xhb
	 * @date 2016-6-6 下午2:32:26
	 */
	public interface UserInfo {
		String TABLE_NAME = DBManager.USER_INFO_TABLENAME;

		String COLUMN_ID = "_id";
		String COLUMN_USERID = "userId";
		String COLUMN_USERNAME = "userName";
		String COLUMN_CATEGORYID = "categoryId";
		String COLUMN_PORTRAILURL = "portrailUrl";
		String COLUMN_UPDATETIME = "updateTime";
		String COLUMN_USERSETTINGS = "userSettings";
		
		String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_USERID + " VARCHAR(64), "
				+ COLUMN_USERNAME + " VARCHAR(64), "
				+ COLUMN_CATEGORYID + " INTEGER, "
				+ COLUMN_PORTRAILURL + " TEXT, "
				+ COLUMN_UPDATETIME + " INTEGER, "
				+ COLUMN_USERSETTINGS + " TEXT" + ")";
	}
	
	/**
	 * @Title GroupInfo   
	 * @Description  群组信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午2:47:15
	 */
	public interface GroupInfo {
		String TABLE_NAME = DBManager.GROUP_INFO_TABLENAME;
		
		String COLUMN_ID = "_id";
		String COLUMN_GROUPID = "groupId";
		String COLUMN_GROUPNAME = "groupName";
		String COLUMN_CATEGORYID = "categoryId";
		String COLUMN_UPDATETIME = "updateTime";
		
		String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_GROUPID + " VARCHAR(64), " 
				+ COLUMN_GROUPNAME + " VARCHAR(64), "
				+ COLUMN_CATEGORYID + " INTEGER, " 
				+ COLUMN_UPDATETIME + " INTEGER" + ")";

	}
	
	/**
	 * @Title DiscussionInfo   
	 * @Description  讨论组信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午2:55:14
	 */
	public interface DiscussionInfo {
		String TABLE_NAME = DBManager.DISCUSSION_INFO_TABLENAME;
		
		String COLUMN_ID = "_id";
		String COLUMN_DISCUSSIONID = "discussionId";
		String COLUMN_DISCUSSIONNAME = "discussionName";
		String COLUMN_CATEGORYID = "categoryId";
		String COLUMN_MEMBERCOUNT = "memberCount";
		String COLUMN_OWNERID = "ownerId";
		String COLUMN_DISCUSSIONMEMBERS = "discussionMembers";
		String COLUMN_DISCUSSIONSETTING = "discussionSetting";
		String COLUMN_CREATETIME = "createTime";
		
		String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_DISCUSSIONID + " VARCHAR(64), " 
				+ COLUMN_DISCUSSIONNAME + " VARCHAR(64), "
				+ COLUMN_CATEGORYID + " INTEGER, "
				+ COLUMN_MEMBERCOUNT + " INTEGER, " 
				+ COLUMN_OWNERID + " VARCHAR(64), "
				+ COLUMN_DISCUSSIONMEMBERS + " TEXT, "
				+ COLUMN_DISCUSSIONSETTING + " TEXT, " 
				+ COLUMN_CREATETIME + " INTEGER" + ")";
	}
	
	/**
	 * @Title Version   
	 * @Description  版本信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午3:25:29
	 */
	public interface Version {
		String TABLE_NAME = DBManager.VERSION_TABLENAME;
		
		String COLUMN_ID = "_id";
		String COLUMN_DATABASEVERSION = "databaseVersion";
		String COLUMN_SDKVERSION = "sdkVersion";
		String COLUMN_CREATETIME = "createTime";
		
		String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_DATABASEVERSION + " VARCHAR(64), "
				+ COLUMN_SDKVERSION + " VARCHAR(64), " 
				+ COLUMN_CREATETIME + " INTEGER" + ")";
	}
	
	/**
	 * @Title Conversation   
	 * @Description  会话信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午3:29:40
	 */
	public interface Conversation {
		String TABLE_NAME = DBManager.CONVERSATION_TABLENAME;
		
		String COLUMN_ID = "_id";
		String COLUMN_TARGETID = "targetId";
		String COLUMN_CONVERSATIONTITLE = "conversationTitle";
		String COLUMN_CATEGORYID = "categoryId";
		String COLUMN_DRAFTMSG = "draftMsg";
		String COLUMN_ISTOP = "isTop";
		String COLUMN_LASTTIME = "lastTime";
		String COLUMN_TOPTIME = "topTime";
		String COLUMN_MSGUNREAD = "msgUnRead";
		
		String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_TARGETID + " VARCHAR(64), "
				+ COLUMN_CONVERSATIONTITLE + " VARCHAR(64), "
				+ COLUMN_CATEGORYID + " INTEGER, " 
				+ COLUMN_DRAFTMSG + " TEXT, " 
				+ COLUMN_ISTOP + " INTEGER, " 
				+ COLUMN_LASTTIME + " TEXT, " 
				+ COLUMN_TOPTIME + " TEXT, " 
				+ COLUMN_MSGUNREAD + " INTEGER" + ")";
	}
	
	/**
	 * @Title GroupChat   
	 * @Description  群组聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午3:47:49
	 */
	public interface GroupChat extends Chat {
		
	}
	
	/**
	 * @Title DiscussionChat   
	 * @Description  讨论组聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午4:14:13
	 */
	public interface DiscussionChat extends Chat {
		
	}
	
	/**
	 * @Title SignalChat   
	 * @Description  单聊聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午4:14:13
	 */
	public interface SignalChat extends Chat {
		
	}
	
	/**
	 * @Title BroadCastChat   
	 * @Description  系统广播会话聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午4:18:07
	 */
	public interface BroadCastChat extends Chat {
		
	}
	
	/**
	 * @Title NoneChat   
	 * @Description  未知会话聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午4:22:45
	 */
	public interface NoneChat extends Chat {

	}
	
	/**
	 * @Title Chat   
	 * @Description  聊天信息表
	 * @Company yunzhixun  
	 * @author xhb
	 * @date 2016-6-6 下午4:24:32
	 */
	public interface Chat {
		
		String COLUMN_ID = "_id";
		String COLUMN_MSGID = "msgId";
		String COLUMN_TARGETID = "targetId";
		String COLUMN_SENDERID = "senderId";
		String COLUMN_NICKNAME = "nickName";
		String COLUMN_CATEGORYID = "categoryId";
		String COLUMN_ISFROMMYSELF = "isFromMySelf";
		String COLUMN_SENDTIME = "sendTime";
		String COLUMN_RECEIVETIME = "receiveTime";
		String COLUMN_MSGTYPE = "msgType";
		String COLUMN_CONTENT = "content";
		String COLUMN_READSTATUS = "readStatus";
		String COLUMN_SENDSTATUS = "sendStatus";
		String COLUMN_PATH = "path";
		String COLUMN_PARENTID = "parentID";
		String COLUMN_EXTMSG = "extmsg";
		String COLUMN_EXTMSG2 = "extmsg2";
		
		String SQL_CREATE_COLUMNS = " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_MSGID + " VARCHAR(64), " 
				+ COLUMN_TARGETID + " VARCHAR(64), " 
				+ COLUMN_SENDERID + " VARCHAR(64), "
				+ COLUMN_NICKNAME + " VARCHAR(64), " 
				+ COLUMN_CATEGORYID + " INTEGER, " 
				+ COLUMN_ISFROMMYSELF + " BOOLEAN, " 
				+ COLUMN_SENDTIME + " TEXT, " 
				+ COLUMN_RECEIVETIME + " TEXT, "
				+ COLUMN_MSGTYPE + " INTEGER, " 
				+ COLUMN_CONTENT + " TEXT, " 
				+ COLUMN_READSTATUS + " INTEGER, " 
				+ COLUMN_SENDSTATUS + " INTEGER, " 
				+ COLUMN_PATH + " TEXT, "
				+ COLUMN_PARENTID + " VARCHAR(64), " 
				+ COLUMN_EXTMSG + " TEXT, " 
				+ COLUMN_EXTMSG2 + " TEXT" + ")";
	}
}
