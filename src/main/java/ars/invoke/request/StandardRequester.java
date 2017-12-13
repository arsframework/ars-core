package ars.invoke.request;

import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.UUID;
import java.util.Locale;
import java.util.HashMap;
import java.util.Collections;

import ars.util.Strings;
import ars.invoke.Channel;
import ars.invoke.Messager;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;

/**
 * 请求对象标准实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardRequester implements Requester {
	private static final long serialVersionUID = 1L;

	private String id; // 请求标识
	private String uri; // 资源地址
	private String host; // 客户主机
	private Token token; // 请求令牌
	private Locale locale; // 客户语言环境
	private String client; // 客户端标识
	private Requester parent; // 父级请求对象
	private transient Channel channel; // 请求通道
	private Map<String, Object> parameters; // 请求参数
	private Date created = new Date(); // 请求时间

	public StandardRequester(Channel channel, Requester parent, Locale locale, String client, String host, Token token,
			String uri, Map<String, Object> parameters) {
		if (channel == null) {
			throw new IllegalArgumentException("Illegal channel:" + channel);
		}
		if (locale == null) {
			throw new IllegalArgumentException("Illegal locale:" + locale);
		}
		if (Strings.isEmpty(client)) {
			throw new IllegalArgumentException("Illegal client:" + client);
		}
		if (Strings.isEmpty(host)) {
			throw new IllegalArgumentException("Illegal host:" + host);
		}
		if (Strings.isEmpty(uri)) {
			throw new IllegalArgumentException("Illegal uri:" + uri);
		}
		this.uri = uri;
		this.host = host;
		this.token = token;
		this.locale = locale;
		this.client = client;
		this.parent = parent;
		this.channel = channel;
		this.parameters = parameters == null ? Collections.<String, Object>emptyMap() : parameters;
	}

	@Override
	public Channel getChannel() {
		return this.channel;
	}

	@Override
	public Requester getParent() {
		return this.parent;
	}

	@Override
	public Date getCreated() {
		return this.created;
	}

	@Override
	public String getId() {
		if (this.id == null) {
			synchronized (this) {
				if (this.id == null) {
					this.id = UUID.randomUUID().toString();
				}
			}
		}
		return this.id;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public String getUser() {
		return this.token == null ? null : this.token.getAudience();
	}

	@Override
	public Token getToken() {
		return this.token;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getClient() {
		return this.client;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public Set<String> getParameterNames() {
		return this.parameters.keySet();
	}

	@Override
	public boolean hasParameter(String key) {
		return this.parameters.containsKey(key);
	}

	@Override
	public Object getParameter(String key) {
		return this.parameters.get(key);
	}

	@Override
	public Map<String, Object> getParameters() {
		return new HashMap<String, Object>(this.parameters);
	}

	@Override
	public Requester build(String uri) {
		return this.build(uri, this.parameters);
	}

	@Override
	public Requester build(Map<String, Object> parameters) {
		return this.build(this.uri, parameters);
	}

	@Override
	public Requester build(String uri, Map<String, Object> parameters) {
		return new StandardRequester(this.channel, this, this.locale, this.client, this.host, this.token, uri,
				parameters);
	}

	@Override
	public Object execute() {
		return this.channel.getContext().getRouter().routing(this);
	}

	@Override
	public Object execute(String uri) {
		return this.execute(uri, this.parameters);
	}

	@Override
	public Object execute(Map<String, Object> parameters) {
		return this.execute(this.uri, parameters);
	}

	@Override
	public Object execute(String uri, Map<String, Object> parameters) {
		return this.build(uri, parameters).execute();
	}

	@Override
	public String format(String key) {
		return this.format(key, null, key);
	}

	@Override
	public String format(String key, String text) {
		return this.format(key, null, text);
	}

	@Override
	public String format(String key, Object[] args) {
		return this.format(key, args, key);
	}

	@Override
	public String format(String key, Object[] args, String text) {
		Messager messager = this.channel.getContext().getMessager();
		return messager == null ? key : messager.format(this.locale, key, args, text);
	}

}
