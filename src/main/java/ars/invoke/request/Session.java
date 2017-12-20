package ars.invoke.request;

import java.util.Set;

import ars.invoke.request.SessionFactory;

/**
 * 会话接口
 * 
 * @author yongqiangwu
 *
 */
public interface Session {
	/**
	 * 获取会话标识
	 * 
	 * @return 会话标识
	 */
	public String getId();

	/**
	 * 获取会话超时时间（秒）
	 * 
	 * @return 超时时间
	 */
	public int getTimeout();

	/**
	 * 获取会话工厂
	 * 
	 * @return 会话工厂对象
	 */
	public SessionFactory getSessionFactory();

	/**
	 * 获取所有属性名称
	 * 
	 * @return 属性名称集合
	 */
	public Set<String> getAttributeNames();

	/**
	 * 获取属性值
	 * 
	 * @param name
	 *            属性名称
	 * @return 属性值
	 */
	public Object getAttribute(String name);

	/**
	 * 设置属性值
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public void setAttribute(String name, Object value);

	/**
	 * 移除属性值
	 * 
	 * @param name
	 *            属性名称
	 */
	public void removeAttribute(String name);

}
