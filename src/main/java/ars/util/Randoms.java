package ars.util;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.lang.reflect.Field;

import ars.util.Dates;
import ars.util.Beans;

/**
 * 随机数工具类
 * 
 * @author yongqiangwu
 *
 */
public final class Randoms {
	private static final ThreadLocal<Random> threadRandom = new ThreadLocal<Random>();

	private Randoms() {

	}

	/**
	 * 随机数生成接口
	 * 
	 * @author yongqiangwu
	 *
	 * @param <T>
	 *            数据类型
	 */
	public static interface RandomGenerator<T> {
		/**
		 * 生成随机数
		 * 
		 * @return 随机数
		 */
		public T generate();

	}

	/**
	 * 随机对象实例生成工厂
	 * 
	 * @author yongqiangwu
	 *
	 * @param <T>
	 *            对象类型
	 */
	public static class RandomBeanFactory<T> {
		protected final Class<T> type; // 对象类型
		private final List<String> excludes = new ArrayList<String>(0);
		private final StringBuilder property = new StringBuilder(); // 当前属性名称
		private final LinkedList<Class<?>> executed = new LinkedList<Class<?>>(); // 已执行对象类型
		private final Map<Class<?>, RandomGenerator<?>> typeGenerators = new HashMap<Class<?>, RandomGenerator<?>>(0);
		private final Map<String, RandomGenerator<?>> propertyGenerators = new HashMap<String, RandomGenerator<?>>(0);

		public RandomBeanFactory(Class<T> type) {
			if (type == null) {
				throw new IllegalArgumentException("Illegal type:" + type);
			}
			this.type = type;
		}

		/**
		 * 执行对象实例构建
		 * 
		 * @param <M>
		 *            对象类型
		 * @param type
		 *            对象类型
		 * @return 对象实例
		 */
		@SuppressWarnings("unchecked")
		protected <M> M execute(Class<M> type) {
			if (type == null) {
				throw new IllegalArgumentException("Illegal type:" + type);
			}
			RandomGenerator<?> generator = this.typeGenerators.get(type);
			if (generator != null) {
				return (M) generator.generate();
			}
			if (Enum.class.isAssignableFrom(type)) {
				return (M) randomEnum((Class<Enum<?>>) type);
			} else if (Date.class.isAssignableFrom(type)) {
				return (M) randomDate();
			} else if (type == byte.class || type == Byte.class) {
				return (M) Byte.valueOf((byte) randomInteger());
			} else if (type == char.class || type == Character.class) {
				return (M) Strings.CHARS[getCurrentRandom().nextInt(Strings.CHARS.length)];
			} else if (type == short.class || type == Short.class) {
				return (M) Short.valueOf((short) randomInteger());
			} else if (type == float.class || type == Float.class) {
				return (M) Float.valueOf(randomInteger());
			} else if (type == double.class || type == Double.class) {
				return (M) Double.valueOf(randomInteger());
			} else if (type == int.class || type == Integer.class) {
				return (M) Integer.valueOf(randomInteger());
			} else if (type == long.class || type == Long.class) {
				return (M) Long.valueOf(randomInteger());
			} else if (type == boolean.class || type == Boolean.class) {
				return (M) Boolean.valueOf(randomBoolean());
			} else if (type == String.class) {
				return (M) randomString();
			} else if (type.isArray()) {
				Class<?> component = type.getComponentType();
				Object[] array = Beans.getArray(component, 1);
				array[0] = this.execute(component);
				return (M) array;
			}
			if (this.executed.contains(type)) {
				return null;
			}
			this.executed.add(type);
			M instance = Beans.getInstance(type);
			for (Field field : Beans.getFields(type)) {
				if (this.property.length() > 0) {
					this.property.append('.');
				}
				this.property.append(field.getName());
				String fullname = this.property.toString();
				if (!this.excludes.isEmpty() && this.excludes.contains(fullname)) {
					continue;
				}

				Object value = null;
				generator = this.propertyGenerators.get(fullname);
				if (generator != null) {
					value = generator.generate();
				} else if (Map.class.isAssignableFrom(field.getType())) {
					Class<?>[] genericTypes = Beans.getGenericTypes(field);
					Map<Object, Object> map = new HashMap<Object, Object>(genericTypes.length == 2 ? 1 : 0);
					if (genericTypes.length == 2) {
						map.put(this.execute(genericTypes[0]), this.execute(genericTypes[1]));
					}
					value = map;
				} else if (Collection.class.isAssignableFrom(field.getType())) {
					Class<?>[] genericTypes = Beans.getGenericTypes(field);
					Collection<Object> collection = Set.class.isAssignableFrom(field.getType())
							? new HashSet<Object>(genericTypes.length == 1 ? 1 : 0)
							: new ArrayList<Object>(genericTypes.length == 1 ? 1 : 0);
					if (genericTypes.length == 1) {
						collection.add(this.execute(genericTypes[0]));
					}
					value = collection;
				} else {
					value = this.execute(field.getType());
				}
				field.setAccessible(true);
				try {
					field.set(instance, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} finally {
					field.setAccessible(false);
				}
				if (this.property.length() == field.getName().length()) {
					this.property.delete(0, this.property.length());
				} else {
					this.property.delete(this.property.length() - field.getName().length() - 1, this.property.length());
				}
			}
			this.executed.removeLast();
			return instance;
		}

		/**
		 * 设置排除属性
		 * 
		 * @param properties
		 *            属性名称数组
		 * @return 随机对象实例生成工厂
		 */
		public RandomBeanFactory<T> excludes(String... properties) {
			this.excludes.clear();
			if (properties != null && properties.length > 0) {
				this.excludes.addAll(Arrays.asList(properties));
			}
			return this;
		}

		/**
		 * 注册随机生成器
		 * 
		 * @param type
		 *            类型
		 * @param generator
		 *            生成器
		 * @return 随机对象实例生成工厂
		 */
		public RandomBeanFactory<T> registerGenerator(Class<?> type, RandomGenerator<?> generator) {
			if (type == null) {
				throw new IllegalArgumentException("Illegal type:" + type);
			}
			if (generator == null) {
				throw new IllegalArgumentException("Illegal generator:" + generator);
			}
			this.typeGenerators.put(type, generator);
			return this;
		}

		/**
		 * 注册随机生成器
		 * 
		 * @param property
		 *            属性名称
		 * @param generator
		 *            生成器
		 * @return 随机对象实例生成工厂
		 */
		public RandomBeanFactory<T> registerGenerator(String property, RandomGenerator<?> generator) {
			if (property == null) {
				throw new IllegalArgumentException("Illegal property:" + property);
			}
			if (generator == null) {
				throw new IllegalArgumentException("Illegal generator:" + generator);
			}
			this.propertyGenerators.put(property, generator);
			return this;
		}

		/**
		 * 构建对象实例
		 * 
		 * @return 对象实例
		 */
		public T build() {
			return this.execute(this.type);
		}

	}

