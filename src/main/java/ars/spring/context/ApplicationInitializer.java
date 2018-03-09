package ars.spring.context;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 基于Spring应用初始化接口抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class ApplicationInitializer implements ApplicationListener<ApplicationEvent> {
	private boolean initialized = false; // 容器是否已经加载完成

	/**
	 * Spring容器初始化完成后执行方法
	 * 
	 * @param event
	 *            Spring上下文加载完成事件
	 */
	protected abstract void execute(ContextRefreshedEvent event);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent && !this.initialized) {
			this.initialized = true;
			this.execute((ContextRefreshedEvent) event);
		}
	}

}
