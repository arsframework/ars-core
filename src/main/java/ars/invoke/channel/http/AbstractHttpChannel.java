package ars.invoke.channel.http;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.util.Beans;
import ars.util.Files;
import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.Context;
import ars.invoke.Invokes;
import ars.invoke.request.Requester;
import ars.invoke.convert.Converter;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.HttpChannel;
import ars.invoke.channel.http.HttpRequester;

/**
 * Http请求通道抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractHttpChannel implements HttpChannel {
	private Context context;
	private String templateDirectory;
	private Redirector[] redirectors = new Redirector[0];
	private Map<String, String> templates = new HashMap<String, String>(0);
	private Map<String, Converter> converters = new HashMap<String, Converter>(0);

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}

	public Redirector[] getRedirectors() {
		return redirectors;
	}

	public void setRedirectors(Redirector... redirectors) {
		this.redirectors = redirectors;
	}

	public Map<String, String> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, String> templates) {
		this.templates = templates;
	}

	public Map<String, Converter> getConverters() {
		return converters;
	}

	public void setConverters(Map<String, Converter> converters) {
		this.converters = converters;
	}

	/**
	 * 获取请求对象
	 * 
	 * @param uri
	 *            资源地址
	 * @param config
	 *            Servlet配置对象
	 * @param request
	 *            Http请求对象
	 * @param response
	 *            Http响应对象
	 * @return 请求对象
	 * @throws IOException
	 *             IO操作异常
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	protected abstract HttpRequester getRequester(String uri, ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException;

	/**
	 * 获取请求资源地址
	 * 
	 * @param request
	 *            Http请求对象
	 * @return 资源地址
	 */
	protected String getUri(HttpServletRequest request) {
		String uri = request.getRequestURI().trim();
		String context = request.getContextPath();
		if (context != null) {
			uri = uri.substring(context.length());
		}
		if (uri.isEmpty()) {
			uri = Https.ROOT_URI;
		} else if (!uri.equals(Https.ROOT_URI)) {
			uri = uri.substring(1);
		}
		return uri;
	}

	/**
	 * 获取请求资源模板
	 * 
	 * @param requester
	 *            请求对象
	 * @return 模板路径
	 */
	protected String getTemplate(HttpRequester requester) {
		String uri = requester.getUri();
		if (uri.indexOf('.') >= 0) {
			return uri;
		}
		if (!this.templates.isEmpty()) {
			for (Entry<String, String> entry : this.templates.entrySet()) {
				if (Strings.matches(uri, entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 获取请求转换处理对象
	 * 
	 * @param requester
	 *            请求对象
	 * @return 数据转换处理对象
	 */
	protected Converter getConverter(HttpRequester requester) {
		if (!this.converters.isEmpty()) {
			for (Entry<String, Converter> entry : this.converters.entrySet()) {
				if (Strings.matches(requester.getUri(), entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            模板路径
	 * @param content
	 *            数据内容
	 * @return 模板内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	protected String render(HttpRequester requester, String template, Object content)
			throws IOException, ServletException {
		int index = template.indexOf('?');
		String suffix = Files.getSuffix(index < 0 ? template : template.substring(0, index));
		if (suffix.equalsIgnoreCase("jsp")) {
			if (this.templateDirectory == null) {
				return Https.render(requester, template, content);
			}
			return Https.render(requester, new StringBuilder(this.templateDirectory).append(template).toString(),
					content);
		}
		return new String(Streams.getBytes(new File(Https.ROOT_PATH, template)));
	}

	/**
	 * 请求重定向
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            重定向内容
	 * @return 是否重定向成功
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	protected boolean redirect(HttpRequester requester, Object content) throws IOException, ServletException {
		for (Redirector redirector : this.redirectors) {
			String redirect = redirector.getRedirect(requester, content);
			if (redirect == null) {
				continue;
			}
			if (redirect.indexOf('.') > 0) {
				Https.response(requester.getHttpServletResponse(), this.render(requester, redirect, content));
			} else {
				String path = requester.getHttpServletRequest().getContextPath();
				if (path != null && !path.isEmpty()) {
					redirect = new StringBuilder(path).append(redirect).toString();
				}
				requester.getHttpServletResponse().sendRedirect(redirect);
			}
			return true;
		}
		return false;
	}

	/**
	 * 响应请求结果
	 * 
	 * @param requester
	 *            请求对象
	 * @param value
	 *            请求结果
	 * @throws IOException
	 *             IO操作异常
	 */
	protected void response(HttpRequester requester, Object value) throws IOException {
		Https.response(requester.getHttpServletResponse(), value);
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Object dispatch(Requester requester) throws Exception {
		return requester.execute();
	}

	@Override
	public void dispatch(ServletConfig config, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpRequester requester = this.getRequester(this.getUri(request), config, request, response);
		Invokes.setCurrentRequester(requester);

		Object value = null;
		String template = this.getTemplate(requester);
		if (template == null) {
			value = this.dispatch(requester);
		} else {
			try {
				value = this.render((HttpRequester) requester, template, null);
			} catch (Throwable e) {
				value = Beans.getThrowableCause(e);
			}
		}

		Converter converter = null;
		if (!Streams.isStream(value) && !Strings.isEmpty(request.getContentType())
				&& (converter = this.getConverter(requester)) != null) {
			value = converter.serialize(value);
		}
		if (converter != null || !this.redirect((HttpRequester) requester, value)) {
			if (value instanceof Exception) {
				throw (Exception) value;
			}
			this.response(requester, value);
		}
	}

}
