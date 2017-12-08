package ars.invoke.event;

import java.util.EventListener;

import ars.invoke.event.InvokeEvent;

/**
 * 请求调用监听器
 * 
 * @author wuyq
 * 
 * @param <E>
 *            事件模型
 */
public interface InvokeListener<E extends InvokeEvent> extends EventListener {
	/**
	 * 事件监听
	 * 
	 * @param event
	 *            事件对象
	 */
	public void onInvokeEvent(E event);

}
