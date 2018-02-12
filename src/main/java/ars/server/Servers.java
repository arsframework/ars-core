package ars.server;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import ars.server.Server;

/**
 * 系统服务工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Servers {
	public static final Logger logger = LoggerFactory.getLogger(Servers.class);

	private static boolean started;
	private static Scheduler scheduler;
	private static ExecutorService executor;
	private static final List<Server> servers = new LinkedList<Server>();

	private Servers() {

	}

	/**
	 * 系统服务注册
	 * 
	 * @param server
	 *            服务对象
	 */
	public static void register(Server server) {
		if (server == null) {
			throw new IllegalArgumentException("Illegal server:" + server);
		}
		servers.add(server);
	}

	/**
	 * 启动所有服务
	 */
	public static void startup() {
		if (!started) {
			synchronized (Servers.class) {
				if (!started) {
					for (Server server : servers) {
						try {
							server.start();
						} catch (Exception e) {
							logger.error("Server start failed", e);
						}
					}
					if (scheduler != null) {
						try {
							scheduler.start();
						} catch (Exception e) {
							logger.error("Scheduler start failed", e);
						}
					}
					started = true;
				}
			}
		}
	}

	/**
	 * 停止所有服务
	 */
	public static void shutdown() {
		if (started) {
			synchronized (Servers.class) {
				if (started) {
					for (Server server : servers) {
						try {
							server.stop();
						} catch (Exception e) {
							logger.error("Server stop failed", e);
						}
					}
					started = false;
				}
			}
		}
		if (scheduler != null) {
			synchronized (Servers.class) {
				if (scheduler != null) {
					try {
						scheduler.shutdown(true);
					} catch (Exception e) {
						logger.error("Scheduler shutdown failed", e);
					} finally {
						scheduler = null;
					}
				}
			}
		}
		if (executor != null) {
			synchronized (Servers.class) {
				if (executor != null) {
					try {
						executor.shutdown();
					} catch (Exception e) {
						logger.error("Executor shutdown failed", e);
					} finally {
						executor = null;
					}
				}
			}
		}
	}

	/**
	 * 获取所有系统服务
	 * 
	 * @return 服务对象列表
	 */
	public static List<Server> getServers() {
		return Collections.unmodifiableList(servers);
	}

	/**
	 * 获取线程池对象
	 * 
	 * @return 线程池对象
	 */
	public static ExecutorService getExecutor() {
		if (executor == null) {
			synchronized (Servers.class) {
				if (executor == null) {
					executor = Executors.newCachedThreadPool();
				}
			}
		}
		return executor;
	}

	/**
	 * 设置线程池对象
	 * 
	 * @param executor
	 *            线程池对象
	 */
	public static void setExecutor(ExecutorService executor) {
		if (executor == null) {
			throw new IllegalArgumentException("Illegal executor:" + executor);
		}
		if (Servers.executor == null) {
			synchronized (Servers.class) {
				if (Servers.executor == null) {
					Servers.executor = executor;
				}
			}
		}
	}

	/**
	 * 执行线程
	 * 
	 * @param runnable
	 *            线程处理接口
	 */
	public static void execute(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Illegal runnable:" + runnable);
		}
		getExecutor().execute(runnable);
	}

	/**
	 * 提交任务
	 * 
	 * @param runnable
	 *            线程处理接口
	 * @return 任务结果
	 */
	public static Future<?> submit(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Illegal runnable:" + runnable);
		}
		return getExecutor().submit(runnable);
	}

	/**
	 * 提交任务
	 * 
	 * @param <T>
	 *            结果类型
	 * @param runnable
	 *            线程处理接口
	 * @param result
	 *            任务结果
	 * @return 任务结果
	 */
	public static <T> Future<T> submit(Runnable runnable, T result) {
		if (runnable == null) {
			throw new IllegalArgumentException("Illegal runnable:" + runnable);
		}
		if (result == null) {
			throw new IllegalArgumentException("Illegal result:" + result);
		}
		return getExecutor().submit(runnable, result);
	}

	/**
	 * 提交任务
	 * 
	 * @param <T>
	 *            结果类型
	 * @param callable
	 *            线程处理接口
	 * @return 任务结果
	 */
	public static <T> Future<T> submit(Callable<T> callable) {
		if (callable == null) {
			throw new IllegalArgumentException("Illegal callable:" + callable);
		}
		return getExecutor().submit(callable);
	}

	/**
	 * 获取任务调度器
	 * 
	 * @return 任务调度器
	 */
	public static Scheduler getScheduler() {
		if (scheduler == null) {
			synchronized (Servers.class) {
				if (scheduler == null) {
					try {
						scheduler = StdSchedulerFactory.getDefaultScheduler();
					} catch (SchedulerException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return scheduler;
	}

	/**
	 * 设置任务调度器
	 * 
	 * @param scheduler
	 *            任务调度器
	 */
	public static void setScheduler(Scheduler scheduler) {
		if (scheduler == null) {
			throw new IllegalArgumentException("Illegal scheduler:" + scheduler);
		}
		if (Servers.scheduler == null) {
			synchronized (Servers.class) {
				if (Servers.scheduler == null) {
					Servers.scheduler = scheduler;
				}
			}
		}
	}

}
