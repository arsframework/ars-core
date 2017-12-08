package ars.file;

import ars.file.DirectoryGenerator;

/**
 * 文件路径名称生成器简单实现
 * 
 * @author wuyq
 * 
 */
public class SimpleDirectoryGenerator implements DirectoryGenerator {
	private String name; // 路径名称

	public SimpleDirectoryGenerator(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String generate(String path) {
		return this.name;
	}

}
