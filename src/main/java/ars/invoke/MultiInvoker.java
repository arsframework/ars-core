package ars.invoke;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import ars.server.Servers;
import ars.invoke.Invoker;
import ars.invoke.MultiResource;
import ars.invoke.request.Requester;

/**
 * 多资源调用实现
 * 
 * @author yongqiangwu
 *
 */
public class MultiInvoker implements Invoker {

	@Override
	public Object execute(final Requester requester, Resource resource) throws Exception {
		String[] uris = ((MultiResource) resource).getResources();
		List<Object> results = new ArrayList<Object>(uris.length);
		for (final String uri : uris) {
			if (uri.equals(requester.getUri())) {
				throw new RuntimeException("Resource address cycle reference:" + uri);
			}
			Object result = Servers.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return requester.execute(uri);
				}

			}).get();
			if (result instanceof Exception) {
				throw (Exception) result;
			}
			results.add(result);
		}
		return results;
	}

}
