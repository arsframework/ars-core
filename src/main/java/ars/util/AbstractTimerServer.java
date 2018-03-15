package ars.util;

import java.util.Timer;
import java.util.TimerTask;

import ars.util.AbstractServer;

/**
 * 系统后台定时任务服务抽象类
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractTimerServer extends AbstractServer {
	private Timer timer; // 定时器
	private int interval = 3; // 执行周期（秒）

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		if (interval < 1) {
			throw new IllegalArgumentException("Illegal interval:" + interval);
		}
		this.interval = interval;
	}

	/**
	 * 执行服务
	 * 
	 * @throws Exception
	 *             操作异常
	 */
	protected abstract void execute() throws Exception;

	@Override
	public void run() {
		this.timer = new Timer(true);
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					execute();
				} catch (Exception e) {
					logger.error("Timer execute failed", e);
				}
			}

		}, 0, this.interval * 1000);
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void stop() {
		if (this.timer != null) {
			synchronized (this) {
				if (this.timer != null) {
					this.timer.cancel();
					this.timer = null;
				}
			}
		}
		super.stop();
	}

}