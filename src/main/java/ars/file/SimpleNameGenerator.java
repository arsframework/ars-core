package ars.file;

import ars.file.NameGenerator;

/**
 * 文件名称生成器简单实现
 * 
 * @author wuyq
 * 
 */
public class SimpleNameGenerator implements NameGenerator {
	private String name; // 文件名称

	public SimpleNameGenerator(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String generate(String name) {
		return this.name;
	}

}
