package com.tsf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @File:tsf: com.tsf :TsfSocket.java
 * @Date:2017年8月6日
 * @Copyright (c) 2017, donnie4w@gmail.com All Rights Reserved.
 * @Author: dong
 * @Desc:
 */
public class TsfSocket {
	private static final Logger logger = Logger.getLogger();
	Socket socket_;

	String host_;
	int port_;
	volatile AtomicInteger i = new AtomicInteger(0);
	boolean isClosed;

	/**
	 * Socket timeout - read timeout on the socket
	 */
	private int socketTimeout_ = 30000;

	/**
	 * Connection timeout
	 */
	private int connectTimeout_ = 30000;

	/**
	 * Creates a new unconnected socket that will connect to the given host on the
	 * given port.
	 *
	 * @param host
	 *            Remote host
	 * @param port
	 *            Remote port
	 */
	public TsfSocket(String host, int port) {
		this(host, port, 0);
	}

	/**
	 * @param host
	 *            Remote host
	 * @param port
	 *            Remote port
	 * @param timeout
	 *            Socket timeout and connection timeout
	 */
	public TsfSocket(String host, int port, int timeout) {
		this(host, port, timeout, timeout);
	}

	/**
	 * @param host
	 *            Remote host
	 * @param port
	 *            Remote port
	 * @param socketTimeout
	 *            Socket timeout
	 * @param connectTimeout
	 *            Connection timeout
	 */
	public TsfSocket(String host, int port, int socketTimeout, int connectTimeout) {
		host_ = host;
		port_ = port;
		socketTimeout_ = socketTimeout;
		connectTimeout_ = connectTimeout;
		initSocket();
	}

	/**
	 * Initializes the socket object
	 */
	private void initSocket() {
		socket_ = new Socket();
		try {
			socket_.setSoLinger(false, 0);
			socket_.setTcpNoDelay(true);
			socket_.setKeepAlive(true);
			// socket_.setSoTimeout(socketTimeout_);
		} catch (Exception e) {
			logger.severe("initSocket errer.", e);
		}
	}

	public void open() throws TsfException {
		if (socket_ == null)
			initSocket();

		if (host_ == null || host_.length() == 0)
			throw new TsfException("Cannot open null host.");

		if (port_ <= 0 || port_ > 65535)
			throw new TsfException("Invalid port " + port_);

		try {
			socket_.connect(new InetSocketAddress(host_, port_), connectTimeout_);
		} catch (Exception e) {
			throw new TsfException(e);
		} finally {
		}

	}

	public void close() {
		try {
			if (socket_ != null)
				socket_.close();
		} catch (Exception e) {
			logger.severe("tsf close err.", e);
		} finally {
			this.isClosed = true;
		}
	}

	public void setTimeout(int timeout) {
		this.setConnectTimeout(timeout);
		this.setSocketTimeout(timeout);
	}

	public void setSocketTimeout(int socketTimeout_) {
		this.socketTimeout_ = socketTimeout_;
	}

	public void setConnectTimeout(int connectTimeout_) {
		this.connectTimeout_ = connectTimeout_;
	}

	public byte[] readPacket() throws TsfException {
		try {
			return _readPacket(socket_.getInputStream());
		} catch (Exception e) {
			logger.severe("read handler err.", e.getMessage());
			close();
			throw new TsfException("read handler err.", e);
		}
	}

	private byte[] _readPacket(InputStream is) throws TsfException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] bs = new byte[4];
			int n = is.read(bs);
			if (n != 4) {
				throw new TsfException("packet head err.");
			}
			int length = ByteUtil.bytes2int(bs);
			bs = new byte[length];
			int i = -1;
			while ((i = is.read(bs)) != -1) {
				baos.write(bs, 0, i);
				bs = new byte[length - baos.size()];
				if (baos.size() == length) {
					break;
				}
			}
			return baos.toByteArray();
		} catch (Exception e) {
			logger.severe("read err.", e);
			throw new TsfException("socket read err.", e);
		} finally {
			try {
				baos.close();
			} catch (Exception e) {
			}
		}
	}

	public void writePacket(Packet p) throws TsfException {
		try {
			OutputStream os = socket_.getOutputStream();
			os.write(p.getPacket());
			os.flush();
		} catch (Exception e) {
			logger.severe("read handler err.", e.getMessage());
			close();
			throw new TsfException("read handler err.", e);
		}
	}

}
