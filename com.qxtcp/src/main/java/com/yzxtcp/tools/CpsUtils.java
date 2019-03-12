package com.yzxtcp.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yzxtcp.data.UcsLoginResponse;
import com.yzxtcp.data.UserData;

public class CpsUtils {
//    public static final String SDK_VERSION = "3.0.5.1";
    private static String PARAMSTR = "appid=xxx&userid=xxx&ver=xxxx";
	private static final String CPSURL = "http://cps.kaixuantx.com:9997/v2/getproxylist?";
	private static final String CPSIP = "http://113.31.89.135:9997/v2/getproxylist?";
	private static int mRetryCnt;// 最多重试3次 url两次，IP一次
	public static long lastGetProxyTime = 0;//最后一下拉取Proxy的时间，每天的第一次固定从服务器拉取

	/**
	 * @author zhangbin
	 * @2016-2-23
	 * @return 是否获取到IP
	 * @descript:获取PROXY IP 尝试两次URL，一次IP
	 */
	public static synchronized boolean getProxyIP() {
	    formatParamStr();//添加URL参数
	    if (PARAMSTR.contains("userid=xxx") && !StringUtils.isEmpty(UcsLoginResponse.userid)) {
	        PARAMSTR.replace("userid=xxx", "userid=" + UcsLoginResponse.userid);
	    }
	    
		String ip = UserData.getCSAddress();//获取用户配置的IP
		if(!StringUtils.isEmpty(ip)){ //用户配置了IP，就不用再从CPS获取了
			return true;
		}
		String cpsIp = UserData.getCpsAddress();
		if(!TextUtils.isEmpty(cpsIp)){
			TCPLog.e("cpsIp is "+cpsIp);
		}
		String url;
		mRetryCnt = 0;
		boolean isok = false;// 是否获取到IP
		//尝试连接CPS 3次去获取PROXY ip
		while (mRetryCnt < 3) {
			try {
				if (mRetryCnt < 2) {
					if(!TextUtils.isEmpty(cpsIp)){
						url = "http://"+cpsIp+"/v2/getproxylist?";
					}else{
						url = CPSURL;
					}
				} else {
					if(!TextUtils.isEmpty(cpsIp)){
						url = "http://"+cpsIp+"/v2/getproxylist?";
					}else{
						url = CPSIP;
					}
				}
				url = url + PARAMSTR;
				TCPLog.d("ProxyIP address: " + url);
				String result = doGet(url);
				TCPLog.d("ProxyIP result: " + result);
				if (result != null) {
					JSONObject tokens = new JSONObject(result);
					if (tokens.has("ret")) {
						mRetryCnt = 3;//跳出循环
						// 解析服务器返回值
						isok = parseCpsJson(tokens);
					} else {// JSON解析失败，重试
						mRetryCnt++;
					}
				} else {// 服务器返回null重试
					mRetryCnt++;
				}
			}catch(UnknownHostException e){
				mRetryCnt++;
				TCPLog.e("UnknownHostException getProxyIP:"+e.getMessage());
				e.printStackTrace();
			}catch(SocketException e){
				mRetryCnt++;
				TCPLog.e("SocketException getProxyIP:"+e.getMessage());
				e.printStackTrace();
			}catch (Exception e) {// HTTP异常重试
				mRetryCnt++;
				TCPLog.e("Exception getProxyIP:"+e.getMessage());
				e.printStackTrace();
			}
		}
		TCPLog.d("getProxyIP isok："+isok);
		lastGetProxyTime = System.currentTimeMillis();
		return isok;
	}
	
	public static boolean isSameDay(Date day1, Date day2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	    String ds1 = sdf.format(day1); 
	    String ds2 = sdf.format(day2); 
	    if (ds1.equals(ds2)) { 
	        return true; 
	    } else { 
	        return false; 
	    }
	}

