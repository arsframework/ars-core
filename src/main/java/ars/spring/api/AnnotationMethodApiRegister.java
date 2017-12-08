package ars.spring.api;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ars.util.Strings;
import ars.invoke.Router;
import ars.invoke.Invoker;
import ars.invoke.Invokes;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;

/**
 * 查找对象实例中所有使用注解的方法，并将接口资源注册
 * 
 * @author wuyq
 * 
 */
public class AnnotationMethodApiRegister implements ApplicationContextAware {
	private String prefix; // 资源地址前缀（多个前缀之间采用“,”号隔开）
	private Object target; // 目标对象
	private Invoker invoker; // 资源调用对象

	public AnnotationMethodApiRegister(String prefix, Object target) {
		this(prefix, target, Invokes.getSingleLocalInvoker());
	}

	public AnnotationMethodApiRegister(String prefix, Object target, Invoker invoker) {
		if (prefix == null) {
			throw new IllegalArgumentException("Illegal prefix:" + prefix);
		}
		if (target == null) {
			throw new IllegalArgumentException("Illegal target:" + target);
		}
		if (invoker == null) {
			throw new IllegalArgumentException("Illegal invoker:" + invoker);
		}
		this.prefix = prefix;
		this.target = target;
		this.invoker = invoker;
	}

	public Object getTarget() {
		return target;
	}

	public String getPrefix() {
		return prefix;
	}

	public Invoker getInvoker() {
		return invoker;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		String[] prefixs = Strings.split(this.prefix, ',');
		Router router = applicationContext.getBean(Router.class);
		Method[] methods = Apis.getApiMethods(this.target.getClass());
		for (Method method : methods) {
			String methodApi = Apis.getApi(method);
			for (String p : prefixs) {
				p = p.trim();
				if (p.isEmpty()) {
					continue;
				}
				String api = Strings.replace(new StringBuilder(p).append('/').append(methodApi), "//", "/");
				router.register(api, this.invoker, new Function(this.target, method, Apis.getConditions(method)));
			}
		}
	}

}
