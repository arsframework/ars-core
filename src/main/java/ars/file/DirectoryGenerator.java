package ars.file;

/**
 * 文件目录生成器
 *
 * @author wuyongqiang
 */
public interface DirectoryGenerator {
    /**
     * 生成文件目录
     *
     * @param name 文件名
     * @return 文件目录
     */
    public String generate(String name);

}
