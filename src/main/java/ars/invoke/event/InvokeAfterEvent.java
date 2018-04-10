package ars.invoke.event;

import ars.invoke.request.Requester;

/**
 * 请求调用成功触发事件
 *
 * @author wuyongqiang
 */
public class InvokeAfterEvent extends InvokeEvent {
    private static final long serialVersionUID = 1L;

    private Object value; // 调用结果

    public InvokeAfterEvent(Requester requester, Object value) {
        super(requester);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
