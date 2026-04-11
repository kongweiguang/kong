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
 * Internal http utility methods.
 */
public final class HttpClientUtil {

    private HttpClientUtil() {
        throw new KongHttpRuntimeException("util not be construct");
    }

    public static String fixUrl(String url) {
        if (isNull(url) || url.trim().isEmpty()) {
            return Const._http + Const.localhost;
        }

        url = url.trim();

        if (url.startsWith(Const._http) || url.startsWith(Const._https)) {
            return url;
        }

        if (url.startsWith(Const._ws)) {
            return Const._http + url.substring(Const._ws.length());
        }

        if (url.startsWith(Const._wss)) {
            return Const._https + url.substring(Const._wss.length());
        }

        return Const._http + Const.localhost + url;
    }

    public static boolean isHttp(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._http);
    }

    public static boolean isHttps(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._https);
    }

    public static boolean isWs(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._ws);
    }

    public static boolean isWss(String url) {
        return nonNull(url) && url.toLowerCase().startsWith(Const._wss);
    }

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
