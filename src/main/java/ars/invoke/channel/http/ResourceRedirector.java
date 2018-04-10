package ars.invoke.channel.http;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import ars.util.Strings;

/**
 * 根据请求资源重定向实现
 *
 * @author wuyongqiang
 */
public class ResourceRedirector implements Redirector {
    private String resource; // 请求资源地址
    private String redirect; // 固定重定向资源
    private Map<Class<?>, String> redirects = new HashMap<Class<?>, String>(0); // 请求结果类型/重定向资源映射

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Map<Class<?>, String> getRedirects() {
        return redirects;
    }

    public void setRedirects(Map<Class<?>, String> redirects) {
        this.redirects = redirects;
    }

    @Override
    public String getRedirect(HttpRequester requester, Object value) {
        if (this.resource == null) {
            throw new IllegalStateException("Resource not initialized");
        }
        if (this.redirect == null && this.redirects.isEmpty()) {
            throw new IllegalStateException("Redirect not initialized");
        }
        if (Strings.matches(requester.getUri(), this.resource)) {
            if (this.redirect != null) {
                return this.redirect;
            } else if (value == null) {
                return this.redirects.get(null);
            }
            Class<?> response = value.getClass();
            String redirect = this.redirects.get(response);
            if (redirect != null) {
                return redirect;
            }
            for (Entry<Class<?>, String> entry : this.redirects.entrySet()) {
                if (entry.getKey().isAssignableFrom(response)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
