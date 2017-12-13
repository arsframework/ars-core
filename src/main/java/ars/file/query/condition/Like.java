package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 模糊匹配
 * 
 * @author yongqiangwu
 *
 */
public class Like implements Condition {
	private Property property; // 比较属性
	private String value; // 比较值
	private Position position; // 比较位置

	public Like(Property property, String value) {
		this(property, value, Position.ANY);
	}

	public Like(Property property, String value, Position position) {
		this.property = property;
		this.value = value;
		this.position = position;
	}

	public Property getProperty() {
		return property;
	}

	public String getValue() {
		return value;
	}

	public Position getPosition() {
		return position;
	}

	/**
	 * 匹配位置
	 * 
	 * @author yongqiangwu
	 *
	 */
	public enum Position {
		/**
		 * 开始位置
		 */
		BEGIN,

		/**
		 * 结束位置
		 */
		END,

		/**
		 * 任何位置
		 */
		ANY;

	}

}
