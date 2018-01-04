package ars.file.disk;

import java.io.File;
import java.io.InputStream;

import ars.util.Files;
import ars.util.Nfile;
import ars.util.Streams;
import ars.file.Describe;
import ars.file.AbstractOperator;
import ars.file.query.Query;
import ars.file.disk.DiskQuery;

/**
 * 磁盘文件操作实现
 * 
 * @author yongqiangwu
 * 
 */
public class DiskOperator extends AbstractOperator {

	@Override
	public boolean exists(String path) throws Exception {
		return new File(this.getWorkingDirectory(), path).exists();
	}

	@Override
	public boolean mkdirs(String path) throws Exception {
		return new File(this.getWorkingDirectory(), path).mkdirs();
	}

	@Override
	public boolean rename(String path, String name) throws Exception {
		File target = new File(new File(this.getWorkingDirectory(), path).getParent(), name);
		return new File(this.getWorkingDirectory(), path).renameTo(target);
	}

	@Override
	public void delete(String path) throws Exception {
		Files.delete(new File(this.getWorkingDirectory(), path));
	}

	@Override
	public void copy(String source, String target) throws Exception {
		Files.copy(new File(this.getWorkingDirectory(), source), new File(this.getWorkingDirectory(), target));
	}

	@Override
	public void move(String source, String target) throws Exception {
		Files.move(new File(this.getWorkingDirectory(), source), new File(this.getWorkingDirectory(), target));
	}

	@Override
	public Query query() {
		return new DiskQuery(this.getWorkingDirectory());
	}

	@Override
	public Describe describe(String path) throws Exception {
		File file = new File(this.getWorkingDirectory(), path);
		if (!file.exists()) {
			return null;
		}
		Describe describe = new Describe(file);
		describe.setPath(describe.getPath().substring(this.getWorkingDirectory().length()));
		return describe;
	}

	@Override
	public Nfile read(String path) throws Exception {
		File file = new File(this.getWorkingDirectory(), path);
		return file.exists() && file.isFile() ? new Nfile(file) : null;
	}

	@Override
	public void write(Nfile file, String path) throws Exception {
		Streams.write(file, new File(this.getWorkingDirectory(), path));
	}

	@Override
	public void write(InputStream stream, String path) throws Exception {
		Streams.write(stream, new File(this.getWorkingDirectory(), path));
	}

}
