package ars.server;

import ars.server.Server;
import ars.server.Servers;

/**
 * 系统后台服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractServer implements Server, Runnable {
	private Thread thread; // 服务执行线程
	private boolean daemon = true;
	private volatile boolean alive; // 服务是否处于活动状态

	public AbstractServer() {
		Servers.register(this);
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	/**
	 * 获取当前服务线程
	 * 
	 * @return 线程对象
	 */
	protected Thread getThread() {
		return this.thread;
	}

	/**
	 * 服务初始化
	 * 
	 */
	protected abstract void initialize();

	/**
	 * 释放服务占用资源
	 */
	protected abstract void destroy();

	@Override
	public final void start() {
		if (!this.alive) {
			synchronized (this) {
				if (!this.alive) {
					try {
						this.initialize();
						this.thread = new Thread(this);
						this.thread.setDaemon(this.daemon);
						this.thread.start();
						this.alive = true;
					} catch (RuntimeException e) {
						this.destroy();
						throw e;
					}
				}
			}
		}
	}

	@Override
	public final void stop() {
		if (this.alive) {
			synchronized (this) {
				if (this.alive) {
					this.alive = false;
					try {
						this.destroy();
					} finally {
						this.thread.interrupt();
					}
				}
			}
		}
	}

	@Override
	public boolean isAlive() {
		return this.alive;
	}

}
