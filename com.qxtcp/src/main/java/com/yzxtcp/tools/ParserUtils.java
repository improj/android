package com.yzxtcp.tools;

/**
 * 基本数据类型之间的转换
 * 
 * @author zhuqian
 */
public class ParserUtils {

	/**
	 * int转换为byte[],高位在前
	 * @param src 数据源
	 * @return
	 */
	public static byte[] intToByte(int src) {

		byte[] result = new byte[4];
		for(int i=0;i<result.length;i++){
			result[i] = (byte)((src >>> 8 * (result.length-1-i)) & 0xff);//说明一  
		}
		return result;
	}
	/**
	 * short转换为byte[],高位在前
	 * @param src 数据源
	 * @return
	 */
	public static byte[] shortToByte(short src) {
		byte[] result = new byte[2];
		for(int i=0;i<result.length;i++){
			result[i] = (byte)((src >>> 8 * (result.length-1-i)) & 0xff);//说明一  
		}
		return result;
	}
	
	/**
	 * short转换为byte[],高位在前
	 * @param src 数据源
	 * @return
	 */
	public static short byteToShort(byte[] src) {
		return (short) ((src[0] << 8)+src[1]);
	}
}
