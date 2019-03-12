package com.yzx.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceUtil;

import android.text.TextUtils;

import com.gl.softphone.CallReport;
import com.yzx.controller.TimerHandler;
import com.yzx.controller.VoipCore;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.preference.UserData;
import com.yzxtcp.UCSManager;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

/**
 * 呼叫日志上报工具类
 * @author zhj
 *
 */
public class CallLogTools {

	public static final int CALLLOG_CYCLE_DEFAULT = 3; //呼叫日志采集周期的默认值
	public static final int CALLLOG_MAX_SIZE = 1024 * 20; //上报日志大小限度为20K
	public static final int QUALITY_MAX_ITEMS = 1000; //质量数据最大条数
	public static final int SINGLEPASS_MAX_ITEMS = 100; //单通数据最大条数
	private static long totallyFailTime = 0; // 彻底失败的时间点，之后的1小时内不再重传
	private static StringBuffer callQualityBuffer = new StringBuffer(); //通话质量数据
	private static StringBuffer upSPBuffer = new StringBuffer(); //上行单通数据
	private static StringBuffer downSPBuffer = new StringBuffer(); //下行单通数据
	private static StringBuffer netTypeBuffer = new StringBuffer(); //网络类型变更数据
	private static StringBuffer iceBuffer = new StringBuffer(); //ICE变更数据
	private static String lastIce; //最后一次记录的ice数据
	private static StringBuffer videoRatioBuffer = new StringBuffer(); //视频宽高变更数据
	private static int videoSW ; //最后一次记录的视频SW
	private static int videoSH ; //最后一次记录的视频SH
	private static int videoDW ; //最后一次记录的视频DW
	private static int videoDH ; //最后一次记录的视频DH
	private static long answerTime = 0; //电话接听时间点
	public static boolean isCaller = true; //是否为主叫，默认为主叫
	
	private static String audioPackages = ""; //音频收发包总数

	/**
	 * 获取通话日志采集周期
	 * @return
	 */
	public static int getCallLogCycle() {
		int cycle = (Integer)SharedPreferencesUtils.getParam(YzxTCPCore.getContext(), AudioDeviceUtil.CALLLOG_CYCLE, CALLLOG_CYCLE_DEFAULT);
		if (cycle <= 0) {
			cycle = CALLLOG_CYCLE_DEFAULT;
		}
		return cycle;
	}
	
	private static String getCallLogDirectory() {
		return FileTools.getSdCardFilePath() +"/" + "log" +"/" + YzxTCPCore.getContext().getPackageName() + "/callfile";
	}
	
	/**
	 * 初始化通话日志
	 */
	public static void initCallLog() {
		answerTime = System.currentTimeMillis();
		callQualityBuffer.setLength(0);
		upSPBuffer.setLength(0);
		downSPBuffer.setLength(0);
		netTypeBuffer.setLength(0);
		iceBuffer.setLength(0);
		videoRatioBuffer.setLength(0);
		videoSW = 0;
		videoSH = 0;
		videoDW = 0;
		videoDH = 0;
		saveNetChange(NetWorkTools.getCurrentNetWorkType(VoipCore.getContext()), true);
	}
	
