package io.github.kongweiguang.http.common.utils;

import io.github.kongweiguang.http.client.core.ReqLog;
import io.github.kongweiguang.http.common.core.Const;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.Map;
import java.util.StringJoiner;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * http内部使用工具
 *
 * @author kongweiguang
 */
public final class HttpClientUtil {

    private HttpClientUtil() {
        throw new KongHttpRuntimeException("util not be construct");
    }

    /**
     * 修复URL，确保其作为URL可被请求
     */
    public static String fixUrl(String url) {
        if (isNull(url)) {
            return Const._http + Const.localhost;
        }

        String value = url.trim();
        if (value.isEmpty()) {
            return Const._http + Const.localhost;
        }

        if (value.startsWith(Const._http) || value.startsWith(Const._https)) {
            return value;
        }

        if (startsWithIgnoreCase(value, Const._http)) {
            return Const._http + value.substring(Const._http.length());
        }

        if (startsWithIgnoreCase(value, Const._https)) {
            return Const._https + value.substring(Const._https.length());
        }

        if (startsWithIgnoreCase(value, Const._ws)) {
            return Const._http + value.substring(Const._ws.length());
        }

        if (startsWithIgnoreCase(value, Const._wss)) {
            return Const._https + value.substring(Const._wss.length());
        }

        if (value.startsWith("//")) {
            return Const._http + value.substring(2);
        }

        if (value.indexOf("://") > 0) {
            return value;
        }

        char first = value.charAt(0);
        if (first == '/') {
            return Const._http + Const.localhost + value;
        }

        if (first == '?' || first == '#') {
            return Const._http + Const.localhost + "/" + value;
        }

        if (looksLikeHost(value)) {
            return Const._http + value;
        }

        return Const._http + Const.localhost + "/" + value;
    }

    private static boolean startsWithIgnoreCase(String value, String prefix) {
        return value.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private static boolean looksLikeHost(String value) {
        if (Const.localhost.equalsIgnoreCase(value)) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                break;
            }
            if (c == '.' || c == ':') {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查URL是否使用HTTP协议
     */
    public static boolean isHttp(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._http);
    }

    /**
     * 检查URL是否使用HTTPS协议
     */
    public static boolean isHttps(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._https);
    }

    /**
     * 检查URL是否使用WS协议
     */
    public static boolean isWs(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._ws);
    }

    /**
     * 检查URL是否使用WSS协议
     */
    public static boolean isWss(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._wss);
    }


    //cookie转字符串
    public static String cookie2Str(Map<String, String> cookies) {
        if (isNull(cookies) || cookies.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("; ");
        cookies.forEach((k, v) -> joiner.add(k + "=" + v));
        return joiner.toString();
    }

    public static HttpLoggingInterceptor httpLoggingInterceptor(ReqLog logger, HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

}
