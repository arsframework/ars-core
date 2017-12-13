package ars.invoke.cache;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

import ars.util.Strings;
import ars.invoke.Context;
import ars.invoke.cache.Key;
import ars.invoke.cache.Rule;
import ars.invoke.cache.Cache;
import ars.invoke.cache.SimpleKey;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeAfterEvent;

/**
 * 数据缓存接口抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractCache implements Cache, InvokeListener<InvokeAfterEvent> {
	protected final Rule[] rules;
	private boolean initialized;
	private final Map<String, Rule> targets = new HashMap<String, Rule>();
	private final Map<String, Set<String>> refreshs = new HashMap<String, Set<String>>();

	public AbstractCache(Rule... rules) {
		if (rules == null || rules.length == 0) {
			throw new IllegalArgumentException("Illegal rules:" + Strings.toString(rules));
		}
		this.rules = rules;
	}

	@Override
	public void initialize(Context context) {
		if (!this.initialized) {
			synchronized (AbstractCache.class) {
				if (!this.initialized) {
					this.initialized = true;
					List<String> apis = context.getRouter().getApis();
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
		Set<String> refresh = this.refreshs.get(event.getSource().getUri());
		if (refresh != null) {
			for (String uri : refresh) {
				this.removeCache(new SimpleKey().uri(uri).user("*"));
			}
		}
	}

	@Override
	public boolean isCacheable(Requester requester) {
		return this.targets.containsKey(requester.getUri());
	}

	@Override
	public Key getKey(Requester requester) {
		Rule rule = this.targets.get(requester.getUri());
		if (rule == null) {
			return null;
		}
		SimpleKey key = new SimpleKey().uri(requester.getUri()).timeout(rule.getTimeout())
				.condtion(requester.getParameters());
		return rule.getScope() == Rule.Scope.USER ? key.user(requester.getUser()) : key;
	}

}
