package com.yzxIM;

import org.json.JSONException;
import org.json.JSONObject;

import com.yzxIM.data.IMUserData;
import com.yzxIM.data.db.DBManager;
import com.yzxIM.protocol.packet.IGGNewInitRequest;
import com.yzxIM.tools.StatisticalReport;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.FileTools;

public class IMLoginCallBack implements ILoginListener {

	@Override
	public void onLogin(UcsReason reason) {
		CustomLog.v("IM Login callback:" + reason.getReason() + ":" + reason.getMsg());
		if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
			String userName ="";
			String appid = "";
			try {
				JSONObject jsonObject = new JSONObject(reason.getMsg());
				if(jsonObject.has("userid")&&jsonObject.has("appid")){
					userName = jsonObject.getString("userid");
					appid = jsonObject.getString("appid");
					if(StatisticalReport.startStatisticalReport(appid, userName) == false){
						StatisticalReport.startStatisticalReportOneDay(appid, userName);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			IMUserData.saveUserName(userName);
			IMUserData.saveUserAPPID(appid);
//			FileTools.createFolder(); // 创建文件夹在TCP service里面就初始化了，此操作可以不做
			String dbName = "YZXIM_" + userName + ".db";
			IMUserData.saveDbName(dbName);
			DBManager.getInstance().createDatabase(dbName);
			IMUserData.loginFlag = true;
			newSynMessage();
		}
	}

	// 同步新消息
	private void newSynMessage() {
		new IGGNewInitRequest().onSendMessage();	
	}

}
