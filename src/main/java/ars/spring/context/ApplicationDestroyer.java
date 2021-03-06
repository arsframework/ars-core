package ars.spring.context;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 基于Spring应用销毁抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class ApplicationDestroyer implements ApplicationListener<ApplicationEvent> {
	private boolean destroied;

	/**
	 * Spring容器销毁后执行方法
	 * 
	 * @param event
	 *            Spring上下文关闭事件
	 */
	protected abstract void execute(ContextClosedEvent event);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent && !this.destroied) {
			this.execute((ContextClosedEvent) event);
			this.destroied = true;
		}
	}

}
