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
	private static final Character[] CHARS = new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };

	private int length = 4; // 随机数长度

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("Illegal length:" + length);
		}
		this.length = length;
	}

	@Override
	public String generate(String name) {
		StringBuilder random = new StringBuilder().append(System.currentTimeMillis())
				.append(Strings.random(this.length, CHARS));
		String suffix = Files.getSuffix(name);
		return suffix == null ? random.toString() : random.append('.').append(suffix).toString();
	}

}
