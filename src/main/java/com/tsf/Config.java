package com.tsf;

public class Config {
	public final static String ENCODE = "utf-8";
	
	private String host = "127.0.0.1";
	private int port = 7373;
	private int socketTimeout_ = 30000;
	private int connectTimeout_ = 30000;
	private String auth = "tsf_auth";

	private int heartbeatTime = 30000;

	public Config(String host, int port, String auth) {
		this.host = host;
		this.port = port;
		this.auth = auth;
	}

	public Config(String host, int port, int socketTimeout_, int connectTimeout_, String auth) {
		this.host = host;
		this.port = port;
		this.socketTimeout_ = socketTimeout_;
		this.connectTimeout_ = connectTimeout_;
		this.auth = auth;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSocketTimeout_() {
		return socketTimeout_;
	}

	public void setSocketTimeout_(int socketTimeout_) {
		this.socketTimeout_ = socketTimeout_;
	}

	public int getConnectTimeout_() {
		return connectTimeout_;
	}

	public void setConnectTimeout_(int connectTimeout_) {
		this.connectTimeout_ = connectTimeout_;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public int getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(int heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}
}
