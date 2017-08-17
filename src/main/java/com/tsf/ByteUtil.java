package com.tsf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * <p>@File:tsf4j: com.tsf :ByteUtil.java
 * <p>@Date:2017年8月10日
 * <p>@Copyright (c) 2017, donnie4w@gmail.com All Rights Reserved.
 * <p>@Author: dong
 * <p>@Desc:
 */
public class ByteUtil {
	public static byte[] long2bytes(long row) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(row);
		return bb.array();
	}

	public static long bytes2long(byte[] bs) {
		ByteBuffer bb = ByteBuffer.wrap(bs);
		return bb.getLong();
	}

	public static byte[] int2bytes(int row) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(row);
		return bb.array();
	}

	public static int bytes2int(byte[] bs) {
		ByteBuffer bb = ByteBuffer.wrap(bs);
		return bb.getInt();
	}

	public static byte[] short2bytes(short value) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.putShort(value);
		return bb.array();
	}

	public static short bytes2short(byte[] bs) {
		ByteBuffer bb = ByteBuffer.wrap(bs);
		return bb.getShort();
	}

	public static void main(String[] args) {
		System.out.println(bytes2long(ByteUtil.long2bytes(Long.MAX_VALUE)));
		System.out.println(bytes2int(ByteUtil.int2bytes(Integer.MAX_VALUE)));
		System.out.println(bytes2short(short2bytes(Short.MAX_VALUE)));
	}
}
