package ars.invoke.convert;

import ars.invoke.convert.ThrowableResolver;

/**
 * 异常转换简单实现
 * 
 * @author wuyq
 * 
 */
public class SimpleThrowableResolver implements ThrowableResolver {
	private Class<?> type;
	private int code;
	private String message;

	public SimpleThrowableResolver(Class<?> type, int code) {
		this(type, code, null);
	}

	public SimpleThrowableResolver(Class<?> type, int code, String message) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (code < 500) {
			throw new IllegalArgumentException("Illegal code:" + code);
		}
		this.type = type;
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode(Throwable throwable) {
		return this.code;
	}

	@Override
	public String getMessage(Throwable throwable) {
		return this.message == null ? throwable.getMessage() : this.message;
	}

	@Override
	public boolean isResolvable(Throwable throwable) {
		return throwable != null && this.type.isAssignableFrom(throwable.getClass());
	}

}
