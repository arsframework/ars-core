package ars.spring.context;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 应用事件监听器
 * 
 * @author yongqiangwu
 *
 */
public abstract class ApplicationListener implements org.springframework.context.ApplicationListener<ApplicationEvent> {
	private boolean initialized, destroied;

	/**
	 * Spring容器初始化完成后执行方法
	 * 
	 * @param event
	 *            Spring上下文加载完成事件
	 */
	protected abstract void initialize(ContextRefreshedEvent event);

	/**
	 * Spring容器销毁后执行方法
	 * 
	 * @param event
	 *            Spring上下文关闭事件
	 */
	protected abstract void destroy(ContextClosedEvent event);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent && !this.initialized) {
			this.initialize((ContextRefreshedEvent) event);
			this.initialized = true;
		} else if (event instanceof ContextClosedEvent && !this.destroied) {
			this.destroy((ContextClosedEvent) event);
			this.destroied = true;
		}
	}

}
