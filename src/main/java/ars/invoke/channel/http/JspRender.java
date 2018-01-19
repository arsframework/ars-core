package ars.invoke.channel.http;

import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.Render;
import ars.invoke.channel.http.HttpRequester;

/**
 * JSP视图渲染实现
 * 
 * @author yongqiangwu
 *
 */
public class JspRender implements Render {
	private String directory; // 模板目录

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	@Override
	public String execute(HttpRequester requester, String template, Object content) throws Exception {
		if (this.directory != null) {
			template = new StringBuilder(this.directory).append(template).toString();
		}
		return Https.render(requester, template, content);
	}

}
