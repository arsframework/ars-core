package ars.file;

import ars.file.DirectoryGenerator;

/**
 * 文件路径名称生成器简单实现
 * 
 * @author yongqiangwu
 * 
 */
public class SimpleDirectoryGenerator implements DirectoryGenerator {
	protected final String name; // 路径名称

	public SimpleDirectoryGenerator(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		this.name = name;
	}

	@Override
	public String generate(String path) {
		return this.name;
	}

}
