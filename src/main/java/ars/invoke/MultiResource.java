package ars.invoke;

import ars.util.Strings;
import ars.invoke.Resource;

/**
 * 多资源包装资源实现
 * 
 * @author yongqiangwu
 *
 */
public class MultiResource implements Resource {
	private static final long serialVersionUID = 1L;

	private String[] resources; // 目标资源地址数组

	public MultiResource(String... resources) {
		if (resources == null || resources.length == 0) {
			throw new IllegalArgumentException("Illegal resources:" + Strings.toString(resources));
		}
		this.resources = resources;
	}

	public String[] getResources() {
		return resources;
	}

}
