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
 * @author wuyq
 * 
 */
public class StandardHttpChannel extends AbstractHttpChannel {
	private ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory()); // 文件上传处理器

	public ServletFileUpload getUploader() {
		return uploader;
	}

	public void setUploader(ServletFileUpload uploader) {
		this.uploader = uploader;
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
		String contentType = request.getContentType();
		try {
			Map<String, Object> parameters = !Strings.isEmpty(contentType)
					&& contentType.startsWith("multipart/form-data") ? Https.getUploadParameters(request, this.uploader)
							: Https.getParameters(request);
			Token token = identity == null ? null : new Token(identity);
			return new StandardHttpRequester(this, config, request, response, null, request.getLocale(), client, host,
					token, uri, parameters);
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
	}

}
