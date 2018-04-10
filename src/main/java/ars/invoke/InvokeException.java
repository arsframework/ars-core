package ars.invoke;

/**
 * 请求调用异常
 *
 * @author wuyongqiang
 */
public class InvokeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvokeException(String message) {
        super(message);
    }

}
