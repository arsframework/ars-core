package ars.util;

/**
 * 系统后台服务接口
 *
 * @author wuyongqiang
 */
public interface Server {
    /**
     * 启动服务
     */
    public void start();

    /**
     * 停止服务
     */
    public void stop();

    /**
     * 获取服务是否存活
     *
     * @return true/false
     */
    public boolean isAlive();

}
