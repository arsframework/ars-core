package ars.file;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

import ars.file.DirectoryGenerator;

/**
 * 基于日期格式的文件目录名称生成器
 * 
 * @author yongqiangwu
 * 
 */
public class DateDirectoryGenerator implements DirectoryGenerator {
	private String format;
	private SimpleDateFormat formater;

	public DateDirectoryGenerator() {
		this("yyyyMM");
	}

	public DateDirectoryGenerator(String format) {
		if (format == null) {
			throw new IllegalArgumentException("Illegal format:" + format);
		}
		this.format = format;
		this.formater = new SimpleDateFormat(this.format);
	}

	public String getFormat() {
		return format;
	}

	@Override
	public String generate(String path) {
		String directory = this.formater.format(new Date());
		return path == null ? directory : new File(path, directory).getPath();
	}

}
