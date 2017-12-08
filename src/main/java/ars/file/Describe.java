package ars.file;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import ars.util.Strings;

/**
 * 文件描述对象
 * 
 * @author wuyq
 * 
 */
public class Describe implements Serializable {
	private static final long serialVersionUID = 1L;

	private String path; // 文件/文件目录相对路径
	private String name; // 文件/文件目录名称
	private long size; // 文件/文件目录大小
	private Date modified; // 文件/文件目录最后修改时间
	private boolean directory; // 是否为文件目录
	private List<Describe> children = new ArrayList<Describe>(0); // 子文件描述

	public Describe() {

	}

	public Describe(File file) {
		this.path = Strings.replace(file.getPath(), "\\", "/");
		this.name = file.getName();
		this.size = file.length();
		this.modified = new Date(file.lastModified());
		this.directory = file.isDirectory();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public List<Describe> getChildren() {
		return children;
	}

	public void setChildren(List<Describe> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return this.path == null ? super.toString() : this.path;
	}

	/**
	 * 文件/文件目录属性
	 * 
	 * @author wuyq
	 * 
	 */
	public enum Property {
		/**
		 * 文件/文件目录名称属性
		 */
		NAME,

		/**
		 * 文件/文件目录大小属性
		 */
		SIZE,

		/**
		 * 文件/文件目录最后修改时间属性
		 */
		MODIFIED,

		/**
		 * 是否为文件目录属性
		 */
		DIRECTORY;

	}

}
