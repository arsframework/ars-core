package ars.invoke.channel.http;

import ars.invoke.channel.http.HttpRequester;

/**
 * 视图渲染接口
 * 
 * @author yongqiangwu
 *
 */
public interface Render {
	/**
	 * 执行视图渲染
	 * 
	 * @param requester
	 *            请求对象
	 * @param template
	 *            模板路径
	 * @param content
	 *            数据内容
	 * @return 模板内容
	 * @throws Exception
	 *             操作异常
	 */
	public String execute(HttpRequester requester, String template, Object content) throws Exception;

}
