package ars.file;

import java.io.File;

import ars.util.Files;
import ars.util.Nfile;
import ars.util.Streams;
import ars.util.Strings;
import ars.file.query.Query;
import ars.file.query.DiskQuery;
import ars.file.AbstractOperator;

/**
 * 磁盘文件操作实现
 * 
 * @author wuyq
 * 
 */
public class DiskOperator extends AbstractOperator {

	public DiskOperator() {

	}

	public DiskOperator(String workingDirectory) {
		super(workingDirectory);
	}

	@Override
	public boolean exists(String path) throws Exception {
		return new File(this.getWorkingDirectory(), path).exists();
	}

	@Override
	public boolean mkdirs(String path) throws Exception {
		return new File(this.getWorkingDirectory(), path).mkdirs();
	}

	@Override
	public void delete(String path) throws Exception {
		Files.delete(new File(this.getWorkingDirectory(), path));
	}

	@Override
	public void copy(String source, String target) throws Exception {
		Files.copy(new File(this.getWorkingDirectory(), source),
				new File(this.getWorkingDirectory(), target));
	}

	@Override
	public void move(String source, String target) throws Exception {
		new File(this.getWorkingDirectory(), source).renameTo(new File(this
				.getWorkingDirectory(), target));
	}

	@Override
	public Query query() {
		return new DiskQuery(this.getWorkingDirectory());
	}

	@Override
	public Describe describe(String path) throws Exception {
		File file = new File(this.getWorkingDirectory(), path);
		if (file.exists()) {
			Describe describe = new Describe(file);
			describe.setPath(describe.getPath().substring(
					this.getWorkingDirectory().length()));
		}
		return null;
	}

	@Override
	public Nfile read(String path) throws Exception {
		File file = new File(this.getWorkingDirectory(), path);
		return file.exists() ? new Nfile(file) : null;
	}

	@Override
	public String write(Nfile file, String path) throws Exception {
		String name = this.getActualPath(file.getName());
		path = path == null ? name : Strings.replace(new StringBuilder(path)
				.append('/').append(name), "//", "/");
		Streams.write(file, new File(this.getWorkingDirectory(), path));
		return Strings.replace(path, "\\", "/");
	}

	@Override
	public void write(byte[] bytes, String path) throws Exception {
		Streams.write(bytes, new File(this.getWorkingDirectory(), path));
	}

}
