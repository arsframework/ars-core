package ars.invoke.channel.http;

import java.io.File;
import java.io.OutputStream;

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

	/**
	 * 获取Cookie
	 * 
	 * @param name
	 *            Cookie名称
	 * @return Cookie值
	 */
	public String getCookie(String name);

	/**
	 * 设置Cookie
	 * 
	 * @param name
	 *            Cookie名称
	 * @param value
	 *            Cookie值
	 * @param timeout
	 *            过期时间（秒）
	 */
	public void setCookie(String name, String value, int timeout);

	/**
	 * 获取并删除Cookie
	 * 
	 * @param name
	 *            Cookie名称
	 * @return Cookie值
	 */
	public String removeCookie(String name);

	/**
	 * 获取视图内容
	 * 
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @return 视图内容
	 * @throws Exception
	 *             渲染异常
	 */
	public String view(String template, Object content) throws Exception;

	/**
	 * 视图渲染
	 * 
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @throws Exception
	 *             渲染异常
	 */
	public void render(String template, Object content) throws Exception;

	/**
	 * 视图渲染
	 * 
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param file
	 *            数据输出文件
	 * @throws Exception
	 *             渲染异常
	 */
	public void render(String template, Object content, File file) throws Exception;

	/**
	 * 视图渲染
	 * 
	 * @param template
	 *            视图模板
	 * @param content
	 *            渲染内容
	 * @param output
	 *            数据输出流
	 * @throws Exception
	 *             渲染异常
	 */
	public void render(String template, Object content, OutputStream output) throws Exception;

}
