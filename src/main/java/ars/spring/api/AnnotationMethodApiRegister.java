package ars.spring.api;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ars.util.Strings;
import ars.invoke.Router;
import ars.invoke.Invoker;
import ars.invoke.local.Apis;
import ars.invoke.local.Function;
import ars.invoke.local.LocalInvoker;

/**
 * 查找对象实例中所有使用注解的方法，并将接口资源注册
 *
 * @author wuyongqiang
 */
public class AnnotationMethodApiRegister implements ApplicationContextAware {
    private boolean cover; // 是否覆盖
    private String prefix; // 资源地址前缀
    private Object target; // 目标对象
    private Invoker invoker; // 资源调用对象
    private String pattern; // 匹配模式

    public AnnotationMethodApiRegister(String prefix, Object target) {
        this(prefix, target, true);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, boolean cover) {
        this(prefix, target, new LocalInvoker(), cover);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, String pattern) {
        this(prefix, target, new LocalInvoker(), pattern);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, Invoker invoker) {
        this(prefix, target, invoker, true, null);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, Invoker invoker, boolean cover) {
        this(prefix, target, invoker, cover, null);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, Invoker invoker, String pattern) {
        this(prefix, target, invoker, true, pattern);
    }

    public AnnotationMethodApiRegister(String prefix, Object target, Invoker invoker, boolean cover, String pattern) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        if (invoker == null) {
            throw new IllegalArgumentException("Invoker must not be null");
        }
        this.cover = cover;
        this.prefix = prefix;
        this.target = target;
        this.invoker = invoker;
        this.pattern = pattern;
    }

    public boolean isCover() {
        return cover;
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
        Router router = applicationContext.getBean(Router.class);
        Method[] methods = Apis.getApiMethods(this.target.getClass());
        for (Method method : methods) {
            String methodApi = Apis.getApi(method);
            if (this.pattern == null || Strings.matches(methodApi, this.pattern)) {
                String api = Strings.replace(new StringBuilder(this.prefix).append('/').append(methodApi), "//", "/");
                router.register(api, this.invoker, new Function(this.target, method, Apis.getArguments(method)),
                    this.cover);
            }
        }
    }

}
