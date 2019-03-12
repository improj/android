package com.yzxtcp.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {
	
	public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
	
	public static String listToString(List<String> memberList){
		StringBuilder sb= new StringBuilder();
		for(int i=0; i<memberList.size(); i++){
			 sb.append(memberList.get(i));
			 if(i < memberList.size()-1){
				 sb.append(",");
			 }
		}
		String str = sb.toString();
				
		return str;
	}
	
	public static ArrayList<String> stringToArrayList(String nums){
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(nums));
		return list;
	}
}
