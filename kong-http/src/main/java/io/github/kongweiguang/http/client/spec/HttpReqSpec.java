package io.github.kongweiguang.http.client.spec;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.entity.FilePart;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.client.executor.HttpExecutor;
import io.github.kongweiguang.http.client.retry.HttpRetryConfig;
import io.github.kongweiguang.http.client.retry.HttpRetryExecutor;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static java.util.Objects.nonNull;

/**
 * HTTP请求构建器
 *
 * @author kongweiguang
 */
public class HttpReqSpec extends ReqSpec<HttpReqSpec, Res> {

    //form
    private final Map<String, String> form = new LinkedHashMap<>();
    private final List<FilePart> files = new ArrayList<>();

    //async
    protected Consumer<Res> success;
    protected Consumer<Throwable> fail;

    /**
     * 保存 retry config
     */
    private final HttpRetryConfig retry = new HttpRetryConfig();

    public HttpReqSpec() {
        super();

    }

    @Override
    protected Res execute(OkHttpClient client) {
        return new HttpExecutor(this, client, new HttpRetryExecutor(retry)).executeBlocking();
    }

    @Override
    public CompletableFuture<Res> okAsync() {
        return super.okAsync().whenComplete((res, error) -> {
            if (error == null) {
                ofNullable(success).ifPresent(callback -> callback.accept(res));
                return;
            }

            Throwable cause = error.getCause() == null ? error : error.getCause();
            ofNullable(fail).ifPresent(callback -> callback.accept(cause));
        });
    }

    /**
     * 重试机制
     *
     * @param consumer 重试机制 {@link HttpRetryConfig}
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec retry(Consumer<HttpRetryConfig> consumer) {
        notNull(consumer, "retry consumer must not be null");
        consumer.accept(retry);
        return this;
    }

    /**
     * 请求时成功时回调函数
     *
     * @param success 成功回调函数
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec success(Consumer<Res> success) {
        notNull(success, "success must not be null");

        this.success = success;
        return this;
    }

    /**
     * 请求失败时回调函数
     *
     * @param fail 失败回调函数
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec fail(Consumer<Throwable> fail) {
        notNull(fail, "fail must not be null");

        this.fail = fail;
        return this;
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param bytes    文件内容
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec file(String name, String fileName, byte[] bytes) {
        notNull(name, "name must not be null");
        notNull(fileName, "fileName must not be null");
        notNull(bytes, "bytes must not be null");

        if (!isMul()) {
            throw new IllegalArgumentException("use file must is multipart ");
        }

        files.add(new FilePart(name, fileName, bytes));
        return this;
    }


    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param path     文件路径
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec file(String name, String fileName, Path path) {

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
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec file(String name, String fileName, String path) {

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
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec file(String name, File file) {

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
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec form(String name, Object value) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        if (nonNull(name) && nonNull(value)) {
            form.put(name, String.valueOf(value));
        }

        return this;
    }

    /**
     * 移除表单内容根据name，只有form_urlencoded或者multipart方式才可以使用
     *
     * @param name 需要移除的昵称
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec removeForm(String name) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        ofNullable(name).ifPresent(form::remove);

        return this;
    }

    /**
     * 添加form表单，只有form_urlencoded或者multipart方式才可以使用
     *
     * @param form form表单map
     * @return ReqBuilder {@link HttpReqSpec}
     */
    public HttpReqSpec form(Map<String, Object> form) {

        if (!isFormUrl() && !isMul()) {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        ofNullable(form).ifPresent(map -> form.forEach((k, v) -> form.put(k, String.valueOf(v))));

        return this;
    }

    private boolean isFormUrl() {

        return false;
    }


    private boolean isMul() {
        return false;
    }

    /**
     * 获取表单参数集合。
     *
     * @return 表单参数集合
     */
    public Map<String, String> form() {
        return form;
    }

    /**
     * 获取上传文件集合。
     *
     * @return 上传文件集合
     */
    public List<FilePart> files() {
        return files;
    }

    /**
     * 获取成功回调函数。
     *
     * @return 成功回调函数
     */
    public Consumer<Res> success() {
        return success;
    }

    /**
     * 获取失败回调函数。
     *
     * @return 失败回调函数
     */
    public Consumer<Throwable> fail() {
        return fail;
    }

    /**
     * 获取重试配置。
     *
     * @return 重试配置 {@link HttpRetryConfig}
     */
    public HttpRetryConfig retry() {
        return retry;
    }

}
