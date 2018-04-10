package ars.file;

/**
 * 文件路径名称生成器简单实现
 *
 * @author wuyongqiang
 */
public class SimpleDirectoryGenerator implements DirectoryGenerator {
    protected final String name; // 路径名称

    public SimpleDirectoryGenerator(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
    }

    @Override
    public String generate(String path) {
        return this.name;
    }

}
