package ars.file.query;

import java.util.Map;
import java.util.List;

import ars.file.Describe;
import ars.file.Describe.Property;

/**
 * 文件查询集合接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Query extends Iterable<Describe> {
	/**
	 * 特性查询分割符号
	 */
	public static final String MARK = "__";

	/**
	 * 匹配开始位置
	 */
	public static final String START = "start";

	/**
	 * 匹配结束位置
	 */
	public static final String END = "end";

	/**
	 * 匹配任意位置
	 */
	public static final String LIKE = "like";

	/**
	 * 等于
	 */
	public static final String EQ = "eq";

	/**
	 * 大于或等于
	 */
	public static final String GE = "ge";

	/**
	 * 大于
	 */
	public static final String GT = "gt";

	/**
	 * 小于或等于
	 */
	public static final String LE = "le";

	/**
	 * 小于
	 */
	public static final String LT = "lt";

	/**
	 * 不等于
	 */
	public static final String NE = "ne";

	/**
	 * 排序
	 */
	public static final String ORDER = "__order";

	/**
	 * 设置查询操作相对路径
	 * 
	 * @param path
	 *            路径
	 * @return 文件集合
	 */
	public Query path(String path);

	/**
	 * 等于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query eq(Property property, Object value);

	/**
	 * 不等于
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query ne(Property property, Object value);

	/**
	 * 大于
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query gt(Property property, Object value);

	/**
	 * 大于或等于
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query ge(Property property, Object value);

	/**
	 * 小于
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query lt(Property property, Object value);

	/**
	 * 小于或等于
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query le(Property property, Object value);

	/**
	 * 属性值在两个值之间
	 * 
	 * @param property
	 *            属性名
	 * @param low
	 *            低值
	 * @param high
	 *            高值
	 * @return 文件集合
	 */
	public Query between(Property property, Object low, Object high);

	/**
	 * 以指定字符串为开始
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query start(Property property, String value);

	/**
	 * 以指定字符串为结束
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query end(Property property, String value);

	/**
	 * 包含指定字符串
	 * 
	 * @param property
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 文件集合
	 */
	public Query like(Property property, String value);

	/**
	 * 自定义查询
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 文件集合
	 */
	public Query custom(String key, Object value);

	/**
	 * 自定义查询
	 * 
	 * @param parameters
	 *            参数键/值表
	 * @return 文件集合
	 */
	public Query custom(Map<String, Object> parameters);

	/**
	 * 多个属性升序排序
	 * 
	 * @param properties
	 *            属性名数组
	 * @return 文件集合
	 */
	public Query asc(Property... properties);

	/**
	 * 多个属性降序排序
	 * 
	 * @param properties
	 *            属性名数组
	 * @return 文件集合
	 */
	public Query desc(Property... properties);

	/**
	 * 设置是否展开
	 * 
	 * @param spread
	 *            true/false
	 * @return 文件集合
	 */
	public Query spread(boolean spread);

	/**
	 * 将文件集合对象转换成List对象
	 * 
	 * @return 列表对象
	 */
	public List<Describe> list();

}
