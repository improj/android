package com.yzx.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.yzxtcp.tools.CustomLog;
/**
 * 图片文件类型过滤
 * @author xhb
 */
public class FileFilter {

	//支持的图片文件格式
	private Map<String,String> imageFiles;
	private String path;
	
	public FileFilter(String path){
		this.path = path;
		imageFiles = new HashMap<String, String>();
		imageFiles.put("FFD8FF", "jpg/jpeg");
		imageFiles.put("89504E", "jpg/jpeg");
		imageFiles.put("89504E47", "png");
	}
	/**
	 * 格式化文件，判断sdk是否支持该图片文件
	 * @return 是否支持该文件 true:支持；false：不支持
	 */
	public boolean format(){
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
        CustomLog.d("file format code = "+format);
        if(!TextUtils.isEmpty(formatImage(format))) {
        	return true;
        } else {
        	return false;
        }
	}
	
	/**
	 * 判断图片是否支持
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
