package ars.invoke.channel.http.spring;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import ars.invoke.channel.http.StandardHttpChannel;

/**
 * 基于Spring MVC 请求调度控制器实现
 *
 * @author wuyongqiang
 */
@Controller
public class SimpleDispatchController extends StandardHttpChannel implements ServletConfigAware {
    private ServletConfig config;

    @Override
    public void setServletConfig(ServletConfig config) {
        this.config = config;
    }

    /**
     * 请求调度
     *
     * @param request  Http请求对象
     * @param response Http响应对象
     * @throws Exception 操作异常
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.dispatch(this.config, request, response);
    }

}
