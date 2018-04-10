package ars.invoke.request;

/**
 * 令牌无效异常
 *
 * @author wuyongqiang
 */
public class TokenInvalidException extends AccessDeniedException {
    private static final long serialVersionUID = 1L;

    public TokenInvalidException(String message) {
        super(message);
    }

}