	private static String doGet(String urlStr) throws Exception {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");
			conn.setRequestProperty("charset", "utf-8");
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else {
				throw new RuntimeException(" responseCode is not 200 ... "
						+ responseCode);
			}
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (baos != null) {
					baos.close();
					baos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}

	/**
	 * @author zhangbin
	 * @2016-2-23
	 * @param json
	 *            服务器返回的JSON串
	 * @return 解析JSON是否正确
	 * @throws JSONException
	 * @descript:解析JSON字符保存PROXYIP
	 */
	private static boolean parseCpsJson(JSONObject json) throws JSONException {
		if (json.has("reason")) {
			TCPLog.d("getproxyIP:" + json.getString("reason"));
		}

		int ret = json.getInt("ret");
		if (ret == 0) {
			if (json.has("proxy")) {
				JSONArray jsonArray = json.getJSONArray("proxy");
				UserData.saveProxyIP(jsonArray.toString());
				return true;
			}
		} 
		return false;
		
	}

	/**
	 * @author zhangbin
	 * @2016-2-23
	 * @param index
	 *            ip索引，目前只有3个(0-2)
	 * @return 返回指定索引的IP
	 * @descript:获取本地保存的IP
	 */
	public static String getLocalProxyIp(int index) {
		try {
			String jsonIP = UserData.getPorxyIP();
			JSONArray jsonArray = new JSONArray(jsonIP);
			if (index < jsonArray.length()) {
				return jsonArray.getString(index);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @author zhangbin
	 * @2016-2-23
	 * @return
	 * @descript:判断本地是否存有PROXY IP
	 */
	public static boolean hasLocalProxyIP(){
		String jsonIP = UserData.getPorxyIP();
		return !TextUtils.isEmpty(jsonIP);
	}
	/**
	 * @author zhangbin
	 * @2016-2-23
	 * @param ip
	 *            需要插入到第一个的IP
	 * @descript:移动指定IP到第一个，后面一次顺延
	 */
	public static void replaceIPtoFirst(String ip) {
		try {
			String jsonIP = UserData.getPorxyIP();
			JSONArray jsonArray = new JSONArray(jsonIP);
			// 已经是第一个IP，不需要再调整位置
			if (jsonArray.getString(0).equals(ip)) {
				return;
			}

			JSONArray replaceArray = new JSONArray();
			replaceArray.put(0, ip);
			boolean isFind = false;
			for (int i = 0; i < jsonArray.length(); i++) {
				if (ip.equals(jsonArray.getString(i))) {
					isFind = true;
				} else {
					if (isFind) {
						replaceArray.put(i, jsonArray.getString(i));
					} else {
						replaceArray.put(i + 1, jsonArray.getString(i));
					}
				}
			}
			
			UserData.saveProxyIP(replaceArray.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description 填充拉取proxy列表的参数串
	 * @date 2016年4月8日 下午2:40:13 
	 * @author zhj
	 * @return void    返回类型
	 * @throws JSONException 
	 */
	private static void formatParamStr() {
	    PARAMSTR = "appid=xxx&userid=xxx&ver=xxxx";
	    String token = UcsLoginResponse.SSID;
	    if (UcsLoginResponse.loginType == UcsLoginResponse.UCS_ClientLogin) {
	        return;
	    }
	    
	    //token后面有一段无用，不去掉会使Base64解析异常
	    if (token.contains(".")) {
	        token = token.substring(0, token.indexOf("."));
	        }
	    
	    byte[] binaryData = null;
	    try {
	        binaryData = android.util.Base64.decode(token, android.util.Base64.DEFAULT);
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return;
	    }
	    
	    if (binaryData != null && binaryData.length > 0) {
	        String jasonStr = new String(binaryData);
	        JSONObject json = null;
            try {
                json = new JSONObject(jasonStr);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return;
            }
            PARAMSTR = "";
	        if (json.has("Appid")) {
	            String appid = null;
                try {
                    appid = json.getString("Appid");
                } catch (JSONException e) {
                    e.printStackTrace();  
                }
	            if (!StringUtils.isEmpty(appid)) {
	                PARAMSTR = "appid=" + appid + "&";
	            }
	            else {
	                PARAMSTR = "appid=xxx&";
	            }
	        }
	        else {
	            PARAMSTR = "appid=xxx&";
	        }
	        
	        if (json.has("Userid")) {
                String userid = null;
                try {
                    userid = json.getString("Userid");
                } catch (JSONException e) {
                    e.printStackTrace();  
                }
                if (!StringUtils.isEmpty(userid)) {
                    PARAMSTR = PARAMSTR + "userid=" + userid + "&";
                }
                else {
                    PARAMSTR += "userid=xxx&";
                }
            }
            else {
                PARAMSTR += "userid=xxx&";
            }
	        
	        PARAMSTR = PARAMSTR + "ver=android";
	    }
	}
}
