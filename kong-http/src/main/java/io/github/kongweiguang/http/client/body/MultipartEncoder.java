package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.entity.FilePart;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 编码 multipart 请求体，包含表单字段和文件分片。
 *
 * @author kongweiguang
 */
public final class MultipartEncoder implements BodyEncoder {

    /**
     * 将表单字段与文件分片编码为 multipart 请求体。
     */
    @Override
    public RequestBody encode(HttpReqSpec spec) {
        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MediaType.parse(spec.contentType()));

        spec.form().forEach(mb::addFormDataPart);

        for (FilePart file : spec.files()) {
            mb.addFormDataPart(
                    file.name(),
                    file.fileName(),
                    RequestBody.create(MediaType.parse(spec.contentType()), file.bytes())
            );
        }

        return mb.build();
    }
}

