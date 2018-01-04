package ars.file.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

/**
 * FTP客户端工厂接口
 * 
 * @author yongqiangwu
 *
 */
public interface ClientFactory {
	/**
	 * 获取FTP客户端连接对象
	 * 
	 * @return FTP客户端连接对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public FTPClient connect() throws IOException;

	/**
	 * 断开FTP客户端连接
	 * 
	 * @param client
	 *            FTP客户端连接对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public void disconnect(FTPClient client) throws IOException;

}
