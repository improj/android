package com.yzx.tools;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import com.gl.softphone.CallInfo;
import com.gl.softphone.CallReport;
import com.gl.softphone.EmodelInfo;
import com.gl.softphone.EmodelValue;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSService;
import com.yzx.preference.UserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

import android.content.Context;
import android.os.Build;

public class EmodelTools {
	
	public static void getEmodelValue(Context mContext){
		
		CallReport callReport = new CallReport();
        int res = UGoManager.getInstance().pub_UGoGetCallReport(callReport);
        if ( res != 0 )
        {
        	CustomLog.v("getEmodelValue fail,  pub_UGoGetCallReport return " + res);
        	return;
        }
        CustomLog.v("CallReport modelValue:" + CallReportToString(callReport));

		JSONObject json = new JSONObject();
		try {
			json.put("ver", "yzx_"+UCSService.getSDKVersion()+"_"+UGoManager.getInstance().pub_UGoGetVersion());
			int worktype = NetWorkTools.getCurrentNetWorkType(mContext);
			switch(worktype){
			case NetWorkTools.NETWORK_3G:
				json.put("net","3g");
				break;
			case NetWorkTools.NETWORK_WIFI:
				json.put("net","wifi");
				break;
			default:
				json.put("net","ethernet");
				break;
			}
			json.put("pv","android_"+android.os.Build.VERSION.SDK_INT+"_"+Build.MODEL.replaceAll(" ", ""));
			json.put("caller", UserData.getClientNumber(mContext));
			json.put("callee", "");
			json.put("mcodec", callReport.sessionInfo.strCodec);
			json.put("cmode", callReport.sessionInfo.callMode);
			json.put("mmode", callReport.sessionInfo.transMode);
			json.put("ctime", callReport.sessionInfo.connTime);
			json.put("cstate", callReport.sessionInfo.callState);
			json.put("role", callReport.sessionInfo.callRole);
			json.put("snr", 0);
			json.put("frate", 0);
			json.put("mgport", callReport.sessionInfo.mgwPort);
			json.put("cmute",  callReport.sessionInfo.isMuted);
			json.put("psend", callReport.sessionInfo.pktSnd);
			json.put("precieve", callReport.sessionInfo.pktRecv);
			json.put("tstate", UCSManager.isConnect());
			json.put("callid", callReport.sessionInfo.strCallId);
			json.put("isVideo", UserData.getVideoEnabled(mContext));
			json.put("mos_min", floatFormat(callReport.emodelMos.min));
			json.put("mos_max", floatFormat(callReport.emodelMos.max));
			json.put("mos_avg", floatFormat(callReport.emodelMos.average));
			json.put("loss_min", floatFormat(callReport.emodelLost.min));
			json.put("loss_max", floatFormat(callReport.emodelLost.max));
			json.put("loss_avg", floatFormat(callReport.emodelLost.average));
			json.put("delay_min", floatFormat(callReport.emodelDelay.min));
			json.put("delay_max", floatFormat(callReport.emodelDelay.max));
			json.put("delay_avg", floatFormat(callReport.emodelDelay.average));
			json.put("jitter_min", floatFormat(callReport.emodelJitter.min));
			json.put("jitter_max", floatFormat(callReport.emodelJitter.max));
			json.put("jitter_avg", floatFormat(callReport.emodelJitter.average));
			
			CustomLog.v("CURRENT_EMODEL:"+json.toString());
			
			FileTools.uploadJson(mContext, json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			CustomLog.v("jsonExecption: " + e.getMessage());
		}
	}
	private static String CallReportToString(CallReport callReport) {
		if (callReport == null) {
			return null;
		}
		return "MOS = " + floatFormat(callReport.emodelMos.min)
    			+ ", " + floatFormat(callReport.emodelMos.max)
    			+ ", " + floatFormat(callReport.emodelMos.average)
    			+ " RTT = " + floatFormat(callReport.emodelRtt.min)
				+ ", " + floatFormat(callReport.emodelRtt.max)
				+ ", " + floatFormat(callReport.emodelRtt.average)
				+ " Lost = " + floatFormat(callReport.emodelLost.min)
				+ ", " + floatFormat(callReport.emodelLost.max)
				+ ", " + floatFormat(callReport.emodelLost.average)
				+ " Delay = " + floatFormat(callReport.emodelDelay.min)
				+ ", " + floatFormat(callReport.emodelDelay.max)
				+ ", " + floatFormat(callReport.emodelDelay.average)
				+ " Jitt = " + floatFormat(callReport.emodelJitter.min)
				+ ", " + floatFormat(callReport.emodelJitter.max)
				+ ", " + floatFormat(callReport.emodelJitter.average)
				+ " sessionInfo = " + "strMgw:"+callReport.sessionInfo.strMgw + ", " + "strSgw:"+callReport.sessionInfo.strSgw
    			+ ", " + "strCodec:"+callReport.sessionInfo.strCodec + ", " + "callMode:"+callReport.sessionInfo.callMode
    			+ ", " + "transMode:"+callReport.sessionInfo.transMode + ", " + "connTime:"+callReport.sessionInfo.connTime
    			+ ", " + "callState:"+callReport.sessionInfo.callState + ", " + "callRole:"+callReport.sessionInfo.callRole
    			+ ", " + "strCallId:"+callReport.sessionInfo.strCallId + ", " + "mgwPort:"+callReport.sessionInfo.mgwPort
    			+ ", " + "pktSnd:"+callReport.sessionInfo.pktSnd + ", " + "pktRecv:"+callReport.sessionInfo.pktRecv
    			+ ", " + "singlePassRsn:"+callReport.sessionInfo.singlePassRsn + ", " + "isMuted:"+callReport.sessionInfo.isMuted;

	}
	/**
	 * ½«bouble¸ñÊ½»¯³Éfloat±£ÁôÁ½Î»Ð¡Êý
	 * @param value
	 * @return
	 * @author: xiaozhenhua
	 * @data:2013-8-26 ÏÂÎç3:35:57
	 */
	private static BigDecimal floatFormat(double value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
}
