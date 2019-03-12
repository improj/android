package com.yzxIM.tools;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import com.yzxIM.IMManager;
import com.yzxIM.data.IMUserData;
import com.yzxtcp.data.UserData;
import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.tools.CustomLog;

public class StatisticalReport {
	// public static final String CLS_HTTP =
	// "http://172.16.12.96:1999/clshttp/active";
	public static final String CLS_HTTP = "http://im3.onccop.com/clshttp/firstlogin";
	public static final String CLS_HTTP_ONEDAY = "http://im3.onccop.com/clshttp/active";
	public static final String KEY = "im#520!";

	/**
	 * @author zhangbin
	 * @2015-11-10
	 * @param appid
	 *            应用ID
	 * @param userid
	 *            用户账号
	 * @return
	 * @descript:帐号在设备上SDK第一次登录时需要收集信息上报到服务器
	 */
	private static String getStaticticalInfo(final String appid,
			final String userid) {

		JSONObject info = new JSONObject();
		try {
			info.put("appid", appid);
			info.put("userid", userid);
			info.put("uin", UserData.getiUin());
			info.put("pf", "android");
			info.put("sdkver", IMManager.getInstance(null).getSDKVersion());
			info.put("os", android.os.Build.VERSION.RELEASE);
			info.put("brand", android.os.Build.MODEL);
			info.put("imei", getImei());
			//info.put("applist", getApp());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// return new String(msg);
		// return RC4Tools.encry_RC4_string(info.toString(), KEY);
		return Base64
				.encodeToString(info.toString().getBytes(), Base64.DEFAULT);

	}

	/**
	 * @author zhangbin
	 * @2015-11-10
	 * @param appid
	 *            应用ID
	 * @param userid
	 *            用户账号
	 * @return
	 * @descript:需要每天只上报一次的信息
	 */
	private static String getStaticticalInfoOneDay(final String appid,
			final String userid) {

		JSONObject info = new JSONObject();
		try {
			info.put("appid", appid);
			info.put("userid", userid);
			info.put("uin", UserData.getiUin());
			info.put("pf", "android");
			info.put("sdkver", IMManager.getInstance(null).getSDKVersion());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return RC4Tools.encry_RC4_string(info.toString());

	}

	private static JSONArray getApp() {
		JSONArray appArray = new JSONArray();
		List<PackageInfo> packages = YzxTCPCore.getContext()
				.getPackageManager().getInstalledPackages(0);
		String appName = "";
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			appName = packageInfo.applicationInfo.loadLabel(
					YzxTCPCore.getContext().getPackageManager())
					.toString();

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appArray.put(appName);
			}
		}
		return appArray;
	}

	private static String getImei() {
		String mImei = ((TelephonyManager) YzxTCPCore.getContext()
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (mImei == null) {
			mImei = "000000000000000";
		}

		return mImei;
	}

	public static boolean startStatisticalReport(final String appid,
			final String userid) {

		if (isStatisticalReportOK(userid)) {
			return false;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String response = HttpUtils.doPost(CLS_HTTP,
						getStaticticalInfo(appid, userid));
				// response = RC4Tools.decry_RC4(response);

				CustomLog.d("staticresult : " + response);
				parseResult(userid, response);

			}
		}).start();
		return true;
	}

	public static void startStatisticalReportOneDay(final String appid,
			final String userid) {
		if (isToday()) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String response = HttpUtils.doPost(CLS_HTTP_ONEDAY,
						getStaticticalInfoOneDay(appid, userid));
				// response = RC4Tools.decry_RC4(response);
				CustomLog.d("staticoneresult : " + response);
				parseResult(userid, response);

			}
		}).start();

	}

	private static void parseResult(final String userid, final String response) {
		if (TextUtils.isEmpty(response) == false) {
			JSONObject tokens;
			try {
				tokens = new JSONObject(response);
				if (tokens.has("result")) {
					String result = tokens.getString("result");
					if (result.equals("0")) {
						saveStatistcalUser(userid, true);
						saveStatistalTime();
						CustomLog.d("Statistical ok");
					} else {
						CustomLog.d("Statistical fail");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			CustomLog.e("report result is null");
		}
	}

	/**
	 * @author zhangbin
	 * @2015-11-19
	 * @param userId
	 *            登陆用户ID
	 * @return
	 * @descript:判断用户信息是否已经上报成功过
	 */
	private static boolean isStatisticalReportOK(String userId) {

		String info = IMUserData.getLoginInfo();
		if (TextUtils.isEmpty(info)) {
			return false;
		}
//		CustomLog.d("info:" + info);
		try {
			JSONObject jsonInfo = new JSONObject(info);
			if (jsonInfo.has(userId)) {
				return jsonInfo.getBoolean(userId);
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @author zhangbin
	 * @2015-11-19
	 * @param userId
	 *            用户ID
	 * @param isReportOk
	 *            是否已经上报成功
	 * @descript:
	 */
	private static void saveStatistcalUser(String userId, boolean isReportOk) {
		String info = IMUserData.getLoginInfo();
		JSONObject jsonInfo = null;
		try {
			if (TextUtils.isEmpty(info)) {
				jsonInfo = new JSONObject();
			} else {
				jsonInfo = new JSONObject(info);
			}
			jsonInfo.put(userId, isReportOk);

			IMUserData.saveLoginInfo(jsonInfo.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author zhangbin
	 * @2015-11-19 @
	 * @descript:保存月日时间
	 */
	private static void saveStatistalTime() {
		String today = getToday();
		IMUserData.saveLoginTimer(today);
	}

	private static boolean isToday() {
		String today = IMUserData.getLoginTime();
		if (TextUtils.isEmpty(today)) {
			return false;
		}

		String oldDay = getToday();
		CustomLog.d("today:" + today + "  oldDay:" + oldDay);
		if (oldDay.equals(today)) {
			return true;
		}

		return false;
	}

	private static String getToday() {
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("MMdd");
		String retStrFormatNowDate = sdFormatter.format(nowTime);

		return retStrFormatNowDate;
	}

	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static byte[] DesEncrypt(byte[] datasource, String password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] DESdecrypt(byte[] src, String password)
			throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");

		if (src == null || src.length == 0) {
			return null;
		}

		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xff;

			String hv = Integer.toHexString(v);
			stringBuilder.append(" 0x");
			if (hv.length() < 2) {
				stringBuilder.append("0");
			}

			stringBuilder.append(hv);
		}

		return stringBuilder.toString();
	}
}
