package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Encodes multipart body, including form fields and file parts.
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

