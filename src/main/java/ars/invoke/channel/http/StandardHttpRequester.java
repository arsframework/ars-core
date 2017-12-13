package ars.invoke.channel.http;

import java.util.Map;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.invoke.Channel;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.invoke.request.StandardRequester;
import ars.invoke.channel.http.HttpRequester;

/**
 * 基于HTTP请求基础实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardHttpRequester extends StandardRequester implements HttpRequester {
	private static final long serialVersionUID = 1L;

	private transient ServletConfig config;
	private transient HttpServletRequest request;
	private transient HttpServletResponse response;

	public StandardHttpRequester(Channel channel, ServletConfig config, HttpServletRequest request,
			HttpServletResponse response, Requester parent, Locale locale, String client, String host, Token token,
			String uri, Map<String, Object> parameters) {
		super(channel, parent, locale, client, host, token, uri, parameters);
		this.config = config;
		this.request = request;
		this.response = response;
	}

	@Override
	public ServletConfig getServletConfig() {
		return this.config;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return this.request;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return this.response;
	}

	@Override
	public Requester build(String uri, Map<String, Object> parameters) {
		return new StandardHttpRequester(this.getChannel(), this.config, this.request, this.response, this,
				this.getLocale(), this.getClient(), this.getHost(), this.getToken(), uri, parameters);
	}

}
