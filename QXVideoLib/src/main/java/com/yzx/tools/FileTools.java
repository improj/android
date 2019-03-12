package com.yzx.tools;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.yzx.http.HttpTools;
import com.yzx.http.net.SharedPreferencesUtils;
import com.yzx.preference.UserData;
import com.yzxtcp.tools.CustomLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author xiaozhenhua
 * 
 */
public class FileTools {
	private static final String AUDIO_PATH = "/audio";
	private static final String AUDIO_RECORD_PATH = "/audio_record";
	private static final String PIC_PATH = "/picture";
	private static final String FILE = "/file";
	private static final String LOG_FILE = "/log";
	
	public static final String FILE_TYPE_IMG = "img";
	public static final String FILE_TYPE_FILE = "file";
	
	// 文件大小单位 M,K,B
	public static enum FileUnit {
		M, K, B
	}
	
	//测试环境
//	public static final String CRASH_URL = "http://113.31.130.125:9094/log-storage/upload/crash";
//	public static final String LOG_URL = "http://113.31.130.125:9094/log-storage/upload/log";
//	public static final String RECORD_URL = "http://113.31.130.125:9094/log-storage/upload/record";
	//线上环境
	public static final String CRASH_URL = "http://ulog.ucpaas.com/log-storage/upload/crash";
	public static final String LOG_URL = "http://ulog.ucpaas.com/log-storage/upload/log";
	public static final String RECORD_URL = "http://ulog.ucpaas.com/log-storage/upload/record";
	/**
	 * 创建录音文件
	 * 
	 * @param uid
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午9:53:12
	 */
	public static String createAudioFileName(String uid) {
		String path = FileTools.getSdCardFilePath() + AUDIO_PATH + "/" + uid
				+ "_" + System.currentTimeMillis()
				+ Math.round(Math.random() * 1000) + ".amr";
		return path;
	}
	
	/**
	 * @Description 创建音频通话录音文件  
	 * @param callID 呼叫ID
	 * @return	文件完整路径
	 * @date 2016年1月8日 下午7:39:12 
	 * @author zhj 
	 * @return String    返回类型
	 */
	public static String createAudioRecordFile(String callID) {
	    String directory = FileTools.getSdCardFilePath() + AUDIO_RECORD_PATH;
	    //首先清空文件夹下的文件
	    File file = new File(directory);
	    if(!file.exists()) {
	    	file.mkdirs();
	    }
	    deleteAllFiles(file);
	    String path = directory + "/" + callID + ".wav";
        return path;
	}
	
	/**
	 * @Description 清空文件夹
	 * @param root	文件夹	
	 * @date 2016年1月22日 下午2:03:06 
	 * @author zhj
	 * @return void    返回类型
	 */
    private static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

	/**
	 * 创建图片文件
	 * 
	 * @param uid
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-7 下午4:03:46
	 */
	public static String createPicFilePath(String uid) {
		String path = FileTools.getSdCardFilePath() + PIC_PATH + "/";
		return path;
	}

	/**
	 * 创建其它附后
	 * 
	 * @param uid
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-7 下午4:04:05
	 */
	public static String createFilePath(String uid) {
		String path = FileTools.getSdCardFilePath() + FILE + "/";
		return path;
	}

	/**
	 * 创建文件夹
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午9:49:22
	 */
	public static void createFolder() {
		File audio_file = new File(FileTools.getSdCardFilePath() + AUDIO_PATH);
		if (!audio_file.exists()) {
			audio_file.mkdirs();
		}
		File audio_record_file = new File(FileTools.getSdCardFilePath() + AUDIO_RECORD_PATH);
        if (!audio_record_file.exists()) {
            audio_record_file.mkdirs();
        }
		File pic_file = new File(FileTools.getSdCardFilePath() + PIC_PATH);
		if (!pic_file.exists()) {
			pic_file.mkdirs();
		}
		File other_file = new File(FileTools.getSdCardFilePath() + FILE);
		if (!other_file.exists()) {
			other_file.mkdirs();
		}
		File log_file = new File(FileTools.getSdCardFilePath() + LOG_FILE);
		if (!log_file.exists()) {
			log_file.mkdirs();
		}
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午2:39:56
	 */
	public static String getSdCardFilePath() {
		String path = "";
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/yunzhixun");
			if (!file.exists()) {
				file.mkdirs();
			}
			path = file.getAbsolutePath();
		} else {
			File mntFile = new File("/mnt");
			File[] mntFileList = mntFile.listFiles();
			if (mntFileList != null) {
				for (int i = 0; i < mntFileList.length; i++) {
					String mmtFilePath = mntFileList[i].getAbsolutePath();
					String sdPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					if (!mmtFilePath.equals(sdPath)
							&& mmtFilePath.contains(sdPath)) {
						File file = new File(mmtFilePath + "/yunzhixun");
						if (!file.exists()) {
							file.mkdirs();
						}
						path = file.getAbsolutePath();
					}
				}
			}
		}
		return path;
	}

