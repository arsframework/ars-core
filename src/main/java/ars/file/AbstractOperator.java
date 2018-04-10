package ars.file;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import ars.util.Nfile;
import ars.util.Strings;
import ars.file.query.Query;

/**
 * 文件操作抽象实现
 */
public abstract class AbstractOperator implements Operator {
    protected final String workingDirectory; // 工作目录

    public AbstractOperator() {
        this(Strings.TEMP_PATH);
    }

    public AbstractOperator(String workingDirectory) {
        this.workingDirectory = workingDirectory == null ? Strings.TEMP_PATH : Strings.getRealPath(workingDirectory);
    }

    @Override
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public List<Describe> trees(String path) throws Exception {
        return this.trees(path, Collections.<String, Object>emptyMap());
    }

    @Override
    public List<Describe> trees(String path, Map<String, Object> parameters) throws Exception {
        Query query = this.query().path(path).custom(parameters);
        List<Describe> describes = query.list();
        List<Describe> trees = new ArrayList<Describe>(describes.size());
        for (int i = 0; i < describes.size(); i++) {
            Describe describe = describes.get(i);
            describe.setChildren(this.trees(describe.getPath(), parameters));
            trees.add(describe);
        }
        return trees;
    }

    @Override
    public void write(File file, String path) throws Exception {
        InputStream is = new FileInputStream(file);
        try {
            this.write(is, path);
        } finally {
            is.close();
        }
    }

    @Override
    public void write(Nfile file, String path) throws Exception {
        InputStream is = file.getInputStream();
        try {
            this.write(is, path);
        } finally {
            is.close();
        }
    }

    @Override
    public void write(byte[] bytes, String path) throws Exception {
        this.write(new ByteArrayInputStream(bytes), path);
    }

}
