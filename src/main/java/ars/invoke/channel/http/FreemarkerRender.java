package ars.invoke.channel.http;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import freemarker.template.Version;
import freemarker.template.Configuration;

import ars.util.Strings;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.Render;
import ars.invoke.channel.http.HttpRequester;

/**
 * Freemarker模板文件渲染实现
 * 
 * @author yongqiangwu
 *
 */
public class FreemarkerRender extends Configuration implements Render {
	private String directory; // 模板目录

	public FreemarkerRender() {
		this(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
	}

	public FreemarkerRender(Version version) {
		super(version);
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = Strings.getRealPath(directory);
		try {
			this.setDirectoryForTemplateLoading(new File(Https.ROOT_PATH, this.directory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(HttpRequester requester, String template, Map<String, Object> context, OutputStream output)
			throws Exception {
		this.getTemplate(template).process(context, new OutputStreamWriter(output));
	}

}
