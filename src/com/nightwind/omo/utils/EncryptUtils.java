package com.nightwind.omo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
	
	public static String str2MD5(String str) {
		String ciphertext = null;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b: bytes) {
				int bt = b & 0xff;
				if (bt <= 0xf) {
					sb.append(0);
				} 
				sb.append(Integer.toHexString(bt));
			}
			ciphertext = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return ciphertext;
	}

}
