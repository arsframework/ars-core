package ars.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.Trigger;
import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;
import org.quartz.SchedulerException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.StdSchedulerFactory;

import ars.util.AbstractServer;

/**
 * 计划任务服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractTaskServer extends AbstractServer {
	private Scheduler scheduler;
	private String expression;
	private boolean concurrent;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 任务处理类（如果为内部类则必须是公共的静态内部类）
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static final class JobHandler implements Job {

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			AbstractTaskServer server = (AbstractTaskServer) data.get("server");
			try {
				if ((Boolean) data.get("concurrent")) {
					server.execute();
				} else {
					synchronized (JobHandler.class) {
						server.execute();
					}
				}
			} catch (Exception e) {
				server.logger.error("Task execute failed", e);
			}
		}

	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
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
		try {
			JobDetail detail = JobBuilder.newJob(JobHandler.class).build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(this.expression)).build();
			JobDataMap data = detail.getJobDataMap();
			data.put("server", this);
			data.put("concurrent", this.concurrent);
			this.scheduler = StdSchedulerFactory.getDefaultScheduler();
			this.scheduler.scheduleJob(detail, trigger);
			this.scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void stop() {
		if (this.scheduler != null) {
			synchronized (this) {
				if (this.scheduler != null) {
					try {
						this.scheduler.shutdown();
					} catch (SchedulerException e) {
						this.logger.error("Task shutdown failed", e);
					} finally {
						this.scheduler = null;
					}
				}
			}
		}
		super.stop();
	}

}
