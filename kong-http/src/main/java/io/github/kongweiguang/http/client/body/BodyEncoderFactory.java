package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;

/**
 * 按 Content-Type 选择请求体编码器。
 *
 * @author kongweiguang
 */
public final class BodyEncoderFactory {

    /**
     * 保存底层 OkHttp response 对象
     */
    private static final BodyEncoder RAW = new RawBodyEncoder();

    /**
     * 保存 form fields
     */
    private static final BodyEncoder FORM = new FormUrlEncodedEncoder();

    /**
     * 定义 multipart constant
     */
    private static final BodyEncoder MULTIPART = new MultipartEncoder();

    /**
     * 创建 BodyEncoderFactory instance
     */
    private BodyEncoderFactory() {
    }


    /**
     * 处理 resolve 根据 Content-Type 选择请求体编码器
     */
    public static BodyEncoder resolve(HttpReqSpec spec) {
        String ct = spec.contentType();
        if (ct != null && ct.contains(ContentType.MULTIPART.value())) {
            return MULTIPART;
        }
        if (ct != null && ct.contains(ContentType.FORM_URLENCODED.value())) {
            return FORM;
        }
        return RAW;
    }
}

