package ars.file;

import java.util.Map;
import java.util.List;

import ars.util.Nfile;
import ars.file.Describe;
import ars.file.query.Query;

/**
 * 文件操作接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Operator {
	/**
	 * 获取文件操作工作目录
	 * 
	 * @return 文件工作目录
	 */
	public String getWorkingDirectory();

	/**
	 * 判断文件/文件夹是否存在
	 * 
	 * @param path
	 *            文件/文件目录相对路径
	 * @return true/false
	 * @throws Exception
	 *             操作异常
	 */
	public boolean exists(String path) throws Exception;

	/**
	 * 创建目录并返回是否创建成功
	 * 
	 * @param path
	 *            文件目录相对路径
	 * @return true/false
	 * @throws Exception
	 *             操作异常
	 */
	public boolean mkdirs(String path) throws Exception;

	/**
	 * 删除文件/文件目录
	 * 
	 * @param path
	 *            文件/文件目录相对路径
	 * @throws Exception
	 *             操作异常
	 */
	public void delete(String path) throws Exception;

	/**
	 * 拷贝文件/文件目录
	 * 
	 * @param source
	 *            源文件/文件目录
	 * @param target
	 *            目标文件目录
	 * @throws Exception
	 *             操作异常
	 */
	public void copy(String source, String target) throws Exception;

	/**
	 * 移动文件/文件目录
	 * 
	 * @param source
	 *            源文件/文件目录
	 * @param target
	 *            目标文件目录
	 * @throws Exception
	 *             操作异常
	 */
	public void move(String source, String target) throws Exception;

	/**
	 * 获取文件查询集合
	 * 
	 * @return 文件查询集合
	 */
	public Query query();

	/**
	 * 获取文件描述对象
	 * 
	 * @param path
	 *            文件/文件目录相对路径
	 * @return 文件描述对象
	 * @throws Exception
	 *             操作异常
	 */
	public Describe describe(String path) throws Exception;

	/**
	 * 获取文件描述树对象
	 * 
	 * @param path
	 *            文件/文件目录相对路径
	 * @return 文件描述树对象列表
	 * @throws Exception
	 *             操作异常
	 */
	public List<Describe> trees(String path) throws Exception;

	/**
	 * 获取文件描述树对象
	 * 
	 * @param path
	 *            文件/文件目录相对路径
	 * @param parameters
	 *            过滤参数
	 * @return 文件描述树对象列表
	 * @throws Exception
	 *             操作异常
	 */
	public List<Describe> trees(String path, Map<String, Object> parameters) throws Exception;

	/**
	 * 读文件
	 * 
	 * @param path
	 *            文件相对路径
	 * @return 文件
	 * @throws Exception
	 *             操作异常
	 */
	public Nfile read(String path) throws Exception;

	/**
	 * 写文件
	 * 
	 * @param file
	 *            源文件
	 * @return 文件相对路径
	 * @throws Exception
	 *             操作异常
	 */
	public String write(Nfile file) throws Exception;

	/**
	 * 写文件
	 * 
	 * @param file
	 *            源文件
	 * @param path
	 *            目标文件路径
	 * @return 文件相对路径
	 * @throws Exception
	 *             操作异常
	 */
	public String write(Nfile file, String path) throws Exception;

	/**
	 * 写文件
	 * 
	 * @param bytes
	 *            文件字节数组
	 * @param path
	 *            目标文件路径
	 * @throws Exception
	 *             操作异常
	 */
	public void write(byte[] bytes, String path) throws Exception;

}
