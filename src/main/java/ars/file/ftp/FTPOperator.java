package ars.file.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;

import ars.util.Nfile;
import ars.file.Describe;
import ars.file.AbstractOperator;
import ars.file.query.Query;
import ars.file.ftp.FTPQuery;
import ars.file.ftp.ClientFactory;

/**
 * FTP文件操作实现
 * 
 * @author yongqiangwu
 *
 */
public class FTPOperator extends AbstractOperator {
	protected final ClientFactory clientFactory;

	public FTPOperator(ClientFactory clientFactory) {
		super("/");
		if (clientFactory == null) {
			throw new IllegalArgumentException("Illegal clientFactory:" + clientFactory);
		}
		this.clientFactory = clientFactory;
	}

	/**
	 * 获取FTP客户端连接对象
	 * 
	 * @return FTP客户端连接对象
	 * @throws IOException
	 *             IO操作异常
	 */
	protected FTPClient connect() throws IOException {
		return this.clientFactory.connect();
	}

	/**
	 * 断开FTP客户端连接
	 * 
	 * @param client
	 *            FTP客户端连接对象
	 * @throws IOException
	 *             IO操作异常
	 */
	protected void disconnect(FTPClient client) throws IOException {
		this.clientFactory.disconnect(client);
	}

	/**
	 * 递归删除文件
	 * 
	 * @param client
	 *            FTP客户端连接对象
	 * @param files
	 *            FTP文件对象数组
	 * @throws IOException
	 *             IO操作异常
	 */
	protected void delete(FTPClient client, FTPFile... files) throws IOException {
		for (FTPFile file : files) {
			if (file.isDirectory()) {
				client.changeWorkingDirectory(file.getName());
				this.delete(client, client.listFiles());
				client.changeToParentDirectory();
				client.removeDirectory(file.getName());
			} else {
				client.deleteFile(file.getName());
			}
		}
	}

