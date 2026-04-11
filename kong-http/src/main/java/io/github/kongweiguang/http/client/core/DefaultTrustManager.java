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
     * 获取managers 对应值。
     */
    public TrustManager[] managers() {
        return new TrustManager[]{this};
    }

    @Override
    /**
     * 执行checkClientTrusted 操作。
     */
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    /**
     * 执行checkServerTrusted 操作。
     */
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    /**
     * 获取getAcceptedIssuers 对应值。
     */
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[]{};
    }
}
