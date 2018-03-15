package ars.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Server;
import ars.util.Servers;

/**
 * 系统后台服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractServer implements Server, Runnable {
	private Thread thread;
	private boolean daemon;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	@Override
	public void start() {
		if (this.thread == null) {
			synchronized (this) {
				if (this.thread == null) {
					this.thread = new Thread(this);
					this.thread.setDaemon(this.daemon);
					this.thread.start();
					Servers.register(this);
				}
			}
		}
	}

	@Override
	public void stop() {
		if (this.thread != null) {
			synchronized (this) {
				if (this.thread != null) {
					this.thread.interrupt();
					this.thread = null;
					Servers.unregister(this);
				}
			}
		}
	}

	@Override
	public boolean isAlive() {
		return this.thread != null && this.thread.isAlive();
	}

}
