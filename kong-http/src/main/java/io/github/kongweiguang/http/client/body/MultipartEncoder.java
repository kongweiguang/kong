package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 编码 multipart 请求体，包含表单字段和文件分片。
 */
public final class MultipartEncoder implements BodyEncoder {
    @Override
    public RequestBody encode(HttpRequestSpec spec) {
        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MediaType.parse(spec.contentType()));
        spec.form().forEach(mb::addFormDataPart);
        for (HttpRequestSpec.FilePart file : spec.files()) {
            mb.addFormDataPart(file.name(), file.fileName(),
                    RequestBody.create(MediaType.parse(spec.contentType()), file.bytes()));
        }
        return mb.build();
    }
}


