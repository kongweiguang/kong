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

    slf4j() {
        @Override
        /**
         * 执行log 操作。
         */
        public void log(String message) {
            log.info(message);
        }
    },


    console() {
        @Override
        /**
         * 执行log 操作。
         */
        public void log(String message) {
            System.out.println(message);
        }
    };


    private static final Logger log = LoggerFactory.getLogger(ReqLog.class);
}
