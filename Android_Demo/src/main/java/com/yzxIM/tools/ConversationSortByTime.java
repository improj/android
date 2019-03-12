package com.yzxIM.tools;

import java.util.Comparator;

import com.yzxIM.data.db.ConversationInfo;

public class ConversationSortByTime implements Comparator{
	public int compare(Object o1, Object o2) {
		ConversationInfo s1 = (ConversationInfo) o1;
		ConversationInfo s2 = (ConversationInfo) o2;
		if((s1.getIsTop()==true) && (s2.getIsTop()==false)){
			return -1;
		}else if(s1.getIsTop() == s2.getIsTop()){
			/*long times1 = Math.max(s1.getLastTime(), s1.getLastTime());
			long times2 = Math.max(s2.getLastTime(), s2.getLastTime());*/

			if (s1.getLastTime() > s2.getLastTime())
				return -1;
			else
				return 1;
		}else{
			return 1;
		}
		
	}
}
