package ars.util;

import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * 系统服务工具类
 *
 * @author wuyongqiang
 */
public final class Servers {
    private static ExecutorService executor;
    private final static LinkedList<Server> servers = new LinkedList<Server>();

    private Servers() {

    }

    /**
     * 注册系统服务
     *
     * @param server 服务对象
     */
    static void register(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Server must not be null");
        }
        synchronized (Servers.class) {
            servers.add(server);
        }
    }

    /**
     * 注销系统服务
     *
     * @param server 服务对象
     */
    static void unregister(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Server must not be null");
        }
        synchronized (Servers.class) {
            servers.remove(server);
        }
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
     * @param executor 线程池对象
     */
    public static void setExecutor(ExecutorService executor) {
        if (executor == null) {
            throw new IllegalArgumentException("ExecutorService must not be null");
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
     * @param runnable 线程处理接口
     */
    public static void execute(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable must not be null");
        }
        getExecutor().execute(runnable);
    }

    /**
     * 提交任务
     *
     * @param runnable 线程处理接口
     * @return 任务结果
     */
    public static Future<?> submit(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable must not be null");
        }
        return getExecutor().submit(runnable);
    }

    /**
     * 提交任务
     *
     * @param <T>      结果类型
     * @param runnable 线程处理接口
     * @param result   任务结果
     * @return 任务结果
     */
    public static <T> Future<T> submit(Runnable runnable, T result) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable must not be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("Result must not be null");
        }
        return getExecutor().submit(runnable, result);
    }

    /**
     * 提交任务
     *
     * @param <T>      结果类型
     * @param callable 线程处理接口
     * @return 任务结果
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException("Callable must not be null");
        }
        return getExecutor().submit(callable);
    }

    /**
     * 销毁服务资源
     */
    public static void destroy() {
        while (!servers.isEmpty()) {
            servers.getLast().stop();
        }
        if (executor != null) {
            synchronized (Servers.class) {
                if (executor != null) {
                    executor.shutdown();
                    executor = null;
                }
            }
        }
    }

}
