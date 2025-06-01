package io.github.kongweiguang.http.client.builder;

import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.ok.WSOK;
import io.github.kongweiguang.http.client.ws.WSListener;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

import static io.github.kongweiguang.core.lang.Assert.notNull;

/**
 * WebSocket请求构建器
 *
 * @author kongweiguang
 */
public class WSReqBuilder extends ReqBuilder<WSReqBuilder, WebSocket> {

    //listener
    private WSListener wsListener;

    public WSReqBuilder() {
        super();
        reqType(ReqType.ws);
    }

    @Override
    public WebSocket ok(OkHttpClient client) {
        before();
        return WSOK.ok(this, client).join();
    }

    /**
     * 设置ws协议的监听函数
     *
     * @param wsListener 监听函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public WSReqBuilder wsListener(WSListener wsListener) {
        notNull(wsListener, "wsListener must not be null");

        this.wsListener = wsListener;
        return this;
    }

    /**
     * 获取ws协议的监听函数
     *
     * @return 监听函数
     */
    public WSListener wsListener() {
        return wsListener;
    }
}