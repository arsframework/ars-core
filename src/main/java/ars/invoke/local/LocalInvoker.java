package ars.invoke.local;

import java.lang.reflect.InvocationTargetException;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;
import ars.invoke.request.Requester;

/**
 * 本地资源调用实现
 * 
 * @author wuyq
 * 
 */
public class LocalInvoker implements Invoker {
	/**
	 * 获取请求资源参数
	 * 
	 * @param requester
	 *            请求对象
	 * @param function
	 *            本地资源对象
	 * @return 参数数组
	 * @throws Exception
	 *             操作异常
	 */
	protected Object[] getParameters(Requester requester, Function function) throws Exception {
		return Apis.getParameters(requester, function.getConditions());
	}

	@Override
	public Object execute(Requester requester, Resource resource) throws Exception {
		Function function = (Function) resource;
		try {
			return function.getMethod().invoke(function.getTarget(), this.getParameters(requester, function));
		} catch (InvocationTargetException e) {
			Throwable error = e.getTargetException();
			if (error instanceof Exception) {
				throw (Exception) error;
			}
			throw new Exception(error);
		}
	}

}
