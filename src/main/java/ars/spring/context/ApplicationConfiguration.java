package ars.spring.context;

import java.util.Locale;
import java.util.Collection;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationContextAware;

import ars.util.Jsons;
import ars.util.Dates;
import ars.util.Strings;
import ars.util.ObjectAdapter;
import ars.server.Servers;
import ars.invoke.Router;
import ars.invoke.Channel;
import ars.invoke.Context;
import ars.invoke.Invoker;
import ars.invoke.Invokes;
import ars.invoke.Messager;
import ars.invoke.StandardRouter;
import ars.invoke.local.Api;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;
import ars.invoke.remote.Remotes;
import ars.invoke.cache.Cache;
import ars.invoke.event.InvokeListener;

/**
 * 基于Spring系统配置
 * 
 * @author wuyq
 * 
 */
public class ApplicationConfiguration extends StandardRouter
		implements Context, ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	private Cache cache; // 缓存对象
	private Invoker invoker; // 资源调用对象
	private Messager messager; // 消息处理对象
	private String pattern; // 资源地址匹配模式
	private String client; // 应用标识
	private String directory; // 文件目录
	private String configure; // 应用配置文件
	private String dateFormat; // 日期格式
	private String datetimeFormat; // 日期时间格式
	private String datenanoFormat; // 日期时间毫秒格式
	private ApplicationContext applicationContext; // 应用上下文对象
	private boolean initialized, destroied; // Spring容器启动/销毁标记

	public Invoker getInvoker() {
		return invoker;
	}

	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getConfigure() {
		return configure;
	}

	public void setConfigure(String configure) {
		this.configure = configure;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDatetimeFormat() {
		return datetimeFormat;
	}

	public void setDatetimeFormat(String datetimeFormat) {
		this.datetimeFormat = datetimeFormat;
	}

	public String getDatenanoFormat() {
		return datenanoFormat;
	}

	public void setDatenanoFormat(String datenanoFormat) {
		this.datenanoFormat = datenanoFormat;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

		// 设置日期格式
		if (this.dateFormat != null) {
			Dates.setDateFormat(new SimpleDateFormat(this.dateFormat));
		}
		if (this.datetimeFormat != null) {
			Dates.setDatetimeFormat(new SimpleDateFormat(this.datetimeFormat));
		}
		if (this.datenanoFormat != null) {
			Dates.setDatenanoFormat(new SimpleDateFormat(this.datenanoFormat));
		}

		// 设置json序列化对象适配器
		ObjectAdapter[] objectAdapters = applicationContext.getBeansOfType(ObjectAdapter.class).values()
				.toArray(new ObjectAdapter[0]);
		Jsons.setObjectAdapters(objectAdapters);

		// 初始化远程调用工具类
		if (this.client != null) {
			Remotes.setClient(this.client);
		}
		if (this.directory != null) {
			Remotes.setDirectory(this.directory);
		}
		if (this.configure != null) {
			Remotes.setConfigure(this.configure);
		}

		// 设置缓存对象
		try {
			this.cache = applicationContext.getBean(Cache.class);
		} catch (NoSuchBeanDefinitionException e) {
		}

		// 注册系统接口资源
		Invoker invoker = this.invoker == null ? Invokes.getSingleLocalInvoker() : this.invoker;
		Collection<?> entities = applicationContext.getBeansWithAnnotation(Api.class).values();
		for (Object entity : entities) {
			Class<?> type = entity.getClass();
			String classApi = Apis.getApi(Apis.getApiClass(type));
			Method[] methods = Apis.getApiMethods(type);
			for (Method method : methods) {
				String methodApi = Apis.getApi(method);
				String api = Strings.replace(new StringBuilder(classApi).append('/').append(methodApi), "//", "/");
				if (this.pattern == null || Strings.matches(api, this.pattern)) {
					this.register(api, invoker, new Function(entity, method, Apis.getConditions(method)));
				}
			}
		}

		// 注册事件监听器
		InvokeListener<?>[] listeners = applicationContext.getBeansOfType(InvokeListener.class).values()
				.toArray(new InvokeListener[0]);
		try {
			for (int i = 0; i < listeners.length; i++) {
				InvokeListener<?> listener = listeners[i];
				if (AopUtils.isAopProxy(listener)) {
					listeners[i] = (InvokeListener<?>) ((Advised) listener).getTargetSource().getTarget();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.setListeners(listeners);

		// 设置请求通道上下文
		Collection<Channel> channels = applicationContext.getBeansOfType(Channel.class).values();
		for (Channel channel : channels) {
			if (channel.getContext() == null) {
				channel.setContext(this);
			}
		}
	}

	@Override
	public final void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent && !this.initialized) {
			this.initialized = true;
			if (this.cache != null) {
				this.cache.initialize(this);
			}
			Servers.startup();
		} else if (event instanceof ContextClosedEvent && !this.destroied) {
			this.destroied = true;
			if (this.cache != null) {
				this.cache.destroy();
			}
			Servers.shutdown();
			Remotes.destroy();
		}
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	@Override
	public Router getRouter() {
		return this;
	}

	@Override
	public Messager getMessager() {
		if (this.messager == null) {
			synchronized (this) {
				if (this.messager == null) {
					this.messager = new Messager() {

						@Override
						public String format(Locale locale, String key, Object[] args) {
							return this.format(locale, key, args, key);
						}

						@Override
						public String format(Locale locale, String key, Object[] args, String text) {
							return applicationContext.getMessage(key, args, text, locale);
						}

					};
				}
			}
		}
		return this.messager;
	}

}
