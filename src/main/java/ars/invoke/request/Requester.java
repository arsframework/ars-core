package ars.invoke.request;

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.Locale;
import java.io.Serializable;

import ars.invoke.Channel;
import ars.invoke.request.Token;

/**
 * 请求对象接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Requester extends Serializable {
	/**
	 * 获取请求通道
	 * 
	 * @return 请求通道
	 */
	public Channel getChannel();

	/**
	 * 获取父级请求对象
	 * 
	 * @return 请求对象
	 */
	public Requester getParent();

	/**
	 * 获取请求时间
	 * 
	 * @return 请求时间
	 */
	public Date getCreated();

	/**
	 * 获取请求标识（唯一）
	 * 
	 * @return 请求标识
	 */
	public String getId();

	/**
	 * 获取请求的资源地址
	 * 
	 * @return 资源地址
	 */
	public String getUri();

	/**
	 * 获取用户标识
	 * 
	 * @return 用户标识
	 */
	public String getUser();

	/**
	 * 获取请求令牌
	 * 
	 * @return 令牌对象
	 */
	public Token getToken();

	/**
	 * 获取客户主机地址
	 * 
	 * @return 主机地址
	 */
	public String getHost();

	/**
	 * 获取客户端标识
	 * 
	 * @return 客户端标识
	 */
	public String getClient();

	/**
	 * 获取客户端语言环境
	 * 
	 * @return 端语言环境对象
	 */
	public Locale getLocale();

	/**
	 * 获取所有请求参数名称
	 * 
	 * @return 参数名称集合
	 */
	public Set<String> getParameterNames();

	/**
	 * 判断参数是否存在
	 * 
	 * @param key
	 *            参数名称
	 * @return true/false
	 */
	public boolean hasParameter(String key);

	/**
	 * 根据参数名称获取参数的值
	 * 
	 * @param key
	 *            参数名称
	 * @return 参数值
	 */
	public Object getParameter(String key);

	/**
	 * 获取请求的参数
	 * 
	 * @return 参数
	 */
	public Map<String, Object> getParameters();

	/**
	 * 构建请求对象
	 * 
	 * @param uri
	 *            请求资源地址
	 * @return 请求对象
	 */
	public Requester build(String uri);

	/**
	 * 构建请求对象
	 * 
	 * @param parameters
	 *            请求参数
	 * @return 请求对象
	 */
	public Requester build(Map<String, Object> parameters);

	/**
	 * 构建请求对象
	 * 
	 * @param uri
	 *            请求资源地址
	 * @param parameters
	 *            请求参数
	 * @return 请求对象
	 */
	public Requester build(String uri, Map<String, Object> parameters);

	/**
	 * 执行请求调用
	 * 
	 * @return 调用结果
	 */
	public Object execute();

	/**
	 * 执行请求调用
	 * 
	 * @param uri
	 *            调用资源地址
	 * @return 调用结果
	 */
	public Object execute(String uri);

	/**
	 * 执行请求调用
	 * 
	 * @param parameters
	 *            调用参数
	 * @return 调用结果
	 */
	public Object execute(Map<String, Object> parameters);

	/**
	 * 执行请求调用
	 * 
	 * @param uri
	 *            调用资源地址
	 * @param parameters
	 *            调用参数
	 * @return 调用结果
	 */
	public Object execute(String uri, Map<String, Object> parameters);

	/**
	 * 消息格式化
	 * 
	 * @param key
	 *            消息索引
	 * @return 消息内容
	 */
	public String format(String key);

	/**
	 * 消息格式化
	 * 
	 * @param key
	 *            消息索引
	 * @param text
	 *            默认消息
	 * @return 消息内容
	 */
	public String format(String key, String text);

	/**
	 * 消息格式化
	 * 
	 * @param key
	 *            消息索引
	 * @param args
	 *            格式化参数
	 * @return 消息内容
	 */
	public String format(String key, Object[] args);

	/**
	 * 消息格式化
	 * 
	 * @param key
	 *            消息索引
	 * @param args
	 *            格式化参数
	 * @param text
	 *            默认消息
	 * @return 消息内容
	 */
	public String format(String key, Object[] args, String text);

}
