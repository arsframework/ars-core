package ars.invoke.channel.http;

import java.util.Map;
import java.io.OutputStream;

import ars.util.Strings;

/**
 * Jsp模板文件渲染实现
 *
 * @author wuyongqiang
 */
public class JspRender implements Render {
    private String directory; // 模板目录

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = Strings.getRealPath(directory);
    }

    @Override
    public void execute(HttpRequester requester, String template, Map<String, Object> context, OutputStream output)
        throws Exception {
        if (this.directory != null) {
            template = new StringBuilder(this.directory).append('/').append(template).toString();
        }
        Https.render(requester.getHttpServletRequest(), requester.getHttpServletResponse(), template, context, output);
    }

}
