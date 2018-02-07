package ars.invoke;

import java.io.Serializable;

/**
 * 资源数据缓存规则类
 * 
 * @author yongqiangwu
 *
 */
public class CacheRule implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认缓存超时时间（秒）
	 */
	public static final int DEFAULT_TIMEOUT = 30 * 60;

	private int timeout = DEFAULT_TIMEOUT; // 缓存超时时间（秒）
	private boolean global = true; // 是否是全局缓存
	private String target; // 缓存目标地址
	private String refresh; // 缓存刷新触发地址

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

}