	/**
	 * 递归拷贝文件
	 * 
	 * @param source
	 *            源文件客户端对象
	 * @param target
	 *            目标文件客户端对象
	 * @param files
	 *            目标文件数组
	 * @throws IOException
	 *             IO操作异常
	 */
	protected void copy(FTPClient source, FTPClient target, FTPFile... files) throws IOException {
		for (FTPFile file : files) {
			if (file.isDirectory()) {
				if (!target.changeWorkingDirectory(file.getName())) {
					synchronized ((target.printWorkingDirectory() + file.getName()).intern()) {
						if (!target.changeWorkingDirectory(file.getName())) {
							target.makeDirectory(file.getName());
							target.changeWorkingDirectory(file.getName());
						}
					}
				}
				source.changeWorkingDirectory(file.getName());
				this.copy(source, target, source.listFiles());
				source.changeToParentDirectory();
				target.changeToParentDirectory();
			} else {
				FTPClient client = this.connect();
				try {
					InputStream stream = client
							.retrieveFileStream(new File(source.printWorkingDirectory(), file.getName()).getPath());
					try {
						if (client != null && client.getReplyCode() != 550) {
							target.storeFile(file.getName(), stream);
						}
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
				} finally {
					this.disconnect(client);
				}
			}
		}
	}

	/**
	 * 递归移动文件
	 * 
	 * @param source
	 *            源文件客户端对象
	 * @param target
	 *            目标文件客户端对象
	 * @param files
	 *            目标文件数组
	 * @throws IOException
	 *             IO操作异常
	 */
	protected void move(FTPClient source, FTPClient target, FTPFile... files) throws IOException {
		for (FTPFile file : files) {
			if (file.isDirectory()) {
				if (!target.changeWorkingDirectory(file.getName())) {
					synchronized ((target.printWorkingDirectory() + file.getName()).intern()) {
						if (!target.changeWorkingDirectory(file.getName())) {
							target.makeDirectory(file.getName());
							target.changeWorkingDirectory(file.getName());
						}
					}
				}
				source.changeWorkingDirectory(file.getName());
				this.move(source, target, source.listFiles());
				source.changeToParentDirectory();
				target.changeToParentDirectory();
				source.removeDirectory(file.getName());
			} else {
				FTPClient client = this.connect();
				try {
					InputStream stream = client
							.retrieveFileStream(new File(source.printWorkingDirectory(), file.getName()).getPath());
					try {
						if (client != null && client.getReplyCode() != 550) {
							target.storeFile(file.getName(), stream);
							source.deleteFile(file.getName());
						}
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
				} finally {
					this.disconnect(client);
				}
			}
		}
	}

	@Override
	public boolean exists(String path) throws Exception {
		path = new File(this.workingDirectory, path).getPath();
		FTPClient client = this.connect();
		try {
			if (client.changeWorkingDirectory(path)) {
				return true;
			}
			InputStream stream = client.retrieveFileStream(path);
			if (stream != null) {
				stream.close();
				return client.getReplyCode() != 550;
			}
		} finally {
			this.disconnect(client);
		}
		return false;
	}

	@Override
	public boolean mkdirs(String path) throws Exception {
		path = new File(this.workingDirectory, path).getPath();
		FTPClient client = this.connect();
		try {
			return client.makeDirectory(path);
		} finally {
			this.disconnect(client);
		}
	}

	@Override
	public boolean rename(String path, String name) throws Exception {
		FTPClient client = this.connect();
		try {
			return client.rename(path, new File(new File(this.workingDirectory, path).getParent(), name).getPath());
		} finally {
			this.disconnect(client);
		}
	}

	@Override
	public void delete(String path) throws Exception {
		path = new File(this.workingDirectory, path).getPath();
		FTPClient client = this.connect();
		try {
			if (client.changeWorkingDirectory(path)) {
				this.delete(client, client.listFiles());
				client.removeDirectory(path);
			} else {
				InputStream stream = client.retrieveFileStream(path);
				if (stream != null) {
					stream.close();
					if (client.getReplyCode() != 550) {
						client.deleteFile(path);
					}
				}
			}
		} finally {
			this.disconnect(client);
		}
	}

	@Override
	public void copy(String source, String target) throws Exception {
		File sfile = new File(this.workingDirectory, source);
		File tfile = new File(this.workingDirectory, target);
		source = sfile.getPath();
		target = new File(tfile, sfile.getName()).getPath();
		FTPClient sourceClient = null, targetClient = null;
		try {
			sourceClient = this.connect();
			targetClient = this.connect();
			if (sourceClient.changeWorkingDirectory(source)) {
				if (!targetClient.changeWorkingDirectory(target)) {
					synchronized (target.intern()) {
						if (!targetClient.changeWorkingDirectory(target)) {
							targetClient.makeDirectory(target);
							targetClient.changeWorkingDirectory(target);
						}
					}
				}
				this.copy(sourceClient, targetClient, sourceClient.listFiles());
			} else {
				InputStream stream = sourceClient.retrieveFileStream(source);
				try {
					if (stream != null && sourceClient.getReplyCode() != 550) {
						if (!targetClient.changeWorkingDirectory(tfile.getPath())) {
							synchronized (tfile.getPath().intern()) {
								if (!targetClient.changeWorkingDirectory(tfile.getPath())) {
									targetClient.makeDirectory(tfile.getPath());
									targetClient.changeWorkingDirectory(tfile.getPath());
								}
							}
						}
						targetClient.storeFile(sfile.getName(), stream);
					}
				} finally {
					if (stream != null) {
						stream.close();
					}
				}
			}
		} finally {
			try {
				if (sourceClient != null) {
					this.disconnect(sourceClient);
				}
			} finally {
				if (targetClient != null) {
					this.disconnect(targetClient);
				}
			}
		}
	}

	@Override
	public void move(String source, String target) throws Exception {
		File sfile = new File(this.workingDirectory, source);
		File tfile = new File(this.workingDirectory, target);
		source = sfile.getPath();
		target = new File(tfile, sfile.getName()).getPath();
		FTPClient sourceClient = null, targetClient = null;
		try {
			sourceClient = this.connect();
			targetClient = this.connect();
			if (sourceClient.changeWorkingDirectory(source)) {
				if (!targetClient.changeWorkingDirectory(target)) {
					synchronized (target.intern()) {
						if (!targetClient.changeWorkingDirectory(target)) {
							targetClient.makeDirectory(target);
							targetClient.changeWorkingDirectory(target);
						}
					}
				}
				this.move(sourceClient, targetClient, sourceClient.listFiles());
				sourceClient.removeDirectory(source);
			} else {
				InputStream stream = sourceClient.retrieveFileStream(source);
				try {
					if (stream != null && sourceClient.getReplyCode() != 550) {
						if (!targetClient.changeWorkingDirectory(tfile.getPath())) {
							synchronized (tfile.getPath().intern()) {
								if (!targetClient.changeWorkingDirectory(tfile.getPath())) {
									targetClient.makeDirectory(tfile.getPath());
									targetClient.changeWorkingDirectory(tfile.getPath());
								}
							}
						}
						targetClient.storeFile(sfile.getName(), stream);
						sourceClient.deleteFile(source);
					}
				} finally {
					if (stream != null) {
						stream.close();
					}
				}
			}
		} finally {
			try {
				if (sourceClient != null) {
					this.disconnect(sourceClient);
				}
			} finally {
				if (targetClient != null) {
					this.disconnect(targetClient);
				}
			}
		}
	}

	@Override
	public Query query() {
		return new FTPQuery(this.clientFactory, this.workingDirectory);
	}

	@Override
	public Describe describe(String path) throws Exception {
		File target = new File(path);
		String name = target.getName();
		String directory = target.getParent();
		FTPClient client = this.connect();
		try {
			if (directory == null) {
				client.changeWorkingDirectory(this.workingDirectory);
			} else {
				client.changeWorkingDirectory(new File(this.workingDirectory, directory).getPath());
			}
			for (FTPFile file : client.listFiles()) {
				if (file.getName().equals(name)) {
					Describe describe = new Describe();
					describe.setPath(path);
					describe.setName(name);
					describe.setSize(file.getSize());
					describe.setModified(file.getTimestamp().getTime());
					describe.setDirectory(file.isDirectory());
					return describe;
				}
			}
		} finally {
			this.disconnect(client);
		}
		return null;
	}

	@Override
	public Nfile read(String path) throws Exception {
		path = new File(this.workingDirectory, path).getPath();
		InputStream stream = null;
		final FTPClient client = this.connect();
		try {
			stream = client.retrieveFileStream(path);
		} finally {
			if (stream == null || client.getReplyCode() == 550) {
				this.disconnect(client);
				return null;
			}
		}
		return new Nfile(new File(path).getName(), stream) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void finalize() throws Throwable {
				disconnect(client);
			}

		};
	}

	@Override
	public void write(InputStream stream, String path) throws Exception {
		path = new File(this.workingDirectory, path).getPath();
		String parent = new File(path).getParent();
		String directory = parent == null ? this.workingDirectory : new File(this.workingDirectory, parent).getPath();
		FTPClient client = this.connect();
		try {
			if (!client.changeWorkingDirectory(directory)) {
				synchronized (directory.intern()) {
					if (!client.changeWorkingDirectory(directory)) {
						client.makeDirectory(directory);
					}
				}
			}
			client.storeFile(path, stream);
		} finally {
			this.disconnect(client);
		}
	}

}
