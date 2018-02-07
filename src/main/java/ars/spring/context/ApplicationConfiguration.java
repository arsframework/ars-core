package ars.spring.context;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.LinkedList;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import org.springframework.beans.BeansException;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationContextAware;
import org.apache.http.conn.ClientConnectionManager;

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
import ars.invoke.CacheRule;
import ars.invoke.StandardRouter;
import ars.invoke.local.Api;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;
import ars.invoke.remote.Remotes;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;
import ars.invoke.request.SessionFactory;
import ars.invoke.request.CacheSessionFactory;
import ars.invoke.channel.http.Https;
import ars.file.office.Converts;

/**
 * 基于Spring系统配置
 * 
 * @author yongqiangwu
 * 
 */
public class ApplicationConfiguration extends StandardRouter
		implements Context, ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	private String pattern; // 资源地址匹配模式
	private Invoker invoker; // 资源调用对象
	private Messager messager; // 消息处理对象
	private SessionFactory sessionFactory; // 会话工厂
	private Map<String, String> configure; // 系统配置
	private ClientConnectionManager httpClientManager; // Http客户端管理器
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

	public Map<String, String> getConfigure() {
		return configure;
	}

	public void setConfigure(Map<String, String> configure) {
		this.configure = configure;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ClientConnectionManager getHttpClientManager() {
		return httpClientManager;
	}

	public void setHttpClientManager(ClientConnectionManager httpClientManager) {
		this.httpClientManager = httpClientManager;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public final void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent && !this.initialized) {
			this.initialize();
			this.initialized = true;
		} else if (event instanceof ContextClosedEvent && !this.destroied) {
			this.destroy();
			this.destroied = true;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize() {
		if (this.invoker == null) {
			this.invoker = Invokes.getSingleLocalInvoker();
		}
		if (this.sessionFactory == null) {
			this.sessionFactory = new CacheSessionFactory();
		}

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

		// 设置Http客户端管理器
		if (this.httpClientManager != null) {
			Https.setManager(this.httpClientManager);
		}

		// 设置json序列化对象适配器
		ObjectAdapter[] objectAdapters = this.applicationContext.getBeansOfType(ObjectAdapter.class).values()
				.toArray(new ObjectAdapter[0]);
		Jsons.setObjectAdapters(objectAdapters);

		// 注册系统接口资源
		Collection<?> entities = this.applicationContext.getBeansWithAnnotation(Api.class).values();
		for (Object entity : entities) {
			Class<?> type = entity.getClass();
			String classApi = Apis.getApi(Apis.getApiClass(type));
			Method[] methods = Apis.getApiMethods(type);
			for (Method method : methods) {
				String methodApi = Apis.getApi(method);
				String api = Strings.replace(new StringBuilder(classApi).append('/').append(methodApi), "//", "/");
				if (this.pattern == null || Strings.matches(api, this.pattern)) {
					this.register(api, this.invoker, new Function(entity, method, Apis.getConditions(method)));
				}
			}
		}

		// 设置资源缓存配置
		Map<String, CacheRule> cacheRules = this.applicationContext.getBeansOfType(CacheRule.class);
		if (!cacheRules.isEmpty()) {
			this.setCacheRules(cacheRules.values().toArray(new CacheRule[0]));
		}

		// 注册事件监听器
		Map<Class, List<InvokeListener>> listeners = new HashMap<Class, List<InvokeListener>>();
		try {
			for (Entry<String, InvokeListener> entry : this.applicationContext.getBeansOfType(InvokeListener.class)
					.entrySet()) {
				InvokeListener target = null;
				InvokeListener listener = entry.getValue();
				if (AopUtils.isAopProxy(listener)) {
					target = (InvokeListener) ((Advised) listener).getTargetSource().getTarget();
				}
				Class type = null;
				for (Method method : (target == null ? listener : target).getClass().getMethods()) {
					if (method.getName().equals("onInvokeEvent") && (type == null || type == InvokeEvent.class)) {
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

		// 设置请求通道上下文
		Collection<Channel> channels = this.applicationContext.getBeansOfType(Channel.class).values();
		for (Channel channel : channels) {
			if (channel.getContext() == null) {
				channel.setContext(this);
			}
		}
		super.initialize();
		Servers.startup();
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

	@Override
	public void destroy() {
		super.destroy();
		this.sessionFactory.destroy();
		Https.destroy();
		Remotes.destroy();
		Servers.shutdown();
	}

}
