package com.tsf;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TProcessor;

public class ServiceClient {
	private static final Logger logger = Logger.getLogger();
	TsfSocket tsfsoket;
	Config config;
	Service service;
	boolean isClosed = false;
	volatile AtomicInteger version = new AtomicInteger(1);

	private ServiceClient(Config config, TProcessor processor) throws TsfException {
		this.config = config;
		open(config);
		service = new Service(processor);
	}

	public static ServiceClient newInstance(Config config, TProcessor processor) throws TsfException {
		return new ServiceClient(config, processor);
	}

	public byte[] register(Packet p) throws TsfException {
		this.tsfsoket.writePacket(p);
		p = Packet.wrap(this.tsfsoket.readPacket());
		return p.getBody();
	}

	public synchronized void reOpen() {
		if (!this.isClosed && this.tsfsoket.isClosed) {
			try {
				open(this.config);
			} catch (Exception e) {
			}
		}
	}

	void open(Config config) throws TsfException {
		try {
			tsfsoket = new TsfSocket(config.getHost(), config.getPort(), config.getSocketTimeout_(),
					config.getConnectTimeout_());
			tsfsoket.open();
		} catch (Exception e) {
			logger.severe("open err.", e.getMessage());
		}
		handler();
	}

	synchronized void handler() throws TsfException {
		auth();
		logger.info("re open version:", version.incrementAndGet());
		new Thread(new HeartBeat(this)).start();
		new Thread(new Handler(this)).start();
	}

	private void auth() throws TsfException {
		try {
			Packet auth = Packet.AUTH;
			auth.body = this.config.getAuth().getBytes(Config.ENCODE);
			tsfsoket.writePacket(auth);
		} catch (Exception e) {
			this.tsfsoket.close();
			throw new TsfException(e);
		}
	}

	private void initPool() throws TsfException {
		for (byte[] bs : Pool.serviceMap.keySet()) {
			service(bs, Pool.serviceMap.get(bs));
		}
	}

	public static void service(byte[] serviceid, ServiceClient client) throws TsfException {
		Packet packet = Packet.SERVICE;
		packet.serviceid = serviceid;
		packet.body = serviceid;
		client.tsfsoket.writePacket(packet);
		Pool.serviceMap.put(serviceid, client);
	}

	class HeartBeat implements Runnable {
		ServiceClient c;
		TsfSocket tsfsoket;
		int version = 1;

		public HeartBeat(ServiceClient c) {
			this.c = c;
			tsfsoket = c.tsfsoket;
			this.version = c.version.get();
		}

		public void run() {
			int i = 0;
			while (!c.isClosed && version == c.version.get()) {
				// && !tsfsoket.isClosed && version == c.version.get()
				if (i >= 3) {
					try {
						tsfsoket.writePacket(Packet.PING);
						if (tsfsoket.i.getAndIncrement() > 3) {
							throw new Exception("");
						}
					} catch (Exception e) {
						tsfsoket.close();
						synchronized (HeartBeat.class) {
							if (version == c.version.get()) {
								c.reOpen();
							}
						}
					}
					i = 0;
				} else {
					i++;
				}
				Sleep(1000);
			}
		}
	}

	class Handler implements Runnable {
		ServiceClient c;

		public Handler(ServiceClient c) {
			this.c = c;
		}

		public void run() {
			TsfSocket tsfsoket = c.tsfsoket;
			while (!c.isClosed && !c.tsfsoket.isClosed) {
				try {
					Packet p = Packet.wrap(c.tsfsoket.readPacket());
					System.out.println("----->" + p.toString());
					switch (p) {
					case AUTH:
						logger.info("auth:", p.toString());
						initPool();
					case PING:
						logger.info("ping:", p.toString());
						tsfsoket.writePacket(Packet.ACKPING);
						break;
					case RESISTER:
						Packet packet = Packet.ACKRESISTER.setBody(service.service(p.getBody()));
						packet.seqId = p.seqId;
						tsfsoket.writePacket(packet);
						break;
					case ACKSERVICEID:
						logger.info("ack serviceid:", new String(p.getBody(), Config.ENCODE));
						break;
					case ACKRESISTER:
						logger.info("ack resister:", new String(p.getBody(), Config.ENCODE));
						break;
					case ACKPING:
						logger.info("ack ping:", new String(p.getBody(), Config.ENCODE));
						tsfsoket.i.set(0);
						break;
					case SERVICE:
						logger.info("service:", p.serviceid.length + " | " + p.getBody().length);
						break;
					case ERR:
						logger.info("err:", p.serviceid.length + " | " + p.getBody().length);
						break;
					default:
						logger.severe("default:", p.toString());
						break;
					}
				} catch (Exception e) {
					logger.severe("handler err.", e.getMessage());
					tsfsoket.close();
					break;
				}
			}
		}
	}

	public void close() {
		this.isClosed = true;
		this.tsfsoket.close();
	}

	void Sleep(int l) {
		try {
			Thread.sleep(l);
		} catch (Exception e) {
		}
	}
}