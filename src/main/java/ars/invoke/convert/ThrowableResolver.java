package ars.invoke.convert;

/**
 * 异常对象转换接口
 *
 * @author wuyongqiang
 */
public interface ThrowableResolver {
    /**
     * 获取异常编码
     *
     * @param throwable 异常对象
     * @return 异常编码
     */
    public int getCode(Throwable throwable);

    /**
     * 获取异常消息
     *
     * @param throwable 异常对象
     * @return 异常信息
     */
    public String getMessage(Throwable throwable);

    /**
     * 判断异常是否可转换
     *
     * @param throwable 异常对象
     * @return true/false
     */
    public boolean isResolvable(Throwable throwable);

}
