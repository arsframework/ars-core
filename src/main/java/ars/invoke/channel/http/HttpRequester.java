package ars.invoke.channel.http;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.invoke.request.Requester;

/**
 * 基于HTTP协议请求接口
 * 
 * @author yongqiangwu
 * 
 */
public interface HttpRequester extends Requester {
	/**
	 * 获取Servlet配制对象
	 * 
	 * @return Servlet配制对象
	 */
	public ServletConfig getServletConfig();

	/**
	 * 获取Http请求对象
	 * 
	 * @return Http请求对象
	 */
	public HttpServletRequest getHttpServletRequest();

	/**
	 * 获取Http响应对象
	 * 
	 * @return Http响应对象
	 */
	public HttpServletResponse getHttpServletResponse();

}
