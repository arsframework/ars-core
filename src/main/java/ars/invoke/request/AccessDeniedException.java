package ars.invoke.request;

import ars.invoke.InvokeException;

/**
 * 拒绝访问异常
 * 
 * @author wuyq
 * 
 */
public class AccessDeniedException extends InvokeException {
	private static final long serialVersionUID = 1L;

	public AccessDeniedException(String message) {
		super(message);
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}
