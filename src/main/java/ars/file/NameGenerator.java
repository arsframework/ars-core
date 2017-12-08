package ars.file;

/**
 * 文件名生成器
 * 
 * @author wuyq
 *
 */
public interface NameGenerator {
	/**
	 * 生成文件目录
	 * 
	 * @param name
	 *            文件名
	 * @return 文件目录
	 */
	public String generate(String name);

}
