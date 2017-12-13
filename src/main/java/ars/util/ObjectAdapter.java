package ars.util;

/**
 * 对象适配接口
 * 
 * @author yongqiangwu
 *
 */
public interface ObjectAdapter {
	/**
	 * 判断对象是否可适配
	 * 
	 * @param object
	 *            被适配对象
	 * @return true/false
	 */
	public boolean isAdaptable(Object object);

	/**
	 * 对象适配
	 * 
	 * @param object
	 *            适配源对象
	 * @return 适配目标对象
	 */
	public Object adaption(Object object);

}
