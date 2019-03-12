package com.yzxIM.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.listener.IMListenerManager;
import com.yzxtcp.tools.CustomLog;

public class TimerSendMsg extends Timer {
	private ChatMessage chatMessage;
	private DBManager dbManager = DBManager.getInstance();
	private IMListenerManager imlManager = IMListenerManager.getInstance();
	private static Map<String, Timer> timerMaps = new HashMap<String, Timer>();
	private String pcClientMsgId;

	private TimerSendMsg(ChatMessage chatMessage,String pcClientMsgId) {
		// TODO Auto-generated constructor stub
		this.chatMessage = chatMessage;
		this.pcClientMsgId = pcClientMsgId;
	}

	private void setTimer(int time) {
		this.schedule(new TimerTask() {

			@Override
			public void run() {
				// 设置消息状态并更新数据库
				CustomLog.v("发送消息超时");
				chatMessage.setSendStatus(ChatMessage.MSG_STATUS_FAIL);
				dbManager.updataMsgStatusAndMsgID(chatMessage, 
						chatMessage.getMsgid());
				//通知APP消息状态改变
				imlManager.notifiSendMsgListener(chatMessage);
				//移除HASHMAP内的定时器
				timerMaps.remove(chatMessage.getMsgid());

				ChatMessage msg = IMUserData.mapGetMsg(pcClientMsgId);
				if (msg != null) {
					System.err.println("超时 删除发送消息!");
					IMUserData.mapDelMsg(pcClientMsgId);
					return;
				}

			}
		}, time);
	}

	public static void startTimer(ChatMessage chatMessage,
			String pcClientMsgId, int time) {
		String key = chatMessage.getMsgid();
		TimerSendMsg timer = new TimerSendMsg(chatMessage,pcClientMsgId);
		timer.setTimer(time);
		timerMaps.put(key, timer);
//		debugTimerHashMap(1);
	}

	public static void cancelTimer(ChatMessage chatMessage) {
		String key = chatMessage.getMsgid();
		timerMaps.get(key).cancel();
		timerMaps.remove(key);
		CustomLog.v("取消发送消息定时器");
//		debugTimerHashMap(0);
	}
	
	public static boolean timerIsContainKey(String key){
		return timerMaps.containsKey(key);
	}
	
	private static void debugTimerHashMap(int add){
		if(add == 1){
			CustomLog.v("加入HASHMAP");
		}else{
			CustomLog.v("删除hashmap");
		}
		
		Iterator iter = timerMaps.keySet().iterator();  
		while (iter.hasNext()) {  
		    Timer val = timerMaps.get(iter.next()); 
		    CustomLog.v("timer:"+val);
		}  
	}
}
