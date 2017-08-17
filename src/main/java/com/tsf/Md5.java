package com.tsf;

import java.security.MessageDigest;

public class Md5 {

	public static byte[] toBytes(String msg) {
		try {
			byte[] result = msg.getBytes("utf-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(result);
		} catch (Exception e) {
		}
		return null;
	}

	public static String byte2hex(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int n = 0; n < b.length; n++) {
			String s1 = Integer.toHexString(b[n] & 0x0f);
			String s2 = Integer.toHexString(b[n] >> 4 & 0x0f);
			sb.append(s2);
			sb.append(s1);
		}
		return sb.toString().toUpperCase();
	}
}
