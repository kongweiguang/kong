package io.github.kongweiguang.http.client.core;

import okhttp3.logging.HttpLoggingInterceptor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * ok-java内部使用工具
 */
public final class InnerUtil {

    private InnerUtil() {
        throw new RuntimeException("util not be construct");
    }

    //移除第一个斜杠
    public static String removeFirstSlash(String path) {
        if (isNull(path)) {
            return "";
        }

        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        return path;
    }

    //url校验
    public static String fixUrl(String url, boolean isWs) {
        if (isNull(url) || Objects.equals("", url)) {
            url = "/";
        }

        if (isWs) {
            if (!isWs(url) && !isWss(url)) {
                if (url.startsWith("/")) {
                    url = Const._http + Const.localhost + url;
                } else {
                    url = Const._http + url;
                }
            }
            if (isWs(url)) {
                url = url.replaceFirst(Const._ws, Const._http);
            }

            if (isWss(url)) {
                url = url.replaceFirst(Const._wss, Const._https);

            }
            return url;
        }

        if (!isHttp(url) && !isHttps(url)) {
            if (url.startsWith("/")) {
                url = Const._http + Const.localhost + url;
            } else {
                url = Const._http + url;
            }
        }

        return url;
    }

    public static boolean isHttp(final String url) {
        if (nonNull(url)) {
            return url.toLowerCase().startsWith(Const._http);
        }

        return false;
    }

    public static boolean isHttps(final String url) {
        if (nonNull(url)) {
            return url.toLowerCase().startsWith(Const._https);
        }

        return false;
    }

    public static boolean isWs(final String url) {
        if (nonNull(url)) {
            return url.toLowerCase().startsWith(Const._ws);
        }

        return false;
    }

    public static boolean isWss(final String url) {
        if (nonNull(url)) {
            return url.toLowerCase().startsWith(Const._wss);
        }

        return false;
    }


    //cookie转字符串
    public static String cookie2Str(final Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();

        cookies.forEach((k, v) -> sb.append(k).append('=').append(v).append("; "));

        return sb.toString();
    }

    public static HttpLoggingInterceptor httpLoggingInterceptor(ReqLog logger, HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

    private static Executor executor;

    static Executor exec() {
        if (isNull(executor)) {
            synchronized (InnerUtil.class) {
                if (isNull(executor)) {
                    InnerUtil.executor = new ThreadPoolExecutor(0,
                            Integer.MAX_VALUE,
                            60,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<>(),
                            r -> new Thread(r, "ok-thread"));
                }
            }

        }

        return executor;
    }
}