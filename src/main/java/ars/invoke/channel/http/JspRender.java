package ars.invoke.channel.http;

import java.io.OutputStream;

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

	@Override
	public void execute(HttpRequester requester, String template, Object content, OutputStream output)
			throws Exception {
		Https.render(requester, template, content, output);
	}

}
