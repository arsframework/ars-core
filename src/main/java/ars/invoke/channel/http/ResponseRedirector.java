package ars.invoke.channel.http;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import ars.util.Strings;
import ars.invoke.channel.http.Redirector;
import ars.invoke.channel.http.HttpRequester;

/**
 * 根据请求结果重定向实现
 * 
 * @author yongqiangwu
 * 
 */
public class ResponseRedirector implements Redirector {
	private Class<?> response; // 请求结果类型
	private String redirect; // 固定重定向资源
	private Map<String, String> redirects = new HashMap<String, String>(0); // 请求资源/重定向资源映射

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
			throw new RuntimeException("Response has not been initialize");
		}
		if (this.redirect == null && this.redirects.isEmpty()) {
			throw new RuntimeException("Redirect has not been initialize");
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
