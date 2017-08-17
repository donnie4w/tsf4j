package com.tsf;

public class RegisterClient {
	private static final Logger logger = Logger.getLogger();
	TsfSocket tsfsoket;
	Config config;
	boolean isClosed = false;

	private RegisterClient(Config config) throws TsfException {
		this.config = config;
		open(config);
	}

	public static RegisterClient newInstance(Config config) throws TsfException {
		return new RegisterClient(config);
	}

	public byte[] register(Packet p) throws TsfException {
		this.tsfsoket.writePacket(p);
		boolean b = true;
		while (b) {
			p = Packet.wrap(this.tsfsoket.readPacket());
			System.out.println("p====>" + p.toString());
			try {
				switch (p) {
				case PING:
					logger.info("ping:", p.toString());
					tsfsoket.writePacket(Packet.ACKPING);
					break;
				case RESISTER:
					break;
				case ACKSERVICEID:
					logger.info("ack serviceid:", new String(p.getBody(), Config.ENCODE));
					break;
				case ACKRESISTER:
					logger.info("ack resister:", new String(p.getBody(), Config.ENCODE));
					b = false;
					break;
				case ACKPING:
					logger.info("ack ping:", new String(p.getBody(), Config.ENCODE));
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
				break;
			}
		}
		return p.getBody();

	}

	void open(Config config) throws TsfException {
		try {
			tsfsoket = new TsfSocket(config.getHost(), config.getPort(), config.getSocketTimeout_(),
					config.getConnectTimeout_());
			tsfsoket.open();
			handler();
		} catch (Exception e) {
			logger.severe("open", e);
			throw new TsfException(e);
		}
	}

	void handler() throws TsfException {
		auth();
		// new Thread(new HeartBeat(this)).start();
	}

	private void auth() throws TsfException {
		try {
			Packet auth = Packet.AUTH;
			auth.body = this.config.getAuth().getBytes(Config.ENCODE);
			tsfsoket.writePacket(auth);
			Packet p = Packet.wrap(this.tsfsoket.readPacket());
			logger.info("auth ack : ", p.toString());
		} catch (Exception e) {
			logger.severe("auth err.", e);
			throw new TsfException(e);
		}
	}

	class HeartBeat implements Runnable {
		RegisterClient c;

		public HeartBeat(RegisterClient c) {
			this.c = c;
		}

		public void run() {
			while (!c.isClosed && !c.tsfsoket.isClosed) {
				try {
					c.tsfsoket.writePacket(Packet.PING);
				} catch (Exception e) {
					logger.severe("heartBeat.", e);
				}
				Sleep(30000);
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