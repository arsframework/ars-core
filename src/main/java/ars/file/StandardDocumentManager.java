package ars.file;

import java.io.File;
import java.util.Map;
import java.util.List;

import ars.util.Files;
import ars.util.Nfile;
import ars.util.Streams;
import ars.file.Operator;
import ars.file.NameGenerator;
import ars.file.DocumentManager;
import ars.file.DirectoryGenerator;
import ars.invoke.request.Requester;
import ars.invoke.request.ParameterInvalidException;

/**
 * 文件外部操作接口标准实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardDocumentManager implements DocumentManager {
	private Operator operator; // 文件处理器
	private NameGenerator nameGenerator; // 文件名称生成器
	private DirectoryGenerator directoryGenerator; // 文件目录生成器

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
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
	 * 文件操作器有效性检查
	 */
	protected void checkOperatorValidity() {
		if (this.operator == null) {
			throw new RuntimeException("File operator has not been initialize");
		}
	}

	@Override
	public String upload(Requester requester, String path, Nfile file, Map<String, Object> parameters)
			throws Exception {
		this.checkOperatorValidity();
		String name = file.getName();
		if (this.directoryGenerator != null) {
			if (path == null) {
				path = this.directoryGenerator.generate(name);
			} else {
				path = new StringBuilder(path).append('/').append(this.directoryGenerator.generate(name)).toString();
			}
		}
		if (this.nameGenerator != null) {
			name = this.nameGenerator.generate(name);
		}
		path = path == null ? name : new StringBuilder(path).append('/').append(name).toString();
		this.operator.write(file, path);
		return path;
	}

	@Override
	public Nfile download(Requester requester, String path, Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		return this.operator.read(path);
	}

	@Override
	public List<Describe> trees(Requester requester, String path, Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		return this.operator.trees(path, parameters);
	}

	@Override
	public String content(Requester requester, String path, Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		return new String(this.operator.read(path).getBytes());
	}

	@Override
	public List<Describe> files(Requester requester, String path, boolean spread, Map<String, Object> parameters)
			throws Exception {
		this.checkOperatorValidity();
		return this.operator.query().spread(spread).path(path).custom(parameters).list();
	}

	@Override
	public void copy(Requester requester, String[] sources, String target, Map<String, Object> parameters)
			throws Exception {
		this.checkOperatorValidity();
		for (String source : sources) {
			this.operator.copy(source, target);
		}
	}

	@Override
	public void move(Requester requester, String[] sources, String target, Map<String, Object> parameters)
			throws Exception {
		this.checkOperatorValidity();
		for (String source : sources) {
			this.operator.move(source, target);
		}
	}

	@Override
	public void remove(Requester requester, String[] paths, Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		for (String path : paths) {
			this.operator.delete(path);
		}
	}

	@Override
	public void append(Requester requester, String path, String content, boolean directory,
			Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		if (this.operator.exists(path)) {
			throw new ParameterInvalidException("name", "exist");
		}
		if (directory) {
			this.operator.mkdirs(path);
		} else if (content == null) {
			this.operator.write(Streams.EMPTY_ARRAY, path);
		} else {
			this.operator.write(content.getBytes(), path);
		}
	}

	@Override
	public void rename(Requester requester, String path, String name, Map<String, Object> parameters) throws Exception {
		this.checkOperatorValidity();
		if (!Files.getName(path).equalsIgnoreCase(name)) {
			String target = new File(new File(path).getParent(), name).getPath();
			if (this.operator.exists(target)) {
				throw new ParameterInvalidException("name", "exist");
			}
			this.operator.rename(path, name);
		}
	}

	@Override
	public void update(Requester requester, String path, String content, Map<String, Object> parameters)
			throws Exception {
		this.checkOperatorValidity();
		if (content == null) {
			this.operator.write(Streams.EMPTY_ARRAY, path);
		} else {
			this.operator.write(content.getBytes(), path);
		}
	}

}