	/**
	 * 获取当前线程随机数处理对象
	 * 
	 * @return 随机数处理对象
	 */
	public static Random getCurrentRandom() {
		Random random = threadRandom.get();
		if (random == null) {
			random = new Random();
			threadRandom.set(random);
		}
		return random;
	}

	/**
	 * 随机生成对象实例
	 * 
	 * @param <T>
	 *            对象类型
	 * @param type
	 *            对象类型
	 * @return 随机对象实例生成工厂
	 */
	public static <T> RandomBeanFactory<T> random(Class<T> type) {
		return new RandomBeanFactory<T>(type);
	}

	/**
	 * 随机生成枚举项
	 * 
	 * @param <T>
	 *            枚举类型
	 * @param type
	 *            枚举类型
	 * @return 枚举项
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T randomEnum(Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		try {
			Object[] values = (Object[]) type.getMethod("values").invoke(type);
			if (values.length == 0) {
				return null;
			}
			return (T) values[getCurrentRandom().nextInt(values.length)];
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 随机生成日期（以当前年份第一天为最小日期，当前日期为最大日期）
	 * 
	 * @return 日期
	 */
	public static Date randomDate() {
		return randomDate(Dates.getFirstDate(), new Date());
	}

	/**
	 * 随机生成日期
	 * 
	 * @param min
	 *            最小日
	 * @param max
	 *            最大日期
	 * @return 日期
	 */
	public static Date randomDate(Date min, Date max) {
		if (min == null) {
			throw new IllegalArgumentException("Illegal min:" + min);
		}
		if (max == null || max.before(min)) {
			throw new IllegalArgumentException("Illegal max:" + max);
		}
		long start = min.getTime();
		long time = max.getTime() - start; // 相差毫秒数
		if (time <= 1000) { // 相差1秒内
			return new Date(start + getCurrentRandom().nextInt((int) time));
		}
		return new Date(start + getCurrentRandom().nextInt((int) (time / 1000)) * 1000);
	}

	/**
	 * 随机生成数字
	 * 
	 * @return 数字
	 */
	public static int randomInteger() {
		return randomInteger(0, 10);
	}

	/**
	 * 随机生成数字
	 * 
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return 数字
	 */
	public static int randomInteger(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException("Max number can't be less than min number(" + min + "," + max + ")");
		}
		return min + getCurrentRandom().nextInt(max - min);
	}

	/**
	 * 随机生成字符串（默认长度4）
	 * 
	 * @return 字符串
	 */
	public static String randomString() {
		return randomString(4);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String randomString(int length) {
		return randomString(length, Strings.CHARS);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param chars
	 *            随机字符数组
	 * @return 字符串
	 */
	public static String randomString(Character[] chars) {
		return randomString(4, chars);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @param chars
	 *            随机字符数组
	 * @return 字符串
	 */
	public static String randomString(int length, Character[] chars) {
		if (length < 1) {
			throw new IllegalArgumentException("Illegal length:" + length);
		}
		if (chars == null || chars.length == 0) {
			throw new IllegalArgumentException("Illegal chars:" + Strings.toString(chars));
		}
		return Strings.random(length, chars);
	}

	/**
	 * 随机生成真假值
	 * 
	 * @return 真假值
	 */
	public static boolean randomBoolean() {
		return getCurrentRandom().nextBoolean();
	}

}
