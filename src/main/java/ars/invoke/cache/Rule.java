package ars.invoke.cache;

import java.io.Serializable;

import ars.invoke.cache.InvokeCache.Scope;

/**
 * 缓存规则
 * 
 * @author yongqiangwu
 * 
 */
public class Rule implements Serializable {
	private static final long serialVersionUID = 1L;

	private Scope scope; // 缓存规则范围
	private String target; // 缓存目标地址
	private String refresh; // 缓存刷新触发地址

	public Rule(String target) {
		this(target, null, Scope.USER);
	}

	public Rule(String target, Scope scope) {
		this(target, null, scope);
	}

	public Rule(String target, String refresh) {
		this(target, refresh, Scope.USER);
	}

	public Rule(String target, String refresh, Scope scope) {
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (scope == null) {
			throw new IllegalArgumentException("Illegal scope:" + scope);
		}
		this.scope = scope;
		this.target = target;
		this.refresh = refresh;
	}

	public String getTarget() {
		return target;
	}

	public Scope getScope() {
		return scope;
	}

	public String getRefresh() {
		return refresh;
	}

}
