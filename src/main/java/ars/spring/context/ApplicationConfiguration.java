package ars.spring.context;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map.Entry;
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
import ars.file.office.Converts;

/**
 * 基于Spring系统配置
 * 
 * @author yongqiangwu
 * 
 */
public class ApplicationConfiguration extends StandardRouter
		implements Context, ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	private Cache cache; // 缓存对象
	private Invoker invoker; // 资源调用对象
	private Messager messager; // 消息处理对象
	private String pattern; // 资源地址匹配模式
	private Map<String, String> configure;
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

	public Map<String, String> getConfigure() {
		return configure;
	}

	public void setConfigure(Map<String, String> configure) {
		this.configure = configure;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

		// 初始化系统配置
		Map<String, String> iceItems = new HashMap<String, String>();
		if (this.configure != null && !this.configure.isEmpty()) {
			for (Entry<String, String> entry : this.configure.entrySet()) {
				String key = entry.getKey().toLowerCase().trim();
				String value = entry.getValue();
				if (value == null || (value = value.trim()).isEmpty()) {
					continue;
				}
				if (key.equals("remote.client")) { // 客户端标识
					Remotes.setClient(value);
				} else if (key.equals("remote.directory")) { // 远程操作文件读取目录
					Remotes.setDirectory(value);
				} else if (key.startsWith("remote.ice.")) { // ice配置项前缀
					String item = key.substring(11).trim();
					if (!item.isEmpty()) {
						iceItems.put(item, value);
					}
				} else if (key.equals("format.date")) { // 日期格式
					Dates.setDateFormat(new SimpleDateFormat(value));
				} else if (key.equals("format.datetime")) { // 日期时间格式
					Dates.setDatetimeFormat(new SimpleDateFormat(value));
				} else if (key.equals("format.datenano")) { // 日期时间毫秒格式
					Dates.setDatenanoFormat(new SimpleDateFormat(value));
				} else if (key.equals("openoffice.host")) { // openoffice服务器地址
					Converts.setOpenOfficeHost(value);
				} else if (key.equals("openoffice.port")) { // openoffice服务器端口
					Converts.setOpenOfficePort(Integer.parseInt(value));
				}
			}
			if (!iceItems.isEmpty()) {
				Remotes.setConfigure(iceItems);
			}
		}

		// 设置json序列化对象适配器
		ObjectAdapter[] objectAdapters = applicationContext.getBeansOfType(ObjectAdapter.class).values()
				.toArray(new ObjectAdapter[0]);
		Jsons.setObjectAdapters(objectAdapters);

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
