package ars.invoke.channel.http;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.remote.Node;
import ars.invoke.remote.Protocol;
import ars.invoke.remote.Endpoint;
import ars.invoke.request.Requester;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.HttpRequester;

/**
 * Http远程调用抽象实现
 * 
 * @author yongqiangwu
 *
 */
public abstract class AbstractHttpInvoker implements Invoker {
	/**
	 * 接收响应结果
	 * 
	 * @param requester
	 *            请求对象
	 * @param endpoint
	 *            远程端点
	 * @param response
	 *            Http响应对象
	 * @return 响应结果
	 * @throws Exception
	 *             操作异常
	 */
	protected abstract Object accept(Requester requester, Endpoint endpoint, HttpResponse response) throws Exception;

	@Override
	public Object execute(Requester requester, Resource resource) throws Exception {
		Endpoint endpoint = (Endpoint) resource;
		String uri = endpoint.getUri();
		Node[] nodes = endpoint.getNodes();
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			String url = Https.getUrl(node, uri == null ? requester.getUri() : uri);
			HttpUriRequest entity = Https.getHttpUriRequest(url.toString(), Https.Method.POST,
					requester.getParameters());
			entity.addHeader(Https.CONTEXT_TOKEN, requester.getToken().getCode());
			if (requester instanceof HttpRequester) {
				HttpServletRequest request = ((HttpRequester) requester).getHttpServletRequest();
				Enumeration<String> headers = request.getHeaderNames();
				while (headers.hasMoreElements()) {
					String header = headers.nextElement();
					entity.setHeader(header, request.getHeader(header));
				}
			}
			HttpClient client = Https.getClient(node.getProtocol() == Protocol.https);
			try {
				return this.accept(requester, endpoint, client.execute(entity));
			} catch (Exception e) {
				if (i == nodes.length - 1) {
					throw e;
				}
			} finally {
				entity.abort();
			}
		}
		return null;
	}

}
