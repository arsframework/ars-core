package ars.file.query;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.LinkedList;

import ars.file.Describe;
import ars.file.query.AbstractQuery;
import ars.file.query.condition.Condition;
import ars.file.query.condition.Conditions;

/**
 * 磁盘文件查询集合实现
 * 
 * @author wuyq
 * 
 */
public class DiskQuery extends AbstractQuery {

	public DiskQuery() {

	}

	public DiskQuery(String workingDirectory) {
		super(workingDirectory);
	}

	@Override
	public List<Describe> execute(String path, final boolean spread,
			final Condition... conditions) {
		final String workingDirectory = this.getWorkingDirectory();
		final List<Describe> describes = new LinkedList<Describe>();
		(path == null ? new File(workingDirectory) : new File(workingDirectory,
				path)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				Describe describe = new Describe(file);
				describe.setPath(describe.getPath().substring(
						workingDirectory.length()));
				if (Conditions.isSatisfy(describe, conditions)) {
					describes.add(describe);
				}
				if (spread && describe.isDirectory()) {
					describes.addAll(execute(describe.getPath(), spread,
							conditions));
				}
				return false;
			}
		});
		return describes;
	}

}
