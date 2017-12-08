package ars.invoke.cache;

import java.io.Serializable;

/**
 * 缓存值接口
 * 
 * @author wuyq
 *
 */
public interface Value extends Serializable {
	/**
	 * 判断缓存值是否已缓存
	 * 
	 * @return true/false
	 */
	public boolean isCached();

	/**
	 * 获取缓存值内容
	 * 
	 * @return 缓存值内容
	 */
	public Object getContent();

}
