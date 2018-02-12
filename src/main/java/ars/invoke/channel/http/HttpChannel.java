package ars.invoke.channel.http;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.invoke.Channel;

/**
 * Http请求通道接口
 * 
 * @author yongqiangwu
 * 
 */
public interface HttpChannel extends Channel {
	/**
	 * 获取视图内容
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            模板路径
	 * @param content
	 *            数据内容
	 * @return 视图内容
	 * @throws Exception
	 *             操作异常
	 */
	public String view(HttpRequester requester, String template, Object content) throws Exception;

	/**
	 * 视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            模板路径
	 * @param content
	 *            数据内容
	 * @throws Exception
	 *             操作异常
	 */
	public void render(HttpRequester requester, String template, Object content) throws Exception;

	/**
	 * 请求重定向
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            重定向内容
	 * @return 是否重定向成功
	 * @throws Exception
	 *             操作异常
	 */
	public boolean redirect(HttpRequester requester, Object content) throws Exception;

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
