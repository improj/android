package com.yzxIM.data.db;

import com.yzxIM.data.db.IMDB.BroadCastChat;
import com.yzxIM.data.db.IMDB.DiscussionChat;
import com.yzxIM.data.db.IMDB.GroupChat;
import com.yzxIM.data.db.IMDB.NoneChat;
import com.yzxIM.data.db.IMDB.SignalChat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IMDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "YZXIM.db";
	private static final int DATABASE_VERSION = 1;
	
	public IMDBHelper(Context context, String DBName) {
		super(context, DBName, null, DATABASE_VERSION);
	}

	public IMDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		createIMTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	private void createIMTables(SQLiteDatabase db) {
		db.execSQL(IMDB.UserInfo.SQL_CREATE_TABLE);
		db.execSQL(IMDB.GroupInfo.SQL_CREATE_TABLE);
		db.execSQL(IMDB.DiscussionInfo.SQL_CREATE_TABLE);
		db.execSQL(IMDB.Version.SQL_CREATE_TABLE);
		db.execSQL(IMDB.Conversation.SQL_CREATE_TABLE);
	}
	
	// 创建数据表开始

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建群组聊天信息表
	 */
	public void createTableGroupChat(SQLiteDatabase db, String tableName) {
		String tableGroupChat = "CREATE TABLE IF NOT EXISTS " + tableName + GroupChat.SQL_CREATE_COLUMNS;
		db.execSQL(tableGroupChat);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建讨论组聊天信息表
	 */
	public void createDiscussionChat(SQLiteDatabase db, String tableName) {
		String tableDiscussionChat = "CREATE TABLE IF NOT EXISTS " + tableName + DiscussionChat.SQL_CREATE_COLUMNS;
		db.execSQL(tableDiscussionChat);
	}

	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建单聊聊天信息表
	 */
	public void createSignalChat(SQLiteDatabase db, String tableName) {
		String tableSingleChat = "CREATE TABLE IF NOT EXISTS " + tableName + SignalChat.SQL_CREATE_COLUMNS;
		db.execSQL(tableSingleChat);
	}
	
	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建系统广播会话聊天信息表
	 */
	public void createBroadCastChat(SQLiteDatabase db, String tableName) {
		String tableSingleChat = "CREATE TABLE IF NOT EXISTS " + tableName + BroadCastChat.SQL_CREATE_COLUMNS;
		db.execSQL(tableSingleChat);
	}
	/**
	 * @author zhangbin
	 * @2015-4-24
	 * @param db
	 * @param tableName 表名
	 * @descript:创建未知会话聊天信息表
	 */
	public void createNoneChat(SQLiteDatabase db, String tableName) {
		String tableSingleChat = "CREATE TABLE IF NOT EXISTS " + tableName + NoneChat.SQL_CREATE_COLUMNS;

		db.execSQL(tableSingleChat);
	}
}
