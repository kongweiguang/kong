package io.github.kongweiguang.http.common.utils;

import io.github.kongweiguang.http.client.core.ReqLog;
import io.github.kongweiguang.http.common.core.Const;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.Map;

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
     * 修复URL，确保其以URL可以被请求
     */
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

        StringBuilder sb = new StringBuilder(cookies.size() * 16); // 预估大小以减少扩容

        cookies.forEach((k, v) -> sb.append(k).append('=').append(v).append("; "));

        return sb.toString();
    }

    public static HttpLoggingInterceptor httpLoggingInterceptor(ReqLog logger, HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

}