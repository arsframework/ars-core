package ars.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * 非完全本地文件接口
 * 
 * @author wuyq
 * 
 */
public class Nfile implements Serializable {
	private static final long serialVersionUID = 1L;

	private File file; // 本地文件对象
	private long size; // 文件大小
	private String name; // 文件名称
	private Date date; // 文件创建时间
	private byte[] bytes; // 文件字节数组
	private transient InputStream input; // 文件输入流

	public Nfile(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		this.name = name;
	}

	public Nfile(File file) {
		this(file.getName(), file);
	}

	public Nfile(String name, File file) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		} else if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		this.file = file;
		this.name = name;
		this.size = file.length();
		this.date = new Date(file.lastModified());
	}

	public Nfile(String name, byte[] bytes) {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		} else if (bytes == null) {
			throw new IllegalArgumentException("Illegal bytes:" + bytes);
		}
		this.name = name;
		this.bytes = bytes;
		this.size = bytes.length;
		this.date = new Date();
	}

	public Nfile(String name, InputStream input) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		} else if (input == null) {
			throw new IllegalArgumentException("Illegal input:" + input);
		}
		this.name = name;
		this.input = input;
		this.size = input.available();
		this.date = new Date();
	}

	/**
	 * 获取文件大小
	 * 
	 * @return 文件大小
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * 获取文件名称
	 * 
	 * @return 文件名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 是否为本地文件
	 * 
	 * @return true/false
	 */
	public boolean isFile() {
		return this.file != null;
	}

	/**
	 * 获取本地文件对象
	 * 
	 * @return 文件对象
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * 获取文件字节内容
	 * 
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public byte[] getBytes() throws IOException {
		if (this.bytes == null) {
			synchronized (this) {
				if (this.bytes == null) {
					InputStream is = this.getInputStream();
					try {
						this.bytes = Streams.getBytes(is);
					} finally {
						is.close();
					}
				}
			}
		}
		return this.bytes;
	}

	/**
	 * 获取文件创建时间
	 * 
	 * @return 创建时间
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * 获取文件数据输入流
	 * 
	 * @return 输入流
	 * @throws IOException
	 *             IO操作异常
	 */
	public InputStream getInputStream() throws IOException {
		if (this.input != null) {
			return this.input;
		} else if (this.bytes != null) {
			return new ByteArrayInputStream(this.bytes);
		} else if (this.file == null) {
			throw new IOException("Data source has not been initialize");
		}
		return new FileInputStream(this.file);
	}

	/**
	 * 获取文件数据输出流
	 * 
	 * @return 输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public OutputStream getOutputStream() throws IOException {
		if (this.file != null) {
			File path = this.file.getParentFile();
			if (path != null && !path.exists()) {
				path.mkdirs();
			}
			return new FileOutputStream(this.file);
		}
		return new ByteArrayOutputStream() {

			@Override
			public void close() throws IOException {
				try {
					bytes = this.toByteArray();
					size = bytes.length;
				} finally {
					super.close();
				}
			}

		};
	}

	@Override
	public String toString() {
		return this.file == null ? this.name == null ? super.toString() : this.name : this.file.toString();
	}

}
