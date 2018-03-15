package ars.spring.context;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import ars.util.Strings;
import ars.file.office.Converts;
import ars.spring.context.ApplicationListener;

/**
 * OpenOffice应用配置
 * 
 * @author yongqiangwu
 *
 */
public class OpenOfficeApplicationConfiguration extends ApplicationListener {
	private boolean autorun = true; // 是否自动运行服务

	public boolean isAutorun() {
		return autorun;
	}

	public void setAutorun(boolean autorun) {
		this.autorun = autorun;
	}

	public void setHost(String host) {
		Converts.setOpenOfficeHost(host);
	}

	public void setPort(int port) {
		Converts.setOpenOfficePort(port);
	}

	/**
	 * 获取服务进程标识
	 * 
	 * @return 服务进程标识
	 * @throws IOException
	 */
	private int getProcess() throws IOException {
		byte[] buffer = new byte[1024];
		if (System.getProperty("os.name").startsWith("Windows")) {
			InputStream is = Runtime.getRuntime().exec("cmd /c tasklist|findstr soffice.exe").getInputStream();
			try {
				while (is.read(buffer) > 0) {
					return Integer.parseInt(new String(buffer).substring(11).trim().split(" ")[0]);
				}
			} finally {
				is.close();
			}
			return 0;
		}
		String[] command = new String[] { "/bin/sh", "-c", "ps -e|grep -w soffice" };
		InputStream is = Runtime.getRuntime().exec(command).getInputStream();
		try {
			while (is.read(buffer) > 0) {
				return Integer.parseInt(new String(buffer).trim().split(" ")[0]);
			}
		} finally {
			is.close();
		}
		return 0;
	}

	@Override
	protected void initialize(ContextRefreshedEvent event) {
		if (this.autorun && (Strings.LOCALHOST.equals(Converts.getOpenOfficeHost())
				|| Strings.LOCALHOST_ADDRESS.equals(Converts.getOpenOfficeHost())
				|| Strings.DEFAULT_LOCALHOST_ADDRESS.equals(Converts.getOpenOfficeHost()))) {
			synchronized (this) {
				try {
					if (this.getProcess() == 0) {
						String command = new StringBuilder("soffice -headless -accept=\"socket,host=")
								.append(Converts.getOpenOfficeHost()).append(",port=")
								.append(Converts.getOpenOfficePort()).append(";urp;\" -nofirststartwizard").toString();
						if (System.getProperty("os.name").startsWith("Windows")) {
							Runtime.getRuntime().exec("cmd /c start " + command);
						} else {
							Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command, "&" });
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	protected void destroy(ContextClosedEvent event) {
		if (this.autorun && (Strings.LOCALHOST.equals(Converts.getOpenOfficeHost())
				|| Strings.LOCALHOST_ADDRESS.equals(Converts.getOpenOfficeHost())
				|| Strings.DEFAULT_LOCALHOST_ADDRESS.equals(Converts.getOpenOfficeHost()))) {
			synchronized (this) {
				try {
					int process = 0;
					if ((process = this.getProcess()) > 0) {
						if (System.getProperty("os.name").startsWith("Windows")) {
							Runtime.getRuntime().exec("cmd /c taskkill /PID " + process + " /F /T");
						} else {
							Runtime.getRuntime().exec("/bin/kill " + process);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
