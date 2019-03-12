package com.yzxtcp.tcp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.tcp.receiver.AlarmReceiver;
import com.yzxtcp.tools.tcp.receiver.MsgBackReceiver;
/**
 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
 */
public class AlarmTools {
	
	public static final int PING_TIME = 1 * 60 * 1000;		// 1锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷锟斤拷
	private static final long PING_BACK = 20 * 1000;		// 20锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	
	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷
	 * 
	 */
	public static void startAlarm(int ping_time) {
		if(YzxTCPCore.getContext() != null){
			Context mContext = YzxTCPCore.getContext().getApplicationContext();
			if(mContext != null){
				AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(mContext, AlarmReceiver.class);
				PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23 Android 6.0使锟斤拷锟斤拷锟斤拷锟皆凤拷锟斤拷锟斤拷锟斤拷
					am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ping_time, pendIntent);
				} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
					// 锟斤拷API 19锟斤拷始锟斤拷alarm锟侥伙拷锟狡讹拷锟角凤拷准确锟斤拷锟捷ｏ拷锟斤拷锟斤拷系统锟斤拷锟斤拷转锟斤拷锟斤拷锟接ｏ拷锟斤拷锟斤拷小锟斤拷锟斤拷锟窖和碉拷锟绞癸拷谩锟?
					am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ping_time, pendIntent);
				} else {
					am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), PING_TIME, pendIntent);
				}
			}
		}
	}

	/**
	 * 停止锟斤拷锟斤拷
	 * 
	 */
	public static void stopAlarm() {
		//CustomLog.v( "1-停止锟斤拷锟斤拷  ... ");
		if(YzxTCPCore.getContext() != null){
			//CustomLog.v( "2-停止锟斤拷锟斤拷  ... ");
			Context mContext = YzxTCPCore.getContext().getApplicationContext();
			if(mContext != null){
				Intent intent = new Intent(mContext, AlarmReceiver.class);
				PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendIntent);
			}
		}
	}
	
	 public static void startBackTcpPing(){
		//CustomLog.v( "1-锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 ...");
		if(YzxTCPCore.getContext() != null){
			//CustomLog.v( "2-锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 ...");
			Context mContext = YzxTCPCore.getContext().getApplicationContext();
			if(mContext != null){
				AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(mContext, MsgBackReceiver.class);
				PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//					am.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PING_BACK, 0, pendIntent);
//					am.setAlarmClock(new AlarmClockInfo(System.currentTimeMillis() + PING_BACK, pendIntent), pendIntent);
					am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PING_BACK, pendIntent);
				} else {
					am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PING_BACK, pendIntent);
				}
			}
		}
		
	}
	
	public static void stopBackTcpPing(){
		//CustomLog.v( "1-停止锟斤拷锟斤拷锟斤拷锟斤拷 ... ");
		if(YzxTCPCore.getContext() != null){
			//CustomLog.v( "2-停止锟斤拷锟斤拷锟斤拷锟斤拷 ... ");
			Context mContext = YzxTCPCore.getContext().getApplicationContext();
			if(mContext != null){
				Intent intent = new Intent(mContext, MsgBackReceiver.class);
				PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendIntent);
			}
			
		}
	}
	
	public static void stopAll(){
		stopAlarm();
		stopBackTcpPing();
	}
}
