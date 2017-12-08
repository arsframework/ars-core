package ars.invoke.request;

import ars.invoke.request.AccessDeniedException;

/**
 * 令牌无效异常
 * 
 * @author wuyq
 * 
 */
public class TokenInvalidException extends AccessDeniedException {
	private static final long serialVersionUID = 1L;

	public TokenInvalidException(String message) {
		super(message);
	}

}
