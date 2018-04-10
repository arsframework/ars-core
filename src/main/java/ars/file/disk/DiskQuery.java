package ars.file.disk;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.LinkedList;

import ars.file.Describe;
import ars.file.query.AbstractQuery;
import ars.file.query.Queries;
import ars.file.query.Queries.Condition;

/**
 * 基于本地磁盘的文件查询集合实现
 *
 * @author wuyongqiang
 */
public class DiskQuery extends AbstractQuery {

    public DiskQuery(String workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public List<Describe> execute(String path, final boolean spread, final Condition... conditions) {
        final List<Describe> describes = new LinkedList<Describe>();
        (path == null ? new File(this.workingDirectory) : new File(this.workingDirectory, path))
            .listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    Describe describe = new Describe(file);
                    describe.setPath(describe.getPath().substring(workingDirectory.length()));
                    if (Queries.isSatisfy(describe, conditions)) {
                        describes.add(describe);
                    }
                    if (spread && describe.isDirectory()) {
                        describes.addAll(execute(describe.getPath(), spread, conditions));
                    }
                    return false;
                }
            });
        return describes;
    }

}
