package ars.invoke.channel.http;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.invoke.Channel;

/**
 * Http请求通道接口
 * 
 * @author wuyq
 * 
 */
public interface HttpChannel extends Channel {
	/**
	 * 请求调度
	 * 
	 * @param config
	 *            Servlet配制对象
	 * @param request
	 *            HTTP请求对象
	 * @param response
	 *            HTTP响应对象
	 * @throws Exception
	 *             操作异常
	 */
	public void dispatch(ServletConfig config, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
