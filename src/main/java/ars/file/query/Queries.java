package ars.file.query;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

import ars.util.Beans;
import ars.util.Dates;
import ars.file.Describe;
import ars.file.Describe.Property;

/**
 * 文件查询操作工具类
 * 
 * @author wuyq
 * 
 */
public final class Queries {
	private Queries() {

	}

	/**
	 * 查询条件接口
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static interface Condition {

	}

	/**
	 * 等于条件
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Equal implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public Equal(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 不等于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class NotEqual implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public NotEqual(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 大于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Large implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public Large(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 大于或等于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class LargeEqual implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public LargeEqual(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 小于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Less implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public Less(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 小于或等于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class LessEqual implements Condition {
		private Property property; // 比较属性
		private Object value; // 比较值

		public LessEqual(Property property, Object value) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			this.property = property;
			this.value = value;
		}

		public Property getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 大于和小于
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Between implements Condition {
		private Property property; // 比较属性
		private Object low; // 低值
		private Object high; // 高值

		public Between(Property property, Object low, Object high) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (low == null) {
				throw new IllegalArgumentException("Illegal low:" + low);
			}
			if (high == null) {
				throw new IllegalArgumentException("Illegal high:" + high);
			}
			this.property = property;
			this.low = low;
			this.high = high;
		}

		public Property getProperty() {
			return property;
		}

		public Object getLow() {
			return low;
		}

		public Object getHigh() {
			return high;
		}

	}

	/**
	 * 模糊匹配
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Like implements Condition {
		private Property property; // 比较属性
		private String value; // 比较值
		private Position position; // 比较位置

		public Like(Property property, String value) {
			this(property, value, Position.ANY);
		}

		public Like(Property property, String value, Position position) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (value == null) {
				throw new IllegalArgumentException("Illegal value:" + value);
			}
			if (position == null) {
				throw new IllegalArgumentException("Illegal position:" + position);
			}
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

	/**
	 * 排序条件
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Order implements Condition {
		private Property property; // 排序属性

		public Order(Property property) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			this.property = property;
		}

		public Property getProperty() {
			return property;
		}

	}

	/**
	 * 升序排序条件
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Asc extends Order {

		public Asc(Property property) {
			super(property);
		}

	}

	/**
	 * 降序排序条件
	 * 
	 * @author yongqiangwu
	 * 
	 */
	public static class Desc extends Order {

		public Desc(Property property) {
			super(property);
		}

	}

	/**
	 * 等于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static Equal eq(Property property, Object value) {
		return new Equal(property, valueAdapter(property, value));
	}

	/**
	 * 不等于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static NotEqual ne(Property property, Object value) {
		return new NotEqual(property, valueAdapter(property, value));
	}

	/**
	 * 大于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static Large gt(Property property, Object value) {
		return new Large(property, valueAdapter(property, value));
	}

	/**
	 * 大于或等于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static LargeEqual ge(Property property, Object value) {
		return new LargeEqual(property, valueAdapter(property, value));
	}

	/**
	 * 小于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static Less lt(Property property, Object value) {
		return new Less(property, valueAdapter(property, value));
	}

	/**
	 * 小于或等于
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @return 条件对象
	 */
	public static LessEqual le(Property property, Object value) {
		return new LessEqual(property, valueAdapter(property, value));
	}

	/**
	 * 属性值在两个值之间
	 * 
	 * @param property
	 *            属性
	 * @param low
	 *            低值
	 * @param high
	 *            高值
	 * @return 条件对象
	 */
	public static Between between(Property property, Object low, Object high) {
		return new Between(property, valueAdapter(property, low), valueAdapter(property, high));
	}

	/**
	 * 包含指定字符串
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            属性值
	 * @param position
	 *            匹配位置
	 * @return 条件对象
	 */
	public static Like like(Property property, String value, Like.Position position) {
		return new Like(property, value, position);
	}

	/**
	 * 升序条件
	 * 
	 * @param property
	 *            属性
	 * @return 条件对象
	 */
	public static Asc asc(Property property) {
		return new Asc(property);
	}

	/**
	 * 降序条件
	 * 
	 * @param property
	 *            属性
	 * @return 条件对象
	 */
	public static Desc desc(Property property) {
		return new Desc(property);
	}

