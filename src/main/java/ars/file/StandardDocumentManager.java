package ars.file;

import java.util.Map;
import java.util.List;

import ars.util.Files;
import ars.util.Nfile;
import ars.util.Streams;
import ars.file.Operator;
import ars.file.DocumentManager;
import ars.invoke.request.Requester;
import ars.invoke.request.ParameterInvalidException;

/**
 * 文件外部操作接口标准实现
 * 
 * @author wuyq
 * 
 */
public class StandardDocumentManager implements DocumentManager {
	private Operator operator;

	public StandardDocumentManager(Operator operator) {
		if (operator == null) {
			throw new IllegalArgumentException("Illegal operator:" + operator);
		}
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

	@Override
	public String upload(Requester requester, String path, Nfile file,
			Map<String, Object> parameters) throws Exception {
		return this.operator.write(file, path);
	}

	@Override
	public Nfile download(Requester requester, String path,
			Map<String, Object> parameters) throws Exception {
		return this.operator.read(path);
	}

	@Override
	public List<Describe> trees(Requester requester, String path,
			Map<String, Object> parameters) throws Exception {
		return this.operator.trees(path, parameters);
	}

	@Override
	public String content(Requester requester, String path,
			Map<String, Object> parameters) throws Exception {
		return new String(this.operator.read(path).getBytes());
	}

	@Override
	public List<Describe> files(Requester requester, String path,
			boolean spread, Map<String, Object> parameters) throws Exception {
		return this.operator.query().spread(spread).path(path)
				.custom(parameters).list();
	}

	@Override
	public void copy(Requester requester, String[] sources, String target,
			Map<String, Object> parameters) throws Exception {
		for (String source : sources) {
			if (!source.equalsIgnoreCase(target)) {
				this.operator.copy(source, target);
			}
		}
	}

	@Override
	public void move(Requester requester, String[] sources, String target,
			Map<String, Object> parameters) throws Exception {
		for (String source : sources) {
			String path = new StringBuilder(target).append('/')
					.append(Files.getName(source)).toString();
			if (!source.equalsIgnoreCase(path)) {
				this.operator.move(source, path);
			}
		}
	}

	@Override
	public void remove(Requester requester, String[] paths,
			Map<String, Object> parameters) throws Exception {
		for (String path : paths) {
			this.operator.delete(path);
		}
	}

	@Override
	public void append(Requester requester, String path, String content,
			boolean directory, Map<String, Object> parameters) throws Exception {
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
	public void rename(Requester requester, String path, String name,
			Map<String, Object> parameters) throws Exception {
		if (!Files.getName(path).equalsIgnoreCase(name)) {
			String directory = Files.getDirectory(path);
			String target = directory == null ? name : new StringBuilder(
					directory).append('/').append(name).toString();
			if (this.operator.exists(target)) {
				throw new ParameterInvalidException("name", "exist");
			}
			this.operator.move(path, target);
		}
	}

	@Override
	public void update(Requester requester, String path, String content,
			Map<String, Object> parameters) throws Exception {
		if (content == null) {
			this.operator.write(Streams.EMPTY_ARRAY, path);
		} else {
			this.operator.write(content.getBytes(), path);
		}
	}

}
