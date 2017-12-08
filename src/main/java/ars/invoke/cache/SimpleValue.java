package ars.invoke.cache;

import ars.invoke.cache.Value;

/**
 * 缓存值对象简单实现
 * 
 * @author wuyq
 *
 */
public class SimpleValue implements Value {
	private static final long serialVersionUID = 1L;

	private boolean cached;
	private transient Object content;

	public SimpleValue(boolean cached, Object content) {
		this.cached = cached;
		this.content = content;
	}

	@Override
	public boolean isCached() {
		return this.cached;
	}

	@Override
	public Object getContent() {
		return this.content;
	}

}
