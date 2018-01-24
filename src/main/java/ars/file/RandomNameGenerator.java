package ars.file;

import ars.util.Files;
import ars.util.Strings;
import ars.file.NameGenerator;

/**
 * 随机文件名生成器
 * 
 * @author yongqiangwu
 * 
 */
public class RandomNameGenerator implements NameGenerator {
	protected final int length; // 随机数长度

	public RandomNameGenerator() {
		this(4);
	}

	public RandomNameGenerator(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("Illegal length:" + length);
		}
		this.length = length;
	}

	@Override
	public String generate(String name) {
		StringBuilder random = new StringBuilder().append(System.currentTimeMillis())
				.append(Strings.random(this.length, Strings.CHARS));
		String suffix = Files.getSuffix(name);
		return suffix == null ? random.toString() : random.append('.').append(suffix).toString();
	}

}
