package ars.file;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 基于日期格式的文件目录名称生成器
 *
 * @author wuyongqiang
 */
public class DateDirectoryGenerator implements DirectoryGenerator {
    protected final String format;

    public DateDirectoryGenerator() {
        this("yyyyMM");
    }

    public DateDirectoryGenerator(String format) {
        if (format == null) {
            throw new IllegalArgumentException("Format must not be null");
        }
        this.format = format;
    }

    @Override
    public String generate(String name) {
        return new SimpleDateFormat(this.format).format(new Date());
    }

}
