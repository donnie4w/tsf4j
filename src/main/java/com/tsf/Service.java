package com.tsf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.util.Arrays;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

/**
 * <p>
 * 
 * @File:tsf: com.tsf.client :Client.java
 *            <p>
 * @Date:2017年8月3日
 *                 <p>
 * @Copyright (c) 2017, donnie4w@gmail.com All Rights Reserved.
 *            <p>
 * @Author: dong
 *          <p>
 * @Desc:
 */
public class Service {
	private static final Logger logger = Logger.getLogger();
	protected static TProtocolFactory inProtocolFactoryTCompact = new TCompactProtocol.Factory();
	protected static TProtocolFactory outProtocolFactoryTCompact = new TCompactProtocol.Factory();

	protected static TProtocolFactory inProtocolFactory = new TBinaryProtocol.Factory();
	protected static TProtocolFactory outProtocolFactory = new TBinaryProtocol.Factory();

	protected static TProtocolFactory inProtocolFactoryJSon = new TJSONProtocol.Factory();
	protected static TProtocolFactory outProtocolFactoryJSon = new TJSONProtocol.Factory();
	TProcessor processor = null;

	protected Service(TProcessor processor) {
		this.processor = processor;
	}

	protected byte[] service(byte[] inbytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 提供 outputStream
		ByteArrayInputStream in = new ByteArrayInputStream(inbytes);
		try {
			TTransport transport = new TIOStreamTransport(in, baos);
			TProtocol inProtocol = inProtocolFactoryTCompact.getProtocol(transport);
			TProtocol outProtocol = outProtocolFactoryTCompact.getProtocol(transport); //
			processor.process(inProtocol, outProtocol);
			transport.flush();
			byte[] outbytes = baos.toByteArray();
			System.out.println("2----->" + Arrays.toString(outbytes));
			return outbytes;
		} catch (Exception e) {
			logger.severe("compactService:", e);
		} finally {
			closeStream(in);
			closeStream(baos);
		}
		return null;
	}

	private static void closeStream(Closeable io) {
		try {
			if (io != null) {
				io.close();
			}
		} catch (Exception e) {
			logger.severe("closeStream", e);
		}
	}

}