	/**
	 * 保存呼叫质量数据
	 * @param message 组件上报的通话质量信息
	 */
	public static void saveCallQuality(String message) {
		int callType = UserData.getCallType();
		if (message == null || message.length() == 0) {
			if (callType == 3) //视频通话
				callQualityBuffer.append("[-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],");
			else
				callQualityBuffer.append("[-1,-1,-1,-1],");
			return;
		}
		
		try {
			JSONObject obj = new JSONObject(message);
				
			if (!obj.has("ul")) {
				callQualityBuffer.append("[-1,");
			}
			else
				callQualityBuffer.append("[" + obj.getInt("ul") + ",");

			if (!obj.has("dl")){
				callQualityBuffer.append("-1,");
			}
			else
				callQualityBuffer.append(obj.getInt("dl") + ",");

			if (!obj.has("rtt")) {
				callQualityBuffer.append("-1,-1],");
			}
			else
				callQualityBuffer.append(obj.getInt("rtt") + ",");

			if (callType != 3) { //若为音频通话则不填写视频数据
				if (!obj.has("dnjt")) {
					callQualityBuffer.append("-1],");
				}
				else 
					callQualityBuffer.append(obj.getInt("dnjt") + "],");
				return;
			}
			
			if (!obj.has("dnjt")) {
				callQualityBuffer.append("-1,");
			}
			else 
				callQualityBuffer.append(obj.getInt("dnjt") + ",");
			
			//以下是视频数据
			if (!obj.has("vul"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("vul") + ",");
			
			if (!obj.has("vdl"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("vdl") + ",");
			
			if (!obj.has("sb"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("sb") + ",");
			
			if (!obj.has("vrtt"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("vrtt") + ",");
			
			if (!obj.has("sf"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("sf") + ",");
			
			if (!obj.has("rb"))
				callQualityBuffer.append("-1,");
			else 
				callQualityBuffer.append(obj.getInt("rb") + ",");
			
			if (!obj.has("df"))
				callQualityBuffer.append("-1],");
			else 
				callQualityBuffer.append(obj.getInt("df") + "],");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			CustomLog.v("saveCallQuality  JSON Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	/**
	 * 保存上行单通数据
	 */
	public static void saveUpSP() {
		upSPBuffer.append((int)(System.currentTimeMillis() - answerTime)/1000).append(",");
	}
	
	/**
	 * 保存下行单通数据
	 */
	public static void saveDownSP() {
		downSPBuffer.append((int)(System.currentTimeMillis() - answerTime)/1000).append(",");
	}
	
	/**
	 * @Description 保存网络类型的变更信息
	 * @param netType 网络类型
	 * @param isInitial 是否是通话初始时的网络状态
	 * @return void    返回类型 
	 * @date 2017年1月12日 上午11:33:52 
	 * @author zhj
	 */
	public static void saveNetChange(int netType, boolean isInitial) {
		int type = -1;
		switch (netType) {
		case 0:
			type = 0;
			break;
		case 1:
			type = 4; //wifi
			break;
		case 2:
			type = 1; //2G
			break;
		case 4:
			type = 3; //4G(sdk内部没有区分3G和4G)
			break;
		case 8:
			type = 5; //以太网
			break;
		}
		
		if (isInitial) {
			netTypeBuffer.append("[0,"+ type +"],");
		}
		else {
			netTypeBuffer.append("["+ (int)(System.currentTimeMillis() - answerTime)/1000 +"," + type + "],");
		}
	}
	
	/**
	 * @Description 保存ICE的变更信息
	 * @param param 包含ice的字符串
	 * @return void    返回类型 
	 * @date 2017年1月12日 上午11:35:03 
	 * @author zhj
	 */
	public static void saveIceChange(String param) {
		try {
			JSONObject obj = new JSONObject(param);
			if (obj == null || !obj.has("ice")) {
				return;
			}
			String ice = obj.getInt("ice") + "";
			if (iceBuffer.length() == 0) {
				iceBuffer.append("[0," + ice + "],");
				lastIce = ice;
			} else {
				if (!ice.equals(lastIce)) {
					iceBuffer.append("[" + (int) (System.currentTimeMillis() - answerTime)/1000 + "," + ice + "],");
					lastIce = ice;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			CustomLog.v("saveIceChange  JSON Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description 保持视频宽高变更信息
	 * @param  param 包含ice的字符串
	 * @return void    返回类型 
	 * @date 2017年1月12日 下午12:30:04 
	 * @author zhj
	 */
	public static void saveVideoRatio(String param) {
		try {
			JSONObject obj = new JSONObject(param);
			if (obj == null || !obj.has("sw") || !obj.has("sh") || !obj.has("dw") || !obj.has("dh") ) {
				return;
			}
			int sw = obj.getInt("sw");
			int sh = obj.getInt("sh");
			int dw = obj.getInt("dw");
			int dh = obj.getInt("dh");
			if (videoRatioBuffer.length() == 0) {
				videoRatioBuffer.append("[0," + sw +","+ sh +","+ dw +","+ dh + "],");
				videoSW = sw;
				videoSH = sh;
				videoDW = dw ;
				videoDH = dh;
			} else {
				if (videoSW != sw || videoSH != sh || videoDW != dw || videoDH != dh) {
					videoRatioBuffer.append("[" + (int) (System.currentTimeMillis() - answerTime)/1000 + "," + sw +","+ sh +","+ dw +","+ dh + "],");
					videoSW = sw;
					videoSH = sh;
					videoDW = dw ;
					videoDH = dh;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			CustomLog.v("saveVideoRatio  JSON Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析emodel数据
	 */
	public static void parseEmodel(CallReport callReport) {
		if (callReport == null) {
			audioPackages = "-1,-1";
			return;
		}
		audioPackages =  callReport.sessionInfo.pktRecv + ",";
		audioPackages = audioPackages + callReport.sessionInfo.pktSnd ;
	}
	
	/**
	 * 将呼叫日志内容保存到文件
	 * @param callId 呼叫ID
	 * @param log 需要保存的日志
	 */
	public synchronized static void saveLog2File(String callId, String log) {
		String filePath = getCallLogDirectory();
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(filePath + "/" + callId + ".txt");

		FileWriter writer = null;
		BufferedWriter bw = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			else {
				file.delete();
				file.createNewFile();
			}
			writer = new FileWriter(file, true);
			bw = new BufferedWriter(writer);
			bw.write(log);
			//写入版本信息
			String strVersion = "logversion=3,";
			if (isCaller)
				strVersion += "role=caller,";
			else
				strVersion += "role=callee,";
			strVersion = strVersion + "sdkversion=" + DefinitionAction.SDK_VERSION;

			bw.write(strVersion + ",qualitystatistics={\"cqs\":{\"cs\":[");
			if (callQualityBuffer.length() > 0) {
				callQualityBuffer.deleteCharAt(callQualityBuffer.length() - 1);
			}
			bw.write(callQualityBuffer.toString());
			bw.write("],\"usps\":[");
			if (upSPBuffer.length() > 0) {
				upSPBuffer.deleteCharAt(upSPBuffer.length() - 1);
			}
			bw.write(upSPBuffer.toString());
			bw.write("],\"dsps\":[");
			if (downSPBuffer.length() > 0) {
				downSPBuffer.deleteCharAt(downSPBuffer.length() - 1);
			}
			bw.write(downSPBuffer.toString());
			
			//收发包总数
			bw.write("],\"aps\":[");
			bw.write(audioPackages);
			
			//网络类型变化
			bw.write("],\"nss\":[");
			if (netTypeBuffer.length() > 0) {
				netTypeBuffer.deleteCharAt(netTypeBuffer.length() - 1);
			}
			bw.write(netTypeBuffer.toString());
			
			//ICE变化
			bw.write("],\"iss\":[");
			if (iceBuffer.length() > 0) {
				iceBuffer.deleteCharAt(iceBuffer.length() - 1);
			}
			bw.write(iceBuffer.toString());
			
			//视频分辨率变化数据
			bw.write("],\"rss\":[");
			if (videoRatioBuffer.length() > 0) {
				videoRatioBuffer.deleteCharAt(videoRatioBuffer.length() - 1);
			}
			bw.write(videoRatioBuffer.toString());

			bw.write("],\"csc\":" + TimerHandler.getInstance().getCalllogCycle() + "}}");

			//保存到文件后清空缓存
			callQualityBuffer.setLength(0);
			upSPBuffer.setLength(0);
			downSPBuffer.setLength(0);
		}catch (Exception e) {
			CustomLog.v("saveLog2File Exception: " + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
					bw = null;
				}
				if(writer != null){
					writer.close();
					writer = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 从呼叫日志文件夹中找出一个文件
	 * @return 找到的一个文件名，没找到则返回null
	 */
	private static String getAFileName() {
		String filePath =  getCallLogDirectory();
		File path = new File(filePath);
		File files[] = path.listFiles();  
        if(files != null){
            for (File f : files){
                if(! f.isDirectory()){
                	return f.getName();
                }
            }  
        } 
        return null;
	}
	
	/**
	 * 上传呼叫日志到服务端
	 * @param fileName
	 * @return 成功返回true，失败返回false
	 */
	private static boolean uploadCalllog(String fileName) {
		CustomLog.d("uploadCalllog enter,  fileName: " + fileName);
		String filePath =  getCallLogDirectory();
		BufferedReader reader = null;
		
		StringBuilder sbf = new StringBuilder();
		String line = null;
		try {
			reader = new BufferedReader(new FileReader(filePath +"/" + fileName));
			while ((line = reader.readLine()) != null) {
				sbf.append(line.trim());
			}
		}catch(IOException e){
			CustomLog.v("uploadCalllog  BufferedReader Exception: " + e.getMessage());
			e.printStackTrace();
		}finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String result = null;
		int respCode = 0;
		String respMsg = null;
		result = FileTools.uploadSDKReport(sbf.toString().trim(), FileTools.LOG_URL);
		if(TextUtils.isEmpty(result)){
			respCode = -1;
		}else{
			try {
				JSONObject obj = new JSONObject(result);
				if(obj.has("respCode")){
					respCode = obj.getInt("respCode");
				}
				else{
					respCode = -1;
				}
				if(obj.has("respMsg")){
					respMsg = obj.getString("respMsg");
				}
				CustomLog.d("uploadCalllog callid:"+fileName + " result:"  + result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if(respCode == 0){
			CustomLog.d("uploadCalllog:"+fileName + "  succeeded");
			//上传成功后删除本地文件
			File file = new File(filePath +"/" + fileName);
			file.delete();
			return true;
		}else{
			CustomLog.d("uploadCalllog callid:"+fileName + "  failed");
			return false;
		}
	}

	/**
	 * @Description  重置日志上传彻底失败的时间
	 * @return void    返回类型 
	 * @date 2017年1月20日 下午3:02:20 
	 * @author zhj
	 */
	public static void resetTotallyFailTime() {
		totallyFailTime = 0;
	}
	
	/**
	 * 启动上传日志文件
	 * @param fileName 指定要上传的日志文件名
	 */
	public synchronized static void launchUploadCalllog(final String fileName) {
		CustomLog.d("uploadCalllog launchUploadCalllog enter, fileName: " + fileName);
		// 累积3次失败后的1小时内不再上传
		if (totallyFailTime != 0 && System.currentTimeMillis() - totallyFailTime < 3600 * 1000)
			return;
		//TCP未连接不上传
		if (!UCSManager.isConnect())
			return;
		// 2G网络下不上传
		if (NetWorkTools.getCurrentNetWorkType(YzxTCPCore.getContext()) == 2)
			return;
		if (getAFileName() == null)
			return;
		CustomLog.d("uploadCalllog launchUploadCalllog run()");
		// 优先上传指定文件，指定为空或不存在则上传其他文件
		String curFileName = fileName;
		if (TextUtils.isEmpty(curFileName)|| !new File(getCallLogDirectory() + "/" + curFileName).exists())
			curFileName = getAFileName();
		while (!TextUtils.isEmpty(curFileName)) {
			int retryTimes = 0;
			for (; retryTimes < 3; retryTimes++) {
				if (uploadCalllog(curFileName)) {
					totallyFailTime = 0;
					break;
				}
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}
			}
			if (retryTimes == 3) {
				totallyFailTime = System.currentTimeMillis();
				break;
			} else {
				curFileName = getAFileName();
			}
		}
	}
}
