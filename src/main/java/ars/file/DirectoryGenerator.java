package ars.file;

/**
 * 文件目录生成器
 * 
 * @author wuyq
 *
 */
public interface DirectoryGenerator {
	/**
	 * 生成文件目录
	 * 
	 * @param path
	 *            文件目录
	 * @return 文件目录
	 */
	public String generate(String path);

}
