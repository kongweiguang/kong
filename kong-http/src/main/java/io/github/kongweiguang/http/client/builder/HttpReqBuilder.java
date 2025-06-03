package io.github.kongweiguang.http.client.builder;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.ok.HttpOK;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import io.github.kongweiguang.json.Json;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static java.util.Objects.*;
import static okhttp3.internal.http.HttpMethod.permitsRequestBody;

/**
 * HTTP请求构建器
 *
 * @author kongweiguang
 */
@SuppressWarnings("unchecked")
public class HttpReqBuilder<T extends HttpReqBuilder<T, R>, R> extends ReqBuilder<T, R> {

    //body
    protected byte[] body;

    //form
    protected Map<String, String> formMap;
    protected MultipartBody.Builder mul;

    //async
    protected Consumer<Res> success;
    protected Consumer<Throwable> fail;

    //retry
    protected RetryableTask<Res> retry;

    public HttpReqBuilder() {
        super();
        this.retry = RetryableTask.<Res>retryForPredicate(() -> null, (res, t) -> {
                    if (nonNull(t)) {
                        return Pair.of(true, res);
                    }

                    if (nonNull(res)) {
                        return Pair.of(!res.isOk(), res);
                    }

                    return Pair.of(false, res);
                })
                .maxAttempts(1)
        ;

    }

    @Override
    protected CompletableFuture<R> execute(OkHttpClient client) {
        return (CompletableFuture<R>) HttpOK.ok((HttpReqBuilder<?, Res>) this, client);
    }

    /**
     * 添加body
     *
     * @return RequestBody {@link RequestBody}
     */
    protected RequestBody addBody() {
        RequestBody rb = null;

        if (permitsRequestBody(method().name())) {
            //multipart 格式提交
            if (isMul()) {

                ofNullable(formMap).ifPresent(ignore -> form().forEach(mul()::addFormDataPart));

                rb = mul().setType(requireNonNull(MediaType.parse(contentType()))).build();

            }
            //form_urlencoded 格式提交
            else if (isFormUrl()) {

                FormBody.Builder formBuilder = new FormBody.Builder(charset());

                ofNullable(formMap).ifPresent(ignore -> form().forEach(formBuilder::addEncoded));

                rb = formBuilder.build();

            }
            //字符串提交
            else {

                if (nonNull(body())) {
                    rb = RequestBody.create(MediaType.parse(contentType()), body());
                }

            }
        }

        return rb;
    }

    /**
     * 请求时成功时回调函数
     *
     * @param success 成功回调函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T success(Consumer<Res> success) {
        notNull(success, "success must not be null");

        this.success = success;
        return (T) this;
    }

    /**
     * 请求失败时回调函数
     *
     * @param fail 失败回调函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T fail(Consumer<Throwable> fail) {
        notNull(fail, "fail must not be null");

        this.fail = fail;
        return (T) this;
    }

    /**
     * 重试机制
     *
     * @param consumer 重试机制 {@link RetryableTask}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T retry(Consumer<RetryableTask<Res>> consumer) {
        notNull(retry, "retry must not be null");
        consumer.accept(retry);
        return (T) this;
    }


    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param bytes    文件内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T file(String name, String fileName, byte[] bytes) {
        notNull(name, "name must not be null");
        notNull(fileName, "fileName must not be null");
        notNull(bytes, "bytes must not be null");

        if (!isMul()) {
            throw new IllegalArgumentException("use file must is multipart ");
        }

        mul().addFormDataPart(
                name,
                fileName,
                RequestBody.create(MediaType.parse(contentType()), bytes)
        );

        return (T) this;
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param path     文件路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T file(String name, String fileName, Path path) {

        try {
            return file(name, fileName, Files.readAllBytes(path));
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }

    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param path     文件路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T file(String name, String fileName, String path) {

        try {
            return file(name, fileName, Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }

    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name 名称
     * @param file 上传文件
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T file(String name, File file) {

        try {
            return file(name, file.getName(), Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }

    }

    /**
     * 添加form表单，只有form_urlencoded或者multipart方式才可以使用
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T form(String name, Object value) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        if (nonNull(name) && nonNull(value)) {
            form().put(name, String.valueOf(value));
        }

        return (T) this;
    }

    /**
     * 移除表单内容根据name，只有form_urlencoded或者multipart方式才可以使用
     *
     * @param name 需要移除的昵称
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T removeForm(String name) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        ofNullable(name).ifPresent(form()::remove);

        return (T) this;
    }

    /**
     * 添加form表单，只有form_urlencoded或者multipart方式才可以使用
     *
     * @param form form表单map
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T form(Map<String, Object> form) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        ofNullable(form).ifPresent(map -> form.forEach((k, v) -> form().put(k, String.valueOf(v))));

        return (T) this;
    }

    /**
     * 添加json字符串的body
     *
     * @param json json字符串
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T json(String json) {
        return body(json, ContentType.JSON.v());
    }

    /**
     * 添加数据类型的对象，使用jackson转换成json字符串
     *
     * @param json 数据类型的对象
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T json(Object json) {
        return body(Json.toStr(json), ContentType.JSON.v());
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T body(String body, String contentType) {
        return body(body.getBytes(charset()), contentType);
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T body(byte[] body, String contentType) {
        contentType(contentType);
        this.body = body;
        return (T) this;
    }

    //get----------
    public Consumer<Res> success() {
        return success;
    }

    public Consumer<Throwable> fail() {
        return fail;
    }


    public byte[] body() {
        return body;
    }

    public RetryableTask<Res> retry() {
        return retry;
    }

    public Map<String, String> form() {
        if (isNull(formMap)) {
            this.formMap = new HashMap<>();
        }

        return formMap;
    }

    public MultipartBody.Builder mul() {
        if (isNull(mul)) {
            this.mul = new MultipartBody.Builder();
        }

        return mul;
    }

}
