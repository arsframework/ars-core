package ars.file;

/**
 * 文件名称生成器简单实现
 *
 * @author wuyongqiang
 */
public class SimpleNameGenerator implements NameGenerator {
    protected final String name; // 文件名称

    public SimpleNameGenerator(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
    }

    @Override
    public String generate(String name) {
        return this.name;
    }

}
