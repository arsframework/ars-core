package ars.invoke.event;

import ars.invoke.request.Requester;

/**
 * 请求调用完成事件（成功/失败）
 *
 * @author wuyongqiang
 */
public class InvokeCompleteEvent extends InvokeEvent {
    private static final long serialVersionUID = 1L;

    private Object value; // 调用结果

    public InvokeCompleteEvent(Requester requester, Object value) {
        super(requester);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isFailed() {
        return value instanceof Throwable;
    }

}
