package ars.invoke.channel.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ClientConnectionManager;

import ars.util.Jsons;
import ars.invoke.remote.Endpoint;
import ars.invoke.request.Requester;
import ars.invoke.channel.http.AbstractHttpInvoker;

/**
 * 基于JSON数据格式的Http远程调用实现
 * 
 * @author yongqiangwu
 *
 */
public class HttpJsonInvoker extends AbstractHttpInvoker {

	public HttpJsonInvoker() {
		super();
	}

	public HttpJsonInvoker(ClientConnectionManager manager) {
		super(manager);
	}

	@Override
	protected Object response(Requester requester, Endpoint endpoint, HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		try {
			return Jsons.parse(EntityUtils.toString(entity));
		} finally {
			EntityUtils.consumeQuietly(entity);
		}
	}

}
