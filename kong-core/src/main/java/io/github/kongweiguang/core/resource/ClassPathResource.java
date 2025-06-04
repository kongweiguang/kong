package io.github.kongweiguang.core.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * ClassPath资源访问类，用于高效获取classpath下的文件资源
 * 类似Spring的ClassPathResource，但更加简化
 *
 * @author kongweiguang
 */
public class ClassPathResource {

    private final String path;
    private final ClassLoader classLoader;
    private final Class<?> clazz;
    private final URL resourceUrl;

    /**
     * 构造方法，默认使用当前线程上下文类加载器
     *
     * @param path 相对于classpath的资源路径
     */
    public ClassPathResource(String path) {
        this(path, null, null);
    }

    /**
     * 构造方法，使用指定的类加载器
     *
     * @param path        相对于classpath的资源路径
     * @param classLoader 类加载器
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        this(path, classLoader, null);
    }

    /**
     * 构造方法，使用指定的类所在的包作为相对路径
     *
     * @param path  相对路径
     * @param clazz 指定类
     */
    public ClassPathResource(String path, Class<?> clazz) {
        this(path, null, clazz);
    }

    /**
     * 完整构造方法
     *
     * @param path        资源路径
     * @param classLoader 类加载器
     * @param clazz       指定类
     */
    public ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }

        // 规范化路径
        this.path = normalizePath(path);
        this.classLoader = (classLoader != null ? classLoader : getDefaultClassLoader());
        this.clazz = clazz;
        this.resourceUrl = resolveURL();
    }

    /**
     * 获取资源的输入流
     *
     * @return 资源输入流
     * @throws IOException 如果无法获取输入流
     */
    public InputStream stream() throws IOException {
        if (resourceUrl != null) {
            return resourceUrl.openStream();
        }

        // 尝试用不同方式加载资源
        InputStream is = null;
        if (clazz != null) {
            is = clazz.getResourceAsStream(path);
        }
        if (is == null) {
            is = classLoader.getResourceAsStream(path);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(path);
        }
        if (is == null) {
            throw new IOException("Could not find resource " + path);
        }
        return is;
    }

    /**
     * 获取资源的URL
     *
     * @return 资源URL
     * @throws IOException 如果无法获取URL
     */
    public URL url() throws IOException {
        if (resourceUrl != null) {
            return resourceUrl;
        }
        throw new IOException("Could not find resource " + path);
    }

    /**
     * 获取资源的URI
     *
     * @return 资源URI
     * @throws IOException 如果无法获取URI
     */
    public URI uri() throws IOException {
        try {
            return url().toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI", e);
        }
    }

    /**
     * 获取资源作为File对象
     * 注意：仅对文件系统中的资源有效，JAR中的资源将抛出异常
     *
     * @return 资源文件
     * @throws IOException 如果无法获取文件或资源不是文件系统中的文件
     */
    public File file() throws IOException {
        URI uri = uri();
        if (!uri.getScheme().equals("file")) {
            throw new IOException("Resource " + path + " is not a file (URI scheme: " + uri.getScheme() + ")");
        }
        return new File(uri);
    }

    /**
     * 获取资源的路径
     *
     * @return 路径
     */
    public String path() {
        return path;
    }

    /**
     * 将资源内容作为字符串读取
     *
     * @return 资源内容
     * @throws IOException 如果读取失败
     */
    public String str() throws IOException {
        return str(StandardCharsets.UTF_8);
    }

    /**
     * 将资源内容作为字符串读取，使用指定的字符集
     *
     * @param charset 字符集
     * @return 资源内容
     * @throws IOException 如果读取失败
     */
    public String str(Charset charset) throws IOException {
        try (InputStream is = stream()) {
            return new String(is.readAllBytes(), charset);
        }
    }

    /**
     * 将资源内容作为字节数组读取
     *
     * @return 资源内容字节数组
     * @throws IOException 如果读取失败
     */
    public byte[] bytes() throws IOException {
        try (InputStream is = stream()) {
            return is.readAllBytes();
        }
    }

    /**
     * 获取资源作为Path对象
     *
     * @return 资源Path
     * @throws IOException 如果无法获取Path
     */
    public Path path(boolean verifyExists) throws IOException {
        URI uri = uri();
        if (!uri.getScheme().equals("file")) {
            throw new IOException("Resource " + path + " is not a file (URI scheme: " + uri.getScheme() + ")");
        }
        Path path = Paths.get(uri);
        if (verifyExists && !Files.exists(path)) {
            throw new IOException("Resource " + this.path + " does not exist");
        }
        return path;
    }

    /**
     * 判断资源是否存在
     *
     * @return 是否存在
     */
    public boolean exists() {
        return resourceUrl != null;
    }

    /**
     * 判断资源是否可读
     *
     * @return 是否可读
     */
    public boolean isReadable() {
        try (InputStream ignored = stream()) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断资源是否是文件
     *
     * @return 是否是文件
     */
    public boolean isFile() {
        try {
            URI uri = uri();
            return uri.getScheme().equals("file");
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取资源的最后修改时间
     *
     * @return 最后修改时间，如果无法获取则返回-1
     */
    public long lastModified() {
        try {
            if (isFile()) {
                return file().lastModified();
            }
        } catch (IOException e) {
            // 忽略异常
        }
        return -1;
    }

    /**
     * 资源的文件名
     *
     * @return 文件名
     */
    public String getFilename() {
        int lastIndex = path.lastIndexOf('/');
        if (lastIndex == -1) {
            return path;
        }
        return path.substring(lastIndex + 1);
    }

    /**
     * 规范化路径
     *
     * @param path 原始路径
     * @return 规范化后的路径
     */
    private String normalizePath(String path) {
        // 处理路径开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        // 替换反斜杠为正斜杠
        return path.replace('\\', '/');
    }

    /**
     * 获取默认的类加载器
     *
     * @return 类加载器
     */
    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法访问线程上下文类加载器
        }
        if (cl == null) {
            // 使用当前类的类加载器
            cl = this.getClass().getClassLoader();
            if (cl == null) {
                // 获取系统类加载器
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法获取类加载器
                }
            }
        }
        return cl;
    }

    /**
     * 解析资源URL
     *
     * @return 资源URL，如果不存在则返回null
     */
    private URL resolveURL() {
        URL url = null;

        // 尝试通过类加载器获取
        if (clazz != null) {
            url = clazz.getResource(path);
        }

        // 使用指定的类加载器
        if (url == null && classLoader != null) {
            url = classLoader.getResource(path);
        }

        // 使用系统类加载器
        if (url == null) {
            url = ClassLoader.getSystemResource(path);
        }

        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassPathResource that = (ClassPathResource) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "ClassPathResource{" +
               "path='" + path + '\'' +
               ", exists=" + exists() +
               '}';
    }
}
