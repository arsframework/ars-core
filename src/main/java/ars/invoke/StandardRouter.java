package ars.invoke;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;

import ars.util.Beans;
import ars.util.Cache;
import ars.util.Strings;
import ars.util.SimpleCache;
import ars.invoke.Router;
import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.CacheRule;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeAfterEvent;
import ars.invoke.event.InvokeErrorEvent;
import ars.invoke.event.InvokeBeforeEvent;
import ars.invoke.event.InvokeCompleteEvent;
import ars.invoke.request.AccessDeniedException;

/**
 * 请求资源路由标准实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardRouter implements Router {
	private Cache cache; // 缓存处理接口
	private List<String> apis; // 资源接口集合
	private boolean initialized; // 是否已初始化
	private CacheRule[] cacheRules; // 缓存规则数组
	private Map<String, CacheRule> cacheTargets = Collections.emptyMap(); // 缓存目标资源地址/缓存规则映射
	private Map<String, Set<String>> cacheRefreshs = Collections.emptyMap(); // 触发缓存刷新资源地址映射
	private final Map<String, String> forwards = new HashMap<String, String>(); // 请求转发资源映射
	private final Map<String, InvokeWrapper> wrappers = new HashMap<String, InvokeWrapper>(); // 请求调用包装器资源映射
	private final List<InvokeListener<?>> invokeBeforeListeners = new LinkedList<InvokeListener<?>>(); // 请求调用之前监听器集合
	private final List<InvokeListener<?>> invokeAfterListeners = new LinkedList<InvokeListener<?>>(); // 请求调用成功监听器集合
	private final List<InvokeListener<?>> invokeErrorListeners = new LinkedList<InvokeListener<?>>(); // 请求调用失败监听器集合
	private final List<InvokeListener<?>> invokeCompleteListeners = new LinkedList<InvokeListener<?>>(); // 请求调用完成监听器集合

	/**
	 * 请求调用包装类
	 * 
	 * @author yongqiangwu
	 *
	 */
	class InvokeWrapper {
		public final Invoker invoker;
		public final Resource resource;

		public InvokeWrapper(Invoker invoker, Resource resource) {
			if (invoker == null) {
				throw new IllegalArgumentException("Illegal invoker:" + invoker);
			}
			if (resource == null) {
				throw new IllegalArgumentException("Illegal resource:" + resource);
			}
			this.invoker = invoker;
			this.resource = resource;
		}

		/**
		 * 执行请求调用
		 * 
		 * @param requester
		 *            请求对象
		 * @return 调用结果
		 * @throws Exception
		 *             操作异常
		 */
		public Object execute(Requester requester) throws Exception {
			return this.invoker.execute(requester, this.resource);
		}

	}

	/**
	 * 获取请求对应的资源数据缓存标识
	 * 
	 * @param requester
	 *            请求对象
	 * @param rule
	 *            缓存规则
	 * @return 缓存标识
	 */
	private String getCacheKey(Requester requester, CacheRule rule) {
		StringBuilder buffer = new StringBuilder("{").append(requester.getUri()).append('}');
		if (requester.getUser() != null && !rule.isGlobal()) {
			buffer.append('{').append(requester.getUser()).append('}');
		}
		Set<String> condtions = requester.getParameterNames();
		if (condtions.isEmpty()) {
			return buffer.toString();
		}
		String[] keys = condtions.toArray(Strings.EMPTY_ARRAY);
		Arrays.sort(keys);
		buffer.append('{');
		for (int i = 0; i < keys.length; i++) {
			if (i > 0) {
				buffer.append(',');
			}
			String key = keys[i];
			Object value = requester.getParameter(key);
			if (value instanceof Collection) {
				Collection<?> collection = (Collection<?>) value;
				if (!collection.isEmpty()) {
					Object[] array = collection.toArray();
					Arrays.sort(array);
					for (Object v : array) {
						buffer.append(key).append('=');
						if (!Beans.isEmpty(v)) {
							buffer.append(Strings.toString(v));
						}
					}
				}
			} else if (value instanceof Object[]) {
				Object[] array = (Object[]) value;
				if (array.length > 0) {
					Object[] copy = Arrays.copyOf(array, array.length);
					Arrays.sort(copy);
					for (Object v : copy) {
						buffer.append(key).append('=');
						if (!Beans.isEmpty(v)) {
							buffer.append(Strings.toString(v));
						}
					}
				}
			} else {
				buffer.append(key).append('=');
				if (!Beans.isEmpty(value)) {
					buffer.append(Strings.toString(value));
				}
			}
		}
		return buffer.append('}').toString();
	}

	/**
	 * 请求调用之前执行
	 * 
	 * @param requester
	 *            请求对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void beforeInvoke(Requester requester) {
		if (!this.invokeBeforeListeners.isEmpty()) {
			InvokeEvent event = new InvokeBeforeEvent(requester);
			for (InvokeListener listener : this.invokeBeforeListeners) {
				listener.onInvokeEvent(event);
			}
		}
	}

	/**
	 * 请求调用成功执行
	 * 
	 * @param requester
	 *            请求对象
	 * @param value
	 *            请求结果
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void afterInvoke(Requester requester, Object value) {
		if (!this.invokeAfterListeners.isEmpty()) {
			InvokeEvent event = new InvokeAfterEvent(requester, value);
			for (InvokeListener listener : this.invokeAfterListeners) {
				listener.onInvokeEvent(event);
			}
		}
	}

	/**
	 * 请求调用失败执行
	 * 
	 * @param requester
	 *            请求对象
	 * @param e
	 *            调用异常
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void errorInvoke(Requester requester, Throwable e) {
		if (!this.invokeErrorListeners.isEmpty()) {
			InvokeEvent event = new InvokeErrorEvent(requester, e);
			for (InvokeListener listener : this.invokeErrorListeners) {
				listener.onInvokeEvent(event);
			}
		}
	}

	/**
	 * 请求调用完成执行
	 * 
	 * @param requester
	 *            请求对象
	 * @param value
	 *            调用结果
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void completeInvoke(Requester requester, Object value) {
		if (!this.invokeCompleteListeners.isEmpty()) {
			InvokeEvent event = new InvokeCompleteEvent(requester, value);
			for (InvokeListener listener : this.invokeCompleteListeners) {
				listener.onInvokeEvent(event);
			}
		}
	}

	/**
	 * 查找请求调用包装对象
	 * 
	 * @param requester
	 *            请求对象
	 * @return 请求调用包装对象
	 */
	protected InvokeWrapper lookupInvokeWrapper(Requester requester) {
		String uri = requester.getUri();
		if (!this.forwards.isEmpty()) {
			for (Entry<String, String> entry : this.forwards.entrySet()) {
				if (Strings.matches(uri, entry.getKey())) {
					uri = entry.getValue();
					break;
				}
			}
		}
		InvokeWrapper wrapper = this.wrappers.get(uri);
		if (wrapper == null) {
			for (Entry<String, InvokeWrapper> entry : this.wrappers.entrySet()) {
				if (Strings.matches(uri, entry.getKey())) {
					wrapper = entry.getValue();
					this.wrappers.put(uri, wrapper);
					return wrapper;
				}
			}
			throw new AccessDeniedException("error.resource.undefined");
		}
		return wrapper;
	}

	/**
	 * 请求访问
	 * 
	 * @param requester
	 *            请求对象
	 * @return 请求结果
	 * @throws Exception
	 *             操作异常
	 */
	protected Object access(Requester requester) throws Exception {
		return this.lookupInvokeWrapper(requester).execute(requester);
	}

	@Override
	public void initialize() {
		if (!this.initialized) {
			synchronized (this) {
				if (!this.initialized) {
					this.apis = new ArrayList<String>(this.wrappers.keySet());
					Collections.sort(this.apis);

					if (this.cacheRules != null && this.cacheRules.length > 0) {
						if (this.cache == null) {
							this.cache = new SimpleCache();
						}
						this.cacheTargets = new HashMap<String, CacheRule>();
						this.cacheRefreshs = new HashMap<String, Set<String>>();
						for (int i = 0; i < this.apis.size(); i++) {
							String api = this.apis.get(i);
							for (CacheRule rule : this.cacheRules) {
								if (rule.getTarget() == null) {
									throw new RuntimeException("Cache rule target resource can't be empty.");
								}
								if (Strings.matches(api, rule.getTarget())) {
									this.cacheTargets.put(api, rule);
									break;
								} else if (!Strings.matches(api, rule.getRefresh())) {
									continue;
								}
								for (int n = 0; n < this.apis.size(); n++) {
									String resource = this.apis.get(n);
									if (Strings.matches(resource, rule.getTarget())) {
										Set<String> resources = this.cacheRefreshs.get(api);
										if (resources == null) {
											resources = new HashSet<String>();
											this.cacheRefreshs.put(api, resources);
										}
										resources.add(resource);
									}
								}
							}
						}
					}
					this.initialized = true;
				}
			}
		}
	}

	@Override
	public List<String> getApis() {
		return this.apis;
	}

	@Override
	public boolean isRegistered(String api) {
		return this.wrappers.containsKey(api);
	}

	@Override
	public Object routing(Requester requester) {
		Object result = null;
		try {
			this.beforeInvoke(requester);
			CacheRule rule = this.cacheTargets.get(requester.getUri());
			if (rule == null) {
				result = this.access(requester);
				Set<String> refresh = this.cacheRefreshs.get(requester.getUri());
				if (refresh != null && !refresh.isEmpty()) {
					for (String uri : refresh) {
						this.cache.remove(new StringBuilder("{").append(uri).append("}*").toString());
					}
				}
			} else {
				String key = this.getCacheKey(requester, rule);
				synchronized (key.intern()) {
					if (this.cache.exists(key)) {
						result = this.cache.get(key);
					} else {
						result = this.access(requester);
						this.cache.set(key, result, rule.getTimeout());
					}
				}
			}
			this.afterInvoke(requester, result);
		} catch (Throwable e) {
			result = e;
			this.errorInvoke(requester, e);
		} finally {
			this.completeInvoke(requester, result);
		}
		return result;
	}

	@Override
	public void revoke(String api) {
		if (api == null) {
			throw new IllegalArgumentException("Illegal api:" + api);
		}
		this.wrappers.remove(api);
	}

	@Override
	public void register(String api, Invoker invoker, Resource resource) {
		this.register(api, invoker, resource, false);
	}

	@Override
	public void register(String api, Invoker invoker, Resource resource, boolean cover) {
		if (api == null) {
			throw new IllegalArgumentException("Illegal api:" + api);
		}
		if (invoker == null) {
			throw new IllegalArgumentException("Illegal invoker:" + invoker);
		}
		if (!cover && this.isRegistered(api)) {
			throw new RuntimeException("Api is already registered:" + api);
		}
		this.wrappers.put(api, new InvokeWrapper(invoker, resource));
	}

	@Override
	public void setCache(Cache cache) {
		if (cache == null) {
			throw new IllegalArgumentException("Illegal cache:" + cache);
		}
		this.cache = cache;
	}

	@Override
	public void setCacheRules(CacheRule... rules) {
		this.cacheRules = rules;
	}

	@Override
	public void setForwards(Map<String, String> forwards) {
		this.forwards.clear();
		if (forwards != null && !forwards.isEmpty()) {
			this.forwards.putAll(forwards);
		}
	}

	@Override
	public <E extends InvokeEvent> void setListeners(Class<E> type, InvokeListener<E>... listeners) {
		if (listeners.length > 0) {
			List<InvokeListener<E>> list = Arrays.asList(listeners);
			if (type == InvokeBeforeEvent.class) {
				this.invokeBeforeListeners.clear();
				this.invokeBeforeListeners.addAll(list);
			} else if (type == InvokeAfterEvent.class) {
				this.invokeAfterListeners.clear();
				this.invokeAfterListeners.addAll(list);
			} else if (type == InvokeErrorEvent.class) {
				this.invokeErrorListeners.clear();
				this.invokeErrorListeners.addAll(list);
			} else if (type == InvokeCompleteEvent.class) {
				this.invokeCompleteListeners.clear();
				this.invokeCompleteListeners.addAll(list);
			} else {
				this.invokeBeforeListeners.clear();
				this.invokeAfterListeners.clear();
				this.invokeErrorListeners.clear();
				this.invokeCompleteListeners.clear();
				this.invokeBeforeListeners.addAll(list);
				this.invokeAfterListeners.addAll(list);
				this.invokeErrorListeners.addAll(list);
				this.invokeCompleteListeners.addAll(list);
			}
		}
	}

	@Override
	public void destroy() {
		if (this.cache != null) {
			this.cache.destroy();
		}
	}

}
