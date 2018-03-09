package ars.file.disk;

import java.io.File;

import ars.util.Files;
import ars.util.Strings;
import ars.util.AbstractTaskServer;

/**
 * 文件清理服务
 * 
 * @author yongqiangwu
 * 
 */
public class FileCleanupServer extends AbstractTaskServer {
	private int expired; // 文件过期时间（天）
	private String pattern; // 文件名称正则表达式
	private String[] directories; // 需要清理的文件目录

	public int getExpired() {
		return expired;
	}

	public void setExpired(int expired) {
		if (expired < 1) {
			throw new IllegalArgumentException("Illegal expired:" + expired);
		}
		this.expired = expired;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String[] getDirectories() {
		return directories;
	}

	public void setDirectories(String... directories) {
		this.directories = directories;
	}

	/**
	 * 文件清理
	 * 
	 * @param file
	 *            文件对象
	 */
	protected void cleanup(File file) {
		if (file != null && file.exists() && (this.pattern == null || Strings.matches(file.getName(), this.pattern))) {
			long created = System.currentTimeMillis() - this.expired * 24 * 60 * 60 * 1000;
			if (file.lastModified() <= created) {
				Files.delete(file);
			} else if (file.isDirectory()) {
				File[] children = file.listFiles();
				if (children != null && children.length > 0) {
					for (File child : children) {
						this.cleanup(child);
					}
				}
			}
		}
	}

	@Override
	protected void execute() throws Exception {
		if (this.directories == null || this.directories.length == 0) {
			throw new RuntimeException("Directories has not been initialize");
		}
		for (String directory : this.directories) {
			this.cleanup(new File(directory));
		}
	}

}
