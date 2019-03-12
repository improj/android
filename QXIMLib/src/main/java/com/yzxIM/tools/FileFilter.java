package com.yzxIM.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.yzxtcp.tools.CustomLog;
/**
 * 文件类型过滤
 * @author zhuqian
 */
public class FileFilter {

	//支持的图片文件格式
	private Map<String,String> imageFiles;
	//支持的语音文件格式
	private Map<String,String> voiceFiles;
	private String path;
	//图片格式化
	public static final int IMAGE_FORMAT = 0;
	//语音格式化
	public static final int VOICE_FORMAT = 1;
	public FileFilter(String path){
		this.path = path;
		imageFiles = new HashMap<String, String>();
		imageFiles.put("FFD8FF", "jpg/jpeg");
		imageFiles.put("89504E", "jpg/jpeg");
		imageFiles.put("89504E47", "png");
		
		voiceFiles = new HashMap<String, String>();
		voiceFiles.put("232141", "arm");
	}
	/**
	 * 格式化文件，判断sdk是否支持该文件
	 * @param formatStyle IMAGE_FORMAT or VOICE_FORMAT
	 * @return 是否支持该文件
	 */
	public boolean format(int formatStyle){
		FileInputStream is = null;
        String format = null;
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[3];  
            is.read(b, 0, b.length);
            format = bytesToHexString(b);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        if(TextUtils.isEmpty(format)){
        	return false;
        }
        CustomLog.e("file format code = "+format);
        if(formatStyle == IMAGE_FORMAT
        		&& !TextUtils.isEmpty(formatImage(format))){
        	return true;
        }else if(formatStyle == VOICE_FORMAT
        		&& !TextUtils.isEmpty(formatVoice(format))){
        	return true;
        }else{
        	return false;
        }
	}
	/**
	 * 格式化图片
	 * @param key
	 * @return
	 */
	private String formatVoice(String key){
		if(voiceFiles.containsKey(key)){
    		CustomLog.e("voice format = "+voiceFiles.get(key));
    		return voiceFiles.get(key);
    	}
		return "";
	}
	
	/**
	 * 格式化语音
	 * @param key
	 * @return
	 */
	private String formatImage(String key){
		if(imageFiles.containsKey(key)){
    		CustomLog.e("image format = "+imageFiles.get(key));
    		return imageFiles.get(key);
    	}
		return "";
	}
	/**
	 * 从十进制到16进制转换
	 * @param src
	 * @return
	 */
	private static String bytesToHexString(byte[] src){     
        StringBuilder builder = new StringBuilder();     
        if (src == null || src.length <= 0) {     
            return null;     
        }
        String hv;
        for (int i = 0; i < src.length; i++) {     
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();     
            if (hv.length() < 2) {     
                builder.append(0);     
            }     
            builder.append(hv);
        }
        return builder.toString();     
    }
}
