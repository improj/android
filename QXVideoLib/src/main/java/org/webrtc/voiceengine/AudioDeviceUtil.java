package org.webrtc.voiceengine;

import android.content.Context;
import android.os.Build;

import com.yzx.api.UCSService;
import com.yzx.http.net.InterfaceConst;
import com.yzx.http.net.InterfaceUrl;
import com.yzx.http.net.NetParameters;
import com.yzx.http.net.NetRequestInterface;
import com.yzx.http.net.NetRequestInterfaceImp;
import com.yzx.http.net.NetResponseListener;
import com.yzx.http.net.ParseUtil;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.preference.UserData;
import com.yzx.tools.CallLogTools;
import com.yzx.tools.CpsTools;
import com.yzx.tools.DevicesAddressTools;
import com.yzx.tools.SignTools;
import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.AudioDeviceParam.AudioDevConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;


public class AudioDeviceUtil {

	private String brand;

	private String model;

	public static final String PERMISSION_KEY="permission_key";//保存服务器下发策略权限
	public static final String RECORD_KEY="record_key";//保存录音使能开关
	public static final String CALLLOG_CYCLE="calllog_cycle";//呼叫日志采集周期
	public static final String PARAM_KEY="param_key";//保存服务器下发
	public static final String PRIVATE_KEY="private_key";//个人配置

	public static AudioDeviceUtil mSingle = null;

	public static AudioDeviceUtil getInstance() {
		if ( mSingle == null ) {
			mSingle = new AudioDeviceUtil();
		}
		return mSingle;
	}

	public static String getPermissionKey() {
		return PERMISSION_KEY;
	}

	private AudioDeviceUtil() {
		brand = Build.BRAND.replaceAll(" ", "");
		model = Build.MODEL.replaceAll(" ", "");
	}

	private void parseJSONObject(JSONObject jsonObject,AudioDevConfig devCfg) {

		try {
			if (jsonObject.has("recordsource")) {
				devCfg.recordsource = jsonObject.getInt("recordsource");
			}
			if (jsonObject.has("recordchannel")) {
				devCfg.recordchannel = jsonObject.getInt("recordchannel");
			}
			if (jsonObject.has("recordsamplerate")) {
				devCfg.recordsamplerate = jsonObject.getInt("recordsamplerate");
			}
			if (jsonObject.has("playstreamtype")) {
				devCfg.playstreamtype = jsonObject.getInt("playstreamtype");
			}
			if (jsonObject.has("playchannel")) {
				devCfg.playchannel = jsonObject.getInt("playchannel");
			}
			if (jsonObject.has("playsamplerate")) {
				devCfg.playsamplerate = jsonObject.getInt("playsamplerate");
			}
			if (jsonObject.has("speakermode")) {
				devCfg.speakermode = jsonObject.getInt("speakermode");
			}
			if (jsonObject.has("earpiecemode")) {
				devCfg.earpiecemode = jsonObject.getInt("earpiecemode");
			}
			if (jsonObject.has("callmode")) {
				devCfg.callmode = jsonObject.getInt("callmode");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}



	public String getStringByInpuStream(InputStream input, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(input, encoding));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			stringBuilder.append(line);
		}
		br.close();
		return stringBuilder.toString();
	}

	public void setDynamicPolicyEnable(boolean enable) {
		//CustomLog.v("setDynamicPolicyEnable:"+enable);
		AudioDeviceParam.getInstance().setDynamicPolicyEnable(enable);
	}
	
	public void setNoRecordEnable(boolean enable) {
		CustomLog.v("setNoRecordEnable:"+enable);
		AudioDeviceParam.getInstance().setNoRecordEnable(enable);
	}
	
	public void setNoTrackEnable(boolean enable) {
		CustomLog.v("setNoTrackEnable:"+enable);
		AudioDeviceParam.getInstance().setNoTrackEnable(enable);
	}
	
	public void setPermissionParam(Context mContext, String param) {
		//CustomLog.v("setPermissionParam---in");
		SharedPreferencesUtils.setParam(mContext, PERMISSION_KEY, param);
		SharedPreferencesUtils.getParam(mContext, PERMISSION_KEY, "");
	}
	
