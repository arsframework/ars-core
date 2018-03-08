package ars.invoke.channel.http;

import java.util.Map;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ars.util.Strings;
import ars.invoke.request.Token;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.HttpRequester;
import ars.invoke.channel.http.AbstractHttpChannel;
import ars.invoke.channel.http.StandardHttpRequester;

/**
 * Http请求通道标准实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardHttpChannel extends AbstractHttpChannel {
	private ServletFileUpload uploader; // 文件上传处理器

	public ServletFileUpload getUploader() {
		if (this.uploader == null) {
			synchronized (this) {
				if (this.uploader == null) {
					this.uploader = new ServletFileUpload(new DiskFileItemFactory());
				}
			}
		}
		return uploader;
	}

	public void setUploader(ServletFileUpload uploader) {
		this.uploader = uploader;
	}

	/**
	 * 获取请求参数
	 * 
	 * @param request
	 *            HTTP请求对象
	 * @return 参数键/值映射
	 * @throws IOException
	 *             IO操作异常
	 * @throws ServletException
	 *             Servlet操作异常
	 */
	protected Map<String, Object> getParameters(HttpServletRequest request) throws IOException, ServletException {
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				return Https.getUploadParameters(request, this.getUploader());
			} catch (FileUploadException e) {
				throw new ServletException(e);
			}
		} else if ("application/json".equals(request.getContentType())) {
			return Https.getStreamParameters(request);
		}
		return Https.getParameters(request);
	}

	@Override
	protected HttpRequester getRequester(String uri, ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String host = request.getRemoteAddr();
		String client = request.getHeader(Https.CONTEXT_CLIENT);
		if (Strings.isEmpty(client)) {
			client = request.getSession().getId();
		}
		String identity = request.getHeader(Https.CONTEXT_TOKEN);
		if (Strings.isEmpty(identity)) {
			identity = Https.getCookie(request, Https.CONTEXT_TOKEN);
		}
		Token token = identity == null ? null : new Token(identity);
		return new StandardHttpRequester(this, config, request, response, this.getRenders(), null, request.getLocale(),
				client, host, token, uri, this.getParameters(request));
	}

}
