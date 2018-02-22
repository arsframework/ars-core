package ars.invoke.channel.http;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.channels.ReadableByteChannel;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ars.util.Nfile;
import ars.util.Files;
import ars.util.Jsons;
import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.remote.Node;
import ars.invoke.remote.Protocol;
import ars.invoke.request.Session;
import ars.invoke.channel.http.HttpRequester;

/**
 * Http操作工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Https {
	/**
	 * 应用根资源地址
	 */
	public static final String ROOT_URI = "/";

	/**
	 * 应用根路径
	 */
	public static final String ROOT_PATH;

	/**
	 * 客户端连接管理器
	 */
	private static ClientConnectionManager manager;

	static {
		ROOT_PATH = new File(Strings.CURRENT_PATH).getParentFile().getParentFile().getPath();
	}

	/**
	 * HTTP协议请求方式
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public enum Method {
		/**
		 * HEAD方式
		 */
		HEAD,

		/**
		 * GET方式
		 */
		GET,

		/**
		 * POST方式
		 */
		POST,

		/**
		 * PUT方式
		 */
		PUT,

		/**
		 * DELETE方式
		 */
		DELETE,

		/**
		 * TRACE方式
		 */
		TRACE,

		/**
		 * CONNECT方式
		 */
		CONNECT,

		/**
		 * OPTIONS方式
		 */
		OPTIONS;

	}

	/**
	 * 资源地址上下文标识
	 */
	public static final String CONTEXT_URI = "uri";

	/**
	 * 资源地址全路径上下问标识
	 */
	public static final String CONTEXT_URL = "url";

	/**
	 * 客户地址上下文标识
	 */
	public static final String CONTEXT_HOST = "host";

	/**
	 * 系统路径上下文标识
	 */
	public static final String CONTEXT_PATH = "path";

	/**
	 * 请求令牌上下文标识
	 */
	public static final String CONTEXT_TOKEN = "token";

	/**
	 * 请求客户端上下文标识
	 */
	public static final String CONTEXT_CLIENT = "client";

	/**
	 * 请求协议上下文标识
	 */
	public static final String CONTEXT_SCHEME = "scheme";

	/**
	 * 请求域名上下文标识
	 */
	public static final String CONTEXT_DOMAIN = "domain";

	/**
	 * 主机地址上下文标识
	 */
	public static final String CONTEXT_SERVER = "server";

	/**
	 * 请求端口号上下文标识
	 */
	public static final String CONTEXT_PORT = "port";

	/**
	 * 请求参数上下文标识
	 */
	public static final String CONTEXT_REQUEST = "request";

	/**
	 * 请求会话上下文标识
	 */
	public static final String CONTEXT_SESSION = "session";

	/**
	 * 请求内容上下文标识
	 */
	public static final String CONTEXT_CONTENT = "content";

	/**
	 * 请求响应上下文标识
	 */
	public static final String CONTEXT_RESPONSE = "response";

	/**
	 * 日期时间上下文标识
	 */
	public static final String CONTEXT_DATETIME = "datetime";

	/**
	 * 当前请求上下文标识
	 */
	public static final String CONTEXT_EXECUTOR = "executor";

	/**
	 * 请求时间戳上下文标识
	 */
	public static final String CONTEXT_TIMESTAMP = "timestamp";

	/**
	 * 请求耗时上下文标识
	 */
	public static final String CONTEXT_TIMESPEND = "timespend";

	private Https() {

	}

	/**
	 * 获取客户端连接管理器
	 * 
	 * @return 客户端连接管理器
	 */
	public static ClientConnectionManager getManager() {
		if (manager == null) {
			synchronized (Https.class) {
				if (manager == null) {
					manager = new PoolingClientConnectionManager();
				}
			}
		}
		return manager;
	}

	/**
	 * 设置客户端连接管理器
	 * 
	 * @param manager
	 *            客户端连接管理器
	 */
	public static void setManager(ClientConnectionManager manager) {
		if (manager == null) {
			throw new IllegalArgumentException("Illegal manager:" + manager);
		}
		if (Https.manager == null) {
			synchronized (Https.class) {
				if (Https.manager == null) {
					Https.manager = manager;
				}
			}
		}
	}

	/**
	 * 获取Http客户端对象
	 * 
	 * @return Http客户端对象
	 */
	public static HttpClient getClient() {
		return getClient(false);
	}

	/**
	 * 获取Http客户端对象
	 * 
	 * @param encrypt
	 *            是否为加密协议
	 * @return Http客户端对象
	 */
	public static HttpClient getClient(boolean encrypt) {
		DefaultHttpClient client = new DefaultHttpClient(getManager());
		if (encrypt) {
			bindSSL(client);
		}
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
		return client;
	}

	/**
	 * 绑定SSL,默认使用443端口
	 * 
	 * @param client
	 *            Http客户端对象
	 */
	public static void bindSSL(HttpClient client) {
		bindSSL(client, 443);
	}

	/**
	 * 绑定SSL
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param port
	 *            端口号
	 */
	public static void bindSSL(HttpClient client, int port) {
		if (client == null) {
			throw new IllegalArgumentException("Illegal client:" + client);
		}
		X509TrustManager trustManager = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { trustManager }, null);
			Scheme scheme = new Scheme("https", port,
					new SSLSocketFactory(context, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
			client.getConnectionManager().getSchemeRegistry().register(scheme);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Cookie
	 * 
	 * @param request
	 *            Http请求对象
	 * @param name
	 *            Cookie名称
	 * @return Cookie值
	 */
	public static String getCookie(HttpServletRequest request, String name) {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(name)) {
					try {
						String value = URLDecoder.decode(cookie.getValue(), Strings.UTF8);
						return value == null || value.isEmpty() ? null : value;
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 设置Cookie
	 * 
	 * @param response
	 *            Http响应对象
	 * @param name
	 *            Cookie名称
	 * @param value
	 *            Cookie值
	 * @param timeout
	 *            过期时间（秒）
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int timeout) {
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("llegal timeout:" + timeout);
		}
		try {
			Cookie cookie = new Cookie(name,
					value == null ? Strings.EMPTY_STRING : URLEncoder.encode(value, Strings.UTF8));
			cookie.setPath("/");
			cookie.setMaxAge(timeout);
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取并删除Cookie
	 * 
	 * @param request
	 *            Http请求对象
	 * @param response
	 *            Http响应对象
	 * @param name
	 *            Cookie名称
	 * @return Cookie值
	 */
	public static String removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(name)) {
					try {
						return URLDecoder.decode(cookie.getValue(), Strings.UTF8);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					} finally {
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取远程节点访问地址
	 * 
	 * @param node
	 *            远程节点对象
	 * @return 远程节点访问地址
	 */
	public static String getUrl(Node node) {
		return getUrl(node, null);
	}

	/**
	 * 获取远程节点访问地址
	 * 
	 * @param node
	 *            远程节点对象
	 * @param uri
	 *            远程节点资源地址
	 * @return 远程节点访问地址
	 */
	public static String getUrl(Node node, String uri) {
		if (node == null) {
			throw new IllegalArgumentException("Illegal node:" + node);
		}
		return getUrl(node.getProtocol(), node.getHost(), node.getPort(), uri);
	}

	/**
	 * 获取请求资源地址（不包含应用上下文地址）
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @return 资源地址
	 */
	public static String getUri(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		String uri = request.getRequestURI();
		String context = request.getContextPath();
		return context == null ? uri : uri.substring(context.length());
	}

	/**
	 * 获取HTTP请求的URL地址（不包含资源地址）
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @return URL地址
	 */
	public static String getUrl(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		StringBuilder url = new StringBuilder(request.getScheme()).append("://").append(request.getServerName())
				.append(':').append(request.getServerPort());
		String context = request.getContextPath();
		return context == null ? url.toString() : url.append(context).toString();
	}

	/**
	 * 获取HTTP URL地址
	 * 
	 * @param protocol
	 *            请求协议
	 * @param host
	 *            请求域名
	 * @param port
	 *            请求端口
	 * @param uri
	 *            请求资源路径
	 * @return URL地址
	 */
	public static String getUrl(Protocol protocol, String host, int port, String uri) {
		if (protocol == null) {
			throw new IllegalArgumentException("Illegal protocol:" + protocol);
		} else if (protocol != Protocol.http && protocol != Protocol.https) {
			throw new IllegalArgumentException("Not support protocol:" + protocol);
		}
		if (host == null) {
			throw new IllegalArgumentException("Illegal host:" + host);
		}
		StringBuilder url = new StringBuilder(protocol.toString()).append("://").append(host).append(':').append(port);
		if (uri == null || uri.isEmpty()) {
			return url.toString();
		} else if (uri.charAt(0) == '/') {
			return url.append(uri).toString();
		}
		return url.append('/').append(uri).toString();
	}

	/**
	 * 将参数字符串形式转换成键/值映射
	 * 
	 * @param param
	 *            参数字符串形式
	 * @return 键/值映射
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseParam(String param) {
		if (Strings.isEmpty(param)) {
			return new HashMap<String, Object>(0);
		}
		String[] setions = Strings.split(param, '&');
		Map<String, Object> parameters = new HashMap<String, Object>(setions.length);
		for (String setion : setions) {
			setion = setion.trim();
			if (setion.isEmpty()) {
				continue;
			}
			String[] kv = Strings.split(setion, '=');
			String key = kv[0].trim();
			if (key.isEmpty()) {
				continue;
			}
			String value = kv.length > 1 ? kv[1].trim() : null;
			Object exist = parameters.get(key);
			if (exist == null) {
				parameters.put(key, value);
			} else if (value != null) {
				if (exist instanceof List) {
					((List<String>) exist).add(value);
				} else {
					List<String> list = new LinkedList<String>();
					list.add((String) exist);
					list.add(value);
					parameters.put(key, list);
				}
			}
		}
		return parameters;
	}

	/**
	 * 获取URL参数
	 * 
	 * @param url
	 *            资源地址
	 * @return 参数键/值映射
	 */
	public static Map<String, Object> getParameters(String url) {
		int index = url == null ? -1 : url.indexOf('?');
		return index < 0 ? new HashMap<String, Object>(0) : parseParam(url.substring(index + 1));
	}

	/**
	 * 获取普通表单请求参数
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @return 参数键/值表
	 * @throws IOException
	 *             IO操作异常
	 */
	public static Map<String, Object> getParameters(HttpServletRequest request) throws IOException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object value = null;
			String[] values = request.getParameterValues(name);
			if (values.length == 1) {
				String param = values[0].trim();
				if (!param.isEmpty()) {
					value = param;
				}
			} else {
				List<String> _values = new ArrayList<String>(values.length);
				for (int i = 0; i < values.length; i++) {
					String param = values[i].trim();
					if (!param.isEmpty()) {
						_values.add(param);
					}
				}
				if (_values.size() == 1) {
					value = _values.get(0);
				} else if (!_values.isEmpty()) {
					value = _values;
				}
			}
			parameters.put(name, value);
		}
		return parameters;
	}

	/**
	 * 获取文件上传表单参数
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param uploader
	 *            文件上传处理器
	 * @return 参数键/值表
	 * @throws IOException
	 *             IO操作异常
	 * @throws FileUploadException
	 *             文件上传异常
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getUploadParameters(HttpServletRequest request, ServletFileUpload uploader)
			throws IOException, FileUploadException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (uploader == null) {
			throw new IllegalArgumentException("Illegal uploader:" + uploader);
		}
		List<?> items = uploader.parseRequest(request);
		Map<String, Object> parameters = new HashMap<String, Object>(items.size());
		for (int i = 0; i < items.size(); i++) {
			final FileItem item = (FileItem) items.get(i);
			Object value = null;
			String name = item.getFieldName();
			if (item.isFormField()) {
				String param = new String(item.get(), Strings.UTF8).trim();
				if (!param.isEmpty()) {
					value = param;
				}
			} else {
				final File file = ((DiskFileItem) item).getStoreLocation();
				value = new Nfile(Files.getName(item.getName())) {
					private static final long serialVersionUID = 1L;

					@Override
					public long getSize() {
						return item.getSize();
					}

					@Override
					public boolean isFile() {
						return file.exists();
					}

					@Override
					public File getFile() {
						return file;
					}

					@Override
					public InputStream getInputStream() throws IOException {
						return item.getInputStream();
					}

				};
			}
			Object o = parameters.get(name);
			if (o == null) {
				parameters.put(name, value);
			} else if (value != null) {
				if (o instanceof List) {
					((List<Object>) o).add(value);
				} else {
					List<Object> values = new LinkedList<Object>();
					values.add(o);
					values.add(value);
					parameters.put(name, values);
				}
			}
		}
		return parameters;
	}

	/**
	 * 获取请求流参数（参数必须满足JSON格式）
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @return 参数键/值表
	 * @throws IOException
	 *             IO操作异常
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getStreamParameters(HttpServletRequest request) throws IOException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		byte[] bytes = null;
		InputStream is = request.getInputStream();
		try {
			bytes = Streams.getBytes(is);
		} finally {
			is.close();
		}
		String json = new String(bytes);
		if (json.isEmpty()) {
			return Collections.emptyMap();
		}
		return (Map<String, Object>) Jsons.parse(json);
	}

	/**
	 * 获取Http请求实体
	 * 
	 * @param parameters
	 *            请求参数
	 * @return 请求实体
	 * @throws IOException
	 *             IO操作异常
	 */
	public static HttpEntity getHttpEntity(Map<String, Object> parameters) throws IOException {
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		Collection<?> values = parameters.values();
		for (Object value : values) {
			if (value instanceof File || value instanceof Nfile) {
				return getUploadEntity(parameters);
			} else if (value instanceof Object[]) {
				for (Object object : (Object[]) value) {
					if (object instanceof File || object instanceof Nfile) {
						return getUploadEntity(parameters);
					}
				}
			} else if (value instanceof Collection) {
				for (Object object : (Collection<?>) value) {
					if (object instanceof File || object instanceof Nfile) {
						return getUploadEntity(parameters);
					}
				}
			}
		}
		return getPostEntity(parameters);
	}

	/**
	 * 获取Get请求参数
	 * 
	 * @param parameters
	 *            请求参数
	 * @return 参数字符串形式
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getGetEntity(Map<String, Object> parameters) throws IOException {
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		String charset = Charset.defaultCharset().name();
		StringBuilder buffer = new StringBuilder();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			String key = entry.getKey();
			if (key == null) {
				continue;
			}
			Object value = entry.getValue();
			Collection<?> collection = value instanceof Collection ? (Collection<?>) value
					: value instanceof Object[] ? Arrays.asList((Object[]) value) : Arrays.asList(value);
			for (Object object : collection) {
				if (buffer.length() > 0) {
					buffer.append('&');
				}
				buffer.append(key);
				buffer.append('=');
				if (object != null) {
					buffer.append(URLEncoder.encode(Strings.toString(object), charset));
				}
			}
		}
		return buffer.length() == 0 ? null : buffer.toString();
	}

	/**
	 * 获取普通表单请求实例
	 * 
	 * @param parameters
	 *            请求参数
	 * @return HTTP实例
	 * @throws IOException
	 *             IO操作异常
	 */
	public static UrlEncodedFormEntity getPostEntity(Map<String, Object> parameters) throws IOException {
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		List<NameValuePair> nameValues = new LinkedList<NameValuePair>();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Collection<?> collection = value instanceof Collection ? (Collection<?>) value
					: value instanceof Object[] ? Arrays.asList((Object[]) value) : Arrays.asList(value);
			for (Object object : collection) {
				nameValues.add(
						new BasicNameValuePair(key, object == null ? Strings.EMPTY_STRING : Strings.toString(object)));
			}
		}
		return new UrlEncodedFormEntity(nameValues, Charset.defaultCharset());
	}

	/**
	 * 获取文件上传请求实例
	 * 
	 * @param parameters
	 *            请求参数
	 * @return HTTP实例
	 * @throws IOException
	 *             IO操作异常
	 */
	public static MultipartEntity getUploadEntity(Map<String, Object> parameters) throws IOException {
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		Charset charset = Charset.defaultCharset();
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, charset);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				entity.addPart(key, new StringBody(Strings.EMPTY_STRING));
			} else if (value instanceof File) {
				entity.addPart(key, new FileBody((File) value));
			} else if (value instanceof Nfile) {
				Nfile file = (Nfile) value;
				if (file.isFile()) {
					entity.addPart(key, new FileBody(file.getFile(), file.getName()));
				} else {
					entity.addPart(key, new ByteArrayBody(file.getBytes(), file.getName()));
				}
			} else if (value instanceof Collection || value instanceof Object[]) {
				Collection<?> collection = value instanceof Collection ? (Collection<?>) value
						: Arrays.asList((Object[]) value);
				for (Object object : collection) {
					if (object instanceof File) {
						entity.addPart(key, new FileBody((File) object));
					} else if (object instanceof Nfile) {
						Nfile file = (Nfile) object;
						if (file.isFile()) {
							entity.addPart(key, new FileBody(file.getFile(), file.getName()));
						} else {
							entity.addPart(key, new ByteArrayBody(file.getBytes(), file.getName()));
						}
					} else {
						entity.addPart(key, new StringBody(Strings.toString(object), charset));
					}
				}
			} else {
				entity.addPart(key, new StringBody(Strings.toString(value), charset));
			}
		}
		return entity;
	}

	/**
	 * 获取Http资源请求对象
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @return Http资源请求对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static HttpUriRequest getHttpUriRequest(String url, Method method) throws IOException {
		return getHttpUriRequest(url, method, Collections.<String, Object>emptyMap());
	}

	/**
	 * 获取Http资源请求对象
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param parameters
	 *            请求参数
	 * @return Http资源请求对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static HttpUriRequest getHttpUriRequest(String url, Method method, Map<String, Object> parameters)
			throws IOException {
		if (url == null) {
			throw new IllegalArgumentException("Illegal url:" + url);
		}
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (method == Method.GET) {
			String entity = getGetEntity(parameters);
			if (entity == null) {
				return new HttpGet(url);
			}
			return new HttpGet(new StringBuilder(url).append('?').append(entity).toString());
		} else if (method == Method.DELETE) {
			String entity = getGetEntity(parameters);
			if (entity == null) {
				return new HttpDelete(url);
			}
			return new HttpDelete(new StringBuilder(url).append('?').append(entity).toString());
		} else if (method == Method.POST) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(getHttpEntity(parameters));
			return httpPost;
		} else if (method == Method.PUT) {
			HttpPut httpPut = new HttpPut(url);
			httpPut.setEntity(getHttpEntity(parameters));
			return httpPut;
		}
		throw new RuntimeException("Not support method:" + method);
	}

	/**
	 * 获取Http请求数据字节流
	 * 
	 * @param request
	 *            Http请求对象
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(HttpServletRequest request) throws IOException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		InputStream is = request.getInputStream();
		try {
			return Streams.getBytes(is);
		} finally {
			is.close();
		}
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param request
	 *            Http请求对象
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(HttpUriRequest request) throws IOException {
		return getBytes(getClient(), request);
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param request
	 *            Http请求对象
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(HttpClient client, HttpUriRequest request) throws IOException {
		if (client == null) {
			throw new IllegalArgumentException("Illegal client:" + client);
		}
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (request.getURI().getScheme().equalsIgnoreCase(Protocol.https.toString())) {
			bindSSL(client);
		}
		HttpEntity entity = null;
		try {
			entity = client.execute(request).getEntity();
			return EntityUtils.toByteArray(entity);
		} finally {
			request.abort();
			if (entity != null) {
				EntityUtils.consumeQuietly(entity);
			}
		}
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(String url, Method method) throws IOException {
		return getBytes(getClient(), url, method);
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(HttpClient client, String url, Method method) throws IOException {
		return getBytes(client, url, method, Collections.<String, Object>emptyMap());
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param parameters
	 *            请求参数
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(String url, Method method, Map<String, Object> parameters) throws IOException {
		return getBytes(getClient(), url, method, parameters);
	}

	/**
	 * 获取Http请求结果字节数组
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param parameters
	 *            请求参数
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(HttpClient client, String url, Method method, Map<String, Object> parameters)
			throws IOException {
		return getBytes(client, getHttpUriRequest(url, method, parameters));
	}

	/**
	 * 获取Http请求数据流字符串形式
	 * 
	 * @param request
	 *            Http请求对象
	 * @return 数据字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(HttpServletRequest request) throws IOException {
		return new String(getBytes(request));
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param request
	 *            Http请求对象
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(HttpUriRequest request) throws IOException {
		return getString(getClient(), request);
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param request
	 *            Http请求对象
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(HttpClient client, HttpUriRequest request) throws IOException {
		if (client == null) {
			throw new IllegalArgumentException("Illegal client:" + client);
		}
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (request.getURI().getScheme().equalsIgnoreCase(Protocol.https.toString())) {
			bindSSL(client);
		}
		HttpEntity entity = null;
		try {
			entity = client.execute(request).getEntity();
			return EntityUtils.toString(entity);
		} finally {
			request.abort();
			if (entity != null) {
				EntityUtils.consumeQuietly(entity);
			}
		}
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(String url, Method method) throws IOException {
		return getString(getClient(), url, method);
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(HttpClient client, String url, Method method) throws IOException {
		return getString(client, url, method, Collections.<String, Object>emptyMap());
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param parameters
	 *            请求参数
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(String url, Method method, Map<String, Object> parameters) throws IOException {
		return getString(getClient(), url, method, parameters);
	}

	/**
	 * 获取Http请求结果字符串
	 * 
	 * @param client
	 *            Http客户端对象
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param parameters
	 *            请求参数
	 * @return 字符串
	 * @throws IOException
	 *             IO操作异常
	 */
	public static String getString(HttpClient client, String url, Method method, Map<String, Object> parameters)
			throws IOException {
		return getString(client, getHttpUriRequest(url, method, parameters));
	}

	/**
	 * 获取视图渲染上下文
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            渲染内容
	 * @return 上下文键/值映射表
	 */
	public static Map<String, Object> getRenderContext(final HttpRequester requester, Object content) {
		return getRenderContext(requester, content, new Map<String, Object>() {

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
				return key == null ? null : requester.getParameter(Strings.toString(key));
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
				return requester.getParameters().toString();
			}

		});
	}

	/**
	 * 获取视图渲染上下文
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            渲染内容
	 * @param parameters
	 *            请求参数
	 * @return 上下文键/值映射表
	 */
	public static Map<String, Object> getRenderContext(HttpRequester requester, Object content,
			final Map<String, Object> parameters) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		if (parameters == null) {
			throw new IllegalArgumentException("Illegal parameters:" + parameters);
		}
		Date datetime = new Date();
		final Session session = requester.getSession();
		HttpServletRequest request = requester.getHttpServletRequest();
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(CONTEXT_URI, requester.getUri());
		context.put(CONTEXT_URL, Https.getUrl(request));
		context.put(CONTEXT_HOST, requester.getHost());
		context.put(CONTEXT_PATH, request.getContextPath());
		context.put(CONTEXT_PORT, request.getServerPort());
		context.put(CONTEXT_TOKEN, requester.getToken());
		context.put(CONTEXT_SCHEME, request.getScheme());
		context.put(CONTEXT_DOMAIN, request.getServerName());
		context.put(CONTEXT_SERVER, Strings.LOCALHOST_ADDRESS);
		context.put(CONTEXT_REQUEST, parameters);
		context.put(CONTEXT_SESSION, new Map<String, Object>() {

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
		context.put(CONTEXT_RESPONSE, content);
		context.put(CONTEXT_DATETIME, datetime);
		context.put(CONTEXT_EXECUTOR, requester);
		context.put(CONTEXT_TIMESPEND, datetime.getTime() - requester.getCreated().getTime());
		return context;
	}

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpRequester requester, String template, Object content)
			throws IOException, ServletException {
		render(requester, template, content, Collections.<String, Object>emptyMap());
	}

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param output
	 *            数据输出流
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpRequester requester, String template, Object content, OutputStream output)
			throws IOException, ServletException {
		render(requester, template, content, Collections.<String, Object>emptyMap(), output);
	}

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param parameters
	 *            请求参数
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpRequester requester, String template, Object content, Map<String, Object> parameters)
			throws IOException, ServletException {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		OutputStream os = requester.getHttpServletResponse().getOutputStream();
		try {
			render(requester, template, content, parameters, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param parameters
	 *            请求参数
	 * @param output
	 *            数据输出流
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpRequester requester, String template, Object content, Map<String, Object> parameters,
			OutputStream output) throws IOException, ServletException {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		render(requester.getHttpServletRequest(), requester.getHttpServletResponse(), template,
				getRenderContext(requester, content, parameters), output);
	}

	/**
	 * 视图渲染
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpServletRequest request, HttpServletResponse response, String template)
			throws IOException, ServletException {
		render(request, response, template, Collections.<String, Object>emptyMap());
	}

	/**
	 * 视图渲染
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @param context
	 *            渲染上下文数据
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpServletRequest request, HttpServletResponse response, String template,
			Map<String, Object> context) throws IOException, ServletException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		OutputStream os = response.getOutputStream();
		try {
			render(request, response, template, context, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 视图渲染
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @param output
	 *            数据输出流
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpServletRequest request, HttpServletResponse response, String template,
			OutputStream output) throws IOException, ServletException {
		render(request, response, template, Collections.<String, Object>emptyMap(), output);
	}

	/**
	 * 视图渲染
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @param context
	 *            渲染上下文数据
	 * @param output
	 *            数据输出流
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void render(HttpServletRequest request, HttpServletResponse response, String template,
			Map<String, Object> context, OutputStream output) throws IOException, ServletException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (template == null) {
			throw new IllegalArgumentException("Illegal template:" + template);
		}
		if (context == null) {
			throw new IllegalArgumentException("Illegal context:" + context);
		}
		if (output == null) {
			throw new IllegalArgumentException("Illegal output:" + output);
		}
		template = Strings.replace(Strings.replace(template, "\\", "/"), "//", "/");
		if (template.charAt(0) != '/') {
			template = new StringBuilder("/").append(template).toString();
		}
		if (!new File(ROOT_PATH, template).exists()) {
			throw new IOException("Template does not exist:" + template);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(template);
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
		try {
			for (Entry<String, Object> entry : context.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			dispatcher.include(request, new HttpServletResponseWrapper(response) {

				@Override
				public PrintWriter getWriter() throws IOException {
					return writer;
				}

			});
			writer.flush();
		} finally {
			writer.close();
		}
	}

	/**
	 * 获取视图内容
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @return 视图内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static String view(HttpRequester requester, String template, Object content)
			throws IOException, ServletException {
		return view(requester, template, content, Collections.<String, Object>emptyMap());
	}

	/**
	 * 获取视图内容
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param parameters
	 *            请求参数
	 * @return 视图内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static String view(HttpRequester requester, String template, Object content, Map<String, Object> parameters)
			throws IOException, ServletException {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		return view(requester.getHttpServletRequest(), requester.getHttpServletResponse(), template,
				getRenderContext(requester, content, parameters));
	}

	/**
	 * 获取视图内容
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @return 视图内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static String view(HttpServletRequest request, HttpServletResponse response, String template)
			throws IOException, ServletException {
		return view(request, response, template, Collections.<String, Object>emptyMap());
	}

	/**
	 * 获取视图内容
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param template
	 *            视图模板名称
	 * @param context
	 *            渲染上下文数据
	 * @return 视图内容
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static String view(HttpServletRequest request, HttpServletResponse response, String template,
			Map<String, Object> context) throws IOException, ServletException {
		OutputStream os = new ByteArrayOutputStream();
		try {
			render(request, response, template, context, os);
		} finally {
			os.close();
		}
		return os.toString();
	}

	/**
	 * 请求转发
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param path
	 *            转发路径
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws IOException, ServletException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (path == null) {
			throw new IllegalArgumentException("Illegal path:" + path);
		}
		String context = request.getContextPath();
		if (context == null) {
			request.getRequestDispatcher(path).forward(request, response);
		} else {
			request.getRequestDispatcher(context + path).forward(request, response);
		}
	}

	/**
	 * 请求重定向
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @param path
	 *            重定向路径
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	public static void redirect(HttpServletRequest request, HttpServletResponse response, String path)
			throws IOException, ServletException {
		if (request == null) {
			throw new IllegalArgumentException("Illegal request:" + request);
		}
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (path == null) {
			throw new IllegalArgumentException("Illegal path:" + path);
		}
		String context = request.getContextPath();
		if (context == null) {
			response.sendRedirect(path);
		} else {
			response.sendRedirect(context + path);
		}
	}

	/**
	 * HTTP响应
	 * 
	 * @param response
	 *            HTTP响应对象
	 * @param object
	 *            输出对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void response(HttpServletResponse response, Object object) throws IOException {
		if (response == null) {
			throw new IllegalArgumentException("Illegal response:" + response);
		}
		if (object == null) {
			return;
		}
		OutputStream os = response.getOutputStream();
		try {
			if (object instanceof File || object instanceof Nfile) {
				Nfile file = object instanceof Nfile ? (Nfile) object : new Nfile((File) object);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition",
						"attachment; filename=" + new String(file.getName().getBytes(), "ISO-8859-1"));
				response.setHeader("Content-Length", String.valueOf(file.getSize()));
				InputStream is = file.getInputStream();
				try {
					Streams.write(is, os);
				} finally {
					is.close();
				}
			} else if (object instanceof byte[]) {
				os.write((byte[]) object);
			} else if (object instanceof InputStream) {
				InputStream is = (InputStream) object;
				try {
					Streams.write(is, os);
				} finally {
					is.close();
				}
			} else if (object instanceof ReadableByteChannel) {
				ReadableByteChannel channel = (ReadableByteChannel) object;
				try {
					Streams.write(channel, os);
				} finally {
					channel.close();
				}
			} else {
				os.write(Strings.toString(object).getBytes());
			}
		} finally {
			os.close();
		}
	}

	/**
	 * 获取html中纯文本
	 * 
	 * @param html
	 *            html文本
	 * @return 纯文本
	 */
	public static String getText(String html) {
		if (Strings.isEmpty(html)) {
			return html;
		}
		try {
			Parser parser = new Parser();
			parser.setInputHTML(html);
			StringBean stringBean = new StringBean();
			stringBean.setLinks(false);
			stringBean.setCollapse(true);
			stringBean.setReplaceNonBreakingSpaces(true);
			parser.visitAllNodesWith(stringBean);
			return stringBean.getStrings();
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 销毁Http调用资源
	 */
	public static void destroy() {
		if (manager != null) {
			synchronized (Https.class) {
				if (manager != null) {
					manager.shutdown();
					manager = null;
				}
			}
		}
	}

}
