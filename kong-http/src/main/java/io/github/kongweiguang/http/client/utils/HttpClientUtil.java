package io.github.kongweiguang.http.client.utils;

import io.github.kongweiguang.http.client.consts.Const;
import io.github.kongweiguang.http.client.core.ReqLog;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.Map;
import java.util.StringJoiner;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * HTTP 内部工具方法
 *
 * @author kongweiguang
 */
public final class HttpClientUtil {

    /**
     * 创建 HttpClientUtil instance
     */
    private HttpClientUtil() {
        throw new KongHttpRuntimeException("HttpClientUtil cannot be instantiated.");
    }

    /**
     * 将 URL 规范化为完整的 HTTP 或 HTTPS URL
     */
    public static String fixUrl(String url) {
        if (isNull(url) || url.trim().isEmpty()) {
            return Const.HTTP_PREFIX + Const.LOCALHOST;
        }

        url = url.trim();
        String lower = url.toLowerCase();

        if (lower.startsWith(Const.HTTP_PREFIX) || lower.startsWith(Const.HTTPS_PREFIX)) {
            int idx = url.indexOf("://");
            return lower.substring(0, idx + 3) + url.substring(idx + 3);
        }

        if (lower.startsWith(Const.WS_PREFIX)) {
            return Const.HTTP_PREFIX + url.substring(Const.WS_PREFIX.length());
        }

        if (lower.startsWith(Const.WSS_PREFIX)) {
            return Const.HTTPS_PREFIX + url.substring(Const.WSS_PREFIX.length());
        }

        if (url.startsWith("//")) {
            return Const.HTTP_PREFIX + url.substring(2);
        }

        if (url.startsWith("/")) {
            return Const.HTTP_PREFIX + Const.LOCALHOST + url;
        }

        if (url.startsWith("?") || url.startsWith("#")) {
            return Const.HTTP_PREFIX + Const.LOCALHOST + "/" + url;
        }

        boolean likelyHost = url.contains(".")
                             || url.startsWith("localhost")
                             || url.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+(:\\d+)?(/.*)?$");
        if (likelyHost) {
            return Const.HTTP_PREFIX + url;
        }

        return Const.HTTP_PREFIX + Const.LOCALHOST + "/" + url;
    }


    /**
     * 判断 URL 是否使用 HTTP scheme
     */
    public static boolean isHttp(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const.HTTP_PREFIX);
    }


    /**
     * 判断 URL 是否使用 HTTPS scheme
     */
    public static boolean isHttps(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const.HTTPS_PREFIX);
    }


    /**
     * 判断 URL 是否使用 WebSocket scheme
     */
    public static boolean isWs(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const.WS_PREFIX);
    }


    /**
     * 判断 URL 是否使用 secure WebSocket scheme
     */
    public static boolean isWss(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const.WSS_PREFIX);
    }

    /**
     * 解析url为builder
     *
     * @param url
     * @return
     */
    public static HttpUrl.Builder parseUrlBuilder(String url) {
        String fixed = fixUrl(url == null ? null : url.trim());
        HttpUrl parsed = HttpUrl.parse(fixed);
        if (parsed == null) {
            throw new KongHttpRuntimeException("invalid url: " + url);
        }
        return parsed.newBuilder();
    }

    /**
     * 将 Cookie map 转换为 header string
     */
    public static String cookie2Str(Map<String, String> cookies) {
        if (isNull(cookies) || cookies.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("; ");

        cookies.forEach((k, v) -> joiner.add(k + "=" + v));
        return joiner.toString();
    }


    /**
     * 创建指定 log level 的 HTTP logging interceptor
     */
    public static HttpLoggingInterceptor httpLoggingInterceptor(ReqLog logger, HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }
}
