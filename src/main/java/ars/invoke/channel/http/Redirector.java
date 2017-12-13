package ars.invoke.channel.http;

/**
 * 请求重定向接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Redirector {
	/**
	 * 获取请求重定向资源路径
	 * 
	 * @param requester
	 *            请求对象
	 * @param content
	 *            请求结果
	 * @return 重定向资源路径
	 */
	public String getRedirect(HttpRequester requester, Object content);

}
