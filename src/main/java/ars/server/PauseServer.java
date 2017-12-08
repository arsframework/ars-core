package ars.server;

import ars.server.Server;

/**
 * 可暂停服务接口
 * 
 * @author wuyq
 * 
 */
public interface PauseServer extends Server {
	/**
	 * 暂停服务(非阻塞)
	 */
	public void pause();

	/**
	 * 暂停服务
	 * 
	 * @param block
	 *            是否阻塞（等待服务真正进入暂停状态）
	 */
	public void pause(boolean block);

	/**
	 * 恢复服务
	 */
	public void restore();

	/**
	 * 服务是否已暂停
	 * 
	 * @return true/false
	 */
	public boolean isPaused();

}
