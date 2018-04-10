package ars.invoke.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Nfile;
import ars.util.Beans;
import ars.util.Jsons;
import ars.util.Strings;
import ars.util.Streams;
import ars.util.Servers;
import ars.invoke.request.Token;
import ars.invoke.remote.slice.Itoken;
import ars.invoke.remote.slice.Iresult;
import ars.invoke.remote.slice.Istream;
import ars.invoke.remote.slice.Istring;
import ars.invoke.remote.slice.ResourcePrx;
import ars.invoke.remote.slice.ResourcePrxHelper;
import ars.invoke.remote.slice.Callback_Resource_download;

/**
 * 基于ICE消息中间件远程操作工具类
 *
 * @author wuyongqiang
 */
public final class Remotes {
    /**
     * 通用对象适配器名称
     */
    public static final String COMMON_ADAPTER_NAME = "common_adapter";

    /**
     * 通用远程调用通道标识
     */
    public static final String COMMON_CHANNEL_NAME = "common_channel";

    /**
     * 当前节点标识
     */
    private static String client;

    /**
     * 文件目录
     */
    private static String directory;

    /**
     * 客户端配置
     */
    private static Map<String, String> configure;

    /**
     * ICE通信器
     */
    private static Ice.Communicator communicator;

    private static final Logger logger = LoggerFactory.getLogger(Remotes.class);

    private Remotes() {

    }

    /**
     * 获取客户端标识
     *
     * @return 客户端标识
     */
    public static String getClient() {
        if (client == null) {
            synchronized (Remotes.class) {
                if (client == null) {
                    client = Strings.LOCALHOST_NAME;
                }
            }
        }
        return client;
    }

    public static void setClient(String client) {
        if (client == null) {
            throw new IllegalArgumentException("Client must not be null");
        }
        if (Remotes.client != null) {
            throw new IllegalStateException("Client already initialized");
        }
        synchronized (Remotes.class) {
            if (Remotes.client == null) {
                Remotes.client = client;
            }
        }
    }

    public static String getDirectory() {
        if (directory == null) {
            synchronized (Remotes.class) {
                if (directory == null) {
                    directory = Strings.TEMP_PATH;
                }
            }
        }
        return directory;
    }

