package com.tsf;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @File:tsf4j: com.tsf :Packet.java
 * @Date:2017年8月8日
 * @Copyright (c) 2017, donnie4w@gmail.com All Rights Reserved.
 * @Author: dong
 * @Desc:
 */
public enum Packet {
	ERR((short) 0), AUTH((short) 1), SERVICE((short) 2), PING((short) 3), RESISTER((short) 4), ACKSERVICEID(
			(short) 5), ACKRESISTER((short) 6), ACKPING((short) 7);

	short type;

	byte[] body = new byte[] { 0 };

	long seqId;

	byte[] serviceid = new byte[16];

	public String toString() {
		return super.toString() + " | " + type + " | " + Arrays.toString(body) + " | " + seqId + " | "
				+ Arrays.toString(serviceid);
	}

	Packet(short i) {
		this.type = i;
	}

	public static Packet wrap(byte[] bs) {
		byte[] typebs = new byte[2];
		System.arraycopy(bs, 0, typebs, 0, 2);
		Packet p = packet(typebs);
		byte[] seqbs = new byte[8];
		System.arraycopy(bs, 2, seqbs, 0, 8);
		p.seqId = ByteUtil.bytes2long(seqbs);
		System.arraycopy(bs, 10, p.serviceid, 0, 16);
		p.body = new byte[bs.length - 26];
		System.arraycopy(bs, 26, p.body, 0, bs.length - 26);
		return p;
	}

	public Packet setBody(byte[] bs) {
		body = bs;
		return this;
	}

	public long getSeqId() {
		return seqId;
	}

	public Packet setSeqId(long seqId) {
		this.seqId = seqId;
		return this;
	}

	public byte[] getPacket() {
		ByteBuffer bb = ByteBuffer.allocate(4 + 2 + 8 + serviceid.length + body.length);
		bb.put(ByteUtil.int2bytes(serviceid.length + body.length + 10));
		bb.put(ByteUtil.short2bytes(type));
		bb.put(ByteUtil.long2bytes(seqId));
		bb.put(serviceid);
		bb.put(body);
		return bb.array();
	}

	public byte[] getBody() {
		return body;
	}

	public byte[] getServiceid() {
		return serviceid;
	}

	public Packet setServiceid(byte[] serviceid) {
		this.serviceid = serviceid;
		return this;
	}

	public static Packet packet(byte[] bs) {
		return packet(ByteUtil.bytes2short(bs));
	}

	public static Packet packet(short i) {
		switch (i) {
		case 1:
			return AUTH;
		case 2:
			return SERVICE;
		case 3:
			return PING;
		case 4:
			return RESISTER;
		case 5:
			return ACKSERVICEID;
		case 6:
			return ACKRESISTER;
		case 7:
			return ACKPING;
		default:
			return ERR;
		}
	}

}
