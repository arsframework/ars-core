package ars.invoke.channel.http;

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Collection;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.util.Files;
import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.Channel;
import ars.invoke.request.Token;
import ars.invoke.request.Session;
import ars.invoke.request.Requester;
import ars.invoke.request.StandardRequester;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.Render;
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
	private transient Map<String, Render> renders;

	public StandardHttpRequester(Channel channel, ServletConfig config, HttpServletRequest request,
			HttpServletResponse response, Map<String, Render> renders, Requester parent, Locale locale, String client,
			String host, Token token, String uri, Map<String, Object> parameters) {
		super(channel, parent, locale, client, host, token, uri, parameters);
		if (config == null) {
			throw new IllegalArgumentException("Illegal config:" + config);
		}
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (renders == null) {
			throw new IllegalArgumentException("Illegal renders:" + renders);
		}
		this.config = config;
		this.request = request;
		this.response = response;
		this.renders = renders;
	}

	/**
	 * 查找视图渲染器
	 * 
	 * @param template
	 *            视图模板
	 * @return 视图渲染器
	 */
	protected Render loolupRender(String template) {
		if (!this.renders.isEmpty()) {
			for (Entry<String, Render> entry : this.renders.entrySet()) {
				if (Strings.matches(template, entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 获取视图渲染上下文
	 * 
	 * @param content
	 *            渲染内容
	 * @return 上下文键/值映射表
	 */
	protected Map<String, Object> getRenderContext(Object content) {
		Date datetime = new Date();
		final Session session = this.getSession();
		Map<String, Object> context = new HashMap<String, Object>(15);
		context.put(Https.CONTEXT_URI, this.getUri());
		context.put(Https.CONTEXT_URL, Https.getUrl(this.request));
		context.put(Https.CONTEXT_HOST, this.getHost());
		context.put(Https.CONTEXT_PATH, this.request.getContextPath());
		context.put(Https.CONTEXT_PORT, this.request.getServerPort());
		context.put(Https.CONTEXT_TOKEN, this.getToken());
		context.put(Https.CONTEXT_SCHEME, this.request.getScheme());
		context.put(Https.CONTEXT_DOMAIN, this.request.getServerName());
		context.put(Https.CONTEXT_SERVER, Strings.LOCALHOST_ADDRESS);
		context.put(Https.CONTEXT_REQUEST, this.getParameters());
		context.put(Https.CONTEXT_SESSION, new Map<String, Object>() {

			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean containsKey(Object key) {
				return false;
			}

			@Override
			public boolean containsValue(Object value) {
				return false;
			}

			@Override
			public Object get(Object key) {
				return key == null ? null : session.getAttribute(Strings.toString(key));
			}

			@Override
			public Object put(String key, Object value) {
				return null;
			}

			@Override
			public Object remove(Object key) {
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ? extends Object> m) {

			}

			@Override
			public void clear() {

			}

			@Override
			public Set<String> keySet() {
				return null;
			}

			@Override
			public Collection<Object> values() {
				return null;
			}

			@Override
			public Set<Entry<String, Object>> entrySet() {
				return null;
			}

			@Override
			public String toString() {
				return session.toString();
			}

		});
		context.put(Https.CONTEXT_RESPONSE, content);
		context.put(Https.CONTEXT_DATETIME, datetime);
		context.put(Https.CONTEXT_EXECUTOR, this);
		context.put(Https.CONTEXT_TIMESPEND, datetime.getTime() - this.getCreated().getTime());
		return context;
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
	public String getCookie(String name) {
		return Https.getCookie(this.request, name);
	}

	@Override
	public void setCookie(String name, String value, int timeout) {
		Https.setCookie(this.response, name, value, timeout);
	}

	@Override
	public String removeCookie(String name) {
		return Https.removeCookie(this.request, this.response, name);
	}

	@Override
	public String view(String template, Object content) throws Exception {
		Render render = this.loolupRender(template);
		Map<String, Object> context = this.getRenderContext(content);
		if (render == null) {
			if ("jsp".equalsIgnoreCase(Files.getSuffix(template))) {
				return Https.view(this.request, this.response, template, context);
			}
			return Files.getString(new File(template));
		}
		OutputStream os = new ByteArrayOutputStream();
		try {
			render.execute(this, template, context, os);
		} finally {
			os.close();
		}
		return os.toString();
	}

	@Override
	public void render(String template, Object content) throws Exception {
		Https.render(this.request, this.response, template, this.getRenderContext(content));
	}

	@Override
	public void render(String template, Object content, File file) throws Exception {
		File directory = file.getParentFile();
		if (directory != null && !directory.exists()) {
			directory.mkdirs();
		}
		OutputStream os = new FileOutputStream(file);
		try {
			this.render(template, content, os);
		} finally {
			os.close();
		}
	}

	@Override
	public void render(String template, Object content, OutputStream output) throws Exception {
		Render render = this.loolupRender(template);
		Map<String, Object> context = this.getRenderContext(content);
		OutputStream os = this.response.getOutputStream();
		try {
			if (render == null) {
				if ("jsp".equalsIgnoreCase(Files.getSuffix(template))) {
					Https.render(this.request, this.response, template, context, output);
				} else {
					Streams.write(new File(template), os);
				}
			} else {
				render.execute(this, template, context, os);
			}
		} finally {
			os.close();
		}
	}

	@Override
	public Requester build(String uri, Map<String, Object> parameters) {
		return new StandardHttpRequester(this.getChannel(), this.config, this.request, this.response, this.renders,
				this, this.getLocale(), this.getClient(), this.getHost(), this.getToken(), uri, parameters);
	}

}
