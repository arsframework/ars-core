package ars.file;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;

import ars.util.Nfile;
import ars.util.Streams;
import ars.util.Strings;
import ars.file.Describe;
import ars.file.Operator;
import ars.file.query.Query;
import ars.file.AbstractOperator;
import ars.file.office.Converts;

/**
 * 文件操作抽象实现
 * 
 * @author yongqiangwu
 * 
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
	public Nfile preview(String path) throws Exception {
		if (!this.exists(path)) {
			return null;
		}
		if (path.toLowerCase().endsWith(".swf")) {
			return this.read(path);
		}
		String swf = path + ".temp.swf";
		if (!this.exists(swf)) {
			synchronized (swf.intern()) {
				if (!this.exists(swf)) {
					Nfile input = this.read(path);
					if (input.isFile()) {
						Converts.file2swf(input.getFile(), new File(this.workingDirectory, swf));
					} else {
						File source = new File(Strings.TEMP_PATH, UUID.randomUUID().toString() + input.getName());
						File target = new File(Strings.TEMP_PATH, UUID.randomUUID().toString() + input.getName());
						try {
							Streams.write(input, source);
							Converts.file2swf(source, target);
							this.write(new Nfile(target), swf);
						} finally {
							source.delete();
							target.delete();
						}
					}
				}
			}
		}
		return this.read(swf);
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
