package com.yzxtcp.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.yzxtcp.core.YzxTCPCore;
import com.yzxtcp.data.UserData;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author xiaozhenhua
 *
 */
public class FileTools {
	
	private static final String TAG = "FILETOOLS";
	private static final String AUDIO_PATH = "/voice";
	private static final String PIC_PATH = "/picture";
	private static final String FILE = "/file";
	private static final String LOG_FILE = "/log";
	private static final String IMAGE_FILE = "/image";
	private static final String LOCATION_FILE = "/location";
	

	/**
	 * 创建录音文件
	 * 
	 * @param uid
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午9:53:12
	 */
	public static String createAudioFileName(String uid) {
		File voice_dir = new File(FileTools.getSdCardFilePath() + AUDIO_PATH);
		if (!voice_dir.exists()) {
			voice_dir.mkdirs();
		}
		String path = FileTools.getSdCardFilePath() + AUDIO_PATH+"/"+uid;
		File voice_file = new File(path);
		if (!voice_file.exists()) {
			voice_file.mkdirs();
		}
		path = path+"/"
				+ "voice_" + System.currentTimeMillis()
				+ Math.round(Math.random() * 1000) + ".amr";
		return path;
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
		String path = FileTools.getSdCardFilePath() + PIC_PATH + "/" + uid;
		return path;
	}

	/**
	 * 创建其它附件
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

	/**创建文件夹
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-5-21 上午9:49:22
	 */
	public static void createFolder() {
		File audio_file = new File(FileTools.getSdCardFilePath() + AUDIO_PATH);
		if (!audio_file.exists()) {
			audio_file.mkdirs();
		}
		File pic_file = new File(FileTools.getSdCardFilePath() + PIC_PATH);
		if (!pic_file.exists()) {
			pic_file.mkdirs();
		}
		File other_file = new File(FileTools.getSdCardFilePath() + FILE);
		if (!other_file.exists()) {
			other_file.mkdirs();
		}
		File image_file = new File(FileTools.getSdCardFilePath() + IMAGE_FILE);
		if (!image_file.exists()) {
			image_file.mkdirs();
		}
		File log_file = new File(FileTools.getSdCardFilePath() + LOG_FILE);
		if (!log_file.exists()) {
			log_file.mkdirs();
		}
		File location_file = new File(FileTools.getSdCardFilePath() + LOCATION_FILE);
		if (!location_file.exists()) {
			location_file.mkdirs();
		}
	}

	/**获取SD卡路径
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-9 下午2:39:56
	 */
	private static String getSdCardFilePath() {
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

	/**获取默认SD卡路径
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

	/**获取除默认以外的存储设备
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

	/**判断SD卡或者手机内存是否可用
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

	/**复制单个文件
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
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**文件是否小于100M
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
			stream.close();
		} catch (FileNotFoundException e) {
			isFileSize = false;
			e.printStackTrace();
		} catch (IOException e) {
			isFileSize = false;
			e.printStackTrace();
		}
		return isFileSize;
	}

	/**获取文件在大小，单位K
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
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size + "";
	}

	private static HashMap<String, String> picMap = new HashMap<String, String>();
	static {
		picMap.put("jpg", null);
		picMap.put("jpeg", null);
//		picMap.put("bmp", null);
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
	}

	/**崩溃日志到SD卡
	 * @author: xiongjijian
	 */
	public static void saveExLog(String result,String fileName) {
		if(YzxTCPCore.getContext() == null)
			return ;
		FileWriter writer = null;
		BufferedWriter bw = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = format.format(new Date(System.currentTimeMillis()));
			String path = FileTools.getSdCardFilePath() + LOG_FILE +"/" + YzxTCPCore.getContext().getPackageName();
			String file = fileName + str.substring(0, 10) + ".txt";
			makeRootDirectory(path, file);
			writer = new FileWriter(path+"/"+file, true);
			UserData.saveCrash(path+"/"+file);
			bw = new BufferedWriter(writer);
			bw.newLine();
			bw.write(str);
			bw.newLine();
			bw.write(result);
			bw.newLine();
			bw.write("----------------------------------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**SDK日志保存SD卡，连接测试服务器时开启
	 * 
	 * @author: xiongjijian
	 */
	public static void saveSdkLog(String result,String fileName) {
		if(YzxTCPCore.getContext() == null)
			return ;
		FileWriter writer = null;
		BufferedWriter bw = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
			String str = format.format(new Date(System.currentTimeMillis()));
			String path = FileTools.getSdCardFilePath() + LOG_FILE +"/" + YzxTCPCore.getContext().getPackageName();
			String file = fileName + str.substring(0, 10) + ".txt";
			makeRootDirectory(path, file);
			writer = new FileWriter(path+"/"+file, true);
			bw = new BufferedWriter(writer);
			bw.write(str+": "+result);
			bw.newLine();
		} catch (IOException e) {
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
	
	/** trace日志至SD卡
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

	private static void makeRootDirectory(String filePath, String fileName) {
		try {
			File path = new File(filePath);
			if (!path.exists()) {
				path.mkdirs();
			}
			File file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				//删除该目录下超过10天的旧文件
				deleteOldFiles(filePath, 10);
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
	 * 判断文件是否超出5M大小
	 * @param file
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-10-31 上午10:50:28
	 */
	private static boolean isFileSizeExceed(String file) {
		boolean fileSizeExceed = false;
		FileInputStream fis = null; 
		try {
			fis = new FileInputStream(file);
			fileSizeExceed = ((fis.available() / 1024 / 1024) <= 4);
//			fis.close();
		} catch (FileNotFoundException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		} catch (IOException e) {
			fileSizeExceed = false;
			e.printStackTrace();
		} finally {
			try {
				if(fis != null) {
					fis.close();
					fis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();  
			}

		}
		return fileSizeExceed;
	}
	
	/**
	 * 删除文件夹和文件夹下所有文件
	 * 
	 * @param file
	 * @return
	 */
	 public static void deleteFile(File file) { 
	        if (file.exists() == false) { 
	            return; 
	        } else { 
	            if (file.isFile()) { 
	                boolean isDeleteFile = file.delete();
	                if(!isDeleteFile){
	                	Log.i(TAG, "delete file :"+file.getAbsolutePath()+"，fail...");
	                }
	                return; 
	            } 
	            if (file.isDirectory()) { 
	                File[] childFile = file.listFiles(); 
	                if (childFile == null || childFile.length == 0) { 
	                    file.delete(); 
	                    return; 
	                } 
	                for (File f : childFile) { 
	                    deleteFile(f); 
	                } 
	               boolean isDeleteDir =  file.delete();
	               if(!isDeleteDir){
	                	Log.i(TAG, "delete dir :"+file.getAbsolutePath()+"，fail...");
	                }
	            } 
	       } 
	} 

	 public static boolean isFileExist(String path){
		 File file = new File(path);
		 return file.exists();
	 }
	 
	/**
	 * 删除目录下超过指定天数的旧文件
	 * 
	 * @param filePath 指定的目录
	 * @param day 指定的天数
	 * @return
	 * @author: zhj
	 * @data:2016-08-04
	 */
	private static void deleteOldFiles(String filePath, int day) {
		File path = new File(filePath);
		File files[] = path.listFiles();  
        if(files != null){
            for (File f : files){
                if(! f.isDirectory()){
                	long timeModified=f.lastModified(); 
                	if (System.currentTimeMillis() - timeModified > day * 86400 * 1000) {
                		f.delete();
                	}
                }
            }  
        }  
	}
}
