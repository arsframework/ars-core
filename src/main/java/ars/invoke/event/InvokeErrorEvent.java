package ars.invoke.event;

import ars.invoke.request.Requester;

/**
 * 请求调用失败触发事件
 *
 * @author wuyongqiang
 */
public class InvokeErrorEvent extends InvokeEvent {
    private static final long serialVersionUID = 1L;

    private Throwable error; // 调用异常

    public InvokeErrorEvent(Requester requester, Throwable error) {
        super(requester);
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

}