	/**
	 * 获取默认SD卡路径
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午2:39:56
	 */
	public static String getDefaultSdCardPath() {
		String path = "";
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			if (!file.exists()) {
				file.mkdirs();
			}
			path = file.getAbsolutePath();
		} else {
			File mntFile = new File("/mnt");
			File[] mntFileList = mntFile.listFiles();
			if (mntFileList != null) {
				for (int i = 0; i < mntFileList.length; i++) {
					String mmtFilePath = mntFileList[i].getAbsolutePath();
					String sdPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					if (!mmtFilePath.equals(sdPath)
							&& mmtFilePath.contains(sdPath)) {
						File file = new File(mmtFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						path = file.getAbsolutePath();
					}
				}
			}
		}
		return path;
	}

	/**
	 * 获取除默认以外的存储设备
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-10 下午4:02:44
	 */
	public static String getExternalSdCardPath() {
		String path = "";
		String defaultPath = getDefaultSdCardPath();
		if (defaultPath.length() > 0) {
			if (defaultPath.contains("ext")) {
				path = Environment.getRootDirectory().getAbsolutePath();
			} else {
				File mntFile = new File("/mnt");
				File[] mntFileList = mntFile.listFiles();
				if (mntFileList != null) {
					for (int i = 0; i < mntFileList.length; i++) {
						String mmtFilePath = mntFileList[i].getAbsolutePath();
						if (mmtFilePath.contains("ext")) {
							path = mmtFilePath;
							break;
						}
					}
				}
			}
		}
		return path;
	}

	/**
	 * 判断SD卡或者手机内存是否可用
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-10 上午11:56:05
	 */
	public static boolean isExistStore() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				|| Environment.getRootDirectory().equals(
						Environment.MEDIA_MOUNTED);
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            ：源文件
	 * @param newPath
	 *            ：新文件
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午2:48:14
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件是否小于100M
	 * 
	 * @param filePaht
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-22 下午4:04:25
	 */
	public static boolean isFileSize(String filePaht) {
		boolean isFileSize = false;
		File file = new File(filePaht);
		try {
			FileInputStream stream = new FileInputStream(file);
			isFileSize = (((stream.available() / 1024) / 1024) + 1) < 100;
		} catch (FileNotFoundException e) {
			isFileSize = false;
			e.printStackTrace();
		} catch (IOException e) {
			isFileSize = false;
			e.printStackTrace();
		}
		return isFileSize;
	}

