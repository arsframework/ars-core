package ars.spring.context;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import ars.util.Jsons;
import ars.util.Dates;
import ars.util.Strings;
import ars.util.Servers;
import ars.util.ObjectAdapter;
import ars.invoke.Router;
import ars.invoke.Channel;
import ars.invoke.Context;
import ars.invoke.Invoker;
import ars.invoke.Messager;
import ars.invoke.Cacheable;
import ars.invoke.StandardRouter;
import ars.invoke.local.Api;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;
import ars.invoke.local.LocalInvoker;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;
import ars.invoke.request.SessionFactory;
import ars.invoke.request.CacheSessionFactory;

/**
 * 应用上下文配置
 * 
 * @author yongqiangwu
 * 
 */
public class ApplicationContextConfiguration extends StandardRouter
		implements Context, ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	private String pattern; // 资源地址匹配模式
	private Invoker invoker; // 资源调用对象
	private Messager messager; // 消息处理对象
	private SessionFactory sessionFactory; // 会话工厂
	private ApplicationContext applicationContext; // 应用上下文对象
	private boolean initialized, destroied; // Spring容器启动/销毁标记

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setExecutor(ExecutorService executor) {
		Servers.setExecutor(executor);
	}

	public void setDateFormat(String format) {
		Dates.setDateFormat(new SimpleDateFormat(format));
	}

	public void setDatetimeFormat(String format) {
		Dates.setDatetimeFormat(new SimpleDateFormat(format));
	}

	public void setDatenanoFormat(String format) {
		Dates.setDatenanoFormat(new SimpleDateFormat(format));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			this.initialize();
		} else if (event instanceof ContextClosedEvent) {
			this.destroy();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize() {
		if (!this.initialized) {
			synchronized (this) {
				if (!this.initialized) {
					if (this.invoker == null) {
						this.invoker = new LocalInvoker();
					}
					if (this.sessionFactory == null) {
						this.sessionFactory = new CacheSessionFactory();
					}

					// 设置json序列化对象适配器
					ObjectAdapter[] objectAdapters = this.applicationContext.getBeansOfType(ObjectAdapter.class)
							.values().toArray(new ObjectAdapter[0]);
					Jsons.setObjectAdapters(objectAdapters);

					// 注册系统接口资源
					Collection<?> entities = this.applicationContext.getBeansWithAnnotation(Api.class).values();
					for (Object entity : entities) {
						Class<?> type = entity.getClass();
						String classApi = Apis.getApi(Apis.getApiClass(type));
						Method[] methods = Apis.getApiMethods(type);
						for (Method method : methods) {
							String methodApi = Apis.getApi(method);
							String api = Strings.replace(new StringBuilder(classApi).append('/').append(methodApi),
									"//", "/");
							if (this.pattern == null || Strings.matches(api, this.pattern)) {
								this.register(api, this.invoker,
										new Function(entity, method, Apis.getConditions(method)));
							}
						}
					}

					// 注册事件监听器
					Map<Class, List<InvokeListener>> listeners = new HashMap<Class, List<InvokeListener>>();
					try {
						for (Entry<String, InvokeListener> entry : this.applicationContext
								.getBeansOfType(InvokeListener.class).entrySet()) {
							InvokeListener target = null;
							InvokeListener listener = entry.getValue();
							if (AopUtils.isAopProxy(listener)) {
								target = (InvokeListener) ((Advised) listener).getTargetSource().getTarget();
							}
							Class type = null;
							for (Method method : (target == null ? listener : target).getClass().getMethods()) {
								if (method.getName().equals("onInvokeEvent")
										&& (type == null || type == InvokeEvent.class)) {
									type = method.getParameterTypes()[0];
								}
							}
							List<InvokeListener> groups = listeners.get(type);
							if (groups == null) {
								groups = new LinkedList<InvokeListener>();
								listeners.put(type, groups);
							}
							groups.add(listener);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					if (!listeners.isEmpty()) {
						for (Entry<Class, List<InvokeListener>> entry : listeners.entrySet()) {
							this.setListeners(entry.getKey(), entry.getValue().toArray(new InvokeListener[0]));
						}
					}

					// 设置可缓存资源规则
					Map<String, Cacheable> cacheables = this.applicationContext.getBeansOfType(Cacheable.class);
					if (!cacheables.isEmpty()) {
						this.setCacheables(cacheables.values().toArray(new Cacheable[0]));
					}

					// 设置请求通道上下文
					Collection<Channel> channels = this.applicationContext.getBeansOfType(Channel.class).values();
					for (Channel channel : channels) {
						if (channel.getContext() == null) {
							channel.setContext(this);
						}
					}
					super.initialize();
					this.initialized = true;
				}
			}
		}
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

	@Override
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (ApplicationContext.class.isAssignableFrom(type)) {
			return (T) this.applicationContext;
		}
		try {
			return this.applicationContext.getBean(type);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	@Override
	public void destroy() {
		if (!this.destroied) {
			synchronized (this) {
				if (!this.destroied) {
					super.destroy();
					this.sessionFactory.destroy();
					Servers.destroy();
					this.destroied = true;
				}
			}
		}
	}

}
