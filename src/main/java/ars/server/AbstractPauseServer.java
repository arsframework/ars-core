package ars.server;

import ars.server.Servers;
import ars.server.PauseServer;
import ars.server.AbstractServer;

/**
 * 可暂停服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractPauseServer extends AbstractServer implements PauseServer {
	private volatile boolean paused;
	private volatile boolean pausing;
	private byte[] lock = new byte[0];

	public AbstractPauseServer() {
		this(true);
	}

	public AbstractPauseServer(boolean pausing) {
		this.pausing = pausing;
	}

	/**
	 * 执行服务
	 * 
	 * @throws Exception
	 *             操作异常
	 */
	protected abstract void execute() throws Exception;

	@Override
	public void pause() {
		this.pause(false);
	}

	@Override
	public void pause(boolean block) {
		if (this.isAlive() && !this.pausing) {
			this.pausing = true;
			if (block) {
				this.getThread().interrupt();
				while (!this.paused) {
				}
			}
		}
	}

	@Override
	public void restore() {
		if (this.isAlive() && this.pausing) {
			synchronized (this.lock) {
				this.lock.notify();
			}
			this.pausing = false;
		}
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}

	@Override
	public final void run() {
		while (this.isAlive()) {
			if (this.pausing) {
				synchronized (this.lock) {
					paused = true;
					try {
						this.lock.wait();
					} catch (InterruptedException e) {
					}
					paused = false;
				}
			}
			if (this.isAlive()) {
				try {
					this.execute();
				} catch (Exception e) {
					Servers.logger.error("Server execute failed", e);
				}
			}
		}
	}

	@Override
	protected void initialize() {

	}

	@Override
	protected void destroy() {

	}

}
