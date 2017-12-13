package ars.invoke.local;

import java.lang.reflect.Method;

import ars.util.Beans;
import ars.invoke.Resource;
import ars.invoke.local.Condition;

/**
 * 对象方法资源
 * 
 * @author yongqiangwu
 * 
 */
public class Function implements Resource {
	private static final long serialVersionUID = 1L;

	private transient Object target; // 目标对象或实例
	private transient Method method; // 目标方法对象
	private transient Condition[] conditions; // 参数条件数组

	public Function(Object target, Method method, Condition... conditions) {
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		if (conditions == null) {
			throw new IllegalArgumentException("Illegal conditions:" + conditions);
		}
		this.target = target;
		this.method = method;
		this.conditions = conditions;
	}

	public Function(Object target, String method, Class<?>... types) {
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		Class<?> meta = target instanceof Class ? (Class<?>) target : target.getClass();
		for (int i = 0; i < conditions.length; i++) {
			types[i] = conditions[i].getType();
		}
		this.target = target;
		this.method = Beans.getMethod(meta, method, types);
		this.conditions = new Condition[types.length];
		for (int i = 0; i < types.length; i++) {
			Condition condition = new Condition();
			condition.setType(types[i]);
			this.conditions[i] = condition;
		}
	}

	public Function(Object target, String method, Condition... conditions) {
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (method == null) {
			throw new IllegalArgumentException("Illegal method:" + method);
		}
		Class<?>[] types = new Class<?>[conditions.length];
		Class<?> targetClass = target instanceof Class ? (Class<?>) target : target.getClass();
		for (int i = 0; i < conditions.length; i++) {
			types[i] = conditions[i].getType();
		}
		this.target = target;
		this.method = Beans.getMethod(targetClass, method, types);
		this.conditions = conditions;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public Condition[] getConditions() {
		return conditions;
	}

	@Override
	public String toString() {
		return this.method.toString();
	}

}
