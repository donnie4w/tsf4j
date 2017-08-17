package com.tsf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Pool {
	static Map<byte[], ServiceClient> serviceMap = new ConcurrentHashMap<byte[], ServiceClient>();
}
