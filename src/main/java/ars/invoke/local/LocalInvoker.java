package ars.invoke.local;

import java.lang.reflect.InvocationTargetException;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.request.Requester;

/**
 * 本地资源调用实现
 *
 * @author wuyongqiang
 */
public class LocalInvoker implements Invoker {

    @Override
    public Object execute(Requester requester, Resource resource) throws Exception {
        Function function = (Function) resource;
        try {
            return function.getMethod().invoke(function.getTarget(), Apis.getParameters(requester, function));
        } catch (InvocationTargetException e) {
            Throwable error = e.getTargetException();
            if (error instanceof Exception) {
                throw (Exception) error;
            }
            throw new Exception(error);
        }
    }

}
