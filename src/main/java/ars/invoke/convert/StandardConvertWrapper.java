package ars.invoke.convert;

import java.util.Map;
import java.util.HashMap;

import ars.util.Beans;
import ars.invoke.InvokeException;
import ars.invoke.convert.Converter;
import ars.invoke.convert.ThrowableResolver;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.request.TokenInvalidException;
import ars.invoke.request.RequestHandleException;
import ars.invoke.request.ParameterInvalidException;

/**
 * 对象转换包装标准实现
 * 
 * @author yongqiangwu
 *
 */
public class StandardConvertWrapper implements Converter {
	/**
	 * 结果码标识
	 */
	public static final String KEY_CODE = "code";

	/**
	 * 结果异常信息标识
	 */
	public static final String KEY_ERROR = "error";

	/**
	 * 结果内容标识
	 */
	public static final String KEY_CONTENT = "content";

	/**
	 * 请求成功编码
	 */
	public static final int CODE_SUCCESS = 200;

	/**
	 * 请求未知异常编码
	 */
	public static final int CODE_ERROR = 500;

	/**
	 * 请求调用异常
	 */
	public static final int CODE_ERROR_INVOKE = 5100;

	/**
	 * 拒绝访问异常编码
	 */
	public static final int CODE_ERROR_ACCESS_DENIED = 51000;

	/**
	 * 令牌无效异常编码
	 */
	public static final int CODE_ERROR_TOKEN_INVALID = 51010;

	/**
	 * 请求处理异常
	 */
	public static final int CODE_ERROR_REQUEST_HANDLE = 52000;

	/**
	 * 参数无效异常编码
	 */
	public static final int CODE_ERROR_PARAMETER_INVALID = 52010;

	protected final Converter converter;
	protected final ThrowableResolver[] throwableResolvers;

	public StandardConvertWrapper(Converter converter) {
		this(converter, new ThrowableResolver[0]);
	}

	public StandardConvertWrapper(Converter converter, ThrowableResolver... throwableResolvers) {
		if (converter == null) {
			throw new IllegalArgumentException("Illegal converter:" + converter);
		}
		this.converter = converter;
		this.throwableResolvers = throwableResolvers;
	}

	/**
	 * 查找异常转换处理器
	 * 
	 * @param throwable
	 *            异常对象
	 * @return 异常转换处理器
	 */
	protected ThrowableResolver lookupThrowableResolver(Throwable throwable) {
		for (ThrowableResolver resolver : this.throwableResolvers) {
			if (resolver.isResolvable(throwable)) {
				return resolver;
			}
		}
		return null;
	}

	/**
	 * 对象包装
	 * 
	 * @param object
	 *            被包装对象
	 * @return 包装结果
	 */
	protected Map<String, Object> wrap(Object object) {
		int code = CODE_SUCCESS;
		String error = null;
		Object content = null;
		if (object instanceof ParameterInvalidException) {
			code = CODE_ERROR_PARAMETER_INVALID;
		} else if (object instanceof TokenInvalidException) {
			code = CODE_ERROR_TOKEN_INVALID;
		} else if (object instanceof AccessDeniedException) {
			code = CODE_ERROR_ACCESS_DENIED;
		} else if (object instanceof RequestHandleException) {
			code = CODE_ERROR_REQUEST_HANDLE;
		} else if (object instanceof InvokeException) {
			code = CODE_ERROR_INVOKE;
		} else if (object instanceof Exception) {
			code = CODE_ERROR;
		} else {
			content = object;
		}
		if (object instanceof Throwable) {
			Throwable throwable = Beans.getThrowableCause((Throwable) object);
			ThrowableResolver resolver = this.lookupThrowableResolver(throwable);
			if (resolver != null) {
				code = resolver.getCode(throwable);
				error = resolver.getMessage(throwable);
			} else if (throwable instanceof ParameterInvalidException) {
				ParameterInvalidException exception = (ParameterInvalidException) object;
				error = exception.getError();
				content = exception.getName();
			} else {
				error = throwable.getMessage();
			}
		}
		Map<String, Object> wrap = new HashMap<String, Object>(3);
		wrap.put(KEY_CODE, code);
		wrap.put(KEY_ERROR, error);
		wrap.put(KEY_CONTENT, content);
		return wrap;
	}

	/**
	 * 对象解包
	 * 
	 * @param wrap
	 *            对象包装实例
	 * @return 原始对象
	 */
	protected Object unwrap(Map<?, ?> wrap) {
		Integer code = (Integer) wrap.get(KEY_CODE);
		String error = (String) wrap.get(KEY_ERROR);
		Object content = wrap.get(KEY_CONTENT);
		if (code == CODE_ERROR_PARAMETER_INVALID) {
			throw new ParameterInvalidException((String) content, error);
		} else if (code == CODE_ERROR_TOKEN_INVALID) {
			throw new TokenInvalidException(error);
		} else if (code == CODE_ERROR_ACCESS_DENIED) {
			throw new AccessDeniedException(error);
		} else if (code == CODE_ERROR_REQUEST_HANDLE) {
			throw new RequestHandleException(error);
		} else if (code == CODE_ERROR_INVOKE) {
			throw new InvokeException(error);
		} else if (code == CODE_ERROR) {
			throw new RuntimeException(error);
		}
		return content;
	}

	@Override
	public String serialize(Object object) {
		return this.converter.serialize(this.wrap(object));
	}

	@Override
	public Object deserialize(String string) {
		return this.unwrap((Map<?, ?>) this.converter.deserialize(string));
	}

}
