package ars.invoke.cache;

import java.io.Serializable;

/**
 * 缓存标识接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Key extends Serializable {
	/**
	 * 获取识别号
	 * 
	 * @return 识别号
	 */
	public String getId();

	/**
	 * 获取超时时间（秒）
	 * 
	 * @return 超时时间
	 */
	public int getTimeout();

}
