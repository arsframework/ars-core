package ars.invoke.request;

import ars.invoke.request.RequestHandleException;

/**
 * 参数无效异常
 * 
 * @author wuyq
 * 
 */
public class ParameterInvalidException extends RequestHandleException {
	private static final long serialVersionUID = 1L;

	private String name; // 参数名称
	private String error; // 异常消息

	public ParameterInvalidException(String name, String error) {
		super(new StringBuilder(name).append(' ').append(error).toString());
		this.name = name;
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public String getError() {
		return error;
	}

}
