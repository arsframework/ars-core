package ars.invoke.channel.http;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.remote.Node;
import ars.invoke.remote.Protocol;
import ars.invoke.remote.Endpoint;
import ars.invoke.request.Requester;
import ars.invoke.channel.http.Https;

/**
 * Http远程调用抽象实现
 * 
 * @author yongqiangwu
 *
 */
public abstract class AbstractHttpInvoker implements Invoker {
	private ClientConnectionManager manager;

	public AbstractHttpInvoker() {
		this(new PoolingClientConnectionManager());
	}

	public AbstractHttpInvoker(ClientConnectionManager manager) {
		if (manager == null) {
			throw new IllegalArgumentException("Illegal manager:" + manager);
		}
		this.manager = manager;
	}

	public ClientConnectionManager getManager() {
		return manager;
	}

	/**
	 * 获取响应结果
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
	protected abstract Object response(Requester requester, Endpoint endpoint, HttpResponse response) throws Exception;

	@Override
	public Object execute(Requester requester, Resource resource) throws Exception {
		Endpoint endpoint = (Endpoint) resource;
		String uri = endpoint.getUri();
		Node[] nodes = endpoint.getNodes();
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			String url = Https.getUrl(node, uri == null ? requester.getUri() : uri);
			HttpUriRequest httpUriRequest = Https.getHttpUriRequest(url.toString(), Https.Method.POST,
					requester.getParameters());
			httpUriRequest.addHeader(Https.CONTEXT_TOKEN, requester.getToken().getCode());
			if (requester instanceof HttpRequester) {
				HttpServletRequest httpServletRequest = ((HttpRequester) requester).getHttpServletRequest();
				Enumeration<String> headers = httpServletRequest.getHeaderNames();
				while (headers.hasMoreElements()) {
					String header = headers.nextElement();
					httpUriRequest.setHeader(header, httpServletRequest.getHeader(header));
				}
			}
			DefaultHttpClient client = new DefaultHttpClient(this.manager);
			if (node.getProtocol() == Protocol.https) {
				Https.bindSSL(client);
			}
			client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
			try {
				return this.response(requester, endpoint, client.execute(httpUriRequest));
			} catch (Exception e) {
				if (i == nodes.length - 1) {
					throw e;
				}
			} finally {
				httpUriRequest.abort();
			}
		}
		return null;
	}

}