    public static void setDirectory(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory must not be null");
        }
        if (Remotes.directory != null) {
            throw new IllegalStateException("Directory already initialized");
        }
        synchronized (Remotes.class) {
            if (Remotes.directory == null) {
                Remotes.directory = Strings.getRealPath(directory);
                File path = new File(Remotes.directory);
                if (!path.exists()) {
                    path.mkdirs();
                }
            }
        }
    }

    public static Map<String, String> getConfigure() {
        if (configure == null) {
            synchronized (Remotes.class) {
                if (configure == null) {
                    configure = Collections.emptyMap();
                }
            }
        }
        return configure;
    }

    public static void setConfigure(Map<String, String> configure) {
        if (configure == null) {
            throw new IllegalArgumentException("Configure must not be null");
        }
        if (Remotes.configure != null) {
            throw new IllegalStateException("Configure already initialized");
        }
        synchronized (Remotes.class) {
            if (Remotes.configure == null) {
                Remotes.configure = Collections.unmodifiableMap(configure);
            }
        }
    }

    /**
     * 获取ICE通信器
     *
     * @return 通信器对象
     */
    public static Ice.Communicator getCommunicator() {
        if (communicator == null) {
            synchronized (Remotes.class) {
                if (communicator == null) {
                    Map<String, String> configure = getConfigure();
                    logger.info("Initialization default ice client communicator with configuration: {}", configure);
                    communicator = initializeCommunicator(configure);
                }
            }
        }
        return communicator;
    }

    /**
     * 设置 ICE通信器
     *
     * @param communicator ICE通信器
     */
    public static void setCommunicator(Ice.Communicator communicator) {
        if (communicator == null) {
            throw new IllegalArgumentException("Communicator must not be null");
        }
        if (Remotes.communicator != null) {
            throw new IllegalStateException("Communicator already initialized");
        }
        synchronized (Remotes.class) {
            if (Remotes.communicator == null) {
                Remotes.communicator = communicator;
            }
        }
    }

    /**
     * 初始化远程通信配置
     *
     * @param configure 配置文件路径
     * @param args      通信器运行参数
     * @return ICE通信器对象
     */
    public static Ice.Communicator initializeCommunicator(String configure, String... args) {
        if (configure == null) {
            return Ice.Util.initialize(args);
        } else {
            Ice.InitializationData data = new Ice.InitializationData();
            Ice.Properties properties = Ice.Util.createProperties();
            properties.load(Strings.getRealPath(configure));
            Ice.StringSeqHolder holder = new Ice.StringSeqHolder(args);
            data.properties = Ice.Util.createProperties(holder, properties);
            return Ice.Util.initialize(holder, data);
        }
    }

    /**
     * 初始化远程通信配置
     *
     * @param configure 配置想映射表
     * @param args      通信器运行参数
     * @return ICE通信器对象
     */
    public static Ice.Communicator initializeCommunicator(Map<String, String> configure, String... args) {
        if (configure == null || configure.isEmpty()) {
            return Ice.Util.initialize(args);
        } else {
            Ice.InitializationData data = new Ice.InitializationData();
            Ice.Properties properties = Ice.Util.createProperties();
            for (Entry<String, String> entry : configure.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            Ice.StringSeqHolder holder = new Ice.StringSeqHolder(args);
            data.properties = Ice.Util.createProperties(holder, properties);
            return Ice.Util.initialize(holder, data);
        }
    }

    /**
     * 将本地令牌对象转换成slice令牌对象
     *
     * @param token 本地令牌对象
     * @return slice令牌对象
     */
    public static Itoken token2itoken(Token token) {
        return token == null ? null
            : new Itoken(token.getCode(), token.getTimeout(), Jsons.format(new HashMap<String, Object>(token)));
    }

    /**
     * 将slice令牌对象转换成本地令牌对象
     *
     * @param token slice令牌对象
     * @return 本地令牌对象
     */
    @SuppressWarnings("unchecked")
    public static Token itoken2token(Itoken token) {
        return token == null || Strings.isEmpty(token.code) ? null
            : new Token(token.code, token.timeout, (Map<String, Object>) Jsons.parse(token.attributes));
    }

    /**
     * 获取远程地址
     *
     * @param nodes 远程节点数组
     * @return 远程地址
     */
    public static String getAddress(Node... nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("Illegal nodes:" + Strings.toString(nodes));
        }
        StringBuilder address = new StringBuilder();
        for (Node node : nodes) {
            if (address.length() > 0) {
                address.append(':');
            }
            String s = getAddress(node.getProtocol(), node.getHost(), node.getPort());
            if (s != null) {
                address.append(s);
            }
        }
        return address.toString();
    }

    /**
     * 获取远程地址
     *
     * @param protocol 远程节点协议
     * @param host     远程节点主机地址
     * @param port     远程节点主机端口
     * @return 远程地址
     */
    public static String getAddress(Protocol protocol, String host, int port) {
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol must not be null");
        }
        if (protocol != Protocol.tcp && protocol != Protocol.udp && protocol != Protocol.ssl) {
            throw new IllegalArgumentException("Not support protocol:" + protocol);
        }
        return new StringBuilder().append(protocol).append(" -h ").append(host).append(" -p ").append(port).toString();
    }

    /**
     * 获取远程调用代理
     *
     * @param address 远程调用地址
     * @return 远程调用代理对象
     */
    public static Ice.ObjectPrx getProxy(String address) {
        return getProxy(address, COMMON_CHANNEL_NAME);
    }

    /**
     * 获取远程调用代理
     *
     * @param address    远程调用地址
     * @param identifier 远程调用通标标识
     * @return 远程调用代理对象
     */
    public static Ice.ObjectPrx getProxy(String address, String identifier) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier must not be null");
        }
        return getCommunicator().stringToProxy(new StringBuilder(identifier).append(':').append(address).toString());
    }

    /**
     * 获取远程调用代理
     *
     * @param nodes 远程节点数组
     * @return 远程调用代理对象
     */
    public static Ice.ObjectPrx getProxy(Node... nodes) {
        return getProxy(getAddress(nodes));
    }

    /**
     * 获取远程调用代理
     *
     * @param protocol 远程节点协议
     * @param host     远程节点主机地址
     * @param port     远程节点主机端口
     * @return 远程调用代理对象
     */
    public static Ice.ObjectPrx getProxy(Protocol protocol, String host, int port) {
        return getProxy(getAddress(protocol, host, port));
    }

    /**
     * 远程资源调用
     *
     * @param proxy 远程资源代理
     * @param uri   远程资源标识
     * @return 调用结果
     * @throws Exception 操作异常
     */
    public static Object invoke(Ice.ObjectPrx proxy, String uri) throws Exception {
        return invoke(proxy, null, uri, null);
    }

    /**
     * 远程资源调用
     *
     * @param proxy      远程资源代理
     * @param uri        远程资源标识
     * @param parameters 请求参数
     * @return 调用结果
     * @throws Exception 操作异常
     */
    public static Object invoke(Ice.ObjectPrx proxy, String uri, Map<String, Object> parameters) throws Exception {
        return invoke(proxy, null, uri, parameters);
    }

    /**
     * 远程资源调用
     *
     * @param proxy 远程资源代理
     * @param token 请求令牌
     * @param uri   远程资源标识
     * @return 调用结果
     * @throws Exception 操作异常
     */
    public static Object invoke(Ice.ObjectPrx proxy, Token token, String uri) throws Exception {
        return invoke(proxy, token, uri, null);
    }

    /**
     * 远程资源调用
     *
     * @param proxy      远程资源代理
     * @param token      请求令牌
     * @param uri        远程资源标识
     * @param parameters 请求参数
     * @return 调用结果（JSON格式）
     * @throws Exception 操作异常
     */
    @SuppressWarnings("unchecked")
    public static Object invoke(final Ice.ObjectPrx proxy, Token token, String uri, Map<String, Object> parameters)
        throws Exception {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy must not be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Uri must not be null");
        }
        if (parameters != null && !parameters.isEmpty()) {
            for (Entry<String, Object> entry : parameters.entrySet()) {
                final Object value = entry.getValue();
                if (Beans.isEmpty(value)) {
                    continue;
                } else if (value instanceof File) {
                    entry.setValue(Servers.submit(new Callable<String>() {

                        @Override
                        public String call() throws Exception {
                            return upload(proxy, new Nfile((File) value));
                        }

                    }).get());
                } else if (value instanceof Nfile) {
                    entry.setValue(Servers.submit(new Callable<String>() {

                        @Override
                        public String call() throws Exception {
                            return upload(proxy, (Nfile) value);
                        }

                    }).get());
                } else if (value instanceof Object[]) {
                    Object[] array = (Object[]) value;
                    if (array.length > 0) {
                        for (int i = 0; i < array.length; i++) {
                            final Object item = array[i];
                            if (item instanceof File) {
                                array[i] = Servers.submit(new Callable<String>() {

                                    @Override
                                    public String call() throws Exception {
                                        return upload(proxy, new Nfile((File) item));
                                    }

                                }).get();
                            } else if (item instanceof Nfile) {
                                array[i] = Servers.submit(new Callable<String>() {

                                    @Override
                                    public String call() throws Exception {
                                        return upload(proxy, (Nfile) item);
                                    }

                                }).get();
                            }
                        }
                    }
                } else if (value instanceof Collection) {
                    Collection<Object> collection = (Collection<Object>) value;
                    if (!collection.isEmpty()) {
                        Iterator<Object> iterator = collection.iterator();
                        while (iterator.hasNext()) {
                            final Object item = iterator.next();
                            if (item instanceof File) {
                                iterator.remove();
                                collection.add(Servers.submit(new Callable<String>() {

                                    @Override
                                    public String call() throws Exception {
                                        return upload(proxy, new Nfile((File) item));
                                    }

                                }).get());
                            } else if (item instanceof Nfile) {
                                collection.add(Servers.submit(new Callable<String>() {

                                    @Override
                                    public String call() throws Exception {
                                        return upload(proxy, (Nfile) item);
                                    }

                                }).get());
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
        ResourcePrx _proxy = ResourcePrxHelper.checkedCast(proxy);
        Iresult iresult = _proxy.invoke(getClient(), token2itoken(token), uri, Jsons.format(parameters));
        if (iresult instanceof Istream) {
            Istream istream = (Istream) iresult;
            if (istream.file) {
                return download(proxy, istream.id, istream.name, istream.size);
            }
            return _proxy.download(istream.id, 0, 0);
        }
        return Jsons.parse(((Istring) iresult).json);
    }

    /**
     * 文件上传
     *
     * @param proxy 远程资源代理
     * @param file  文件对象
     * @return 远程文件名称
     * @throws IOException IO操作异常
     */
    public static String upload(Ice.ObjectPrx proxy, Nfile file) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy must not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        }
        ResourcePrx _proxy = ResourcePrxHelper.checkedCast(proxy);
        byte[] buffer = new byte[Streams.DEFAULT_BUFFER_SIZE * 100];
        LinkedList<Ice.AsyncResult> results = new LinkedList<Ice.AsyncResult>();
        String name = new StringBuilder("upload-").append(UUID.randomUUID()).append('.').append(file.getName())
            .toString();
        int n = 0;
        InputStream is = file.getInputStream();
        try {
            while ((n = is.read(buffer)) > 0) {
                Ice.AsyncResult result = _proxy.begin_upload(name, buffer, n);
                result.waitForSent();
                results.add(result);
                if (results.size() > 3) {
                    Ice.AsyncResult first = results.removeFirst();
                    first.waitForCompleted();
                    first.throwLocalException();
                }
            }
            while (results.size() > 0) {
                Ice.AsyncResult first = results.removeFirst();
                first.waitForCompleted();
                first.throwLocalException();
            }
        } finally {
            is.close();
        }
        return name;
    }

    /**
     * 文件下载
     *
     * @param proxy 远程资源代理
     * @param id    文件标识
     * @param name  文件名称
     * @param size  文件大小（字节）
     * @return 文件对象
     * @throws IOException IO操作异常
     */
    public static File download(Ice.ObjectPrx proxy, String id, String name, long size) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy must not be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size must not be null");
        }
        final File file = new File(getDirectory(),
            new StringBuilder("download-").append(UUID.randomUUID()).append('.').append(name).toString());
        ResourcePrx _proxy = ResourcePrxHelper.checkedCast(proxy);
        LinkedList<Ice.AsyncResult> results = new LinkedList<Ice.AsyncResult>();
        Callback_Resource_download callback = new Callback_Resource_download() {

            @Override
            public void response(byte[] buffer) {
                OutputStream output = null;
                try {
                    output = new FileOutputStream(file, true);
                    output.write(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        output.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void exception(Ice.LocalException e) {

            }

        };
        int index = 0, length = Streams.DEFAULT_BUFFER_SIZE * 100;
        while (index < size) {
            Ice.AsyncResult result = _proxy.begin_download(id, index, length, callback);
            result.waitForSent();
            results.add(result);
            if (results.size() > 3) {
                Ice.AsyncResult first = results.removeFirst();
                first.waitForCompleted();
                first.throwLocalException();
            }
            index += length;
        }
        while (results.size() > 0) {
            Ice.AsyncResult first = results.removeFirst();
            first.waitForCompleted();
            first.throwLocalException();
        }
        return file;
    }

    /**
     * 销毁远程调用资源
     */
    public static void destroy() {
        if (communicator != null) {
            synchronized (Remotes.class) {
                if (communicator != null) {
                    logger.info("Destroy ice client communicator");
                    communicator.shutdown();
                    communicator.destroy();
                    communicator = null;
                }
            }
        }
    }

}
