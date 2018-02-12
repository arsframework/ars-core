package ars.invoke.local;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;

import ars.util.Beans;
import ars.util.Strings;
import ars.util.SimpleTree;
import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.local.Condition;
import ars.invoke.local.ParamAdapter;
import ars.invoke.request.Requester;
import ars.invoke.request.ParameterInvalidException;

/**
 * 接口工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Apis {
	private Apis() {

	}

	/**
	 * 获取所有使用了Api注解的公共类
	 * 
	 * @param packages
	 *            包路径数组
	 * @return 类对象集合
	 */
	public static List<Class<?>> getApiClasses(String... packages) {
		if (packages == null || packages.length == 0) {
			return new ArrayList<Class<?>>(0);
		}
		List<Class<?>> classes = new LinkedList<Class<?>>();
		for (String pack : packages) {
			List<Class<?>> metas = Beans.getClasses(pack);
			for (Class<?> meta : metas) {
				if (meta.isAnnotationPresent(Api.class)) {
					classes.add(meta);
				}
			}
		}
		return classes;
	}

	/**
	 * 获取使用了Api注解的公共类
	 * 
	 * @param cls
	 *            类对象
	 * @return 类对象
	 */
	public static Class<?> getApiClass(Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (cls.isAnnotationPresent(Api.class)) {
			return cls;
		}
		Class<?>[] interfaces = cls.getInterfaces();
		for (Class<?> c : interfaces) {
			Class<?> apiClass = getApiClass(c);
			if (apiClass != null) {
				return apiClass;
			}
		}
		Class<?> parent = cls.getSuperclass();
		return parent == null ? null : getApiClass(parent);
	}

	/**
	 * 获取类中使用了Api注解的公共方法
	 * 
	 * @param cls
	 *            类
	 * @return 方法数组
	 */
	public static Method[] getApiMethods(Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		List<Method> methods = _getApiMethods(cls);
		if (methods.isEmpty()) {
			return new Method[0];
		}
		Set<String> apis = new HashSet<String>(methods.size());
		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			String api = getApi(iterator.next());
			if (apis.contains(api)) {
				iterator.remove();
			} else {
				apis.add(api);
			}
		}
		return methods.toArray(new Method[0]);
	}

	/**
	 * 获取类中使用了Api注解的公共方法
	 * 
	 * @param cls
	 *            类
	 * @return 方法数组
	 */
	private static List<Method> _getApiMethods(Class<?> cls) {
		List<Method> methods = new LinkedList<Method>();
		for (Method method : cls.getMethods()) {
			if (method.isAnnotationPresent(Api.class)) {
				methods.add(method);
			}
		}
		Class<?>[] interfaces = cls.getInterfaces();
		for (Class<?> c : interfaces) {
			methods.addAll(_getApiMethods(c));
		}
		Class<?> parent = cls.getSuperclass();
		if (parent != null) {
			methods.addAll(_getApiMethods(parent));
		}
		return methods;
	}

	/**
	 * 获取类接口地址
	 * 
	 * @param cls
	 *            类对象
	 * @return 接口地址
	 */
	public static String getApi(Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("Illegal cls:" + cls);
		}
		if (!cls.isAnnotationPresent(Api.class)) {
			return null;
		}
		String api = cls.getAnnotation(Api.class).value();
		return api.isEmpty() ? Strings.replace(cls.getName(), '.', '/') : api;
	}

	/**
	 * 获取方法接口地址
	 * 
	 * @param method
	 *            方法对象
	 * @return 接口地址
	 */
	public static String getApi(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (!method.isAnnotationPresent(Api.class)) {
			return null;
		}
		String api = method.getAnnotation(Api.class).value();
		return api.isEmpty() ? method.getName() : api;
	}

	/**
	 * 获取方法接口条件
	 * 
	 * @param method
	 *            方法对象
	 * @return 条件对象数组
	 */
	public static Condition[] getConditions(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		Class<?>[] types = method.getParameterTypes();
		Condition[] conditions = new Condition[types.length];
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < types.length; i++) {
			String name = null;
			String regex = null;
			String value = null;
			boolean required = false;
			ParamAdapter adapter = null;
			for (Annotation annotation : annotations[i]) {
				if (annotation.annotationType() == Param.class) {
					Param param = (Param) annotation;
					name = param.name().trim();
					value = param.value().trim();
					regex = param.regex().trim();
					required = param.required();
					Class<? extends ParamAdapter> adapterType = param.adapter();
					if (adapterType != ParamAdapter.class) {
						adapter = Beans.getInstance(adapterType);
					}
					break;
				}
			}
			Condition condition = new Condition();
			condition.setType(types[i]);
			if (!Strings.isEmpty(name)) {
				condition.setName(name);
			}
			if (!Strings.isEmpty(value)) {
				condition.setValue(value);
			}
			if (!Strings.isEmpty(regex)) {
				condition.setRegex(regex);
			}
			condition.setAdapter(adapter);
			condition.setRequired(required);
			conditions[i] = condition;
		}
		return conditions;
	}

	/**
	 * 将参数转换成对象实体，同时移除参数
	 * 
	 * @param type
	 *            对象类型
	 * @param parameters
	 *            参数键/值映射
	 * @return 对象实体
	 */
	private static Object param2entity(Class<?> type, Map<String, Object> parameters) {
		if (type == null) {
			throw new IllegalArgumentException("Illegal type:" + type);
		}
		Class<?> cls = type;
		Object instance = Beans.getInstance(type);
		if (parameters != null && !parameters.isEmpty()) {
			while (cls != Object.class) {
				Field[] fields = cls.getDeclaredFields();
				for (Field field : fields) {
					if (parameters.containsKey(field.getName()) && !Modifier.isStatic(field.getModifiers())) {
						Object value = parameters.remove(field.getName());
						if (value == null) {
							continue;
						}
						field.setAccessible(true);
						try {
							field.set(instance, Beans.toObject(field.getType(), value));
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} finally {
							field.setAccessible(false);
						}
					}
				}
				cls = cls.getSuperclass();
			}
		}
		return instance;
	}

	/**
	 * 获取本地接口方法参数
	 * 
	 * @param requester
	 *            请求对象
	 * @param conditions
	 *            接口方法条件对象数据
	 * @return 本地接口方法参数数组
	 * @throws Exception
	 *             操作异常
	 */
	public static Object[] getParameters(Requester requester, Condition... conditions) throws Exception {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		Object[] _parameters = new Object[conditions.length];
		Map<String, Object> parameters = requester.getParameters();
		for (int i = 0; i < conditions.length; i++) {
			Condition condition = conditions[i];
			Class<?> type = condition.getType();
			String name = condition.getName();
			String regex = condition.getRegex();
			ParamAdapter adapter = condition.getAdapter();
			if (Requester.class.isAssignableFrom(type)) {
				_parameters[i] = requester;
			} else if (Map.class.isAssignableFrom(type)) {
				_parameters[i] = parameters;
			} else if (adapter == null) {
				try {
					if (name == null) {
						_parameters[i] = Beans.isMetaClass(type) ? Beans.toObject(type, condition.getValue())
								: param2entity(type, parameters);
					} else {
						Object value = parameters.remove(name);
						if (value == null) {
							value = condition.getValue();
						}
						if (regex != null && value instanceof CharSequence
								&& !Strings.getPattern(regex).matcher((CharSequence) value).matches()) {
							throw new ParameterInvalidException(name, "invalid");
						}
						_parameters[i] = Beans.toObject(type, value);
					}
				} catch (IllegalArgumentException e) {
					throw new ParameterInvalidException(name, e.getMessage());
				}
			} else {
				_parameters[i] = adapter.adaption(requester, type, parameters);
			}
			if (condition.isRequired() && Beans.isEmpty(_parameters[i])) {
				throw new ParameterInvalidException(name, "required");
			}
		}
		return _parameters;
	}

	/**
	 * 获取接口地址树
	 * 
	 * @param apis
	 *            接口地址集合
	 * @return 树列表
	 */
	public static List<SimpleTree> getTrees(Collection<String> apis) {
		if (apis == null) {
			throw new IllegalArgumentException("Illegal apis:" + apis);
		}
		List<SimpleTree> roots = new ArrayList<SimpleTree>(apis.size());
		Map<String, SimpleTree> trees = new HashMap<String, SimpleTree>(apis.size());
		for (String api : apis) {
			if (api.equals("/")) {
				SimpleTree tree = new SimpleTree(api);
				roots.add(tree);
				continue;
			}
			int from = -1, last = -1;
			while ((from = api.indexOf('/', from + 1)) > -1) {
				last = from;
				String id = api.substring(0, from);
				if (trees.containsKey(id)) {
					continue;
				}
				int index = id.lastIndexOf('/');
				SimpleTree tree = new SimpleTree(id);
				if (index < 0) {
					roots.add(tree);
				} else {
					SimpleTree parent = trees.get(id.substring(0, index));
					tree.setParent(parent);
					parent.getChildren().add(tree);
				}
				trees.put(id, tree);
			}
			if (last < api.length() - 1) {
				SimpleTree leaf = new SimpleTree(api);
				SimpleTree parent = trees.get(api.substring(0, last));
				leaf.setParent(parent);
				parent.getChildren().add(leaf);
				trees.put(api, leaf);
			}
		}
		return roots;
	}

}
