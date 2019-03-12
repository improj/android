package com.yzxIM;

import android.text.TextUtils;

import com.yzxIM.data.IMUserData;
import com.yzxIM.tools.StatisticalReport;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.IReLoginListener;
import com.yzxtcp.tools.CustomLog;

public class IMReLoginCallBack implements IReLoginListener{

	@Override
	public void onReLogin(UcsReason reason) {
		if(reason.getReason() == UcsErrorCode.NET_ERROR_RECONNECTOK){
			String appid = IMUserData.getUserAPPID();
			String userName = IMUserData.getUserName();
			if(TextUtils.isEmpty(userName) == false &&
					TextUtils.isEmpty(appid) == false){
				StatisticalReport.startStatisticalReportOneDay(appid, userName);
			}else{
				CustomLog.d("userName 或 appid is null");
			}
			newSynMessage();
		}
	}

	private void newSynMessage(){
		IMManager imManager = IMManager.getInstance(null);
		if(imManager != null){
			imManager.newSynMessage();
		}else{
			CustomLog.e("newSynMessage imManager is null");
		}
	}
}
