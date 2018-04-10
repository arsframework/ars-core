package ars.invoke.channel.http.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ars.invoke.channel.http.HttpChannel;

/**
 * 请求调度Servlet简单实现
 *
 * @author wuyongqiang
 */
public class SimpleDispatchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private HttpChannel channel; // Http请求通道
    private ApplicationContext applicationContext; // Spring应用上下文

    public HttpChannel getChannel() {
        return this.channel;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        this.channel = this.applicationContext.getBean(HttpChannel.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            this.channel.dispatch(this.getServletConfig(), request, response);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof ServletException) {
                throw (ServletException) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new RuntimeException(e);
        }
    }

}
