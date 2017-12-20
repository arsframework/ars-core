package ars.invoke.cache;

import ars.util.Cache;
import ars.invoke.request.Requester;

/**
 * 请求数据缓存接口
 * 
 * @author yongqiangwu
 * 
 */
public interface InvokeCache extends Cache {
	/**
	 * 获取缓存标识
	 * 
	 * @param requester
	 *            请求对象
	 * @return 缓存标识
	 */
	public Key key(Requester requester);

	/**
	 * 根据请求判断是否可缓存
	 * 
	 * @param requester
	 *            请求对象
	 * @return true/false
	 */
	public boolean isCacheable(Requester requester);

}
