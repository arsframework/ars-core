package ars.invoke.remote;

import java.util.Map;
import java.util.Locale;

import ars.invoke.Channel;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.invoke.request.StandardRequester;
import ars.invoke.remote.RemoteRequester;

/**
 * 基于ICE请求对象标准实现
 * 
 * @author wuyq
 * 
 */
public class StandardRemoteRequester extends StandardRequester implements RemoteRequester {
	private static final long serialVersionUID = 1L;

	private transient Ice.Current context;

	public StandardRemoteRequester(Channel channel, Ice.Current context, Requester parent, Locale locale, String client,
			String host, Token token, String uri, Map<String, Object> parameters) {
		super(channel, parent, locale, client, host, token, uri, parameters);
		this.context = context;
	}

	@Override
	public Ice.Current getIceContext() {
		return this.context;
	}

	@Override
	public Requester build(String uri, Map<String, Object> parameters) {
		return new StandardRemoteRequester(this.getChannel(), this.context, this, this.getLocale(), this.getClient(),
				this.getHost(), this.getToken(), uri, parameters);
	}

}
