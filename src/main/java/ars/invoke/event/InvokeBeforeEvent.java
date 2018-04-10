package ars.invoke.event;

import ars.invoke.request.Requester;

/**
 * 请求调用之前触发事件
 *
 * @author wuyongqiang
 */
public class InvokeBeforeEvent extends InvokeEvent {
    private static final long serialVersionUID = 1L;

    public InvokeBeforeEvent(Requester requester) {
        super(requester);
    }

}
