package ars.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Multipart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * 邮件工具类
 *
 * @author wuyongqiang
 */
public final class Mail {
    private String host; // 邮件服务器地址
    private int port = 25; // 邮件服务器端口
    private String from; // 发件人
    private String[] receives = Strings.EMPTY_ARRAY; // 收件人
    private String[] copies = Strings.EMPTY_ARRAY; // 抄送人
    private String user; // 发件人用户名
    private String password; // 发件人密码
    private String title; // 邮件标题
    private Body body = new Body();

    public String getHost() {
        return host;
    }

    public Mail setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Mail setPort(int port) {
        if (port < 1) {
            throw new IllegalArgumentException("Illegal port:" + port);
        }
        this.port = port;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public Mail setFrom(String from) {
        this.from = from;
        return this;
    }

    public String[] getReceives() {
        return receives;
    }

    public Mail setReceives(String... receives) {
        this.receives = receives;
        return this;
    }

    public String[] getCopies() {
        return copies;
    }

    public Mail setCopies(String... copies) {
        this.copies = copies;
        return this;
    }

    public String getUser() {
        return user;
    }

    public Mail setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Mail setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Mail setTitle(String title) {
        this.title = title;
        return this;
    }

    public Mail setText(String text) {
        this.body.setText(text);
        return this;
    }

    public Mail setMixes(File... files) {
        this.body.mixed(files);
        return this;
    }

    public Mail setMixes(Nfile... files) {
        this.body.mixed(files);
        return this;
    }

    public Mail setRelates(File... files) {
        this.body.related(files);
        return this;
    }

    public Mail setRelates(Nfile... files) {
        this.body.related(files);
        return this;
    }

    /**
     * 发送邮件
     *
     * @throws Exception 操作异常
     */
    public void send() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", this.host);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", String.valueOf(this.port));
        Session session = Session.getInstance(properties);
        Transport transport = session.getTransport();
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.from));
            if (this.receives.length > 0) {
                InternetAddress[] addresses = new InternetAddress[this.receives.length];
                for (int i = 0; i < this.receives.length; i++) {
                    addresses[i] = new InternetAddress(this.receives[i]);
                }
                message.setRecipients(Message.RecipientType.TO, addresses);
            }
            if (this.copies.length > 0) {
                InternetAddress[] addresses = new InternetAddress[this.copies.length];
                for (int i = 0; i < this.copies.length; i++) {
                    addresses[i] = new InternetAddress(this.copies[i]);
                }
                message.setRecipients(Message.RecipientType.CC, addresses);
            }
            message.setSubject(this.title);
            message.setContent(this.body.combine());
            transport.connect(this.host, this.user, this.password);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    /**
     * Nfile数据源
     *
     * @author wuyongqiang
     */
    private class NfileDataSource implements DataSource {
        private Nfile file;

        public NfileDataSource(Nfile file) {
            this.file = file;
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.file.getInputStream();
        }

        @Override
        public String getName() {
            return this.file.getName();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

    }

    /**
     * 邮件体
     *
     * @author wuyongqiang
     */
    private class Body {
        private String text = Strings.EMPTY_STRING; // 邮件正文部分
        private List<DataSource> mixes = new LinkedList<DataSource>(); // 混合数据源列表
        private List<DataSource> relates = new LinkedList<DataSource>(); // 关联数据库列表

        public Body setText(String text) {
            this.text = text;
            return this;
        }

        public Body mixed(File... files) {
            for (File file : files) {
                this.mixes.add(new FileDataSource(file));
            }
            return this;
        }

        public Body mixed(Nfile... files) {
            for (Nfile file : files) {
                if (file.isFile()) {
                    this.mixes.add(new FileDataSource(file.getFile()));
                } else {
                    this.mixes.add(new NfileDataSource(file));
                }
            }
            return this;
        }

        public Body related(File... files) {
            for (File file : files) {
                this.relates.add(new FileDataSource(file));
            }
            return this;
        }

        public Body related(Nfile... files) {
            for (Nfile file : files) {
                if (file.isFile()) {
                    this.relates.add(new FileDataSource(file.getFile()));
                } else {
                    this.relates.add(new NfileDataSource(file));
                }
            }
            return this;
        }

        /**
         * 组合邮件体
         *
         * @return 邮件体
         * @throws Exception 操作异常
         */
        public Multipart combine() throws Exception {
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(this.text, "text/html;charset=UTF-8");
            if (this.relates.isEmpty() && this.mixes.isEmpty()) {
                MimeMultipart part = new MimeMultipart();
                part.addBodyPart(text);
                return part;
            }

            MimeMultipart part = new MimeMultipart();
            if (!this.relates.isEmpty()) {
                part.addBodyPart(text);
                for (DataSource dataSource : this.relates) {
                    MimeBodyPart body = new MimeBodyPart();
                    body.setDataHandler(new DataHandler(dataSource));
                    body.setContentID(MimeUtility.encodeText(dataSource.getName()));
                    part.addBodyPart(body);
                }
                part.setSubType("related");
            }
            if (!this.mixes.isEmpty()) {
                if (!this.relates.isEmpty()) {
                    MimeMultipart mixed = new MimeMultipart();
                    MimeBodyPart content = new MimeBodyPart();
                    content.setContent(part);
                    mixed.addBodyPart(content);
                    part = mixed;
                }
                for (DataSource dataSource : this.mixes) {
                    MimeBodyPart body = new MimeBodyPart();
                    body.setDataHandler(new DataHandler(dataSource));
                    body.setFileName(MimeUtility.encodeText(dataSource.getName()));
                    part.addBodyPart(body);
                }
                part.setSubType("mixed");
            }
            return part;
        }
    }

}
