package ars.invoke;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

import ars.util.Strings;
import ars.invoke.Router;
import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.cache.InvokeCache;
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
	private List<String> apis;
	private InvokeCache cache;
	private Map<String, InvokeWrapper> wrappers = new HashMap<String, InvokeWrapper>(0);
	private List<InvokeListener<?>> invokeBeforeListeners = new LinkedList<InvokeListener<?>>();
	private List<InvokeListener<?>> invokeAfterListeners = new LinkedList<InvokeListener<?>>();
	private List<InvokeListener<?>> invokeErrorListeners = new LinkedList<InvokeListener<?>>();
	private List<InvokeListener<?>> invokeCompleteListeners = new LinkedList<InvokeListener<?>>();

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

	public InvokeCache getCache() {
		return cache;
	}

	public void setCache(InvokeCache cache) {
		this.cache = cache;
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
	public List<String> getApis() {
		if (this.apis == null) {
			synchronized (this) {
				if (this.apis == null) {
					this.apis = new ArrayList<String>(this.wrappers.keySet());
					Collections.sort(this.apis);
				}
			}
		}
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
			String key = this.cache == null ? null : this.cache.key(requester);
			if (key == null) {
				result = this.access(requester);
			} else {
				synchronized (key.intern()) {
					if (this.cache.exists(key)) {
						result = this.cache.get(key);
					} else {
						result = this.access(requester);
						this.cache.set(key, result);
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
	public <E extends InvokeEvent> void addListeners(Class<E> type, InvokeListener<E>... listeners) {
		if (listeners.length > 0) {
			List<InvokeListener<E>> list = Arrays.asList(listeners);
			if (type == InvokeBeforeEvent.class) {
				this.invokeBeforeListeners.addAll(list);
			} else if (type == InvokeAfterEvent.class) {
				this.invokeAfterListeners.addAll(list);
			} else if (type == InvokeErrorEvent.class) {
				this.invokeErrorListeners.addAll(list);
			} else if (type == InvokeCompleteEvent.class) {
				this.invokeCompleteListeners.addAll(list);
			} else {
				this.invokeBeforeListeners.addAll(list);
				this.invokeAfterListeners.addAll(list);
				this.invokeErrorListeners.addAll(list);
				this.invokeCompleteListeners.addAll(list);
			}
		}
	}

}
