package com.yzxtcp.tools;  

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;

/**
 * @Title CommonUtil   
 * @Description 公共工具类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-6-12 上午10:25:28
 */
public class CommonUtil {

	/**
	 * @Description 判断服务是否运行
	 * @param context	应用上下文
	 * @param clazz	要判断服务的class
	 * @return	true：服务已经开启，false：服务还没有开启
	 * @date 2016-6-12 上午10:32:29 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	public static boolean isServiceRunning(Context context, Class<? extends Service> clazz) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
		for(int i=0; i<runningServices.size(); i++) {
			String className = runningServices.get(i).service.getClassName();
			if(className.endsWith(clazz.getName())) {
				return true;
			}
		}
		return false;
	}
}
  
