package io.github.kongweiguang.http.client.spec;

import io.github.kongweiguang.http.client.executor.WsExecutor;
import io.github.kongweiguang.http.client.retry.NoRetryExecutor;
import io.github.kongweiguang.http.client.ws.WsListener;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

import static io.github.kongweiguang.core.lang.Assert.notNull;

/**
 * WebSocket请求构建器
 *
 * @author kongweiguang
 */
public class WsReqSpec extends ReqSpec<WsReqSpec, WebSocket> {

    //listener
    private WsListener wsListener;

    public WsReqSpec() {
        super();
    }

    @Override
    protected WebSocket execute(OkHttpClient client) {
        return new WsExecutor(this, client, new NoRetryExecutor<>()).executeBlocking();
    }

    /**
     * 设置ws协议的监听函数
     *
     * @param wsListener 监听函数
     * @return ReqBuilder {@link ReqSpec}
     */
    public WsReqSpec wsListener(WsListener wsListener) {
        notNull(wsListener, "wsListener must not be null");

        this.wsListener = wsListener;
        return this;
    }

    /**
     * 获取ws协议的监听函数
     *
     * @return 监听函数
     */
    public WsListener wsListener() {
        return wsListener;
    }
}
