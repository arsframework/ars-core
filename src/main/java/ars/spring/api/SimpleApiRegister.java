package ars.spring.api;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ars.invoke.Router;
import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.local.Function;
import ars.invoke.local.Argument;
import ars.invoke.local.LocalInvoker;
import ars.invoke.remote.Node;
import ars.invoke.remote.Endpoint;
import ars.invoke.remote.Protocol;
import ars.invoke.remote.RemoteInvoker;

/**
 * 本地接口资源注册简单实现
 *
 * @author wuyongqiang
 */
public class SimpleApiRegister implements ApplicationContextAware {
    private String api; // 接口地址
    private boolean cover; // 是否覆盖
    private Invoker invoker; // 资源调用器
    private Resource resource; // 服务资源

    public SimpleApiRegister(String api, Invoker invoker, Resource resource) {
        this(api, invoker, resource, true);
    }

    public SimpleApiRegister(String api, Invoker invoker, Resource resource, boolean cover) {
        if (api == null) {
            throw new IllegalArgumentException("Api must not be null");
        }
        if (invoker == null) {
            throw new IllegalArgumentException("Invoker must not be null");
        }
        if (resource == null) {
            throw new IllegalArgumentException("Resource must not be null");
        }
        this.api = api;
        this.invoker = invoker;
        this.resource = resource;
        this.cover = cover;
    }

    public SimpleApiRegister(String api, Node... nodes) {
        this(api, null, nodes);
    }

    public SimpleApiRegister(String api, String uri, Node... nodes) {
        this(api, new RemoteInvoker(), new Endpoint(uri, nodes));
    }

    public SimpleApiRegister(String api, Protocol protocol, String host, int port) {
        this(api, protocol, host, port, null);
    }

    public SimpleApiRegister(String api, Protocol protocol, String host, int port, String uri) {
        this(api, new RemoteInvoker(), new Endpoint(protocol, host, port, uri));
    }

    public SimpleApiRegister(String api, Object target, String method) {
        this(api, target, method, new Argument[0]);
    }

    public SimpleApiRegister(String api, Object target, String method, Argument... arguments) {
        this(api, new LocalInvoker(), new Function(target, method, arguments));
    }

    public String getApi() {
        return api;
    }

    public boolean isCover() {
        return cover;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBean(Router.class).register(this.api, this.invoker, this.resource, this.cover);
    }

}
