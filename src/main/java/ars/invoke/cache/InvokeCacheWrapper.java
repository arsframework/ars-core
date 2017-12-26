package ars.invoke.cache;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import ars.util.Beans;
import ars.util.Cache;
import ars.util.Strings;
import ars.util.SimpleCache;
import ars.invoke.cache.Rule;
import ars.invoke.cache.InvokeCache;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeAfterEvent;

/**
 * 请求调用缓存包装实现
 * 
 * @author yongqiangwu
 *
 */
public class InvokeCacheWrapper implements InvokeCache, InvokeListener<InvokeAfterEvent> {
	/**
	 * 默认缓存超时时间（秒）
	 */
	public static final int DEFAULT_TIMEOUT = 30 * 60;

	protected final Cache cache;
	protected final int timeout;
	protected final Rule[] rules;
	private Map<String, Rule> targets;
	private Map<String, Set<String>> refreshs;

	public InvokeCacheWrapper(Rule... rules) {
		this(new SimpleCache(), DEFAULT_TIMEOUT, rules);
	}

	public InvokeCacheWrapper(int timeout, Rule... rules) {
		this(new SimpleCache(), timeout, rules);
	}

	public InvokeCacheWrapper(Cache cache, Rule... rules) {
		this(cache, DEFAULT_TIMEOUT, rules);
	}

	public InvokeCacheWrapper(Cache cache, int timeout, Rule... rules) {
		if (cache == null) {
			throw new IllegalArgumentException("Illegal cache:" + cache);
		}
		if (rules == null || rules.length == 0) {
			throw new IllegalArgumentException("Illegal rules:" + Strings.toString(rules));
		}
		this.cache = cache;
		this.rules = rules;
		this.timeout = timeout;
	}

	/**
	 * 系统缓存初始化
	 * 
	 * @param requester
	 *            请求对象
	 */
	protected void initialize(Requester requester) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		if (this.targets == null || this.refreshs == null) {
			synchronized (this) {
				if (this.targets == null || this.refreshs == null) {
					this.targets = new HashMap<String, Rule>();
					this.refreshs = new HashMap<String, Set<String>>();
					List<String> apis = requester.getChannel().getContext().getRouter().getApis();
					for (int i = 0; i < apis.size(); i++) {
						String api = apis.get(i);
						for (Rule rule : this.rules) {
							if (Strings.matches(api, rule.getTarget())) {
								this.targets.put(api, rule);
								break;
							} else if (!Strings.matches(api, rule.getRefresh())) {
								continue;
							}
							for (int n = 0; n < apis.size(); n++) {
								String resource = apis.get(n);
								if (Strings.matches(resource, rule.getTarget())) {
									Set<String> resources = this.refreshs.get(api);
									if (resources == null) {
										resources = new HashSet<String>();
										this.refreshs.put(api, resources);
									}
									resources.add(resource);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onInvokeEvent(InvokeAfterEvent event) {
		Requester requester = event.getSource();
		this.initialize(requester);
		Set<String> refresh = this.refreshs.get(requester.getUri());
		if (refresh != null) {
			for (final String uri : refresh) {
				this.remove(new StringBuilder("{").append(uri).append("}*").toString());
			}
		}
	}

	@Override
	public String key(Requester requester) {
		this.initialize(requester);
		Rule rule = this.targets.get(requester.getUri());
		if (rule == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder().append('{').append(requester.getUri()).append('}');
		if (requester.getUser() != null && rule.getScope() == Scope.USER) {
			buffer.append('{').append(requester.getUser()).append('}');
		}
		Set<String> condtions = requester.getParameterNames();
		if (condtions.isEmpty()) {
			return buffer.toString();
		}
		int i = 0;
		buffer.append('{');
		for (String key : Beans.sort(condtions)) {
			if (key == null || key.isEmpty()) {
				continue;
			}
			if (i++ > 0) {
				buffer.append(',');
			}
			Object value = requester.getParameter(key);
			if (value instanceof Collection) {
				for (Object v : Beans.sort((Collection<?>) value)) {
					buffer.append(key).append('=');
					if (!Beans.isEmpty(v)) {
						buffer.append(Strings.toString(v));
					}
				}
			} else if (value instanceof Object[]) {
				for (Object v : Beans.sort(Arrays.asList((Object[]) value))) {
					buffer.append(key).append('=');
					if (!Beans.isEmpty(v)) {
						buffer.append(Strings.toString(v));
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

	@Override
	public boolean isCacheable(Requester requester) {
		this.initialize(requester);
		return this.targets.containsKey(requester.getUri());
	}

	@Override
	public Object get(String key) {
		return this.cache.get(key);
	}

	@Override
	public void set(String key, Object value) {
		this.set(key, value, this.timeout);
	}

	@Override
	public void set(String key, Object value, int timeout) {
		this.cache.set(key, value, timeout);
	}

	@Override
	public void remove(String key) {
		this.cache.remove(key);
	}

	@Override
	public boolean exists(String key) {
		return this.cache.exists(key);
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

	@Override
	public void destroy() {
		this.cache.destroy();
	}

	@Override
	public boolean isDestroyed() {
		return this.cache.isDestroyed();
	}

}
