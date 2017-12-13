package ars.invoke.cache;

import java.io.Serializable;

/**
 * 缓存规则
 * 
 * @author yongqiangwu
 * 
 */
public class Rule implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认缓存超时时间（秒）
	 */
	public static final int DEFAULT_TIMEOUT = 30 * 60;

	private Scope scope; // 缓存规则范围
	private String target; // 缓存目标地址
	private String refresh; // 缓存刷新触发地址
	private int timeout; // 超时时间（秒），0表示永久有效

	public Rule(String target) {
		this(target, Scope.USER, DEFAULT_TIMEOUT, null);
	}

	public Rule(String target, Scope scope) {
		this(target, scope, DEFAULT_TIMEOUT, null);
	}

	public Rule(String target, int timeout) {
		this(target, Scope.USER, timeout, null);
	}

	public Rule(String target, String refresh) {
		this(target, Scope.USER, DEFAULT_TIMEOUT, refresh);
	}

	public Rule(String target, Scope scope, int timeout) {
		this(target, scope, timeout, null);
	}

	public Rule(String target, Scope scope, String refresh) {
		this(target, scope, DEFAULT_TIMEOUT, refresh);
	}

	public Rule(String target, int timeout, String refresh) {
		this(target, Scope.USER, timeout, refresh);
	}

	public Rule(String target, Scope scope, int timeout, String refresh) {
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (scope == null) {
			throw new IllegalArgumentException("Illegal scope:" + scope);
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("Illegal timeout:" + timeout);
		}
		this.target = target;
		this.scope = scope;
		this.timeout = timeout;
		this.refresh = refresh;
	}

	public String getTarget() {
		return target;
	}

	public Scope getScope() {
		return scope;
	}

	public int getTimeout() {
		return timeout;
	}

	public String getRefresh() {
		return refresh;
	}

	/**
	 * 规则应用范围
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public enum Scope {
		/**
		 * 用户范围
		 */
		USER,

		/**
		 * 应用范围
		 */
		APPLICATION;

	}

}
