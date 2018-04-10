package ars.invoke;

import ars.invoke.request.Requester;

/**
 * 服务调用接口
 *
 * @author wuyongqiang
 */
public interface Invoker {
    /**
     * 执行请求调用
     *
     * @param requester 请求对象
     * @param resource  接口资源
     * @return 调用结果
     * @throws Exception 操作异常
     */
    public Object execute(Requester requester, Resource resource) throws Exception;

}
