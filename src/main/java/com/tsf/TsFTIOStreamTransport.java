package com.tsf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the most commonly used base transport. It takes an InputStream and an
 * OutputStream and uses those to perform all transport operations. This allows
 * for compatibility with all the nice constructs Java already has to provide a
 * variety of types of streams.
 *
 */
public class TsFTIOStreamTransport extends TTransport {

	private static final Logger LOGGER = LoggerFactory.getLogger(TsFTIOStreamTransport.class.getName());

	/** Underlying inputStream */
	protected InputStream inputStream_;

	/** Underlying outputStream */
	protected ByteArrayOutputStream outputStream_ = new ByteArrayOutputStream();

	/**
	 * Subclasses can invoke the default constructor and then assign the input
	 * streams in the open method.
	 */
	RegisterClient rc;
	byte[] serviceid;

	private TsFTIOStreamTransport(RegisterClient rc, byte[] serviceid) {
		this.rc = rc;
		this.serviceid = serviceid;
	}

	public static TsFTIOStreamTransport newInstance(RegisterClient rc, byte[] serviceid) {
		return new TsFTIOStreamTransport(rc, serviceid);
	}

	/**
	 * The streams must already be open at construction time, so this should always
	 * return true.
	 *
	 * @return true
	 */
	public boolean isOpen() {
		return true;
	}

	/**
	 * The streams must already be open. This method does nothing.
	 */
	public void open() throws TTransportException {
	}

	/**
	 * Closes both the input and output streams.
	 */
	public void close() {
		if (inputStream_ != null) {
			try {
				inputStream_.close();
			} catch (IOException iox) {
				LOGGER.warn("Error closing input stream.", iox);
			}
			inputStream_ = null;
		}
		if (outputStream_ != null) {
			try {
				outputStream_.close();
			} catch (IOException iox) {
				LOGGER.warn("Error closing output stream.", iox);
			}
			outputStream_ = null;
		}
		if (rc != null) {
			rc.close();
		}
	}

	/**
	 * Reads from the underlying input stream if not null.
	 */
	public int read(byte[] buf, int off, int len) throws TTransportException {
		if (inputStream_ == null) {
			try {
				Packet p = Packet.RESISTER;
				p.setSeqId(System.nanoTime()).setBody(outputStream_.toByteArray()).setServiceid(serviceid);
				inputStream_ = new ByteArrayInputStream(rc.register(p));
			} catch (Exception e) {
				throw new TTransportException(e);
			}

		}
		int bytesRead;
		try {
			bytesRead = inputStream_.read(buf, off, len);
		} catch (IOException iox) {
			throw new TTransportException(TTransportException.UNKNOWN, iox);
		}
		if (bytesRead < 0) {
			throw new TTransportException(TTransportException.END_OF_FILE);
		}
		outputStream_ = new ByteArrayOutputStream();
		return bytesRead;
	}

	/**
	 * Writes to the underlying output stream if not null.
	 */
	public void write(byte[] buf, int off, int len) throws TTransportException {
		if (outputStream_ == null) {
			throw new TTransportException(TTransportException.NOT_OPEN, "Cannot write to null outputStream");
		}
		if (inputStream_ != null) {
			inputStream_ = null;
		}
		outputStream_.write(buf, off, len);
	}

	/**
	 * Flushes the underlying output stream if not null.
	 */
	public void flush() throws TTransportException {
		if (outputStream_ == null) {
			throw new TTransportException(TTransportException.NOT_OPEN, "Cannot flush null outputStream");
		}
		try {
			outputStream_.flush();
		} catch (IOException iox) {
			throw new TTransportException(TTransportException.UNKNOWN, iox);
		}
	}
}
