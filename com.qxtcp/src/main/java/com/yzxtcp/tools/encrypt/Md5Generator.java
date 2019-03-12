package com.yzxtcp.tools.encrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* @Description Md5生成器
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-8 上午10:37:35 
*/
public class Md5Generator {

	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters

	public static String generate(String src) {
		byte[] md5 = getMD5(src.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(RADIX);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}
	
}
