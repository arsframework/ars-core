package ars.invoke.channel.http.spring;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.security.CodeSource;

import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileManager;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ForwardingJavaFileManager;

import org.apache.commons.codec.binary.Hex;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

/**
 * 接口操作工具类
 *
 * @author wuyongqiang
 */
public final class Apis {
    /**
     * 默认请求读取超时时间（毫秒）
     */
    public static final int DEFAULT_READ_TIMEOUT = 30000;

    /**
     * 默认请求连接超时时间（毫秒）
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    /**
     * JSON数据类型
     */
    public static final MediaType JSON_MEDIA_TYPE = MediaType.parseMediaType("application/json; charset=UTF-8");

    /**
     * 包装类型参数类型名称/实例映射表
     */
    private static final Map<String, ParameterizedTypeReference> wrappers = new HashMap<String, ParameterizedTypeReference>();

    /**
     * 基于内存的java文件对象
     *
     * @author wuyongqiang
     */
    private static class MemoryJavaFileObject extends SimpleJavaFileObject {
        private final String source;

        public MemoryJavaFileObject(String name, String source) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return this.source;
        }
    }

    /**
     * 基于内存的java文件管理器
     *
     * @author wuyongqiang
     */
    private static class MemoryJavaFileManager extends ForwardingJavaFileManager {
        protected Map<String, byte[]> bytes = new HashMap<String, byte[]>();

        public MemoryJavaFileManager(JavaFileManager manager) {
            super(manager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, final String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            if (kind == JavaFileObject.Kind.CLASS) {
                URI uri = URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.CLASS.extension);
                return new SimpleJavaFileObject(uri, JavaFileObject.Kind.CLASS) {

                    public OutputStream openOutputStream() {
                        return new ByteArrayOutputStream() {

                            @Override
                            public void close() throws IOException {
                                try {
                                    bytes.put(className, this.toByteArray());
                                } finally {
                                    super.close();
                                }
                            }

                        };
                    }

                };
            }
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    /**
     * 范型类型对象
     *
     * @author wuyongqiang
     */
    private static class GenericWrapper implements Type {
        public final Class<?> target; // 包装目标类型
        public final Type[] generics; // 包装目标类型范型数组

        public GenericWrapper(Class<?> target, Type... generics) {
            if (target == null) {
                throw new IllegalArgumentException("Target must not be empty");
            }
            if (generics == null || generics.length == 0) {
                throw new IllegalArgumentException("Generics must not be empty");
            }
            this.target = target;
            this.generics = generics;
        }
    }

    /**
     * 获取类对象所在文件路径
     *
     * @param target 目标类对象
     * @return 文件路径
     */
    private static String getSourcePath(Class<?> target) {
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        CodeSource source = target.getProtectionDomain().getCodeSource();
        return source == null ? null : source.getLocation().getPath();
    }

    /**
     * 构建目标类源码类名称
     *
     * @param classpaths 需要加载的类路径集合
     * @param target     目标类对象
     * @param generics   类范型数组
     * @return 目标类源码类名称
     */
    private static CharSequence buildSourceName(List<String> classpaths, Class<?> target, Type... generics) {
        if (target == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
        if (classpaths == null) {
            classpaths = new LinkedList<String>();
        }
        StringBuilder buffer = new StringBuilder(target.getName().replace('$', '.'));
        String path = getSourcePath(target);
        if (path != null && !classpaths.contains(path)) {
            classpaths.add(path);
        }
        if (generics != null && generics.length > 0) {
            buffer.append('<');
            for (int i = 0; i < generics.length; i++) {
                if (i > 0) {
                    buffer.append(',');
                }
                Type type = generics[i];
                if (type instanceof Class) {
                    Class<?> cls = (Class<?>) type;
                    buffer.append(cls.getName().replace('$', '.'));
                    path = getSourcePath(cls);
                    if (path != null && !classpaths.contains(path)) {
                        classpaths.add(path);
                    }
                } else if (type instanceof GenericWrapper) {
                    GenericWrapper wrapper = (GenericWrapper) type;
                    buffer.append(buildSourceName(classpaths, wrapper.target, wrapper.generics));
                } else {
                    throw new IllegalStateException("Not support generic type:" + type);
                }
            }
            buffer.append('>');
        }
        return buffer;
    }

    /**
     * 获取对象类型类型
     *
     * @param generics 类型数组
     * @return 对象类型
     */
    public static Type type(Class<?> target, Type... generics) {
        return new GenericWrapper(target, generics);
    }

    /**
     * 获取包装类型参数类型实例
     *
     * @param target   包装目标对象类型
     * @param generics 目标对象类型范型数组
     * @return 包装类型参数类型实例
     */
    public static ParameterizedTypeReference wrap(Class<?> target, Type... generics) {
        if (target == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
        LinkedList<String> classpaths = new LinkedList<String>(); // 类加载路径集合
        String types = buildSourceName(classpaths, target, generics).toString(); // 目标类范型类型字符串
        ParameterizedTypeReference wrapper = wrappers.get(types);
        if (wrapper == null) {
            synchronized (types.intern()) {
                wrapper = wrappers.get(types);
                if (wrapper == null) {
                    String name = new StringBuilder("ParameterizedTypeReferenceWrapper$").append(Hex.encodeHexString(types.getBytes())).toString();
                    classpaths.addFirst(getSourcePath(ParameterizedTypeReference.class)); // ParameterizedTypeReference所在包路径
                    classpaths.addFirst(Apis.class.getResource("/").getPath()); // 添加当前包路径

                    // 构建类🌧️源码
                    final StringBuilder source = new StringBuilder("public class ").append(name);
                    source.append(" extends ").append(ParameterizedTypeReference.class.getName()).append('<').append(types).append(">{}");

                    // 初始化编译选项
                    String separator = System.getProperty("path.separator");
                    StringBuilder classpath = new StringBuilder();
                    for (String path : classpaths) {
                        if (classpath.length() > 0) {
                            classpath.append(separator);
                        }
                        classpath.append(path);
                    }
                    final List<String> options = Arrays.asList("-encoding", "UTF-8", "-classpath", classpath.toString());

                    // 构建类加载器
                    ClassLoader classLoader = new URLClassLoader(new URL[0], Apis.class.getClassLoader()) {

                        @Override
                        protected Class<?> findClass(String name) throws ClassNotFoundException {
                            try {
                                return super.findClass(name);
                            } catch (ClassNotFoundException e) {
                                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                                MemoryJavaFileManager manager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));
                                List<JavaFileObject> files = new ArrayList<JavaFileObject>(1);
                                files.add(new MemoryJavaFileObject(name, source.toString()));
                                JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, files);
                                if (task.call()) {
                                    byte[] bytes = manager.bytes.get(name);
                                    return defineClass(name, bytes, 0, bytes.length);
                                }
                                throw e;
                            }
                        }
                    };

                    // 初始化类实例
                    try {
                        wrapper = (ParameterizedTypeReference) classLoader.loadClass(name).newInstance();
                        wrappers.put(name, wrapper);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return wrapper;
    }

    /**
     * Http请求
     *
     * @param url 请求地址
     * @return 请求结果
     */
    public static Object request(String url) {
        return request(Object.class, url, null, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @return 请求结果
     */
    public static Object request(String url, Object parameter) {
        return request(Object.class, url, parameter, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param url    请求地址
     * @param method 请求方式
     * @return 请求结果
     */
    public static Object request(String url, HttpMethod method) {
        return request(Object.class, url, null, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @return 请求结果
     */
    public static Object request(String url, Object parameter, HttpMethod method) {
        return request(Object.class, url, parameter, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @param timeout   超时时间（毫秒）
     * @param headers   请求头键/值表
     * @return 请求结果
     */
    public static Object request(String url, Object parameter, HttpMethod method, int timeout, Map<String, String> headers) {
        return request(Object.class, url, parameter, method, timeout, headers);
    }

    /**
     * Http请求
     *
     * @param type 结果对象类型
     * @param url  请求地址
     * @param <T>  结果数据类型
     * @return 请求结果
     */
    public static <T> T request(Class<T> type, String url) {
        return request(type, url, null, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param type      结果对象类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(Class<T> type, String url, Object parameter) {
        return request(type, url, parameter, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param type   结果对象类型
     * @param url    请求地址
     * @param method 请求方式
     * @param <T>    结果数据类型
     * @return 请求结果
     */
    public static <T> T request(Class<T> type, String url, HttpMethod method) {
        return request(type, url, null, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param type      结果对象类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(Class<T> type, String url, Object parameter, HttpMethod method) {
        return request(type, url, parameter, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param type      结果对象类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @param timeout   超时时间（毫秒）
     * @param headers   请求头键/值表
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(Class<T> type, String url, Object parameter, HttpMethod method, int timeout, Map<String, String> headers) {
        return (T) request(wrap(type), url, parameter, method, timeout, headers);
    }

    /**
     * Http请求
     *
     * @param reference 参数类型
     * @param url       请求地址
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(ParameterizedTypeReference<T> reference, String url) {
        return request(reference, url, null, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param reference 参数类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(ParameterizedTypeReference<T> reference, String url, Object parameter) {
        return request(reference, url, parameter, HttpMethod.POST, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param reference 参数类型
     * @param url       请求地址
     * @param method    请求方式
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(ParameterizedTypeReference<T> reference, String url, HttpMethod method) {
        return request(reference, url, null, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param reference 参数类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(ParameterizedTypeReference<T> reference, String url, Object parameter, HttpMethod method) {
        return request(reference, url, parameter, method, DEFAULT_READ_TIMEOUT, Collections.<String, String>emptyMap());
    }

    /**
     * Http请求
     *
     * @param reference 参数类型
     * @param url       请求地址
     * @param parameter 请求参数对象
     * @param method    请求方式
     * @param timeout   超时时间（毫秒）
     * @param headers   请求头键/值表
     * @param <T>       结果数据类型
     * @return 请求结果
     */
    public static <T> T request(ParameterizedTypeReference<T> reference, String url, Object parameter, HttpMethod method, int timeout, Map<String, String> headers) {
        if (reference == null) {
            throw new IllegalArgumentException("ParameterizedTypeReference must not be null");
        }
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL must not be empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        // 设置请求头
        HttpHeaders header = new HttpHeaders();
        header.setContentType(JSON_MEDIA_TYPE);
        header.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        // 构建请求实体
        HttpEntity<Object> body = new HttpEntity(parameter, header);

        // 执行请求
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        if (timeout >= 0) {
            requestFactory.setReadTimeout(timeout);
        }
        RestTemplate template = new RestTemplate(requestFactory);
        return template.exchange(url, method, body, reference).getBody();
    }
}
