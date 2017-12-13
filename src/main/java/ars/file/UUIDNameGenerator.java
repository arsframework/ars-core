package ars.file;

import java.util.UUID;

import ars.util.Files;
import ars.file.NameGenerator;

/**
 * UUID名称生成器
 * 
 * @author yongqiangwu
 * 
 */
public class UUIDNameGenerator implements NameGenerator {

	@Override
	public String generate(String name) {
		String uuid = UUID.randomUUID().toString();
		String suffix = Files.getSuffix(name);
		return suffix == null ? uuid : new StringBuilder(uuid).append('.').append(suffix).toString();
	}

}
