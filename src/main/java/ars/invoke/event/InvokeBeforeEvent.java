package ars.invoke.event;

import ars.invoke.event.InvokeEvent;
import ars.invoke.request.Requester;

/**
 * 请求调用之前触发事件
 * 
 * @author yongqiangwu
 * 
 */
public class InvokeBeforeEvent extends InvokeEvent {
	private static final long serialVersionUID = 1L;

	public InvokeBeforeEvent(Requester requester) {
		super(requester);
	}

}
