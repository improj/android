package com.yzx.tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gl.softphone.RtppSrvConfig;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.http.HttpTools;
import com.yzx.http.net.InterfaceUrl;
import com.yzx.listenerInterface.RtppListener;
import com.yzx.preference.UserData;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

public class RtppConfigTools {

	
	/**
	 * 获取RTPP List 列表
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:30:36
	 */
    /*
	public static void getRtppAndStunList(final Context mContext,final RtppListener rtppListener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				//String url = UserData.getHost()+"/static/address.txt";
				//String url = DefinitionAction.RTPP_URL+":"+DefinitionAction.RTPP_PORT+"/v2/getrtpplist?clientnum="+UserData.getClientId();
				//String url = (UserData.getHost().startsWith("https")?UserData.getHost():UserData.getHost())+"/static/address.txt";
				if(UserData.getUserId(mContext).length() > 0){
					String url = InterfaceUrl.RTPP_URL_ADDRESS+"clientnum="+UserData.getClientNumber(mContext);
					try {
						CustomLog.v("RTPP_REPUEST_URL:"+url);
						JSONObject json = HttpTools.doGetMethod(url, UserData.getImSsid(mContext));
						if(json != null){
							CustomLog.v("RTPP_RESPONSE:"+json);
							if(json.has("rtpp")){
								UserData.saveRtppAddressList(mContext,json.getJSONArray("rtpp").toString());
							}
							if(json.has("stun")){
								UserData.saveStunAddressList(mContext,json.getJSONArray("stun").toString());
							}
							if(rtppListener != null){
								rtppListener.onRtpp();
							}
						}
					} catch (IOException e) {
						//e.printStackTrace();
						CustomLog.v("RTPP_RESPONSE:"+e.toString());
					} catch (JSONException e) {
						//e.printStackTrace();
						CustomLog.v("RTPP_RESPONSE:"+e.toString());
					}
				}else{
					CustomLog.v("RTPP_USERID:"+UserData.getUserId(mContext));
				}
			}
		}).start();
		
	}
	*/
    