	/*该方法有问题，PRIVATE_KEY值实际上并没有使用，对PARAM_KEY值得读取和读取本地代码中的配置表的读取逻辑错乱
	public void setAudioDeviceParam(Context mContext) {
		CustomLog.v("setAudioDeviceParam---in");
		String mAudioDeviceParam = (String) SharedPreferencesUtils.getParam(mContext, PRIVATE_KEY, "");
		CustomLog.v("setAudioDeviceParam PRIVATE_KEY mAudioDeviceParam：" + mAudioDeviceParam);
		if (mAudioDeviceParam.length() <= 0) {
			mAudioDeviceParam = (String) SharedPreferencesUtils.getParam(mContext, PARAM_KEY, "");
			CustomLog.v("setAudioDeviceParam PARAM_KEY mAudioDeviceParam：" + mAudioDeviceParam);
			getAdapterParameter(brand,model);
		} else {
			try {
				AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
				parseJSONObject(new JSONObject(mAudioDeviceParam),devCfg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	*/

	/**
	 * @Description 设置适配参数
	 * @param mContext	上下文
	 * @date 2015年12月23日 下午5:36:35 
	 * @author zhj
	 * @return void    返回类型
	 */
	public void setAudioDeviceParam(Context mContext) {
		CustomLog.v("setAudioDeviceParam---in");
		String mAudioDeviceParam = (String) SharedPreferencesUtils.getParam(mContext, PARAM_KEY, "");
		CustomLog.v("setAudioDeviceParam PARAM_KEY mAudioDeviceParam：" + mAudioDeviceParam);
		if (mAudioDeviceParam.length() <= 0) {
			CustomLog.v("setAudioDeviceParam PARAM_KEY length = 0");
			getAdapterParameter(mContext,brand,model);
		} else {
			try {
				AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
				parseJSONObject(new JSONObject(mAudioDeviceParam),devCfg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**配置文件中获取*/
	public void getAdapterParameter(Context mContext,String brand, String model) {
		if ("SamSung".equalsIgnoreCase(brand)) {
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
				AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
				devCfg.callmode=3;
				try {
					JSONObject jsonObject=new JSONObject();
					jsonObject.put("callmode", 3);
					SharedPreferencesUtils.setParam(mContext, PARAM_KEY, jsonObject.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return;
			} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR
					|| Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_0_1
					|| Build.VERSION.SDK_INT == Build.VERSION_CODES.ECLAIR_MR1) {
				AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
				devCfg.callmode=0;
				devCfg.speakermode=1;
				try {
					JSONObject jsonObject=new JSONObject();
					jsonObject.put("callmode", 0);
					jsonObject.put("speakermode", 1);
					SharedPreferencesUtils.setParam(mContext, PARAM_KEY, jsonObject.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return;
			}
		}
		boolean ismatching=true;
		try {
			JSONArray object = new JSONArray(Param.getSbf().toString());
			//CustomLog.v("AUDIO_PARME:"+object.toString());
			JSONObject jsonObject = null;
			for (int i = 0; i < object.length(); i++) {
				jsonObject = new JSONObject(object.getString(i));
				String _brand = jsonObject.getString("brand");
				String _model = null;
				if(jsonObject.has("model")){
					_model=jsonObject.getString("model");
				}
				if (brand.equalsIgnoreCase(_brand)) {
					if (_model == null || model.equalsIgnoreCase(_model)){
						ismatching=false;
						AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
						parseJSONObject(jsonObject,devCfg);
						CustomLog.v("getAdapterParameter 本地表匹配成功：" + jsonObject.toString());
						SharedPreferencesUtils.setParam(mContext, PARAM_KEY, jsonObject.toString());
						//CustomLog.v("SharedPreferencesUtils.setParam--audiodevice");
						//CustomLog.v("recordsource:"+devCfg.recordsource+"  recordchannel:"+devCfg.recordchannel+"  recordsamplerate:"+devCfg.recordsamplerate+"  " +
						//		"playstreamtype:"+devCfg.playstreamtype+"  playchannel:"+devCfg.playchannel+"  playsamplerate:"+devCfg.playsamplerate+"  " +
						//				"speakermode:"+devCfg.speakermode+"  earpiecemode:"+devCfg.earpiecemode+"  callmode:"+devCfg.callmode);
						break;
					}
				}
			}

			if(ismatching){//华为和中兴所有未适配机型默认callmode设置未1
				if(brand.equalsIgnoreCase("Huawei")||brand.equalsIgnoreCase("ZTE")){//modified from "model" to "brand" by zhj
					AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
					devCfg.callmode=1;
					JSONObject jsonObject2=new JSONObject();
					jsonObject2.put("callmode", 1);
					parseJSONObject(jsonObject2, devCfg);
					String mAudioDeviceParam=jsonObject2.toString();
					SharedPreferencesUtils.setParam(mContext, PARAM_KEY, mAudioDeviceParam);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取适配参数
	 *
	 * @param tag
	 */
	public void AakTaskload(final Context mContext , int tag, boolean isFastGet) {
		//CustomLog.v("AakTaskload---in");
		NetParameters params = new NetParameters();
		params.addParam(NetRequestInterface.REQUESTYPE, NetRequestInterface.REQUESTBYGET);
		switch (tag) {
			case InterfaceConst.getParameter://获取智能适配参数
				//String private_params=(String) SharedPreferencesUtils.getParam(mContext, PRIVATE_KEY, "");
				//if(!isFastGet && private_params.length() <= 0){
				//	return;
				//}
				//CustomLog.v("AakTaskload---tag="+tag);
				CustomLog.v("获取智能适配参数 ");
				params.addParam(InterfaceUrl.URL, InterfaceUrl.GET_PARAMETER);
				CustomLog.v("InterfaceUrl.GET_PARAMETER:"+InterfaceUrl.GET_PARAMETER);
				break;
			case InterfaceConst.getParameter_list://获取智能适配列表
				params.addParam(InterfaceUrl.URL, InterfaceUrl.GET_PARAMETER_LIST);
				break;
			case InterfaceConst.postadexception://异常适配参数上报
				params.addParam(InterfaceUrl.URL, InterfaceUrl.POST_PARAMETER_ADEXCEPTION);
				break;
			case InterfaceConst.postadsuccess://成功适配参数上报
				params.addParam(InterfaceUrl.URL, InterfaceUrl.POST_PARAMETER_ADSUCCESS);
				JSONObject jsonObject=new JSONObject();
				try {
					jsonObject.put("playstreamtype", 0);
					jsonObject.put("recordsource", 1);
					jsonObject.put("recordchannel", 0);
					jsonObject.put("recordsamplerate", 0);
					jsonObject.put("playchannel", 0);
					jsonObject.put("playsamplerate", 0);
					jsonObject.put("speakermode", 1);
					jsonObject.put("earpiecemode", 1);
					jsonObject.put("callmode", 1);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				params.addParam("audiodevice", jsonObject.toString());
				break;
		}
		params.addParam("sn", SignTools.getSn());
		params.addParam("uid", UserData.getClientNumber(mContext));
		params.addParam("user_id", UserData.getUserId(mContext));
		params.addParam("brand", brand);
		params.addParam("model", model);
		params.addParam("imei", DevicesAddressTools.getDevicesImei(mContext));
		params.addParam("pv", InterfaceConst.getPlatform);
		params.addParam("app_ver", UCSService.getSDKVersion());
		params.addParam("osv",  URLEncoder.encode(android.os.Build.VERSION.RELEASE));
		params.addParam("api_level",  URLEncoder.encode(android.os.Build.VERSION.SDK));
		params.addParam("nettype", String.valueOf(NetWorkTools.getCurrentNetWorkType(mContext)));
		params.addParam("app_id", UcsLoginResponse.appid);
		CustomLog.v("getUserId---"+UserData.getUserId(mContext));
		CustomLog.v("getClientNumber---"+UserData.getClientNumber(mContext));
		CustomLog.v("UcsLoginResponse.appid---"+UcsLoginResponse.appid);
		//CustomLog.v("AakTaskload---"+params.getParam("sn"));
		new NetRequestInterfaceImp().dorequest(params, new NetResponseListener() {

			public void onException(Exception e, int tag) {
				getAdapterParameter(mContext, brand, model);
			}

			@Override
			public void onComplete(Object object, int tag) {
				//CustomLog.v("onComplete---in:tag="+tag);
				JSONObject jsonObject;
				switch (tag) {
					case InterfaceConst.getParameter://获取智能适配参数
						try {
							CustomLog.v("获取智能适配参数 onComplete:"+object.toString());
							jsonObject = new JSONObject(object.toString());

							//save permission param
							String permission = jsonObject.getString("permission");
							if (permission != null){
								SharedPreferencesUtils.setParam(mContext, PERMISSION_KEY, permission);
								//CustomLog.v("onComplete---111:"+permission);
							}

							//录音使能
							String recordenable = jsonObject.getString("record_enable");
							CustomLog.v("record_enable:"+recordenable);
							if (recordenable != null) {
								SharedPreferencesUtils.setParam(mContext, RECORD_KEY, recordenable);
							}
							else {
								SharedPreferencesUtils.setParam(mContext, RECORD_KEY, "0");
							}

							//呼叫日志采集周期
							int  calllogcycle= jsonObject.optInt("calllog_cycle", CallLogTools.CALLLOG_CYCLE_DEFAULT);
							CustomLog.v("calllog_cycle:"+calllogcycle);
							if (calllogcycle <= 0) {
								calllogcycle = CallLogTools.CALLLOG_CYCLE_DEFAULT;
							}
							SharedPreferencesUtils.setParam(mContext, CALLLOG_CYCLE, calllogcycle);


							boolean isParameter = ParseUtil.getInstance().isParseJson(jsonObject);
							if (!isParameter) {// 下发参数配置
								//save audio device
								AudioDevConfig devCfg=AudioDeviceParam.getInstance().getAudioDevCfg();
								if(jsonObject.has("audiodevice")){
									parseJSONObject(jsonObject.getJSONObject("audiodevice"),devCfg);
									SharedPreferencesUtils.setParam(mContext, PARAM_KEY, jsonObject.getString("audiodevice"));
									CpsTools.setCpsAudioAdapterParam(mContext);//设置CPS参数到组件
								}
								//CustomLog.v("onComplete---222");
							} else {// 配置文件
								getAdapterParameter(mContext, brand, model);
							}
						} catch (JSONException e) {
							getAdapterParameter(mContext, brand, model);
							e.printStackTrace();
						}
						break;
					case InterfaceConst.getParameter_list://获取智能适配列表
						try {
							CustomLog.v("获取智能适配列表 onComplete:"+object.toString());
							jsonObject = new JSONObject(object.toString());
							boolean isParameter = ParseUtil.getInstance().isParseJson(jsonObject);
							if (!isParameter) {//解析智能适配列表
							/*
							JSONObject jsObject = jsonObject.getJSONObject("adlist");
							JSONArray jsonArray = JSONArray.fromObject(jsObject.toString());
							List<AudioDevConfig> adList = new ArrayList(AudioDevConfig);

							for (int i=0; i<jsonArray.size();i++){
								AudioDevConfig devCfg=new AudioDevConfig();
								parseJSONObject(jsObject, devCfg);
								adList.add(devCfg);
								//.......start check audio device param----
							}
							*/
								//save adList jsonobject
								SharedPreferencesUtils.setParam(mContext, PARAM_KEY, jsonObject.getString("adlist"));
							} else {// 配置文件
								getAdapterParameter(mContext, brand, model);
							}
						} catch (JSONException e) {
							getAdapterParameter(mContext, brand, model);
							e.printStackTrace();
						}
						break;
					case InterfaceConst.postadexception://异常适配参数上报
						CustomLog.v("异常适配参数上报 onComplete:"+object.toString());
						//report exception param success
						break;
					case InterfaceConst.postadsuccess://成功适配参数上报
						CustomLog.v("成功适配参数上报 onComplete:"+object.toString());
						//report exception param success
						break;
				}

			}
		}, mContext, tag);
	}
}
