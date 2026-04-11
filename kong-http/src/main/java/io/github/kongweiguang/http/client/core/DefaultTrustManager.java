package io.github.kongweiguang.http.client.core;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * DefaultTrustManager
 *
 * @author kongweiguang
 */
public enum DefaultTrustManager implements X509TrustManager {
    of;

    /**
     * 返回当前信任管理器数组。
     */
    public TrustManager[] managers() {
        return new TrustManager[]{this};
    }


    /**
     * 校验客户端证书，默认不执行校验。
     */
    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }


    /**
     * 校验服务端证书，默认不执行校验。
     */
    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }


    /**
     * 返回受信任颁发者列表。
     */
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[]{};
    }
}
