package ars.file;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import ars.util.Nfile;
import ars.util.Strings;
import ars.file.Describe;
import ars.file.Operator;
import ars.file.query.Query;
import ars.file.NameGenerator;
import ars.file.AbstractOperator;
import ars.file.DirectoryGenerator;

/**
 * 文件操作抽象实现
 * 
 * @author wuyq
 * 
 */
public abstract class AbstractOperator implements Operator {
	private String workingDirectory; // 工作目录
	private NameGenerator nameGenerator; // 文件名称生成器
	private DirectoryGenerator directoryGenerator; // 文件目录生成器

	public AbstractOperator() {
		this.workingDirectory = Strings.TEMP_PATH;
	}

	public AbstractOperator(String workingDirectory) {
		this.workingDirectory = Strings.getRealPath(workingDirectory);
	}

	public NameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(NameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	public DirectoryGenerator getDirectoryGenerator() {
		return directoryGenerator;
	}

	public void setDirectoryGenerator(DirectoryGenerator directoryGenerator) {
		this.directoryGenerator = directoryGenerator;
	}

	/**
	 * 获取文件存储实际路径
	 * 
	 * @param path
	 *            文件相对路径
	 * @return 实际文件路径
	 * @throws IOException
	 */
	protected String getActualPath(String path) {
		File file = new File(path);
		if (this.directoryGenerator != null && this.nameGenerator != null) {
			return new File(this.directoryGenerator.generate(file.getParent()),
					this.nameGenerator.generate(file.getName())).getPath();
		} else if (this.directoryGenerator != null) {
			return new File(this.directoryGenerator.generate(file.getParent()),
					file.getName()).getPath();
		} else if (this.nameGenerator != null) {
			return new File(file.getParent(), this.nameGenerator.generate(file
					.getName())).getPath();
		}
		return path;
	}

	@Override
	public String getWorkingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public List<Describe> trees(String path) throws Exception {
		return this.trees(path, Collections.<String, Object> emptyMap());
	}

	@Override
	public List<Describe> trees(String path, Map<String, Object> parameters)
			throws Exception {
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
	public String write(Nfile file) throws Exception {
		return this.write(file, null);
	}

}
