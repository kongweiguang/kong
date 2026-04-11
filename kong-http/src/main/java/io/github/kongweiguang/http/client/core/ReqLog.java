package io.github.kongweiguang.http.client.core;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求日志器
 *
 * @author kongweiguang
 */
public enum ReqLog implements HttpLoggingInterceptor.Logger {


    /**
     * 输出日志到 slf4j。
     */
    slf4j() {
        @Override
        public void log(String message) {
            log.info(message);
        }
    },

    /**
     * 输出日志到控制台。
     */
    console() {
        @Override
        public void log(String message) {
            System.out.println(message);
        }
    };

    private static final Logger log = LoggerFactory.getLogger(ReqLog.class);
}
