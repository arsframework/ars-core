package ars.file;

import java.util.Map;
import java.util.List;

import ars.util.Nfile;
import ars.file.Describe;
import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.request.Requester;

/**
 * 文件操作外部接口
 * 
 * @author yongqiangwu
 * 
 */
public interface DocumentManager {
	/**
	 * 上传文件
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件目录
	 * @param file
	 *            文件对象
	 * @param parameters
	 *            请求参数
	 * @return 文件路径
	 * @throws Exception
	 *             操作异常
	 */
	@Api("upload")
	public String upload(Requester requester, @Param(name = "path") String path,
			@Param(name = "file", required = true) Nfile file, Map<String, Object> parameters) throws Exception;

	/**
	 * 下载文件
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件路径
	 * @param parameters
	 *            附件过滤参数
	 * @return 文件对象
	 * @throws Exception
	 *             操作异常
	 */
	@Api("download")
	public Nfile download(Requester requester, @Param(name = "path", required = true) String path,
			Map<String, Object> parameters) throws Exception;

	/**
	 * 获取文件描述对象树列表
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件路径
	 * @param parameters
	 *            请求参数
	 * @return 文件描述对象树列表
	 * @throws Exception
	 *             操作异常
	 */
	@Api("trees")
	public List<Describe> trees(Requester requester, @Param(name = "path") String path, Map<String, Object> parameters)
			throws Exception;

	/**
	 * 获取文件内容
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件相对路径
	 * @param parameters
	 *            请求参数
	 * @return 文件内容
	 * @throws Exception
	 *             操作异常
	 */
	@Api("content")
	public String content(Requester requester, @Param(name = "path", required = true) String path,
			Map<String, Object> parameters) throws Exception;

	/**
	 * 获取文件描述对象列表
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件路径
	 * @param spread
	 *            是否展开
	 * @param parameters
	 *            请求参数
	 * @return 文件描述对象列表
	 * @throws Exception
	 *             操作异常
	 */
	@Api("files")
	public List<Describe> files(Requester requester, @Param(name = "path") String path,
			@Param(name = "spread") boolean spread, Map<String, Object> parameters) throws Exception;

	/**
	 * 拷贝文件或文件目录
	 * 
	 * @param requester
	 *            请求对象
	 * @param sources
	 *            源文件/文件目录相对路径数组
	 * @param target
	 *            目标文件目录相对路径
	 * @param parameters
	 *            请求参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("copy")
	public void copy(Requester requester, @Param(name = "source", required = true) String[] sources,
			@Param(name = "target", required = true) String target, Map<String, Object> parameters) throws Exception;

	/**
	 * 移动文件或文件目录
	 * 
	 * @param requester
	 *            请求对象
	 * @param sources
	 *            源文件/文件目录相对路径数组
	 * @param target
	 *            目标文件目录相对路径
	 * @param parameters
	 *            请求参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("move")
	public void move(Requester requester, @Param(name = "source", required = true) String[] sources,
			@Param(name = "target", required = true) String target, Map<String, Object> parameters) throws Exception;

	/**
	 * 移除文件
	 * 
	 * @param requester
	 *            请求对象
	 * @param paths
	 *            文件路径数组
	 * @param parameters
	 *            附件过滤参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("remove")
	public void remove(Requester requester, @Param(name = "path", required = true) String[] paths,
			Map<String, Object> parameters) throws Exception;

	/**
	 * 新增文件/文件目录
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件/文件目录名称
	 * @param content
	 *            文件内容
	 * @param directory
	 *            是否为文件目录
	 * @param parameters
	 *            请求参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("append")
	public void append(Requester requester, @Param(name = "path", required = true) String path,
			@Param(name = "content") String content, @Param(name = "directory") boolean directory,
			Map<String, Object> parameters) throws Exception;

	/**
	 * 重命名文件或文件目录
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            源文件/文件目录相对路径
	 * @param name
	 *            目标文件/文件目录名称
	 * @param parameters
	 *            请求参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("rename")
	public void rename(Requester requester, @Param(name = "path", required = true) String path,
			@Param(name = "name", required = true) String name, Map<String, Object> parameters) throws Exception;

	/**
	 * 修改文件内容
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件相对路径
	 * @param content
	 *            文件内容
	 * @param parameters
	 *            请求参数
	 * @throws Exception
	 *             操作异常
	 */
	@Api("update")
	public void update(Requester requester, @Param(name = "path", required = true) String path,
			@Param(name = "content") String content, Map<String, Object> parameters) throws Exception;

}
