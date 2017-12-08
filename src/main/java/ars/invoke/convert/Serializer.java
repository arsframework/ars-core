package ars.invoke.convert;

/**
 * 对象序列化接口
 * 
 * @author wuyq
 *
 */
public interface Serializer {
	/**
	 * 将对象转换成字符串
	 * 
	 * @param object
	 *            被序列化对象
	 * @return 字符串
	 */
	public String serialize(Object object);

}
