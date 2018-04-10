package ars.invoke.channel.http;

import java.util.Map;
import java.util.Map.Entry;

import ars.util.Strings;

/**
 * 根据请求结果重定向实现
 *
 * @author wuyongqiang
 */
public class ResponseRedirector implements Redirector {
    private Class<?> response; // 请求结果类型
    private String redirect; // 固定重定向资源
    private Map<String, String> redirects; // 请求资源/重定向资源映射

    public Class<?> getResponse() {
        return response;
    }

    public void setResponse(Class<?> response) {
        this.response = response;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Map<String, String> getRedirects() {
        return redirects;
    }

    public void setRedirects(Map<String, String> redirects) {
        this.redirects = redirects;
    }

    @Override
    public String getRedirect(HttpRequester requester, Object value) {
        if (this.response == null) {
            throw new IllegalStateException("Response not initialized");
        }
        if (this.redirect == null && this.redirects.isEmpty()) {
            throw new IllegalStateException("Redirect not initialized");
        }
        if (value != null && this.response.isAssignableFrom(value.getClass())) {
            if (this.redirect != null) {
                return this.redirect;
            }
            String resource = requester.getUri();
            String redirect = this.redirects.get(resource);
            if (redirect != null) {
                return redirect;
            }
            for (Entry<String, String> entry : this.redirects.entrySet()) {
                if (Strings.matches(resource, entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
