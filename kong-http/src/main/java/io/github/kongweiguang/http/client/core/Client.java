package io.github.kongweiguang.http.client.core;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.List;
import java.util.function.Supplier;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static java.time.Duration.ofMinutes;
import static javax.net.ssl.SSLContext.getInstance;

/**
 * OkHttp client factory.
 *
 * @author kongweiguang
 */
public class Client {


    /**
     * 定义 dispatcher supplier constant
     */
    public static final Supplier<Dispatcher> DISPATCHER_SUPPLIER = () -> {
        Dispatcher dis = new Dispatcher();
        dis.setMaxRequests(1 << 20);
        dis.setMaxRequestsPerHost(1 << 20);
        return dis;
    };


    /**
     * 定义 default client constant
     */
    public static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient.Builder()
            .dispatcher(DISPATCHER_SUPPLIER.get())
            .connectTimeout(ofMinutes(1))
            .writeTimeout(ofMinutes(1))
            .readTimeout(ofMinutes(1))
            .build();


    /**
     * 定义 appliers constant
     */
    private static final List<ConfApplier> APPLIERS = List.of(
            (conf, builder) -> ofNullable(conf.httpLoggingInterceptor()).ifPresent(builder::addInterceptor),
            (conf, builder) -> ofNullable(conf.interceptors()).ifPresent(interceptors -> interceptors.forEach(builder::addInterceptor)),
            (conf, builder) -> ofNullable(conf.dispatcher()).ifPresent(builder::dispatcher),
            (conf, builder) -> ofNullable(conf.connectionPool()).ifPresent(builder::connectionPool),
            (conf, builder) -> ofNullable(conf.proxy()).ifPresent(builder::proxy),
            (conf, builder) -> ofNullable(conf.proxyAuthenticator()).ifPresent(builder::proxyAuthenticator),
            (conf, builder) -> ofNullable(conf.proxySelector()).ifPresent(builder::proxySelector),
            (conf, builder) -> ofNullable(conf.eventListener()).ifPresent(builder::eventListener),
            (conf, builder) -> ofNullable(conf.cookieJar()).ifPresent(builder::cookieJar),
            (conf, builder) -> {
                if (!conf.followRedirects()) {
                    builder.followRedirects(false);
                }
            },
            (conf, builder) -> {
                if (!conf.followSslRedirects()) {
                    builder.followSslRedirects(false);
                }
            },
            (conf, builder) -> {
                if (!conf.ssl()) {
                    ssl(builder);
                }
            },
            (conf, builder) -> ofNullable(conf.timeout()).ifPresent(timeout -> builder
                    .connectTimeout(timeout.connect())
                    .writeTimeout(timeout.write())
                    .readTimeout(timeout.read()))
    );


    /**
     * 根据给定对象创建 wrapper instance
     */
    public static OkHttpClient of(Conf conf) {
        OkHttpClient.Builder builder = DEFAULT_CLIENT.newBuilder();
        for (ConfApplier applier : APPLIERS) {
            applier.apply(conf, builder);
        }
        return builder.build();
    }


    /**
     * 执行 ssl 操作
     */
    private static void ssl(Builder builder) {
        try {
            TrustManager[] trustAllCerts = DefaultTrustManager.of.managers();
            SSLContext sslContext = getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception ignored) {
        }
    }
}
