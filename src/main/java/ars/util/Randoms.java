package ars.util;

import java.util.Map;
import java.util.Set;
import java.util.Date;
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
	 * 随机生成对象属性排除策略接口
	 * 
	 * @author yongqiangwu
	 *
	 */
	public static interface ExcludeStrategy {
		/**
		 * 判断是否排除
		 * 
		 * @param type
		 *            对象类型
		 * @param field
		 *            字段对象
		 * @return true/false
		 */
		public boolean exclude(Class<?> type, Field field);

	}

	/**
	 * 对象属性随机数生成接口工厂
	 * 
	 * @author yongqiangwu
	 *
	 */
	public static interface RandomGeneratorFactory {
		/**
		 * 获取随机数生成接口
		 * 
		 * @param <T>
		 *            数据类型
		 * @param type
		 *            对象类型
		 * @param field
		 *            字段对象
		 * @return 随机数生成接口
		 */
		public <T> RandomGenerator<T> getRandomGenerator(Class<T> type, Field field);

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
		protected final Random random;
		private ExcludeStrategy excludeStrategy;
		private RandomGeneratorFactory randomGeneratorFactory;
		private final LinkedList<Class<?>> executed = new LinkedList<Class<?>>(); // 已执行对象类型

		public RandomBeanFactory(Class<T> type) {
			if (type == null) {
				throw new IllegalArgumentException("Illegal type:" + type);
			}
			this.type = type;
			this.random = new Random();
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
			if (this.excludeStrategy != null && this.excludeStrategy.exclude(type, null)) {
				return null;
			}
			RandomGenerator<?> generator = this.randomGeneratorFactory == null ? null
					: this.randomGeneratorFactory.getRandomGenerator(type, null);
			if (generator != null) {
				return (M) generator.generate();
			}
			if (Enum.class.isAssignableFrom(type)) {
				return (M) randomEnum(this.random, (Class<Enum<?>>) type);
			} else if (Date.class.isAssignableFrom(type)) {
				return (M) randomDate(this.random);
			} else if (type == byte.class || type == Byte.class) {
				return (M) Byte.valueOf((byte) randomInteger(this.random));
			} else if (type == char.class || type == Character.class) {
				return (M) randomCharacter(this.random);
			} else if (type == short.class || type == Short.class) {
				return (M) Short.valueOf((short) randomInteger(this.random));
			} else if (type == float.class || type == Float.class) {
				return (M) Float.valueOf(randomInteger(this.random));
			} else if (type == double.class || type == Double.class) {
				return (M) Double.valueOf(randomInteger(this.random));
			} else if (type == int.class || type == Integer.class) {
				return (M) Integer.valueOf(randomInteger(this.random));
			} else if (type == long.class || type == Long.class) {
				return (M) Long.valueOf(randomInteger(this.random));
			} else if (type == boolean.class || type == Boolean.class) {
				return (M) Boolean.valueOf(randomBoolean(this.random));
			} else if (type == String.class) {
				return (M) randomString(this.random);
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
				if (this.excludeStrategy != null && this.excludeStrategy.exclude(type, field)) {
					continue;
				}
				Object value = null;
				generator = this.randomGeneratorFactory == null ? null
						: this.randomGeneratorFactory.getRandomGenerator(type, field);
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
			}
			this.executed.removeLast();
			return instance;
		}

		/**
		 * 注册随机生成属性排除策略
		 * 
		 * @param excludeStrategy
		 *            随机生成属性排除策略
		 * @return 随机对象实例生成工厂
		 */
		public RandomBeanFactory<T> register(ExcludeStrategy excludeStrategy) {
			this.excludeStrategy = excludeStrategy;
			return this;
		}

		/**
		 * 注册随机数生成接口工厂
		 * 
		 * @param randomGeneratorFactory
		 *            随机数生成接口工厂
		 * @return 随机对象实例生成工厂
		 */
		public RandomBeanFactory<T> register(RandomGeneratorFactory randomGeneratorFactory) {
			this.randomGeneratorFactory = randomGeneratorFactory;
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
	public static <T extends Enum<?>> T randomEnum(Class<T> type) {
		return randomEnum(new Random(), type);
	}

	/**
	 * 随机生成枚举项
	 * 
	 * @param <T>
	 *            枚举类型
	 * @param random
	 *            随机处理对象
	 * @param type
	 *            枚举类型
	 * @return 枚举项
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T randomEnum(Random random, Class<T> type) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		try {
			Object[] values = (Object[]) type.getMethod("values").invoke(type);
			return values.length == 0 ? null : (T) values[random.nextInt(values.length)];
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
		return randomDate(new Random());
	}

	/**
	 * 随机生成日期（以当前年份第一天为最小日期，当前日期为最大日期）
	 * 
	 * @param random
	 *            随机处理对象
	 * @return 日期
	 */
	public static Date randomDate(Random random) {
		return randomDate(random, Dates.getFirstDate(), new Date());
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
		return randomDate(new Random(), min, max);
	}

	/**
	 * 随机生成日期
	 * 
	 * @param random
	 *            随机处理对象
	 * @param min
	 *            最小日
	 * @param max
	 *            最大日期
	 * @return 日期
	 */
	public static Date randomDate(Random random, Date min, Date max) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		if (min == null) {
			throw new IllegalArgumentException("Illegal min:" + min);
		}
		if (max == null || max.before(min)) {
			throw new IllegalArgumentException("Illegal max:" + max);
		}
		long start = min.getTime();
		long time = max.getTime() - start; // 相差毫秒数
		if (time <= 1000) { // 相差1秒内
			return new Date(start + random.nextInt((int) time));
		}
		return new Date(start + random.nextInt((int) (time / 1000)) * 1000);
	}

	/**
	 * 随机生成数字
	 * 
	 * @return 数字
	 */
	public static int randomInteger() {
		return randomInteger(new Random());
	}

	/**
	 * 随机生成数字
	 * 
	 * @param random
	 *            随机处理对象
	 * @return 数字
	 */
	public static int randomInteger(Random random) {
		return randomInteger(random, 0, 10);
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
		return randomInteger(new Random(), min, max);
	}

	/**
	 * 随机生成数字
	 * 
	 * @param random
	 *            随机处理对象
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return 数字
	 */
	public static int randomInteger(Random random, int min, int max) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		if (max < min) {
			throw new IllegalArgumentException("Max number can't be less than min number(" + min + "," + max + ")");
		}
		return min + random.nextInt(max - min);
	}

	/**
	 * 随机生成字符串（默认长度4）
	 * 
	 * @return 字符串
	 */
	public static String randomString() {
		return randomString(new Random());
	}

	/**
	 * 随机生成字符串（默认长度4）
	 * 
	 * @param random
	 *            随机处理对象
	 * @return 字符串
	 */
	public static String randomString(Random random) {
		return randomString(random, 4);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String randomString(int length) {
		return randomString(Strings.CHARS, length);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param random
	 *            随机处理对象
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String randomString(Random random, int length) {
		return randomString(random, Strings.CHARS, length);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param chars
	 *            随机字符数组
	 * @return 字符串
	 */
	public static String randomString(Character[] chars) {
		return randomString(chars, 4);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param random
	 *            随机处理对象
	 * @param chars
	 *            随机字符数组
	 * @return 字符串
	 */
	public static String randomString(Random random, Character[] chars) {
		return randomString(random, chars, 4);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param chars
	 *            随机字符数组
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String randomString(Character[] chars, int length) {
		return randomString(new Random(), chars, length);
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param random
	 *            随机处理对象
	 * @param chars
	 *            随机字符数组
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String randomString(Random random, Character[] chars, int length) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		if (chars == null || chars.length == 0) {
			throw new IllegalArgumentException("Illegal chars:" + Strings.toString(chars));
		}
		if (length < 1) {
			throw new IllegalArgumentException("Illegal length:" + length);
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			buffer.append(chars[random.nextInt(chars.length)]);
		}
		return buffer.toString();
	}

	/**
	 * 随机生成字符
	 * 
	 * @return 字符
	 */
	public static Character randomCharacter() {
		return randomCharacter(new Random());
	}

	/**
	 * 随机生成字符
	 * 
	 * @param random
	 *            随机处理对象
	 * @return 字符
	 */
	public static Character randomCharacter(Random random) {
		return randomCharacter(random, Strings.CHARS);
	}

	/**
	 * 随机生成字符
	 * 
	 * @param chars
	 *            随机字符数组
	 * @return 字符
	 */
	public static Character randomCharacter(Character[] chars) {
		return randomCharacter(new Random(), chars);
	}

	/**
	 * 随机生成字符
	 * 
	 * @param random
	 *            随机处理对象
	 * @param chars
	 *            随机字符数组
	 * @return 字符
	 */
	public static Character randomCharacter(Random random, Character[] chars) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		if (chars == null || chars.length == 0) {
			throw new IllegalArgumentException("Illegal chars:" + Strings.toString(chars));
		}
		return chars[random.nextInt(chars.length)];
	}

	/**
	 * 随机生成真假值
	 * 
	 * @return 真假值
	 */
	public static boolean randomBoolean() {
		return randomBoolean(new Random());
	}

	/**
	 * 随机生成真假值
	 * 
	 * @param random
	 *            随机处理对象
	 * @return 真假值
	 */
	public static boolean randomBoolean(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("Illegal random:" + random);
		}
		return random.nextBoolean();
	}

}
