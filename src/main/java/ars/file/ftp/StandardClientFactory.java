package ars.file.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

/**
 * FTP客户端工厂标准实现
 *
 * @author wuyongqiang
 */
public class StandardClientFactory extends FTPClientConfig implements ClientFactory {
    private String host;
    private int port;
    private String user;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public FTPClient connect() throws IOException {
        FTPClient client = new FTPClient();
        client.configure(this);
        client.connect(this.host, this.port);
        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            client.disconnect();
            throw new IOException("FTP server refused connection");
        }
        if (!client.login(this.user, this.password)) {
            client.disconnect();
            throw new IOException("FTP server login failed");
        }
        return client;
    }

    @Override
    public void disconnect(FTPClient client) throws IOException {
        if (client != null && client.isConnected()) {
            try {
                client.logout();
            } finally {
                client.disconnect();
            }
        }
    }

}
