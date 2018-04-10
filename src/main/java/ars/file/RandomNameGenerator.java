package ars.file;

import ars.util.Files;
import ars.util.Strings;

/**
 * 随机文件名生成器
 *
 * @author wuyongqiang
 */
public class RandomNameGenerator implements NameGenerator {
    protected final int length; // 随机数长度

    public RandomNameGenerator() {
        this(4);
    }

    public RandomNameGenerator(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must not be less than 1, got " + length);
        }
        this.length = length;
    }

    @Override
    public String generate(String name) {
        StringBuilder random = new StringBuilder().append(System.currentTimeMillis())
            .append(Strings.random(Strings.CHARS, this.length));
        String suffix = Files.getSuffix(name);
        return suffix == null ? random.toString() : random.append('.').append(suffix).toString();
    }

}
