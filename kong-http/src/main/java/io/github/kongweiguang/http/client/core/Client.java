package io.github.kongweiguang.http.client.core;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.function.Supplier;

import static java.time.Duration.ofMinutes;
import static java.util.Optional.ofNullable;
import static javax.net.ssl.SSLContext.getInstance;

/**
 * 请求客户端
 *
 * @author kongweiguang
 */
public final class Client {

    /**
     * 默认分发器
     */
    private static final Supplier<Dispatcher> disSup = () -> {
        final Dispatcher dis = new Dispatcher();
        dis.setMaxRequests(1 << 20);
        dis.setMaxRequestsPerHost(1 << 20);
        return dis;
    };

    /**
     * 默认的客户端
     */
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .dispatcher(disSup.get())
            .connectTimeout(ofMinutes(1))
            .writeTimeout(ofMinutes(1))
            .readTimeout(ofMinutes(1))
            .build();

    /**
     * 创建OkHttpClient
     *
     * @return OkHttpClient {@link OkHttpClient}
     */
    public static OkHttpClient of() {
        return of(Conf.global());
    }

    /**
     * 创建OkHttpClient
     *
     * @param conf 配置
     * @return OkHttpClient {@link OkHttpClient}
     */
    public static OkHttpClient of(final Conf conf) {
        final OkHttpClient.Builder builder = client.newBuilder();

        ofNullable(conf.httpLoggingInterceptor()).ifPresent(conf::addInterceptor);

        ofNullable(conf.interceptors()).ifPresent(interceptors -> interceptors.forEach(builder::addInterceptor));

        ofNullable(conf.dispatcher()).ifPresent(builder::dispatcher);

        ofNullable(conf.connectionPool()).ifPresent(builder::connectionPool);

        ofNullable(conf.proxy()).ifPresent(builder::proxy);

        ofNullable(conf.proxyAuthenticator()).ifPresent(builder::proxyAuthenticator);

        ofNullable(conf.eventListener()).ifPresent(builder::eventListener);

        ofNullable(conf.cookieJar()).ifPresent(builder::cookieJar);

        if (!conf.followRedirects()) {
            builder.followRedirects(false);
        }

        if (!conf.followSslRedirects()) {
            builder.followSslRedirects(false);
        }

        if (!conf.ssl()) {
            ssl(builder);
        }

        ofNullable(conf.timeout()).ifPresent(timeout -> builder.connectTimeout(timeout.connect())
                .writeTimeout(timeout.write())
                .readTimeout(timeout.read()));

        return builder.build();
    }

    /**
     * 构建ssl请求链接
     *
     * @param builder 构建类
     */
    private static void ssl(final Builder builder) {
        try {
            final TrustManager[] trustAllCerts = DefaultTrustManager.of.managers();

            final SSLContext sslContext = getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception ignored) {

        }
    }
}