	/**
	 * 将值类型转换成属性对应类型
	 * 
	 * @param property
	 *            属性
	 * @param value
	 *            值
	 * @return 转换后的值
	 */
	public static Object valueAdapter(Property property, Object value) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		boolean array = value != null && value.getClass().isArray();
		if (property == Property.NAME) {
			return array ? Beans.toArray(String.class, value) : Beans.toObject(String.class, value);
		} else if (property == Property.SIZE) {
			return array ? Beans.toArray(long.class, value) : Beans.toObject(long.class, value);
		} else if (property == Property.MODIFIED) {
			return array ? Beans.toArray(Date.class, value) : Beans.toObject(Date.class, value);
		}
		return array ? Beans.toArray(boolean.class, value) : Beans.toObject(boolean.class, value);
	}

	/**
	 * 判断文件描述是否满足小于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param less
	 *            小于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Less less) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (less == null) {
			throw new IllegalArgumentException("Illegal less:" + less);
		}
		Property property = less.getProperty();
		Object value = less.getValue();
		if (property == Property.NAME && describe.getName().compareToIgnoreCase((String) value) > -1) {
			return false;
		} else if (property == Property.SIZE && describe.getSize() >= (Long) value) {
			return false;
		} else if (property == Property.MODIFIED && !describe.getModified().before((Date) value)) {
			return false;
		}
		return describe.isDirectory() == false && (Boolean) value == true;
	}

	/**
	 * 判断文件描述是否满足模糊匹配条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param like
	 *            模糊匹配条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Like like) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (like == null) {
			throw new IllegalArgumentException("Illegal like:" + like);
		}
		Property property = like.getProperty();
		String value = like.getValue().toUpperCase();
		Like.Position position = like.getPosition();
		String source = null;
		if (property == Property.NAME) {
			source = describe.getName().toUpperCase();
		} else if (property == Property.SIZE) {
			source = String.valueOf(describe.getSize());
		} else if (property == Property.MODIFIED) {
			source = Dates.format(describe.getModified());
		} else {
			source = String.valueOf(describe.isDirectory());
		}
		if (position == Like.Position.BEGIN && source.indexOf(value) > 0) {
			return false;
		} else if (position == Like.Position.END && source.length() > source.lastIndexOf(value) + value.length()) {
			return false;
		} else if (position == Like.Position.ANY && source.indexOf(value) < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断文件描述是否满足等于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param equal
	 *            等于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Equal equal) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (equal == null) {
			throw new IllegalArgumentException("Illegal equal:" + equal);
		}
		Property property = equal.getProperty();
		Object value = equal.getValue();
		int matched = 0;
		if (property == Property.NAME) {
			if (value instanceof String[]) {
				for (String name : (String[]) value) {
					if (describe.getName().equalsIgnoreCase(name)) {
						matched++;
						break;
					}
				}
			} else if (describe.getName().equalsIgnoreCase((String) value)) {
				matched++;
			}
		} else if (property == Property.SIZE) {
			if (value instanceof long[]) {
				for (long size : (long[]) value) {
					if (describe.getSize() == size) {
						matched++;
						break;
					}
				}
			} else if (describe.getSize() == (Long) value) {
				matched++;
			}
		} else if (property == Property.MODIFIED) {
			if (value instanceof Date[]) {
				for (Date date : (Date[]) value) {
					if (describe.getModified().compareTo(date) == 0) {
						matched++;
						break;
					}
				}
			} else if (describe.getModified().compareTo((Date) value) == 0) {
				matched++;
			}
		} else {
			if (value instanceof boolean[]) {
				for (boolean directory : (boolean[]) value) {
					if (describe.isDirectory() == directory) {
						matched++;
						break;
					}
				}
			} else if (describe.isDirectory() == (Boolean) value) {
				matched++;
			}
		}
		return matched > 0;
	}

	/**
	 * 判断文件描述是否满足大于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param large
	 *            大于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Large large) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (large == null) {
			throw new IllegalArgumentException("Illegal large:" + large);
		}
		Property property = large.getProperty();
		Object value = large.getValue();
		if (property == Property.NAME && describe.getName().compareToIgnoreCase((String) value) < 0) {
			return false;
		} else if (property == Property.SIZE && describe.getSize() <= (Long) value) {
			return false;
		} else if (property == Property.MODIFIED && !describe.getModified().after((Date) value)) {
			return false;
		}
		return describe.isDirectory() == true && (Boolean) value == false;
	}

	/**
	 * 判断文件描述是否满足大于小于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param between
	 *            大于小于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Between between) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (between == null) {
			throw new IllegalArgumentException("Illegal between:" + between);
		}
		Property property = between.getProperty();
		Object low = between.getLow();
		Object high = between.getHigh();
		if (property == Property.NAME
				&& (describe.getName().compareToIgnoreCase((String) low) < 0 || describe.getName().compareToIgnoreCase(
						(String) high) > 0)) {
			return false;
		} else if (property == Property.SIZE && (describe.getSize() < (Long) low || describe.getSize() > (Long) high)) {
			return false;
		} else if (property == Property.MODIFIED
				&& (describe.getModified().before((Date) low) || describe.getModified().after((Date) high))) {
			return false;
		}
		return true;
	}

	/**
	 * 判断文件描述是否满足不等于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param notEqual
	 *            不等于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, NotEqual notEqual) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (notEqual == null) {
			throw new IllegalArgumentException("Illegal notEqual:" + notEqual);
		}
		Property property = notEqual.getProperty();
		Object value = notEqual.getValue();
		if (property == Property.NAME) {
			if (value instanceof String[]) {
				for (String name : (String[]) value) {
					if (describe.getName().equalsIgnoreCase(name)) {
						return false;
					}
				}
			} else if (describe.getName().equalsIgnoreCase((String) value)) {
				return false;
			}
		} else if (property == Property.SIZE) {
			if (value instanceof long[]) {
				for (long size : (long[]) value) {
					if (describe.getSize() == size) {
						return false;
					}
				}
			} else if (describe.getSize() == (Long) value) {
				return false;
			}
		} else if (property == Property.MODIFIED) {
			if (value instanceof Date[]) {
				for (Date date : (Date[]) value) {
					if (describe.getModified().compareTo(date) == 0) {
						return false;
					}
				}
			} else if (describe.getModified().compareTo((Date) value) == 0) {
				return false;
			}
		} else {
			if (value instanceof boolean[]) {
				for (boolean directory : (boolean[]) value) {
					if (describe.isDirectory() == directory) {
						return false;
					}
				}
			} else if (describe.isDirectory() == (Boolean) value) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断文件描述是否满足小于或等于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param lessEqual
	 *            小于或等于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, LessEqual lessEqual) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (lessEqual == null) {
			throw new IllegalArgumentException("Illegal lessEqual:" + lessEqual);
		}
		Property property = lessEqual.getProperty();
		Object value = lessEqual.getValue();
		if (property == Property.NAME && describe.getName().compareToIgnoreCase((String) value) > 0) {
			return false;
		} else if (property == Property.SIZE && describe.getSize() > (Long) value) {
			return false;
		} else if (property == Property.MODIFIED && describe.getModified().after((Date) value)) {
			return false;
		}
		return describe.isDirectory() == false;
	}

	/**
	 * 判断文件描述是否满足大于或等于条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param largeEqual
	 *            大于或等于条件
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, LargeEqual largeEqual) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (largeEqual == null) {
			throw new IllegalArgumentException("Illegal largeEqual:" + largeEqual);
		}
		Property property = largeEqual.getProperty();
		Object value = largeEqual.getValue();
		if (property == Property.NAME && describe.getName().compareToIgnoreCase((String) value) < 0) {
			return false;
		} else if (property == Property.SIZE && describe.getSize() < (Long) value) {
			return false;
		} else if (property == Property.MODIFIED && describe.getModified().before((Date) value)) {
			return false;
		}
		return describe.isDirectory() == true;
	}

	/**
	 * 判断文件描述是否满足查询条件
	 * 
	 * @param describe
	 *            文件描述对象
	 * @param conditions
	 *            查询条件数组
	 * @return true/false
	 */
	public static boolean isSatisfy(Describe describe, Condition... conditions) {
		if (describe == null) {
			throw new IllegalArgumentException("Illegal describe:" + describe);
		}
		if (conditions == null) {
			throw new IllegalArgumentException("Illegal conditions:" + conditions);
		}
		for (Condition condition : conditions) {
			if (condition instanceof Less && !isSatisfy(describe, (Less) condition)) {
				return false;
			} else if (condition instanceof Like && !isSatisfy(describe, (Like) condition)) {
				return false;
			} else if (condition instanceof Equal && !isSatisfy(describe, (Equal) condition)) {
				return false;
			} else if (condition instanceof Large && !isSatisfy(describe, (Large) condition)) {
				return false;
			} else if (condition instanceof Between && !isSatisfy(describe, (Between) condition)) {
				return false;
			} else if (condition instanceof NotEqual && !isSatisfy(describe, (NotEqual) condition)) {
				return false;
			} else if (condition instanceof LessEqual && !isSatisfy(describe, (LessEqual) condition)) {
				return false;
			} else if (condition instanceof LargeEqual && !isSatisfy(describe, (LargeEqual) condition)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 文件比较
	 * 
	 * @param o1
	 *            文件对象
	 * @param o2
	 *            文件对象
	 * @param orders
	 *            排序对象
	 * @return 比较结果
	 */
	public static int compare(File o1, File o2, Order... orders) {
		if (o1 == null) {
			throw new IllegalArgumentException("Illegal o1:" + o1);
		}
		if (o2 == null) {
			throw new IllegalArgumentException("Illegal o2:" + o2);
		}
		if (orders == null) {
			throw new IllegalArgumentException("Illegal orders:" + orders);
		}
		for (Order order : orders) {
			int compare = 0;
			Property property = order.getProperty();
			if (property == Property.NAME) {
				compare = o1.getName().compareTo(o2.getName());
			} else if (property == Property.SIZE) {
				long l1 = o1.length();
				long l2 = o2.length();
				compare = l1 < l2 ? -1 : l1 == l2 ? 0 : 1;
			} else if (property == Property.MODIFIED) {
				long m1 = o1.lastModified();
				long m2 = o2.lastModified();
				compare = m1 < m2 ? -1 : m1 == m2 ? 0 : 1;
			} else if (property == Property.DIRECTORY) {
				boolean d1 = o1.isDirectory();
				boolean d2 = o2.isDirectory();
				compare = d1 && !d2 ? -1 : d1 == d2 ? 0 : 1;
			}
			if (compare != 0) {
				return order instanceof Asc ? compare : -compare;
			}
		}
		return 0;
	}

	/**
	 * 文件描述比较
	 * 
	 * @param o1
	 *            文件描述对象
	 * @param o2
	 *            文件描述对象
	 * @param orders
	 *            排序对象
	 * @return 比较结果
	 */
	public static int compare(Describe o1, Describe o2, Order... orders) {
		if (o1 == null) {
			throw new IllegalArgumentException("Illegal o1:" + o1);
		}
		if (o2 == null) {
			throw new IllegalArgumentException("Illegal o2:" + o2);
		}
		if (orders == null) {
			throw new IllegalArgumentException("Illegal orders:" + orders);
		}
		for (Order order : orders) {
			int compare = 0;
			Property property = order.getProperty();
			if (property == Property.NAME) {
				compare = o1.getName().compareTo(o2.getName());
			} else if (property == Property.SIZE) {
				long s1 = o1.getSize();
				long s2 = o2.getSize();
				compare = s1 < s2 ? -1 : s1 == s2 ? 0 : 1;
			} else if (property == Property.MODIFIED) {
				compare = o1.getModified().compareTo(o2.getModified());
			} else if (property == Property.DIRECTORY) {
				boolean d1 = o1.isDirectory();
				boolean d2 = o2.isDirectory();
				compare = d1 && !d2 ? -1 : d1 == d2 ? 0 : 1;
			}
			if (compare != 0) {
				return order instanceof Asc ? compare : -compare;
			}
		}
		return 0;
	}

	/**
	 * 对文件列表排序
	 * 
	 * @param files
	 *            文件对象数组
	 * @param orders
	 *            排序对象数组
	 */
	public static void sort(File[] files, final Order... orders) {
		if (files == null) {
			throw new IllegalArgumentException("Illegal files:" + files);
		}
		if (orders == null) {
			throw new IllegalArgumentException("Illegal orders:" + orders);
		}
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return Queries.compare(o1, o2, orders);
			}

		});
	}

	/**
	 * 对文件描述列表排序
	 * 
	 * @param describes
	 *            文件描述对象数组
	 * @param orders
	 *            排序对象数组
	 */
	public static void sort(Describe[] describes, final Order... orders) {
		if (describes == null) {
			throw new IllegalArgumentException("Illegal describes:" + describes);
		}
		if (orders == null) {
			throw new IllegalArgumentException("Illegal orders:" + orders);
		}
		Arrays.sort(describes, new Comparator<Describe>() {

			@Override
			public int compare(Describe o1, Describe o2) {
				return Queries.compare(o1, o2, orders);
			}

		});
	}

	/**
	 * 对文件描述列表排序
	 * 
	 * @param describes
	 *            文件描述对象列表
	 * @param orders
	 *            排序对象数组
	 */
	public static void sort(List<Describe> describes, final Order... orders) {
		if (describes == null) {
			throw new IllegalArgumentException("Illegal describes:" + describes);
		}
		if (orders == null) {
			throw new IllegalArgumentException("Illegal orders:" + orders);
		}
		Collections.sort(describes, new Comparator<Describe>() {

			@Override
			public int compare(Describe o1, Describe o2) {
				return Queries.compare(o1, o2, orders);
			}

		});
	}

}
