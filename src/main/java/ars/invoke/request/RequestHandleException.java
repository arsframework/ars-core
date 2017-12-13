package ars.invoke.request;

import ars.invoke.InvokeException;

/**
 * 请求处理异常
 * 
 * @author yongqiangwu
 * 
 */
public class RequestHandleException extends InvokeException {
	private static final long serialVersionUID = 1L;

	public RequestHandleException(String message) {
		super(message);
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}
