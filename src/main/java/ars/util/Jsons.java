package ars.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Collection;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.reflect.TypeToken;

import ars.util.Beans;
import ars.util.Strings;
import ars.util.Formable;
import ars.util.ObjectAdapter;

/**
 * 基于google gson的json处理工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Jsons {
	private static ObjectAdapter[] objectAdapters = new ObjectAdapter[0];

	/**
	 * Gson通用类型适配器
	 */
	private static final TypeAdapter<Object> commonTypeAdapter = new TypeAdapter<Object>() {

		@Override
		public Object read(JsonReader reader) throws IOException {
			return null;
		}

		@Override
		public void write(JsonWriter writer, Object object) throws IOException {
			Jsons.write(writer, object, 1);
		}

	};

	/**
	 * 完整的JSON解析处理对象
	 */
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	/**
	 * 最小化JSON解析处理对象
	 */
	private static final Gson minGson = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {

		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> token) {
			return (TypeAdapter<T>) commonTypeAdapter;
		}

	}).disableHtmlEscaping().create();

	private Jsons() {

	}

	public static ObjectAdapter[] getObjectAdapters() {
		return objectAdapters;
	}

	public static void setObjectAdapters(ObjectAdapter... objectAdapters) {
		Jsons.objectAdapters = objectAdapters;
	}

	/**
	 * 对象适配
	 * 
	 * @param object
	 *            被适配对象
	 * @return 适配对象
	 */
	public static Object adaption(Object object) {
		if (object != null && objectAdapters.length > 0) {
			for (ObjectAdapter adapter : objectAdapters) {
				object = adapter.adaption(object);
			}
		}
		return object;
	}

	/**
	 * 将对象写入json
	 * 
	 * @param writer
	 *            json写操作对象
	 * @param object
	 *            对象实例
	 * @param level
	 *            当前层级
	 * @throws IOException
	 *             IO操作异常
	 */
	private static void write(JsonWriter writer, Object object, int level) throws IOException {
		Object adapted;
		if (object == null || level > 2) {
			writer.nullValue();
		} else if (object instanceof String) {
			writer.value((String) object);
		} else if (object instanceof Number) {
			writer.value((Number) object);
		} else if (object instanceof Boolean) {
			writer.value((Boolean) object);
		} else if (Beans.isMetaClass(object.getClass())) {
			writer.value(Strings.toString(object));
		} else if ((adapted = adaption(object)) != object) {
			write(writer, adapted, level);
		} else if (object instanceof Formable) {
			write(writer, ((Formable) object).format(), level + 1);
		} else if (object instanceof Map) {
			writer.beginObject();
			for (Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				writer.name(Strings.toString(entry.getKey()));
				write(writer, entry.getValue(), 1);
			}
			writer.endObject();
		} else if (object instanceof Collection) {
			writer.beginArray();
			for (Object value : ((Collection<?>) object)) {
				write(writer, value, 1);
			}
			writer.endArray();
		} else if (object instanceof Object[]) {
			writer.beginArray();
			for (Object value : ((Object[]) object)) {
				write(writer, value, 1);
			}
			writer.endArray();
		} else if (object instanceof int[]) {
			writer.beginArray();
			for (int value : ((int[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof long[]) {
			writer.beginArray();
			for (long value : ((long[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof short[]) {
			writer.beginArray();
			for (short value : ((short[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof float[]) {
			writer.beginArray();
			for (float value : ((float[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof double[]) {
			writer.beginArray();
			for (double value : ((double[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof char[]) {
			writer.beginArray();
			for (char value : ((char[]) object)) {
				writer.value(String.valueOf(value));
			}
			writer.endArray();
		} else if (object instanceof byte[]) {
			writer.beginArray();
			for (byte value : ((byte[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else if (object instanceof boolean[]) {
			writer.beginArray();
			for (boolean value : ((boolean[]) object)) {
				writer.value(value);
			}
			writer.endArray();
		} else {
			writer.beginObject();
			Class<?> type = object.getClass();
			while (type != Object.class) {
				for (Field field : type.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					Object value;
					field.setAccessible(true);
					try {
						value = field.get(object);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} finally {
						field.setAccessible(false);
					}
					writer.name(field.getName());
					if (value instanceof String) {
						value = Strings.escape((String) value);
					}
					if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean
							|| Beans.isMetaClass(value.getClass())) {
						write(writer, value, level);
					} else {
						write(writer, value, level + 1);
					}
				}
				type = type.getSuperclass();
			}
			writer.endObject();
		}
	}

	/**
	 * 将对象转换成JSON字符串
	 * 
	 * 对象向下关联一级
	 * 
	 * @param object
	 *            被转换对象
	 * @return JSON字符串
	 */
	public static String format(Object object) {
		return format(object, true);
	}

	/**
	 * 将对象转换成JSON字符串
	 * 
	 * 对象向下关联一级
	 * 
	 * @param object
	 *            被转换对象
	 * @param min
	 *            是否最小化转换
	 * @return JSON字符串
	 */
	public static String format(Object object, boolean min) {
		return object == null || object instanceof String ? (String) object
				: min ? minGson.toJson(object) : gson.toJson(object);
	}

	/**
	 * 将JSON字符串反转成对象
	 * 
	 * @param json
	 *            JSON字符串
	 * @return 对象实例
	 */
	public static Object parse(String json) {
		return parse(Object.class, json);
	}

	/**
	 * 将JSON字符串反转成对象
	 * 
	 * @param <T>
	 *            数据类型
	 * @param type
	 *            目标对象类型
	 * @param json
	 *            JSON字符串
	 * @return 对象实例
	 */
	public static <T> T parse(Class<T> type, String json) {
		return Strings.isEmpty(json) ? null : gson.fromJson(json, type);
	}

}
