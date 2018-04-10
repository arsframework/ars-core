package ars.invoke.local;

import java.lang.reflect.Method;

import ars.util.Beans;
import ars.invoke.Resource;

/**
 * 对象方法资源
 *
 * @author wuyongqiang
 */
public class Function implements Resource {
    private static final long serialVersionUID = 1L;

    private transient Object target; // 目标对象或实例
    private transient Method method; // 目标方法对象
    private transient Argument[] arguments; // 参数数组

    public Function(Object target, Method method, Argument... arguments) {
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }

    public Function(Object target, String method, Class<?>... types) {
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        Class<?> meta = target instanceof Class ? (Class<?>) target : target.getClass();
        this.target = target;
        this.method = Beans.getMethod(meta, method, types);
        this.arguments = new Argument[types.length];
        for (int i = 0; i < types.length; i++) {
            Argument argument = new Argument();
            argument.setType(types[i]);
            this.arguments[i] = argument;
        }
    }

    public Function(Object target, String method, Argument... arguments) {
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        Class<?>[] types = new Class<?>[arguments.length];
        Class<?> targetClass = target instanceof Class ? (Class<?>) target : target.getClass();
        for (int i = 0; i < arguments.length; i++) {
            types[i] = arguments[i].getType();
        }
        this.target = target;
        this.method = Beans.getMethod(targetClass, method, types);
        this.arguments = arguments;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return this.method.toString();
    }

}
