package ars.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.JarURLConnection;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.text.DecimalFormat;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.InvocationTargetException;

import ars.util.Dates;
import ars.util.Strings;
import ars.util.Streams;

/**
 * 对象工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Beans {
	/**
	 * 空对象数组
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * 默认数字格式化对象
	 */
	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("0.##");

	/**
	 * 对象单例映射表
	 */
	private static final Map<Class<?>, Object> singles = new HashMap<Class<?>, Object>(0);

	private Beans() {

	}

	/**
	 * 获取对象类型
	 * 
	 * @param objects
	 *            对象数组
	 * @return 类型数组
	 */
	public static Class<?>[] getTypes(Object... objects) {
		if (objects == null) {
			throw new IllegalArgumentException("Illegal objects:" + objects);
		}
		Class<?>[] types = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			types[i] = object == null ? null : object.getClass();
		}
		return types;
	}

	/**
	 * 判断类型是否是基本数据类型
	 * 
	 * @param cls
	 *            数据类型
	 * @return true/false
	 */
	public static boolean isBasicClass(Class<?> cls) {
		if (cls == null) {
			return false;
		} else if (Number.class.isAssignableFrom(cls)) {
			return true;
		}
		try {
			return ((Class<?>) cls.getField("TYPE").get(null)).isPrimitive();
		} catch (NoSuchFieldException e) {
			return cls.isPrimitive();
		} catch (IllegalAccessException e) {
			return cls.isPrimitive();
		}
	}

	/**
	 * 判断对象类型是否是元类型
	 * 
	 * 元类型包括基本数据类型、字符串类型、枚举类型、日期类型、类对象类型
	 * 
	 * @param cls
	 *            对象类型
	 * @return true/false
	 */
	public static boolean isMetaClass(Class<?> cls) {
		return cls != null && (isBasicClass(cls) || cls == String.class || cls == Class.class || cls == Object.class
				|| Enum.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls));
	}

	/**
	 * 判断类型是否是基本数据包装类型
	 * 
	 * @param cls
	 *            数据类型
	 * @return true/false
	 */
	public static boolean isBasicWrapClass(Class<?> cls) {
		return cls != null && (cls == Byte.class || cls == Character.class || cls == Integer.class || cls == Short.class
				|| cls == Long.class || cls == Float.class || cls == Double.class || cls == Boolean.class);
	}

	/**
	 * 判断类型是否是基本数据数字类型
	 * 
	 * @param cls
	 *            数据类型
	 * @return true/false
	 */
	public static boolean isBasicNumberClass(Class<?> cls) {
		return cls != null && (cls == byte.class || cls == char.class || cls == short.class || cls == int.class
				|| cls == double.class || cls == long.class);
	}

	/**
	 * 判断类型是否是基本数据数字包装类型
	 * 
	 * @param cls
	 *            数据类型
	 * @return true/false
	 */
	public static boolean isBasicNumberWrapClass(Class<?> cls) {
		return cls != null && (cls == Byte.class || cls == Character.class || cls == Short.class || cls == Integer.class
				|| cls == Double.class || cls == Long.class);
	}

	/**
	 * 判断数据类型是否是数字类型
	 * 
	 * @param cls
	 *            数据类型
	 * @return true/false
	 */
	public static boolean isNumberClass(Class<?> cls) {
		return isBasicNumberClass(cls) || isBasicNumberWrapClass(cls);
	}

	/**
	 * 获取基本数据包装类型
	 * 
	 * @param cls
	 *            基本数据类型
	 * @return 基本数据包装类型
	 */
	public static Class<?> getBasicWrapClass(Class<?> cls) {
		if (cls == byte.class) {
			return Byte.class;
		} else if (cls == char.class) {
			return Character.class;
		} else if (cls == int.class) {
			return Integer.class;
		} else if (cls == short.class) {
			return Short.class;
		} else if (cls == long.class) {
			return Long.class;
		} else if (cls == float.class) {
			return Float.class;
		} else if (cls == double.class) {
			return Double.class;
		} else if (cls == boolean.class) {
			return Boolean.class;
		}
		return cls;
	}

	/**
	 * 获取树型键/值对象比较器
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param map
	 *            树型键/值对象
	 * @return 比较器对象
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Comparator<K> getTreeMapComparator(TreeMap<K, V> map) {
		if (map == null) {
			throw new IllegalArgumentException("Illegal map:" + map);
		}
		Field field = null;
		try {
			field = map.getClass().getDeclaredField("comparator");
			field.setAccessible(true);
			return (Comparator<K>) field.get(map);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			if (field != null) {
				field.setAccessible(false);
			}
		}
	}

	/**
	 * 获取类对象的泛型
	 * 
	 * @param cls
	 *            类对象对象
	 * @return 泛型类型数组
	 */
	public static Class<?>[] getGenericTypes(Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		Type genericSuperclass = cls.getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
			List<Class<?>> classes = new ArrayList<Class<?>>(types.length);
			for (Type type : types) {
				if (type instanceof Class) {
					classes.add((Class<?>) type);
				}
			}
			return classes.toArray(new Class<?>[0]);
		}
		Class<?> parent = cls.getSuperclass();
		return parent == null ? new Class<?>[0] : getGenericTypes(parent);
	}

	/**
	 * 获取字段的泛型
	 * 
	 * @param field
	 *            字段对象
	 * @return 泛型类型数组
	 */
	public static Class<?>[] getGenericTypes(Field field) {
		if (field == null) {
			throw new IllegalArgumentException("Illegal field:" + field);
		}
		Type genericSuperclass = field.getGenericType();
		if (genericSuperclass instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
			List<Class<?>> classes = new ArrayList<Class<?>>(types.length);
			for (Type type : types) {
				if (type instanceof Class) {
					classes.add((Class<?>) type);
				}
			}
			return classes.toArray(new Class<?>[0]);
		}
		return new Class<?>[0];
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param object
	 *            对象
	 * @return true/false
	 */
	public static boolean isEmpty(Object object) {
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)
				|| (object instanceof Map && ((Map<?, ?>) object).isEmpty())
				|| (object instanceof Collection && ((Collection<?>) object).isEmpty())
				|| (object instanceof Iterable && !((Iterable<?>) object).iterator().hasNext())) {
			return true;
		}
		Class<?> type = object.getClass();
		if (type.isArray()) {
			Class<?> component = type.getComponentType();
			if (component == byte.class) {
				return ((byte[]) object).length == 0;
			} else if (component == char.class) {
				return ((char[]) object).length == 0;
			} else if (component == int.class) {
				return ((int[]) object).length == 0;
			} else if (component == short.class) {
				return ((short[]) object).length == 0;
			} else if (component == long.class) {
				return ((long[]) object).length == 0;
			} else if (component == float.class) {
				return ((float[]) object).length == 0;
			} else if (component == double.class) {
				return ((double[]) object).length == 0;
			} else if (component == boolean.class) {
				return ((boolean[]) object).length == 0;
			}
			return ((Object[]) object).length == 0;
		}
		return false;
	}

	/**
	 * 判断数组中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array
	 *            数组
	 * @param object
	 *            对象
	 * @return true/false
	 */
	public static <T> boolean isExist(T[] array, T object) {
		return array != null && array.length > 0 && object != null && Arrays.asList(array).contains(object);
	}

	/**
	 * 判断数组中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array
	 *            数组
	 * @param object
	 *            对象
	 * @param comparator
	 *            比较器
	 * @return true/false
	 */
	public static <T> boolean isExist(T[] array, T object, Comparator<T> comparator) {
		return array != null && array.length > 0 && object != null && isExist(Arrays.asList(array), object, comparator);
	}

	/**
	 * 判断数组中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array
	 *            数组
	 * @param objects
	 *            对象数组
	 * @return true/false
	 */
	public static <T> boolean isExist(T[] array, T[] objects) {
		return array != null && array.length > 0 && objects != null && objects.length > 0
				&& isExist(Arrays.asList(array), objects);
	}

	/**
	 * 判断数组中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array
	 *            数组
	 * @param objects
	 *            对象数组
	 * @param comparator
	 *            比较器
	 * @return true/false
	 */
	public static <T> boolean isExist(T[] array, T[] objects, Comparator<T> comparator) {
		return array != null && array.length > 0 && objects != null && objects.length > 0
				&& isExist(Arrays.asList(array), objects, comparator);
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param object
	 *            对象
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, T object) {
		return collection != null && collection.size() > 0 && object != null && collection.contains(object);
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param object
	 *            对象
	 * @param comparator
	 *            对象比较器
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, T object, Comparator<T> comparator) {
		if (collection != null && collection.size() > 0 && object != null) {
			for (T _object : collection) {
				if (comparator.compare(_object, object) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param objects
	 *            对象数组
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, T[] objects) {
		if (collection != null && collection.size() > 0 && objects != null && objects.length > 0) {
			for (T object : objects) {
				if (!collection.contains(object)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param objects
	 *            对象集合
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, Collection<T> objects) {
		if (collection != null && collection.size() > 0 && objects != null && !objects.isEmpty()) {
			for (T object : objects) {
				if (!collection.contains(object)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param objects
	 *            对象数组
	 * @param comparator
	 *            对象比较器
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, T[] objects, Comparator<T> comparator) {
		if (collection != null && collection.size() > 0 && objects != null && objects.length > 0) {
			for (T object : objects) {
				if (!isExist(collection, object, comparator)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断集合中是否存在制定对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection
	 *            集合
	 * @param objects
	 *            对象集合
	 * @param comparator
	 *            对象比较器
	 * @return true/false
	 */
	public static <T> boolean isExist(Collection<T> collection, Collection<T> objects, Comparator<T> comparator) {
		if (collection != null && collection.size() > 0 && objects != null && !objects.isEmpty()) {
			for (T object : objects) {
				if (!isExist(collection, object, comparator)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断两个对象是否相同
	 * 
	 * @param object
	 *            对象
	 * @param other
	 *            对象
	 * @return true/false
	 */
	public static boolean isEqual(Object object, Object other) {
		return object == other || (object != null && object.equals(other)) || (other != null && other.equals(object));
	}

	/**
	 * 判断两个数组中元素是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array1
	 *            数组1
	 * @param array2
	 *            数组2
	 * @return true/false
	 */
	public static <T> boolean isEqual(T[] array1, T[] array2) {
		return isEqual(array1, array2, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return o1.equals(o2) ? 0 : -1;
			}

		});
	}

	/**
	 * 判断两个数组中元素是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param array1
	 *            数组1
	 * @param array2
	 *            数组2
	 * @param comparator
	 *            比较器
	 * @return true/false
	 */
	public static <T> boolean isEqual(T[] array1, T[] array2, Comparator<T> comparator) {
		return array1 == array2 || (array1 != null && array2 != null && array1.length == 0 && array2.length == 0)
				|| isEqual(Arrays.asList(array1), Arrays.asList(array2), comparator);
	}

	/**
	 * 判断两个集合中元素是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection1
	 *            集合1
	 * @param collection2
	 *            集合2
	 * @return true/false
	 */
	public static <T> boolean isEqual(Collection<T> collection1, Collection<T> collection2) {
		if (collection1 == collection2
				|| (collection1 != null && collection2 != null && collection1.isEmpty() && collection2.isEmpty())) {
			return true;
		} else if (collection1.size() != collection2.size()) {
			return false;
		}
		boolean equals = true;
		List<T> list2 = collection2 instanceof List ? (List<T>) collection2 : new ArrayList<T>(collection2);
		Map<Integer, Object> exists = new HashMap<Integer, Object>();
		for (T o1 : collection1) {
			boolean found = false;
			for (int i = 0; i < list2.size(); i++) {
				if (list2.get(i).equals(o1)) {
					found = true;
					exists.put(i, null);
					break;
				}
			}
			if (!found) {
				equals = false;
				break;
			}
		}
		if (equals) {
			for (int i = 0; i < list2.size(); i++) {
				if (!exists.containsKey(i) && !collection1.contains(list2.get(i))) {
					return false;
				}
			}
		}
		return equals;
	}

	/**
	 * 判断两个集合中元素是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param collection1
	 *            集合1
	 * @param collection2
	 *            集合2
	 * @param comparator
	 *            比较器
	 * @return true/false
	 */
	public static <T> boolean isEqual(Collection<T> collection1, Collection<T> collection2, Comparator<T> comparator) {
		if (collection1 == collection2
				|| (collection1 != null && collection2 != null && collection1.isEmpty() && collection2.isEmpty())) {
			return true;
		} else if (collection1.size() != collection2.size()) {
			return false;
		}
		boolean equals = true;
		List<T> list2 = collection2 instanceof List ? (List<T>) collection2 : new ArrayList<T>(collection2);
		Map<Integer, Object> exists = new HashMap<Integer, Object>();
		for (T o1 : collection1) {
			boolean found = false;
			for (int i = 0; i < list2.size(); i++) {
				if (comparator.compare(o1, list2.get(i)) == 0) {
					found = true;
					exists.put(i, null);
					break;
				}
			}
			if (!found) {
				equals = false;
				break;
			}
		}
		if (equals) {
			for (int i = 0; i < list2.size(); i++) {
				if (exists.containsKey(i)) {
					continue;
				}
				boolean found = false;
				for (T o1 : collection1) {
					if (comparator.compare(o1, list2.get(i)) == 0) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}
		return equals;
	}

	/**
	 * 判断两个对象属性值是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param object
	 *            对象
	 * @param other
	 *            对象
	 * @param fields
	 *            属性字段数组
	 * @return true/false
	 */
	public static <T> boolean isEqual(T object, T other, Field... fields) {
		if (object == other) {
			return true;
		} else if (object == null || other == null) {
			return false;
		}
		for (Field field : fields) {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			try {
				if (!isEqual(field.get(object), field.get(other))) {
					return false;
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} finally {
				if (!accessible) {
					field.setAccessible(false);
				}
			}
		}
		return true;
	}

	/**
	 * 判断两个对象属性值是否相同
	 * 
	 * @param <T>
	 *            数据类型
	 * @param object
	 *            对象
	 * @param other
	 *            对象
	 * @param properties
	 *            属性名称数组
	 * @return true/false
	 */
	public static <T> boolean isEqual(T object, T other, String... properties) {
		if (object == other) {
			return true;
		} else if (object == null || other == null) {
			return false;
		}
		for (String property : properties) {
			if (!isEqual(getValue(object, property), getValue(other, property))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断对象是否为数组
	 * 
	 * @param object
	 *            对象
	 * @return true/false
	 */
	public static boolean isArray(Object object) {
		return object != null && object.getClass().isArray();
	}

	/**
	 * 判断对象是否为数字
	 * 
	 * @param object
	 *            对象
	 * @return true/false
	 */
	public static boolean isNumber(Object object) {
		return object != null && isNumberClass(object.getClass());
	}

	/**
	 * 获取对象实例
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            对象类型
	 * @return 对象实例
	 */
	public static <T> T getInstance(Class<T> type) {
		return getInstance(type, false);
	}

	/**
	 * 获取对象实例
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            对象类型
	 * @param single
	 *            是否单例
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> type, boolean single) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		try {
			if (single) {
				T instance = (T) singles.get(type);
				if (instance == null) {
					synchronized (type) {
						instance = (T) singles.get(type);
						if (instance == null) {
							instance = type.newInstance();
							singles.put(type, instance);
						}
					}
				}
				return instance;
			}
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取对象实例
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            对象类型
	 * @param arguments
	 *            实例化参数
	 * @return 对象实例
	 */
	public static <T> T getInstance(Class<T> type, Object... arguments) {
		return getInstance(type, false, arguments);
	}

	/**
	 * 获取对象实例
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            对象类型
	 * @param single
	 *            是否单例
	 * @param arguments
	 *            实例化参数
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> type, boolean single, Object... arguments) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (arguments == null) {
			throw new IllegalArgumentException("Illegal arguments:" + arguments);
		}
		try {
			if (single) {
				T instance = (T) singles.get(type);
				if (instance == null) {
					synchronized (type) {
						instance = (T) singles.get(type);
						if (instance == null) {
							instance = type.getConstructor(getTypes(arguments)).newInstance(arguments);
							singles.put(type, instance);
						}
					}
				}
				return instance;
			}
			return type.getConstructor(getTypes(arguments)).newInstance(arguments);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 判断对象是否可以实例化
	 * 
	 * @param type
	 *            对象类型
	 * @return true/false
	 */
	public static boolean isInstantiable(Class<?> type) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		int mod = type.getModifiers();
		if (!(Modifier.isAbstract(mod) || Modifier.isInterface(mod) || Enum.class.isAssignableFrom(type))) {
			Constructor<?>[] constructors = type.getConstructors();
			if (constructors.length == 0) {
				return true;
			}
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据字段名称获取字段对象
	 * 
	 * @param cls
	 *            类对象
	 * @param name
	 *            字段名称
	 * @return 字段对象
	 */
	public static Field getField(Class<?> cls, String name) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		try {
			return cls.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			Class<?> parent = cls.getSuperclass();
			if (parent == null) {
				throw new RuntimeException(e);
			}
			return getField(parent, name);
		}
	}

	/**
	 * 根据字段名称获取字段对象
	 * 
	 * @param cls
	 *            类对象
	 * @param names
	 *            字段名称数组
	 * @return 字段对象数组
	 */
	public static Field[] getFields(Class<?> cls, String... names) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (names == null || names.length == 0) {
			List<Field> fields = new LinkedList<Field>();
			while (cls != Object.class) {
				for (Field field : cls.getDeclaredFields()) {
					if (!Modifier.isStatic(field.getModifiers()) && !field.getName().startsWith("this$")) {
						fields.add(field);
					}
				}
				cls = cls.getSuperclass();
			}
			return fields.toArray(new Field[0]);
		}
		Field[] fields = new Field[names.length];
		Field[] _fields = cls.getDeclaredFields();
		outer: for (int i = 0; i < names.length; i++) {
			String name = names[i];
			for (Field field : _fields) {
				if (field.getName().equals(name)) {
					fields[i] = field;
					continue outer;
				}
			}
			Class<?> parent = cls.getSuperclass();
			while (parent != null) {
				try {
					fields[i] = parent.getDeclaredField(name);
					continue outer;
				} catch (NoSuchFieldException e) {
					parent = parent.getSuperclass();
				}
			}
			throw new RuntimeException("No such field:" + name);
		}
		return fields;
	}

	/**
	 * 获取对象属性名称
	 * 
	 * @param cls
	 *            对象类型
	 * @return 字段名称数组
	 */
	public static String[] getProperties(Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (Enum.class.isAssignableFrom(cls)) {
			Method method = null;
			try {
				method = cls.getMethod("values");
				method.setAccessible(true);
				Object[] values = (Object[]) method.invoke(cls);
				String[] properties = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					properties[i] = values[i].toString();
				}
				return properties;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} finally {
				if (method != null) {
					method.setAccessible(false);
				}
			}
		}
		List<String> properties = new LinkedList<String>();
		while (cls != Object.class) {
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
					properties.add(field.getName());
				}
			}
			cls = cls.getSuperclass();
		}
		return properties.toArray(Strings.EMPTY_ARRAY);
	}

	/**
	 * 获取对象方法
	 * 
	 * @param cls
	 *            类对象
	 * @param name
	 *            方法名称
	 * @param parameterTypes
	 *            方法参数类型数组
	 * @return 方法对象
	 */
	public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		return getMethod(cls, cls, name, parameterTypes);
	}

	/**
	 * 获取对象方法
	 * 
	 * @param original
	 *            原始类对象
	 * @param cls
	 *            类对象
	 * @param name
	 *            方法名称
	 * @param parameterTypes
	 *            方法参数类型数组
	 * @return 方法对象
	 */
	private static Method getMethod(Class<?> original, Class<?> cls, String name, Class<?>... parameterTypes) {
		methods: for (Method method : cls.getMethods()) {
			if (method.getName().equals(name)) {
				Class<?>[] _parameterTypes = method.getParameterTypes();
				if (parameterTypes.length == _parameterTypes.length) {
					for (int i = 0; i < parameterTypes.length; i++) {
						if (!_parameterTypes[i].isAssignableFrom(parameterTypes[i])) {
							continue methods;
						}
					}
					return method;
				}
			}
		}
		Class<?> parent = cls.getSuperclass();
		if (parent == null) {
			StringBuilder buffer = new StringBuilder().append('(');
			for (int i = 0; i < parameterTypes.length; i++) {
				if (i > 0) {
					buffer.append(", ");
				}
				buffer.append(parameterTypes[i].getName());
			}
			buffer.append(')');
			throw new RuntimeException("No such method:" + original.getName() + '.' + name + buffer.toString());
		}
		return getMethod(original, parent, name, parameterTypes);
	}

	/**
	 * 获取字段get方法
	 * 
	 * @param cls
	 *            类对象
	 * @param field
	 *            字段对象
	 * @return 方法对象
	 */
	public static Method getGetMethod(Class<?> cls, Field field) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (field == null) {
			throw new IllegalArgumentException("Illegal field:" + field);
		}
		String property = field.getName();
		StringBuilder buffer = new StringBuilder().append(Character.toUpperCase(property.charAt(0)));
		if (property.length() > 1) {
			buffer.append(property.substring(1));
		}
		try {
			return cls.getMethod(buffer.insert(0, "get").toString());
		} catch (NoSuchMethodException e) {
			if (field.getType() == boolean.class || field.getType() == Boolean.class) {
				try {
					return cls.getMethod(buffer.insert(0, "is").toString());
				} catch (NoSuchMethodException e1) {
					throw new RuntimeException(e);
				}
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取字段set方法
	 * 
	 * @param cls
	 *            类对象
	 * @param field
	 *            字段对象
	 * @return 方法对象
	 */
	public static Method getSetMethod(Class<?> cls, Field field) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (field == null) {
			throw new IllegalArgumentException("Illegal field:" + field);
		}
		String property = field.getName();
		StringBuilder buffer = new StringBuilder("set").append(Character.toUpperCase(property.charAt(0)));
		if (property.length() > 1) {
			buffer.append(property.substring(1));
		}
		try {
			return cls.getMethod(buffer.toString(), field.getType());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取对象指定字段的值，对象属性必须支持get方法，如果属性类型为boolean,则可以是is方法
	 * 
	 * @param object
	 *            对象实例
	 * @param property
	 *            属性名称
	 * @return 字段值
	 */
	public static Object getValue(Object object, String property) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (object == null) {
			return null;
		}
		String suffix = null;
		int index = property.indexOf('.');
		if (index > 0) {
			suffix = property.substring(index + 1);
			property = property.substring(0, index);
		}
		Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
		Object value = getValue(object, getField(meta, property));
		if (value == null || suffix == null) {
			return value;
		}
		return getValue(value, suffix);
	}

	/**
	 * 获取对象指定字段的值，对象属性必须支持get方法，如果属性类型为boolean,则可以是is方法
	 * 
	 * @param object
	 *            对象实例
	 * @param field
	 *            字段对象
	 * @return 字段值
	 */
	public static Object getValue(Object object, Field field) {
		if (field == null) {
			throw new IllegalArgumentException("Illegal field:" + field);
		}
		if (object == null) {
			return null;
		}
		Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
		Method method = getGetMethod(meta, field);
		method.setAccessible(true);
		try {
			return Modifier.isPublic(method.getModifiers()) ? method.invoke(object) : null;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			method.setAccessible(false);
		}
	}

	/**
	 * 获取对象实例指定属性，对象属性必须支持get方法，如果属性类型为boolean,则可以是is方法
	 * 
	 * @param object
	 *            对象实例
	 * @param properties
	 *            属性名称数组（如果为空则获取所有属性值）
	 * @return 键/值对象
	 */
	public static Map<String, Object> getValues(Object object, String... properties) {
		if (object == null) {
			return new HashMap<String, Object>(0);
		}
		if (properties.length == 0) {
			Map<String, Object> values = new HashMap<String, Object>();
			Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
			while (meta != Object.class) {
				Field[] fields = meta.getDeclaredFields();
				for (Field field : fields) {
					if (!Modifier.isStatic(field.getModifiers())) {
						values.put(field.getName(), getValue(object, field));
					}
				}
				meta = meta.getSuperclass();
			}
			return values;
		}
		Map<String, Object> values = new HashMap<String, Object>(properties.length);
		for (String property : properties) {
			values.put(property, getValue(object, property));
		}
		return values;
	}

	/**
	 * 获取对象方法调用结果（级联属性之间使用“.”号隔开）
	 * 
	 * @param object
	 *            对象实例
	 * @param method
	 *            属性方法名称
	 * @return 属性方法值
	 */
	public static Object getMethodValue(Object object, String method) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (object == null) {
			return null;
		}
		Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
		int sign = method.lastIndexOf('.');
		if (sign <= 0) {
			try {
				return meta.getMethod(method).invoke(object);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return getMethodValue(getValue(object, method.substring(0, sign)), method.substring(sign + 1));
	}

	/**
	 * 获取集合中对象方法值
	 * 
	 * 如果对象实例为Collection或数组，则返回所有集合中所有对象指定方法名称的返回值； 否则返回实例指定属性值
	 * 
	 * @param object
	 *            对象实例
	 * @param method
	 *            方法名称
	 * @return 值
	 */
	public static Object getAssembleMethodValue(Object object, String method) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (isEmpty(object)) {
			return object;
		}
		if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			List<Object> values = new ArrayList<Object>(collection.size());
			for (Object o : collection) {
				Object v = getMethodValue(o, method);
				if (v != null) {
					values.add(v);
				}
			}
			return values;
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			List<Object> values = new ArrayList<Object>(array.length);
			for (Object o : array) {
				Object v = getMethodValue(o, method);
				if (v != null) {
					values.add(v);
				}
			}
			return values;
		}
		return getMethodValue(object, method);
	}

	/**
	 * 获取集合中对象属性值，对象属性必须支持get方法，如果属性类型为boolean,则可以是is方法
	 * 
	 * 如果对象为Map类型，则返回指定属性名称对应值；如果对象实例为Collection或数组，则返回所有集合中所有对象指定属性名称的值；
	 * 否则返回实例指定属性值
	 * 
	 * @param object
	 *            对象实例
	 * @param property
	 *            属性名称
	 * @return 值
	 */
	public static Object getAssemblePropertyValue(Object object, String property) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (isEmpty(object)) {
			return object;
		}
		Object value = null;
		String suffix = null;
		int index = property.indexOf('.');
		if (index > 0) {
			suffix = property.substring(index + 1);
			property = property.substring(0, index);
		}
		if (object instanceof Map) {
			value = ((Map<?, ?>) object).get(property);
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			List<Object> values = new ArrayList<Object>(collection.size());
			for (Object o : collection) {
				Object v = getAssemblePropertyValue(o, property);
				if (v != null) {
					values.add(v);
				}
			}
			value = values;
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			List<Object> values = new ArrayList<Object>(array.length);
			for (Object o : array) {
				Object v = getAssemblePropertyValue(o, property);
				if (v != null) {
					values.add(v);
				}
			}
			value = values;
		} else {
			value = getValue(object, property);
		}
		return suffix == null ? value : getAssemblePropertyValue(value, suffix);
	}

	/**
	 * 获取目标对象指定属性值作为键，指定属性值作为值，返回键/值对
	 * 
	 * @param object
	 *            对象实例
	 * @param property
	 *            键属性名称
	 * @param mapping
	 *            值属性名称
	 * @return 键/值对
	 */
	public static Map<Object, Object> getAssemblePropertyValue(Object object, String property, String mapping) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (mapping == null) {
			throw new IllegalArgumentException("Illegal mapping:" + mapping);
		}
		if (isEmpty(object)) {
			return new HashMap<Object, Object>(0);
		}
		if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			Map<Object, Object> mappings = new LinkedHashMap<Object, Object>(collection.size());
			for (Object o : collection) {
				Object key = getAssemblePropertyValue(o, property);
				if (key == null) {
					continue;
				}
				mappings.put(key, getAssemblePropertyValue(o, mapping));
			}
			return mappings;
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			Map<Object, Object> mappings = new LinkedHashMap<Object, Object>(array.length);
			for (Object o : array) {
				Object key = getAssemblePropertyValue(o, property);
				if (key == null) {
					continue;
				}
				mappings.put(key, getAssemblePropertyValue(o, mapping));
			}
			return mappings;
		}
		Object key = getAssemblePropertyValue(object, property);
		if (key == null) {
			return new HashMap<Object, Object>(0);
		}
		Map<Object, Object> mappings = new LinkedHashMap<Object, Object>(1);
		mappings.put(key, getAssemblePropertyValue(object, mapping));
		return mappings;
	}

	/**
	 * 根据属性名称递归获取对象属性值
	 * 
	 * @param object
	 *            目标对象
	 * @param property
	 *            属性名称
	 * @return 属性值列表
	 */
	public static List<Object> getAssemblePropertyValues(Object object, String property) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (isEmpty(object)) {
			return new ArrayList<Object>(0);
		}
		String suffix = null;
		int index = property.indexOf('.');
		if (index > 0) {
			suffix = property.substring(index + 1);
			property = property.substring(0, index);
		}
		List<Object> values = new LinkedList<Object>();
		if (object instanceof Map) {
			Object value = ((Map<?, ?>) object).get(property);
			if (suffix == null) {
				values.add(value);
			} else {
				values.addAll(getAssemblePropertyValues(value, suffix));
			}
		} else if (object instanceof Collection) {
			for (Object o : (Collection<?>) object) {
				Object value = getValue(o, property);
				if (suffix == null) {
					values.add(value);
				} else {
					values.addAll(getAssemblePropertyValues(value, suffix));
				}
			}
		} else if (object instanceof Object[]) {
			for (Object o : (Object[]) object) {
				Object value = getValue(o, property);
				if (suffix == null) {
					values.add(value);
				} else {
					values.addAll(getAssemblePropertyValues(value, suffix));
				}
			}
		} else {
			Object value = getValue(object, property);
			if (suffix == null) {
				values.add(value);
			} else {
				values.addAll(getAssemblePropertyValues(value, suffix));
			}
		}
		return values;
	}

	/**
	 * 根据对象数据分组
	 * 
	 * @param object
	 *            目标对象
	 * @param property
	 *            键属性名称
	 * @return 键/值映射
	 */
	public static Map<Object, List<Object>> getAssemblePropertyGroups(Object object, String property) {
		return getAssemblePropertyGroups(object, property, null);
	}

	/**
	 * 根据对象数据分组
	 * 
	 * @param object
	 *            目标对象
	 * @param property
	 *            键属性名称
	 * @param mapping
	 *            值属性名称
	 * @return 键/值映射
	 */
	public static Map<Object, List<Object>> getAssemblePropertyGroups(Object object, String property, String mapping) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (mapping == null) {
			throw new IllegalArgumentException("Illegal mapping:" + mapping);
		}
		if (Beans.isEmpty(object)) {
			return new HashMap<Object, List<Object>>(0);
		}
		Map<Object, List<Object>> groups = new LinkedHashMap<Object, List<Object>>();
		if (object instanceof Collection) {
			for (Object o : (Collection<?>) object) {
				doAssemblePropertyGrouping(groups, o, o, property, mapping);
			}
		} else if (object instanceof Object[]) {
			for (Object o : (Object[]) object) {
				doAssemblePropertyGrouping(groups, o, o, property, mapping);
			}
		} else {
			doAssemblePropertyGrouping(groups, object, object, property, mapping);
		}
		return groups;
	}

	/**
	 * 根据对象数据分组
	 * 
	 * @param groups
	 *            分组
	 * @param source
	 *            对象源
	 * @param object
	 *            目标对象
	 * @param property
	 *            键属性名称
	 * @param mapping
	 *            值属性名称
	 */
	private static void doAssemblePropertyGrouping(Map<Object, List<Object>> groups, Object source, Object current,
			String property, String mapping) {
		if (isEmpty(current)) {
			return;
		}
		String suffix = null;
		int index = property.indexOf('.');
		if (index > 0) {
			suffix = property.substring(index + 1);
			property = property.substring(0, index);
		}
		Collection<?> collection = current instanceof Collection ? (Collection<?>) current
				: current instanceof Object[] ? Arrays.asList((Object[]) current) : Arrays.asList(current);
		for (Object o : collection) {
			Object key = getValue(o, property);
			if (Beans.isEmpty(key)) {
				continue;
			}
			if (suffix == null) {
				if (key instanceof Collection || key instanceof Object[]) {
					Collection<?> keys = key instanceof Collection ? (Collection<?>) key
							: Arrays.asList((Object[]) key);
					for (Object k : keys) {
						List<Object> group = groups.get(k);
						if (group == null) {
							group = new LinkedList<Object>();
							groups.put(k, group);
						}
						group.add(mapping == null ? source : getAssemblePropertyValue(source, mapping));
					}
				} else {
					List<Object> group = groups.get(key);
					if (group == null) {
						group = new LinkedList<Object>();
						groups.put(key, group);
					}
					group.add(mapping == null ? source : getAssemblePropertyValue(source, mapping));
				}
			} else {
				doAssemblePropertyGrouping(groups, source, key, suffix, mapping);
			}
		}

	}

	/**
	 * 设置对象指定字段的值，对象属性必须支持set方法
	 * 
	 * @param object
	 *            对象实例
	 * @param field
	 *            字段对象
	 * @param value
	 *            字段值
	 */
	public static void setValue(Object object, Field field, Object value) {
		if (field == null) {
			throw new IllegalArgumentException("Illegal field:" + field);
		}
		if (object != null && field != null) {
			Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
			Method method = getSetMethod(meta, field);
			if (Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
				try {
					method.invoke(object, toObject(field.getType(), value));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				} finally {
					method.setAccessible(false);
				}
			}
		}
	}

	/**
	 * 设置对象指定属性的值，对象属性必须支持set方法
	 * 
	 * @param object
	 *            对象实例
	 * @param property
	 *            属性名称
	 * @param value
	 *            字段值
	 */
	public static void setValue(Object object, String property, Object value) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (object != null) {
			Class<?> meta = object instanceof Class ? (Class<?>) object : object.getClass();
			setValue(object, getField(meta, property), value);
		}
	}

	/**
	 * 将Map对象所包含的键/值填充到Bean对象实例对应的非静态属性中，对象属性必须支持set方法
	 * 
	 * @param object
	 *            对象实例
	 * @param values
	 *            需要填充的属性/值Map对象
	 */
	public static void setValues(Object object, Map<?, ?> values) {
		if (object != null && values != null && !values.isEmpty()) {
			Class<?> cls = object.getClass();
			while (cls != Object.class) {
				Field[] fields = cls.getDeclaredFields();
				for (Field field : fields) {
					if (values.containsKey(field.getName()) && !Modifier.isStatic(field.getModifiers())) {
						setValue(object, field, values.get(field.getName()));
					}
				}
				cls = cls.getSuperclass();
			}
		}
	}

	/**
	 * 对象拷贝（属性复制）
	 * 
	 * @param <T>
	 *            数据类型
	 * @param source
	 *            源对象
	 * @return 目标对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T source) {
		if (source == null) {
			return null;
		}
		T target = (T) getInstance(source.getClass());
		copy(source, target);
		return target;
	}

	/**
	 * 对象拷贝（属性复制）
	 * 
	 * @param <T>
	 *            数据类型
	 * @param source
	 *            源对象
	 * @param target
	 *            目标对象
	 */
	public static <T> void copy(T source, T target) {
		if (source != null && target != null) {
			Class<?> type = source.getClass();
			while (type != Object.class) {
				for (Field field : type.getDeclaredFields()) {
					int modifiers = field.getModifiers();
					if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
						continue;
					}
					field.setAccessible(true);
					try {
						field.set(target, field.get(source));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} finally {
						field.setAccessible(false);
					}
				}
				type = type.getSuperclass();
			}
		}
	}

	/**
	 * 初始化对象实例
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            对象类型
	 * @param values
	 *            初始化参数
	 * @return 对象实例
	 */
	public static <T> T initialize(Class<T> type, Map<?, ?> values) {
		if (type == null) {
			return null;
		}
		Class<?> cls = type;
		T instance = getInstance(type);
		if (values != null && !values.isEmpty()) {
			while (cls != Object.class) {
				Field[] fields = cls.getDeclaredFields();
				for (Field field : fields) {
					Object value = null;
					if (values.containsKey(field.getName()) && !Modifier.isStatic(field.getModifiers())
							&& !isEmpty(value = values.get(field.getName()))) {
						setValue(instance, field, value);
					}
				}
				cls = cls.getSuperclass();
			}
		}
		return instance;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 *            包路径名
	 * @return Java类集合
	 */
	public static List<Class<?>> getClasses(String pack) {
		if (pack == null) {
			throw new IllegalArgumentException("Illegal pack:" + pack);
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 获取包的名字 并进行替换
		String path = Strings.replace(pack, '.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		try {
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(path);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					classes.addAll(getClasses(pack, URLDecoder.decode(url.getFile(), "UTF-8")));
				} else if ("jar".equals(protocol)) {
					// 获取jar
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					// 从此jar包 得到一个枚举类
					Enumeration<JarEntry> entries = jar.entries();
					// 同样的进行循环迭代
					while (entries.hasMoreElements()) {
						// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						// 如果是以/开头的
						if (name.charAt(0) == '/') {
							// 获取后面的字符串
							name = name.substring(1);
						}
						// 如果前半部分和定义的包名相同
						if (name.startsWith(path)) {
							int idx = name.lastIndexOf('/');
							// 如果以"/"结尾 是一个包
							if (idx != -1) {
								// 获取包名 把"/"替换成"."
								pack = Strings.replace(name.substring(0, idx), '/', '.');
							}
							// 如果可以迭代下去 并且是一个包，且是一个.class文件 而且不是目录
							if (idx != -1 && name.endsWith(".class") && !entry.isDirectory()) {
								// 去掉后面的".class" 获取真正的类名
								String className = name.substring(pack.length() + 1, name.length() - 6);
								classes.add(Class.forName(pack + '.' + className));
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param pack
	 *            包名
	 * @param path
	 *            包路径
	 * @return 对象列表
	 */
	private static List<Class<?>> getClasses(String pack, String path) {
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			return new ArrayList<Class<?>>(0);
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}

		});

		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				classes.addAll(getClasses(pack + '.' + file.getName(), file.getAbsolutePath()));
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(classLoader.loadClass(pack + '.' + className));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return classes;
	}

	/**
	 * 获取数组对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            数组类型
	 * @param length
	 *            数组长度
	 * @return 数组对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getArray(Class<T> type, int length) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (length < 0) {
			throw new IllegalArgumentException("Illegal length:" + length);
		}
		Class<?> _type = isBasicClass(type) ? getBasicWrapClass(type) : type;
		return (T[]) Array.newInstance(_type, length);
	}

	/**
	 * 对象类型转换
	 * 
	 * @param type
	 *            转换目标类型
	 * @param object
	 *            被转换对象
	 * @return 转换后对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object toObject(Class<?> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (type == Object.class) {
			return object;
		} else if (type.isArray()) {
			return toArray(type.getComponentType(), object);
		} else if (type == byte.class || type == Byte.class) {
			return toByte((Class<Byte>) type, object);
		} else if (type == char.class || type == Character.class) {
			return toCharacter((Class<Character>) type, object);
		} else if (type == boolean.class || type == Boolean.class) {
			return toBoolean((Class<Boolean>) type, object);
		} else if (type == int.class || type == Integer.class) {
			return toInteger((Class<Integer>) type, object);
		} else if (type == short.class || type == Short.class) {
			return toShort((Class<Short>) type, object);
		} else if (type == float.class || type == Float.class) {
			return toFloat((Class<Float>) type, object);
		} else if (type == double.class || type == Double.class) {
			return toDouble((Class<Double>) type, object);
		} else if (type == long.class || type == Long.class) {
			return toLong((Class<Long>) type, object);
		} else if (Enum.class.isAssignableFrom(type)) {
			return toEnum((Class<Enum>) type, object);
		} else if (Date.class.isAssignableFrom(type)) {
			return toDate(object);
		} else if (type == String.class) {
			return Strings.toString(object);
		} else if (type == Class.class) {
			return toClass(object);
		} else if (object instanceof Set) {
			return toSet(type, object);
		} else if (object instanceof List) {
			return toList(type, object);
		} else if (object instanceof Iterable) {
			return toList(type, object);
		} else if (object instanceof Map) {
			return toMap(type, (Map<?, ?>) object);
		} else if (object instanceof byte[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (byte[]) object);
		} else if (object instanceof char[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (char[]) object);
		} else if (object instanceof int[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (int[]) object);
		} else if (object instanceof short[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (short[]) object);
		} else if (object instanceof long[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (long[]) object);
		} else if (object instanceof float[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (float[]) object);
		} else if (object instanceof double[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (double[]) object);
		} else if (object instanceof boolean[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (boolean[]) object);
		} else if (object instanceof Object[]) {
			return toArray(type.isArray() ? type.getComponentType() : type, (Object[]) object);
		} else if (object != null && !type.isAssignableFrom(object.getClass())) {
			throw new IllegalArgumentException("Cannot convert " + object + " to " + type);
		}
		return object;
	}

	/**
	 * 键/值对类型转换
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param <T>
	 *            目标数据类型
	 * @param type
	 *            转换类型
	 * @param object
	 *            被转换对象
	 * @return 键/值对象
	 */
	@SuppressWarnings("unchecked")
	public static <K, V, T> Map<K, T> toMap(Class<T> type, Map<K, V> object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null) {
			return new HashMap<K, T>(0);
		}
		Map<K, T> map = object instanceof TreeMap ? new TreeMap<K, T>(getTreeMapComparator((TreeMap<K, V>) object))
				: object instanceof LinkedHashMap ? new LinkedHashMap<K, T>(object.size())
						: new HashMap<K, T>(object.size());
		for (Entry<K, V> entry : object.entrySet()) {
			map.put(entry.getKey(), (T) toObject(type, entry.getValue()));
		}
		return map;
	}

	/**
	 * 集合类型转换
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            转换类型
	 * @param object
	 *            被转换对象
	 * @return Set
	 */
	public static <T> Set<T> toSet(Class<T> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null) {
			return new HashSet<T>(0);
		}
		T[] array = toArray(type, object);
		Set<T> set = new HashSet<T>(array.length);
		for (T o : array) {
			set.add(o);
		}
		return set;
	}

	/**
	 * 列表类型转换
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            转换类型
	 * @param object
	 *            被转换对象
	 * @return List
	 */
	public static <T> List<T> toList(Class<T> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null) {
			return new ArrayList<T>(0);
		}
		T[] array = toArray(type, object);
		List<T> list = new ArrayList<T>(array.length);
		for (T o : array) {
			list.add(o);
		}
		return list;
	}

	/**
	 * 将对象转换成数组
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            数组类型
	 * @param object
	 *            被转换对象
	 * @return 数组对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null) {
			return getArray(type, 0);
		} else if (object instanceof List) {
			List<?> list = (List<?>) object;
			T[] array = getArray(type, list.size());
			for (int i = 0; i < list.size(); i++) {
				array[i] = (T) toObject(type, list.get(i));
			}
			return array;
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			T[] array = getArray(type, collection.size());
			int i = 0;
			for (Object o : collection) {
				array[i++] = (T) toObject(type, o);
			}
			return array;
		} else if (object instanceof Iterable) {
			List<T> list = new LinkedList<T>();
			Iterator<?> iterator = ((Iterable<?>) object).iterator();
			while (iterator.hasNext()) {
				list.add((T) toObject(type, iterator.next()));
			}
			return list.toArray(getArray(type, 0));
		} else if (object.getClass().isArray()) {
			Class<?> component = object.getClass().getComponentType();
			if (type == component || type.isAssignableFrom(component)) {
				return (T[]) object;
			} else if (component == byte.class) {
				byte[] bytes = (byte[]) object;
				T[] array = getArray(type, bytes.length);
				for (int i = 0, len = bytes.length; i < len; i++) {
					array[i] = (T) toObject(type, bytes[i]);
				}
				return array;
			} else if (component == char.class) {
				char[] chars = (char[]) object;
				T[] array = getArray(type, chars.length);
				for (int i = 0, len = chars.length; i < len; i++) {
					array[i] = (T) toObject(type, chars[i]);
				}
				return array;
			} else if (component == int.class) {
				int[] ints = (int[]) object;
				T[] array = getArray(type, ints.length);
				for (int i = 0, len = ints.length; i < len; i++) {
					array[i] = (T) toObject(type, ints[i]);
				}
				return array;
			} else if (component == short.class) {
				short[] shorts = (short[]) object;
				T[] array = getArray(type, shorts.length);
				for (int i = 0, len = shorts.length; i < len; i++) {
					array[i] = (T) toObject(type, shorts[i]);
				}
				return array;
			} else if (component == long.class) {
				long[] longs = (long[]) object;
				T[] array = getArray(type, longs.length);
				for (int i = 0, len = longs.length; i < len; i++) {
					array[i] = (T) toObject(type, longs[i]);
				}
				return array;
			} else if (component == float.class) {
				float[] floats = (float[]) object;
				T[] array = getArray(type, floats.length);
				for (int i = 0, len = floats.length; i < len; i++) {
					array[i] = (T) toObject(type, floats[i]);
				}
				return array;
			} else if (component == double.class) {
				double[] doubles = (double[]) object;
				T[] array = getArray(type, doubles.length);
				for (int i = 0, len = doubles.length; i < len; i++) {
					array[i] = (T) toObject(type, doubles[i]);
				}
				return array;
			} else if (component == boolean.class) {
				boolean[] booleans = (boolean[]) object;
				T[] array = getArray(type, booleans.length);
				for (int i = 0, len = booleans.length; i < len; i++) {
					array[i] = (T) toObject(type, booleans[i]);
				}
				return array;
			}
			Object[] _array = (Object[]) object;
			T[] array = getArray(type, _array.length);
			for (int i = 0, len = _array.length; i < len; i++) {
				array[i] = (T) toObject(type, _array[i]);
			}
			return array;
		}
		T[] array = getArray(type, 1);
		array[0] = (T) toObject(type, object);
		return array;
	}

	/**
	 * 字节类型转换
	 * 
	 * @param type
	 *            字节类型
	 * @param object
	 *            被转换对象
	 * @return 字节对象
	 */
	public static Byte toByte(Class<Byte> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == byte.class ? (byte) 0 : null;
		}
		return (Byte) (object instanceof Byte ? object
				: object instanceof Number ? ((Number) object).byteValue() : Byte.parseByte(object.toString()));
	}

	/**
	 * 字符类型转换
	 * 
	 * @param type
	 *            字符类型
	 * @param object
	 *            被转换对象
	 * @return 字符对象
	 */
	public static Character toCharacter(Class<Character> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == char.class ? (char) 0 : null;
		}
		return (Character) (object instanceof Character ? object
				: object instanceof Number ? ((Number) object).intValue() : Integer.parseInt(object.toString()));
	}

	/**
	 * 真假类型转换
	 * 
	 * @param type
	 *            真假类型
	 * @param object
	 *            被转换对象
	 * @return 真假对象
	 */
	public static Boolean toBoolean(Class<Boolean> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == boolean.class ? false : null;
		}
		return (Boolean) (object instanceof Boolean ? object : Boolean.parseBoolean(object.toString()));
	}

	/**
	 * 整形类型转换
	 * 
	 * @param type
	 *            整形类型
	 * @param object
	 *            被转换对象
	 * @return 整形对象
	 */
	public static Integer toInteger(Class<Integer> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == int.class ? 0 : null;
		}
		return (Integer) (object instanceof Character ? object
				: object instanceof Number ? ((Number) object).intValue() : Integer.parseInt(object.toString()));
	}

	/**
	 * 短整形类型转换
	 * 
	 * @param type
	 *            短整形类型
	 * @param object
	 *            被转换对象
	 * @return 短整形对象
	 */
	public static Short toShort(Class<Short> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == short.class ? (short) 0 : null;
		}
		return (Short) (object instanceof Short ? object
				: object instanceof Number ? ((Number) object).shortValue() : Short.parseShort(object.toString()));
	}

	/**
	 * 单精度浮点类型转换
	 * 
	 * @param type
	 *            单精度浮点类型
	 * @param object
	 *            被转换对象
	 * @return 单精度浮点对象
	 */
	public static Float toFloat(Class<Float> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == float.class ? (float) 0 : null;
		}
		return (Float) (object instanceof Short ? object
				: object instanceof Number ? ((Number) object).floatValue() : Float.parseFloat(object.toString()));
	}

	/**
	 * 双精度浮点类型转换
	 * 
	 * @param type
	 *            双精度浮点类型
	 * @param object
	 *            被转换对象
	 * @return 双精度浮点对象
	 */
	public static Double toDouble(Class<Double> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == double.class ? (double) 0 : null;
		}
		return (Double) (object instanceof Double ? object
				: object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble(object.toString()));
	}

	/**
	 * 长整形类型转换
	 * 
	 * @param type
	 *            长整形类型
	 * @param object
	 *            被转换对象
	 * @return 长整形对象
	 */
	public static Long toLong(Class<Long> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		if (object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0)) {
			return type == long.class ? (long) 0 : null;
		}
		return (Long) (object instanceof Long ? object
				: object instanceof Number ? ((Number) object).longValue() : Long.parseLong(object.toString()));
	}

	/**
	 * 枚举类型转换
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            Enum类型
	 * @param object
	 *            被转换对象
	 * @return 枚举实例
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T toEnum(Class<T> type, Object object) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		return object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0) ? null
				: object instanceof Enum ? (T) object : Enum.valueOf(type, object.toString());
	}

	/**
	 * 日期类型转换
	 * 
	 * @param object
	 *            被转换对象
	 * @return 日期
	 */
	public static Date toDate(Object object) {
		return object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0) ? null
				: object instanceof Date ? (Date) object
						: object instanceof Number ? new Date(((Number) object).longValue())
								: Dates.parse(object.toString());
	}

	/**
	 * 类对象转换
	 * 
	 * @param object
	 *            被转换对象
	 * @return 类对象
	 */
	public static Class<?> toClass(Object object) {
		try {
			return object == null || (object instanceof CharSequence && ((CharSequence) object).length() == 0) ? null
					: object instanceof Class ? (Class<?>) object : Class.forName(object.toString());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 拷贝对象实例，深度克隆
	 * 
	 * @param <T>
	 *            数据类型
	 * @param source
	 *            源对象
	 * @return 对象实例副本
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T source) {
		try {
			return source == null ? null : (T) Streams.deserialize(Streams.serialize(source));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对象或实例方法调用，方法参数类型由实际参数决定
	 * 
	 * @param target
	 *            调用目标对象或实例
	 * @param method
	 *            调用方法对象
	 * @param args
	 *            方法参数数组
	 * @return 调用结果
	 */
	public static Object invoke(Object target, Method method, Object... args) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (target == null) {
			return null;
		}
		method.setAccessible(true);
		try {
			return args == null ? method.invoke(target) : method.invoke(target, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			method.setAccessible(false);
		}
	}

	/**
	 * 对象或实例方法调用，方法参数类型由实际参数决定
	 * 
	 * @param target
	 *            调用目标对象或实例
	 * @param method
	 *            调用方法名称
	 * @param args
	 *            方法参数数组
	 * @return 调用结果
	 */
	public static Object invoke(Object target, String method, Object... args) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (target == null) {
			return null;
		}
		Class<?>[] parameterTypes = new Class<?>[args == null ? 0 : args.length];
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = args[i].getClass();
		}
		Class<?> targetClass = target instanceof Class ? ((Class<?>) target) : target.getClass();
		return invoke(target, getMethod(targetClass, method, parameterTypes), args);
	}

	/**
	 * 对象或实例方法调用，方法参数类型由实际参数决定
	 * 
	 * @param targets
	 *            调用目标对象或实例数组
	 * @param method
	 *            调用方法名称
	 * @param args
	 *            方法参数数组
	 * @return 调用结果数组
	 */
	public static Object[] invoke(Object[] targets, String method, Object... args) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (targets == null) {
			return null;
		}
		Object[] values = new Object[targets.length];
		for (int i = 0; i < targets.length; i++) {
			values[i] = invoke(targets[i], method, args);
		}
		return values;
	}

	/**
	 * 计算对象数组的hash值
	 * 
	 * @param objects
	 *            对象数组
	 * @return hash值
	 */
	public static int hashCode(Object... objects) {
		if (objects == null || objects.length == 0) {
			return 0;
		}
		int code = 1;
		for (Object object : objects) {
			if (object instanceof Byte) {
				code = 31 * code + (int) (Byte) object;
			} else if (object instanceof Character) {
				code = 31 * code + (int) (Character) object;
			} else if (object instanceof Short) {
				code = 31 * code + (int) (Short) object;
			} else if (object instanceof Integer) {
				code = 31 * code + (int) (Integer) object;
			} else if (object instanceof Long) {
				long v = (long) (Long) object;
				code = 31 * code + (int) (v ^ (v >>> 32));
			} else if (object instanceof Float) {
				code = 31 * code + Float.floatToIntBits((float) (Float) object);
			} else if (object instanceof Double) {
				long bits = Double.doubleToLongBits((double) (Double) object);
				code = 31 * code + (int) (bits ^ (bits >>> 32));
			} else if (object instanceof Boolean) {
				code = 31 * code + ((boolean) (Boolean) object ? 1 : 0);
			} else if (object instanceof byte[]) {
				code = 31 * code + Arrays.hashCode((byte[]) object);
			} else if (object instanceof char[]) {
				code = 31 * code + Arrays.hashCode((char[]) object);
			} else if (object instanceof short[]) {
				code = 31 * code + Arrays.hashCode((short[]) object);
			} else if (object instanceof int[]) {
				code = 31 * code + Arrays.hashCode((int[]) object);
			} else if (object instanceof long[]) {
				code = 31 * code + Arrays.hashCode((long[]) object);
			} else if (object instanceof float[]) {
				code = 31 * code + Arrays.hashCode((float[]) object);
			} else if (object instanceof double[]) {
				code = 31 * code + Arrays.hashCode((double[]) object);
			} else if (object instanceof boolean[]) {
				code = 31 * code + Arrays.hashCode((boolean[]) object);
			} else if (object instanceof Object[]) {
				code = 31 * code + hashCode((Object[]) object);
			} else {
				code = 31 * code + (object == null ? 0 : object.hashCode());
			}
		}
		return code;
	}

	/**
	 * 将对象转换成键/值对形式
	 * 
	 * @param object
	 *            被转换对象
	 * @return 对象键/值对
	 */
	public static Object format(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			if (map.isEmpty()) {
				return new HashMap<String, Object>(0);
			}
			Map<String, Object> copy = new HashMap<String, Object>(map.size());
			for (Entry<?, ?> entry : map.entrySet()) {
				copy.put(String.valueOf(entry.getKey()), format(entry.getValue()));
			}
			return copy;
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			if (collection.isEmpty()) {
				return new ArrayList<Object>(0);
			}
			List<Object> copy = new ArrayList<Object>(collection.size());
			for (Object o : collection) {
				copy.add(format(o));
			}
			return copy;
		}
		Map<String, Object> values = getValues(object);
		for (Entry<String, Object> entry : values.entrySet()) {
			entry.setValue(format(entry.getValue()));
		}
		return values;
	}

	/**
	 * 合并对象集合（过滤重复对象）
	 * 
	 * @param <M>
	 *            数据类型
	 * @param collection1
	 *            对象集合
	 * @param collection2
	 *            对象集合
	 * @return 合并后的对象集合
	 */
	public static <M> List<M> merge(Collection<M> collection1, Collection<M> collection2) {
		if ((collection1 == null || collection1.isEmpty()) && (collection2 == null || collection2.isEmpty())) {
			return new ArrayList<M>(0);
		}
		List<M> objects = new LinkedList<M>();
		if (collection1 != null && !collection1.isEmpty()) {
			for (M object : collection1) {
				if (!objects.contains(object)) {
					objects.add(object);
				}
			}
		}
		if (collection2 != null && !collection2.isEmpty()) {
			for (M object : collection2) {
				if (!objects.contains(object)) {
					objects.add(object);
				}
			}
		}
		return objects;
	}

	/**
	 * 对象比较
	 * 
	 * @param o1
	 *            比较对象
	 * @param o2
	 *            比较对象
	 * @return 比较结果数字
	 */
	@SuppressWarnings("unchecked")
	public static int compare(Object o1, Object o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else if (o1.getClass() == o2.getClass() && o1 instanceof Comparable) {
			return ((Comparable<Object>) o1).compareTo(o2);
		}
		int h1 = o1.hashCode();
		int h2 = o2.hashCode();
		return h1 < h2 ? -1 : h1 == h2 ? 0 : 1;
	}

	/**
	 * 将对象排序
	 * 
	 * @param <M>
	 *            数据类型
	 * @param collection
	 *            对象集合
	 * @return 排序后对象集合
	 */
	public static <M> List<M> sort(Collection<M> collection) {
		return sort(collection, Strings.EMPTY_ARRAY);
	}

	/**
	 * 将对象集合按照属性值排序（属性名以“+”号开头或不以“-”号开头表示升序，以“-”号开头表示降序）
	 * 
	 * @param <M>
	 *            数据类型
	 * @param collection
	 *            对象集合
	 * @param properties
	 *            属性名称数组
	 * @return 排序后对象集合
	 */
	public static <M> List<M> sort(Collection<M> collection, final String... properties) {
		if (collection == null || collection.isEmpty()) {
			return new ArrayList<M>(0);
		}
		List<M> list = collection instanceof List ? (List<M>) collection : new ArrayList<M>(collection);
		Collections.sort(list, new Comparator<M>() {

			@Override
			public int compare(M o1, M o2) {
				if (properties.length == 0) {
					return Beans.compare(o1, o2);
				}
				for (String property : properties) {
					Boolean asc = property.charAt(0) == '+' ? Boolean.TRUE
							: property.charAt(0) == '-' ? Boolean.FALSE : null;
					if (asc != null) {
						property = property.substring(1);
					}
					int offset = Beans.compare(getValue(o1, property), getValue(o2, property));
					if (offset != 0) {
						return asc == null || asc == Boolean.TRUE ? offset : -offset;
					}
				}
				return 0;
			}

		});
		return list;
	}

	/**
	 * 获取两个对象实例不同属性值
	 * 
	 * @param <M>
	 *            数据类型
	 * @param object
	 *            对象实例
	 * @param other
	 *            对象实例
	 * @param fields
	 *            比较字段数组
	 * @return 不同属性值
	 */
	public static <M> Map<String, Object[]> getDifferent(M object, M other, Field... fields) {
		if (object == null || other == null) {
			return new HashMap<String, Object[]>(0);
		}
		if (fields == null || fields.length == 0) {
			fields = getFields(object.getClass());
		}
		Map<String, Object[]> different = new LinkedHashMap<String, Object[]>();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object value1 = field.get(object);
				Object value2 = field.get(other);
				if (!isEqual(value1, value2)) {
					different.put(field.getName(), new Object[] { value1, value2 });
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} finally {
				field.setAccessible(false);
			}
		}
		return different;
	}

	/**
	 * 获取两个对象实例不同属性值
	 * 
	 * @param <M>
	 *            数据类型
	 * @param object
	 *            对象实例
	 * @param other
	 *            对象实例
	 * @param properties
	 *            比较属性名称数组
	 * @return 不同属性值
	 */
	public static <M> Map<String, Object[]> getDifferent(M object, M other, String... properties) {
		if (object == null || other == null) {
			return new HashMap<String, Object[]>(0);
		}
		return getDifferent(object, other, getFields(object.getClass(), properties));
	}

	/**
	 * 获取实际异常对象
	 * 
	 * @param throwable
	 *            异常对象
	 * @return 异常对象
	 */
	public static Throwable getThrowableCause(Throwable throwable) {
		if (throwable == null) {
			throw new IllegalArgumentException("Illegal throwable:" + throwable);
		}
		Throwable parent = throwable;
		Throwable cause = throwable.getCause();
		while (cause != null) {
			parent = cause;
			cause = cause.getCause();
		}
		return parent;
	}

}
