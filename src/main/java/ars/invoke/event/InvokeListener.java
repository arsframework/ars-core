package ars.invoke.event;

import java.util.EventListener;

/**
 * 请求调用监听器
 *
 * @param <E> 事件模型
 * @author wuyongqiang
 */
public interface InvokeListener<E extends InvokeEvent> extends EventListener {
    /**
     * 事件监听
     *
     * @param event 事件对象
     */
    public void onInvokeEvent(E event);

}