	/**
	 * 获取文件在大小，单位K
	 * 
	 * @param filePath
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-6-24 下午5:53:36
	 */
	public static String getFileSize(String filePath) {
		long size = 0;
		File file = new File(filePath);
		if (file.exists()) {
			try {
				FileInputStream stream = new FileInputStream(file);
				long s = stream.available();
				if (s > 1024) {
					size = stream.available() / 1024;
				} else {
					size = 1;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size + "";
	}

/*	private static HashMap<String, String> picMap = new HashMap<String, String>();
	static {
		picMap.put("jpg", null);
		picMap.put("jpeg", null);
		picMap.put("bmp", null);
		picMap.put("png", null);
	}

	public static boolean isPic(String path) {
		boolean isPic = false;
		for (String key : picMap.keySet()) {
			if (path.toLowerCase().endsWith(key)) {
				isPic = true;
				break;
			}
		}
		return isPic;
	}*/

	/**
	 * 
	 * SDK日志保存SD卡，连接测试服务器时开启
	 * 
	 * @author: xiongjijian
	 */
	private static void saveSdkLog(String result,String fileName) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = format.format(new Date(System.currentTimeMillis()));
			String path = FileTools.getSdCardFilePath() + LOG_FILE;
			String file = fileName + str.substring(0, 10) + ".txt";
			makeRootDirectory(path, file);
			FileWriter writer = new FileWriter(path+"/"+file, true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(str+": "+result);
			bw.newLine();
			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 崩溃日志至SD卡
	 * 
	 * @author: xiongjijian
	 */
	private static void saveExLog(String result,String fileName) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = format.format(new Date(System.currentTimeMillis()));
			String path = FileTools.getSdCardFilePath() + LOG_FILE;
			String file = fileName + str.substring(0, 10) + ".txt";
			makeRootDirectory(path, file);
			FileWriter writer = new FileWriter(path+"/"+file, true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.newLine();
			bw.write(str);
			bw.newLine();
			bw.write(result);
			bw.newLine();
			bw.write("----------------------------------------------------------");
			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * trace日志至SD卡
	 * 
	 * @author: xiongjijian
	 */
	/*public static void saveTraceLog(String result) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());
			String str = format.format(curDate);
			String path = FileTools.getSdCardFilePath() + LOG_FILE;
			String file = "YZX_trace_" + str.substring(0, 10)+ ".txt";
			makeRootDirectory(path, file);
			FileWriter writer = new FileWriter(path+"/"+file, true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.newLine();
			bw.write(str);
			bw.newLine();
			bw.write(result);
			bw.newLine();
			bw.newLine();
			bw.write("----------------------------------------------------------");
			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	public static void makeRootDirectory(String filePath, String fileName) {
		try {
			File path = new File(filePath);
			if (!path.exists()) {
				path.mkdirs();
			}
			File file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				if (!isFileSizeExceed(filePath + "/" + fileName)) {
					file.delete();
					file.createNewFile();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件是否超出1M大小
	 * 
	 * @param file
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-31 上午10:50:28
	 */
	public static boolean isFileSizeExceed(String file) {
		boolean fileSizeExceed = false;
		try {
			fileSizeExceed = new FileInputStream(file).available() / 1024 / 1024 <= 1;
		} catch (FileNotFoundException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		} catch (IOException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		}
		return fileSizeExceed;
	}
	
	/**
	 * @Description 判断文件大小
	 * @param file	文件
	 * @param maxSize	最大大小
	 * @param fileUnit 大小单位,有M,K,B
	 * @return	true: 没有超过最大消息；false：超过最大大小
	 * @date 2017-2-26 下午12:44:33 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	public static boolean isFileSizeExceed(String file, int maxSize, FileUnit fileUnit) {
		boolean fileSizeExceed = false;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int fileSize = fis.available();
			switch (fileUnit) {
			case M:
				fileSizeExceed = fileSize / 1024 / 1024 < maxSize;
				break;
			case K:
				fileSizeExceed = fileSize / 1024 < maxSize;
				break;
			case B:
				fileSizeExceed = fileSize < maxSize;
				break;
			default:
				break;
			}
		} catch (FileNotFoundException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		} catch (IOException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();  
				}
			}
		}
		return fileSizeExceed;
	}

	/**
	 * 扫描并上传日志文件
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-10-31 下午12:17:41
	 */
	public static void searchAndUploadFiles(final Context mContext) {
		if (isReportFileCode(mContext)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					searchFiles(mContext);
				}
			}).start();
		}

	}

	public static void searchFiles(Context mContext) {
		File path = new File(FileTools.getSdCardFilePath() + LOG_FILE);
		File[] files = path.listFiles();
		if (files != null && files.length > 0) {
			ArrayList<File> fileLogArray = new ArrayList<File>();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile() && (file.getName().startsWith("YZX_log")
						|| file.getName().startsWith("YZX_trace"))) {
					fileLogArray.add(file);
				}
			}
			
			CustomLog.v("EX_FILE_SIZE:" + fileLogArray.size());
			CustomLog.v("UPLOAD_FILE:" + SharedPreferencesUtils.isLogReportEnable(mContext));
			if(SharedPreferencesUtils.isLogReportEnable(mContext)){
				for(File file:fileLogArray){
					String uploadFileResponse = uploadFile(file, getUploadUrl(2, mContext));
					CustomLog.v("EXCEPTION_UPLOAD_RESPONSE:" + uploadFileResponse);
					if(uploadFileResponse != null && uploadFileResponse.length() > 0){
						try {
							JSONObject json = new JSONObject(uploadFileResponse);
							if(json.has("code") && json.getInt("code") == 0){
								file.delete();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private static String getUploadUrl(int type, Context mContext) {
		if (type == 1) {
			//上传通话emodel值
			return DefinitionAction.REPORT_URL+"/ulog/log?event=quality&uid="
					+ UserData.getClientNumber(mContext);
		} else {
			//上传文件
			return DefinitionAction.REPORT_URL+"/ulog/log?event=logfile&uid="
					+ UserData.getClientNumber(mContext);
		}
	}

	/**
	 * 上传通话质量JSON
	 * @param mContext
	 * @param json
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-11-6 上午11:26:15
	 */
	public static void uploadJson(final Context mContext,final String json){
		new Thread(new Runnable() {
			@Override
			public void run() {
				CustomLog.v("UPLOAD_RUL:" + getUploadUrl(1, mContext));
				JSONObject resultJson = HttpTools.doPostMethod(mContext, getUploadUrl(1, mContext), json.replace(" ", ""));
				CustomLog.v("UPLOAD_JSON_RESPONSE:" + (resultJson != null ? resultJson.toString() : ""));
			}
		}).start();
	}

	
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	public static String uploadFile(File file, String requestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				CustomLog.v("response code:" + res);
				if (res == 200) {
					CustomLog.v("request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					CustomLog.v("result : " + result);
				} else {
					CustomLog.v("request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static final String UPLOAD_FILE_TIME = "UPLOAD_FILE_TIME";

	public static boolean isReportFileCode(Context mContext) {
		return ((23 * 60 * 60 * 1000) + mContext.getSharedPreferences(
				DefinitionAction.PREFERENCE_NAME, 0).getLong(UPLOAD_FILE_TIME,
				0)) < System.currentTimeMillis();
	}

	public static void saveReportFileCode(Context mContext) {
		mContext.getSharedPreferences(DefinitionAction.PREFERENCE_NAME, 0)
				.edit().putLong(UPLOAD_FILE_TIME, System.currentTimeMillis())
				.commit();
	}

	public static String uploadCrashFile(Context context, String filePath,
			String requestURL) {
		File file = new File(filePath);
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			conn.setRequestProperty("Accept", 
					"text/plain, */*; q=0.01");
			if (file != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				String time = format.format(new Date(System.currentTimeMillis()));
				String fileName = file.getName();
				fileName = fileName.substring(0, fileName.lastIndexOf("_")+1)+time+".txt";
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				createhtteFormData(sb, "uid", null, UserData.getUserId(context),BOUNDARY);
				createhtteFormData(sb, "app_id", null,
						UserData.getAppid(context),BOUNDARY);
				createhtteFormData(sb, "pv", null, "android",BOUNDARY);
				createhtteFormData(sb, "ver", null,
						UserData.getVersionName(context),BOUNDARY);
				createhtteFormData(sb, "osv", null,
						android.os.Build.VERSION.RELEASE,BOUNDARY);
				createhtteFormData(sb, "brand", null, android.os.Build.MODEL,BOUNDARY);
				createhtteFormData(sb, "model", null, android.os.Build.MODEL,BOUNDARY);
				createhtteFormData(sb, "file", fileName, null,BOUNDARY);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
//				CustomLog.d("response code:" + res);
				if (res == 200) {
//					CustomLog.d("request success");
					is = conn.getInputStream();
					ByteArrayOutputStream  baos = new ByteArrayOutputStream();
					len = -1;
					byte[] buf = new byte[128];
					while ((len = is.read(buf)) != -1)
					{
						baos.write(buf, 0, len);
					}
					baos.flush();
					result = baos.toString();
//					CustomLog.d("result : " + result);
				} else {
//					CustomLog.d("request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @author zhangbin
	 * @2016-1-27
	 * @param sb  内容缓存
	 * @param name
	 * @param filename
	 * @param content
	 *            表单内容
	 * @return
	 * @descript:创建表单项
	 */
	private static StringBuffer createhtteFormData(StringBuffer sb,
			String name, String filename, String content, String BOUNDARY) {
		String PREFIX = "--", LINE_END = "\r\n";
		
		sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
		if (TextUtils.isEmpty(filename)) {
			sb.append("Content-Disposition: form-data; name=\"");
			sb.append(name).append("\"").append(LINE_END);
		} else {
			sb.append("Content-Disposition: form-data; name=\"" + name
					+ "\"; filename=\"" + filename + "\"" + LINE_END);
		}
		
		if (!TextUtils.isEmpty(content)) {
			sb.append(LINE_END).append(content).append(LINE_END);
			//append(URLEncoder.encode(content, "UTF-8"))
		}
				
		return sb;
	}

	public static String uploadSDKReport(String log, String requestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		
		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", 
					"text/html,application/xhtml+xml," +
					"application/xml;q=0.9,image/webp,*/*;q=0.8");

			DataOutputStream dos = new DataOutputStream(
					conn.getOutputStream());
			StringBuffer sb = new StringBuffer();
			createhtteFormData(sb, "log", null, log,BOUNDARY);
			dos.write(sb.toString().getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
					.getBytes();
			dos.write(end_data);
			dos.flush();
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
//			CustomLog.d("response code:" + res);
			if (res == 200) {
//				CustomLog.d("request success");
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream  baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];
				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				result = baos.toString();
//				CustomLog.d("result : " + result);
			} else {
//				CustomLog.d("request error");
			}
		
		} catch (MalformedURLException e) {
			CustomLog.v("uploadSDKReport MalformedURLException:" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			CustomLog.v("uploadSDKReport IOException:" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
}
