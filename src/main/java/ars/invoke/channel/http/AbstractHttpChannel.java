package ars.invoke.channel.http;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.Context;
import ars.invoke.request.Requester;
import ars.invoke.convert.Converter;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.Render;
import ars.invoke.channel.http.Redirector;
import ars.invoke.channel.http.HttpChannel;
import ars.invoke.channel.http.HttpRequester;

/**
 * Http请求通道抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractHttpChannel implements HttpChannel {
	private Context context; // 应用上下文
	private String directory; // 文件目录
	private Redirector[] redirectors = new Redirector[0]; // 请求重定向配置
	private Map<String, Render> renders = new HashMap<String, Render>(0); // 视图渲染配置
	private Map<String, String> templates = new HashMap<String, String>(0); // 模板映射
	private Map<String, Converter> converters = new HashMap<String, Converter>(0); // 数据转换映射

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Redirector[] getRedirectors() {
		return redirectors;
	}

	public void setRedirectors(Redirector... redirectors) {
		this.redirectors = redirectors;
	}

	public Map<String, Render> getRenders() {
		return renders;
	}

	public void setRenders(Map<String, Render> renders) {
		this.renders = renders;
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
	 * 查找请求资源模板
	 * 
	 * @param requester
	 *            请求对象
	 * @return 模板路径
	 */
	protected String lookupTemplate(HttpRequester requester) {
		String template = null;
		String uri = requester.getUri();
		if (uri.indexOf('.') >= 0) {
			template = uri;
		} else if (!this.templates.isEmpty()) {
			for (Entry<String, String> entry : this.templates.entrySet()) {
				if (Strings.matches(uri, entry.getKey())) {
					template = entry.getValue();
					break;
				}
			}
		}
		if (template != null) {
			int index = template.indexOf('?');
			if (index > 0) {
				template = template.substring(0, index);
			}
			if (this.directory != null) {
				template = new StringBuilder(this.directory).append('/').append(template).toString();
			}
		}
		return template;
	}

	/**
	 * 查找请求转换处理对象
	 * 
	 * @param requester
	 *            请求对象
	 * @return 数据转换处理对象
	 */
	protected Converter lookupConverter(HttpRequester requester) {
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
	 * 请求重定向
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            重定向内容
	 * @return 是否重定向成功
	 * @throws Exception
	 *             操作异常
	 */
	protected boolean redirect(HttpRequester requester, Object content) throws Exception {
		for (Redirector redirector : this.redirectors) {
			String redirect = redirector.getRedirect(requester, content);
			if (redirect == null) {
				continue;
			}
			if (redirect.indexOf('.') > 0) {
				if (this.directory != null) {
					redirect = new StringBuilder(this.directory).append('/').append(redirect).toString();
				}
				requester.render(redirect, content);
			} else {
				String context = requester.getHttpServletRequest().getContextPath();
				if (context != null && !context.isEmpty()) {
					redirect = new StringBuilder(context).append(redirect).toString();
				}
				requester.getHttpServletResponse().sendRedirect(redirect);
			}
			return true;
		}
		return false;
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
		Object value = null;
		Converter converter = null;
		HttpRequester requester = this.getRequester(this.getUri(request), config, request, response);
		String template = this.lookupTemplate(requester);
		if (template == null) {
			try {
				value = this.dispatch(requester);
			} catch (Exception e) {
				value = e;
			}
			if (!Streams.isStream(value) && (converter = this.lookupConverter(requester)) != null) {
				value = converter.serialize(value);
			}
		} else if (Strings.isEmpty(request.getContentType()) || (converter = this.lookupConverter(requester)) == null) {
			try {
				requester.render(template, null);
			} catch (Exception e) {
				value = e;
			}
		} else {
			try {
				value = requester.view(template, null);
			} catch (Exception e) {
				value = e;
			}
			value = converter.serialize(value);
		}
		if (value != null && !this.redirect(requester, value)) {
			if (value instanceof Exception) {
				throw (Exception) value;
			}
			Https.response(response, value);
		}
	}

}
