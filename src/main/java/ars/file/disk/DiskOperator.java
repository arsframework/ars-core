package ars.file.disk;

import java.io.File;
import java.io.InputStream;

import ars.util.Files;
import ars.util.Nfile;
import ars.util.Streams;
import ars.file.Describe;
import ars.file.AbstractOperator;
import ars.file.query.Query;

/**
 * 基于本地磁盘的文件操作实现
 *
 * @author wuyongqiang
 */
public class DiskOperator extends AbstractOperator {

    public DiskOperator() {

    }

    public DiskOperator(String workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public boolean exists(String path) throws Exception {
        return new File(this.workingDirectory, path).exists();
    }

    @Override
    public boolean mkdirs(String path) throws Exception {
        return new File(this.workingDirectory, path).mkdirs();
    }

    @Override
    public boolean rename(String path, String name) throws Exception {
        File target = new File(new File(this.workingDirectory, path).getParent(), name);
        return new File(this.workingDirectory, path).renameTo(target);
    }

    @Override
    public void delete(String path) throws Exception {
        Files.delete(new File(this.workingDirectory, path));
    }

    @Override
    public void copy(String source, String target) throws Exception {
        Files.copy(new File(this.workingDirectory, source), new File(this.workingDirectory, target));
    }

    @Override
    public void move(String source, String target) throws Exception {
        Files.move(new File(this.workingDirectory, source), new File(this.workingDirectory, target));
    }

    @Override
    public Query query() {
        return new DiskQuery(this.workingDirectory);
    }

    @Override
    public Describe describe(String path) throws Exception {
        File file = new File(this.workingDirectory, path);
        if (!file.exists()) {
            return null;
        }
        Describe describe = new Describe(file);
        describe.setPath(describe.getPath().substring(this.workingDirectory.length()));
        return describe;
    }

    @Override
    public Nfile read(String path) throws Exception {
        File file = new File(this.workingDirectory, path);
        return file.exists() && file.isFile() ? new Nfile(file) : null;
    }

    @Override
    public void write(File file, String path) throws Exception {
        Streams.write(file, new File(this.workingDirectory, path));
    }

    @Override
    public void write(Nfile file, String path) throws Exception {
        Streams.write(file, new File(this.workingDirectory, path));
    }

    @Override
    public void write(InputStream stream, String path) throws Exception {
        Streams.write(stream, new File(this.workingDirectory, path));
    }

}
