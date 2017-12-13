package ars.server.task;

import org.quartz.Job;
import org.quartz.Trigger;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ars.server.Servers;
import ars.server.AbstractServer;

/**
 * 计划任务服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractTaskServer extends AbstractServer {
	private String expression;
	private boolean concurrent;

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
				throw new JobExecutionException(e);
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
	protected void initialize() {
		if (this.expression == null) {
			throw new RuntimeException("Expression has not been initialize");
		}
		try {
			JobDetail detail = JobBuilder.newJob(JobHandler.class).build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(this.expression)).build();
			JobDataMap data = detail.getJobDataMap();
			data.put("server", this);
			data.put("concurrent", this.concurrent);
			Servers.getDefaultScheduler().scheduleJob(detail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final void run() {

	}

	@Override
	protected final void destroy() {

	}

}
