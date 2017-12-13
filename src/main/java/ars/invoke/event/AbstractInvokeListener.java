package ars.invoke.event;

import ars.util.Strings;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;

/**
 * 请求调用事件监听抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <E>
 *            事件模型
 */
public abstract class AbstractInvokeListener<E extends InvokeEvent> implements InvokeListener<E> {
	private String pattern; // 资源地址匹配模式

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 执行事件监听处理
	 * 
	 * @param event
	 *            事件对象
	 */
	public abstract void execute(E event);

	@Override
	public final void onInvokeEvent(E event) {
		if (this.pattern == null || Strings.matches(event.getSource().getUri(), this.pattern)) {
			this.execute(event);
		}
	}

}
