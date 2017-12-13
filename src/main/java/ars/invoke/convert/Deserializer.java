package ars.invoke.convert;

/**
 * 对象反序列化接口
 * 
 * @author yongqiangwu
 *
 */
public interface Deserializer {
	/**
	 * 将字符串转换成对象
	 * 
	 * @param string
	 *            字符串
	 * @return 对象
	 */
	public Object deserialize(String string);

}
