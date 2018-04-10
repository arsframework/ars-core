package ars.invoke.local;

import ars.invoke.request.Requester;

/**
 * 对象适配接口
 *
 * @author wuyongqiang
 */
public interface ParamAdapter {
    /**
     * 对象适配操作
     *
     * @param requester 请求对象
     * @param type      参数类型
     * @return 适配结果对象
     * @throws Exception 操作异常
     */
    public Object adaption(Requester requester, Class<?> type) throws Exception;

}
