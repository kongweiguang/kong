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


    public TrustManager[] managers() {
        return new TrustManager[]{this};
    }

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[]{};
    }
}