    /**
     * 获取RTPP List 列表
     * 
     * @author: xiaozhenhua
     * @data:2014-6-16 上午11:30:36
     */
    public static void getRtppAndStunList(final Context mContext,final RtppListener rtppListener){
        //String url = UserData.getHost()+"/static/address.txt";\
        //String url = DefinitionAction.RTPP_URL+":"+DefinitionAction.RTPP_PORT+"/v2/getrtpplist?clientnum="+UserData.getClientId();
        if(UserData.getUserId(mContext).length() > 0){
            String url = InterfaceUrl.RTPP_URL_ADDRESS+"clientnum="+UserData.getClientNumber(mContext);
            try {
                CustomLog.v("RTPP_REPUEST_URL:"+url);
                JSONObject json = HttpTools.doGetMethod(url, UserData.getImSsid(mContext));
                if(json != null){
                    CustomLog.v("RTPP_RESPONSE:"+json);
                    if(json.has("rtpp")){
                        UserData.saveRtppAddressList(mContext,json.getJSONArray("rtpp").toString());
                        CustomLog.v("getRtppAndStunList rtpp success");
                    }
                    if(json.has("stun")){
                        UserData.saveStunAddressList(mContext,json.getJSONArray("stun").toString());
                        CustomLog.v("getRtppAndStunList stun success");
                    }
                    if(rtppListener != null){
                        rtppListener.onRtpp();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                CustomLog.v("RTPP_RESPONSE:"+e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                CustomLog.v("RTPP_RESPONSE:"+e.toString());
            }
        }
        else {
            CustomLog.v("getUserId error, RTPP_USERID:"+UserData.getUserId(mContext));
        }
        
    }


	/**
	 * 计算RTPP地址的丢包率与延迟时间
	 * @param list:需要ping的IP地址列表
	 * @param totalTime：每一个IP地址ping的时间
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-16 上午11:39:48
	 */
	public static void pingRtpp(Context mContext){
		if (mContext == null) {
			CustomLog.v("pingRtpp mContext is null!!!");
			return;
		}
		if(NetWorkTools.isNetWorkConnect(mContext)){
			String hostList = "";
			if (!UserData.getRtppAddress(mContext).isEmpty()) {
				hostList = "[\"" + UserData.getRtppAddress(mContext) + "\"]";
			}
			else {
				hostList = UserData.getRtppAddressList(mContext);
			}
			
			HashMap<String,JSONObject> jsonMap = new HashMap<String,JSONObject>();
			ArrayList<String> arrlyList = new ArrayList<String>();
			if (hostList.length() <= 0)
				return;
			
			// CustomLog.v("HOST_LIST:"+hostList);
			HashMap<String, String> ipMap = new HashMap<String, String>();
			try {
				JSONArray array = new JSONArray(hostList);
				for (int i = 0; i < array.length(); i++) {
					String ipAndPort = array.getString(i).replace("http://", "");
					if (ipAndPort.contains(":")) {
						String ip = ipAndPort.split(":")[0];
						String port = ipAndPort.split(":")[1];
						ipMap.put(ip, port);
						//CustomLog.v("UDP_SEND_IP:" + ip + "    " + port);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			for (String ip : ipMap.keySet()) {
				String port = ipMap.get(ip);
				ArrayList<Boolean> lostSize = new ArrayList<Boolean>();

				int timeOut = 2000;
				double averageTime = 2000.00; // 平均时间
				HashMap<String, Long> startAverageTime = new HashMap<String, Long>();
				HashMap<String, Long> endAverageTime = new HashMap<String, Long>();
				int lost = 0; // 丢包率
				int packageSize = 10; // 发包个数
				for (int j = 0; j < packageSize; j++) {
					String updSend = "";
					String updReveiver = "";
					DatagramSocket socket = null;
					try {
						socket = new DatagramSocket();
						socket.setSoTimeout(timeOut);
						updSend = "pong " + j;
						startAverageTime.put(updSend, System.currentTimeMillis());

						InetAddress address = InetAddress.getByName(ip);
						byte[] udp = updSend.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(udp, udp.length, address, Integer.parseInt(port));
						socket.send(sendPacket);

						DatagramPacket receivePacket = new DatagramPacket( new byte[udp.length], udp.length);
						socket.receive(receivePacket);
						updReveiver = new String(receivePacket.getData()) .replace(" ", " ");
						// CustomLog.v("UDP_SEND:"+updSend);
						// CustomLog.v("UDP_RECEIVER:"+updReveiver);
						// CustomLog.v("UDP_CASE:"+updReveiver.replace(" ", "").equals(updSend.replace(" ", "")));
						endAverageTime.put(updSend, System.currentTimeMillis());
					} catch (SocketException e) {
						endAverageTime.put(updSend, startAverageTime.get(updSend) + 2000);
						//e.printStackTrace();
					} catch (UnknownHostException e) {
						endAverageTime.put(updSend, startAverageTime.get(updSend) + 2000);
						//e.printStackTrace();
					} catch (IOException e) {
						endAverageTime.put(updSend, startAverageTime.get(updSend) + 2000);
						//e.printStackTrace();
					} finally {
						if (socket != null) {
							socket.close();
						}
						if (updReveiver.length() > 0
								&& updSend.length() > 0
								&& updReveiver.replace(" ", "").equals(updSend.replace(" ", ""))) {
							lostSize.add(true);
						}
					}
				}
				lost = Integer.parseInt(new DecimalFormat("00").format(((double) (packageSize - lostSize.size()) / packageSize) * 100) + "");
				if (lost >= 100) {
					averageTime = 2000;
				} else {
					long pingTimeCount = 0;
					for (String key : startAverageTime.keySet()) {
						pingTimeCount = pingTimeCount + (endAverageTime.get(key) - startAverageTime.get(key));
						// CustomLog.v("每一个包的延时:"+key+"    "+(endAverageTime.get(key) - startAverageTime.get(key)));
					}
					if (pingTimeCount > 0) {
						averageTime = (double) (pingTimeCount / packageSize);
					} else {
						averageTime = 2000;
					}
				}

				// CustomLog.v(i+"-1-LOST:" + (((double)(packageSize-lostSize.size())/packageSize)*100));
				// CustomLog.v(i+"-1-LOST_FORMAT:" + (new DecimalFormat("00").format(((double)(packageSize-lostSize.size())/packageSize)*100)+""));
				// CustomLog.v("1-AVERAGE_TIME:" + averageTime);
				// CustomLog.v("1-PING_COUNT:" + packageSize);
				// CustomLog.v("1-LOST_COUNT:" + (packageSize-lostSize.size()));
				// CustomLog.v("1-ADDRESS:" + ip);
				// CustomLog.v("1-丢包率:" + lost + "%");
				// CustomLog.v("1-延迟:" + (int) averageTime);
				JSONObject rtppCfg = new JSONObject();
				try {
					rtppCfg.put("delay", (int) averageTime);
					rtppCfg.put("lost", lost);
					rtppCfg.put("ip", ip);
					jsonMap.put((int)averageTime + ":" + ip, rtppCfg);
					arrlyList.add((int)averageTime + ":" + ip);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				lostSize.clear();
			}
			
			// 冒泡算法，把最大的先算出来，从小到大排列
			for (int i = 0; i < arrlyList.size(); ++i) {
				for (int j = 0; j < arrlyList.size() - i - 1; ++j) {
					if(Integer.parseInt(arrlyList.get(j).split(":")[0]) > Integer.parseInt(arrlyList.get(j + 1).split(":")[0])){
						String v = arrlyList.get(j +1);
						arrlyList.set(j+1, arrlyList.get(j));
						arrlyList.set(j, v);
					}
				}
			}
			ArrayList<RtppSrvConfig> rtppList = new ArrayList<RtppSrvConfig>();
			for(int i = 0 ; i < arrlyList.size() && i < 5 ; i ++){
				JSONObject json = jsonMap.get(arrlyList.get(i));
				CustomLog.v("RTPP:"+"index:" + i +": " + json.toString());
				RtppSrvConfig rtppSrvConfig = new RtppSrvConfig();
				rtppSrvConfig.delay = json.optInt("delay",0);
				rtppSrvConfig.lost = json.optInt("lost",0);
				rtppSrvConfig.ipString = json.optString("ip", "");
				rtppList.add(rtppSrvConfig);
				CustomLog.v("RTPP:"+"index:" + i +": " + rtppSrvConfig.delay  + "," + rtppSrvConfig.lost + ", " + rtppSrvConfig.ipString);
			}
			int RTPP_CFG_MODULE_ID =  UGoManager.getInstance().pub_UGoSetConfig(UGoAPIParam.UGO_RTPP_CFG_MODULE_ID,rtppList,0);
			CustomLog.v("RTPP_CFG_MODULE_ID: " + RTPP_CFG_MODULE_ID);
			creatTimerRtppPing(mContext);
		}
	}
	
	
	public static void creatTimerRtppPing(Context mContext){
		//CustomLog.v("1-START_ALARM_PING:"+ConnectionControllerService.getInstance());
		if(mContext != null){
			Intent intent = new Intent(mContext,RtppReceiver.class);
			intent.setAction("short");
		    PendingIntent pendingIntent= PendingIntent.getBroadcast(mContext, 0, intent, 0);
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(System.currentTimeMillis());
		    //calendar.add(Calendar.SECOND, 10);
		    calendar.add(Calendar.SECOND, 900);
		    ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		    //CustomLog.v("2-START_ALARM_PING:"+ConnectionControllerService.getInstance());
		}
	}
	
	public static void cancleRtppPing(Context mContext){
		//CustomLog.v("1-STOP_ALARM_PING:"+ConnectionControllerService.getInstance());
		if(mContext != null){
			Intent intent = new Intent(mContext,RtppReceiver.class);
			PendingIntent pendingIntent= PendingIntent.getBroadcast(mContext, 0, intent, 0);
			((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
			//CustomLog.v("2-STOP_ALARM_PING:"+ConnectionControllerService.getInstance());
		}
	}
}


