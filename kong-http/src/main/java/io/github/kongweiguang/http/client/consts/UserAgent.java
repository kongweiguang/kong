package io.github.kongweiguang.http.client.consts;

/**
 * 常用 User-Agent 常量。
 *
 * @author kongweiguang
 */
public interface UserAgent {

    /**
     * 返回 User-Agent 字符串。
     *
     * @return User-Agent 值
     */
    String value();

    enum Windows implements UserAgent {
        CHROME("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36"),
        FIREFOX("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");

        private final String value;

        Windows(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum MacOS implements UserAgent {
        CHROME("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36"),
        FIREFOX("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0");

        private final String value;

        MacOS(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum Ubuntu implements UserAgent {
        CHROME("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Ubuntu/11.10 Chromium/27.0.1453.93 Chrome/27.0.1453.93 Safari/537.36"),
        FIREFOX("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:21.0) Gecko/20130331 Firefox/21.0");

        private final String value;

        Ubuntu(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum Android implements UserAgent {
        CHROME("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19"),
        FIREFOX("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0");

        private final String value;

        Android(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum IPhone implements UserAgent {
        CHROME("Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_4 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) CriOS/27.0.1453.10 Mobile/10B350 Safari/8536.25");

        private final String value;

        IPhone(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum IPad implements UserAgent {
        SAFARI("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");

        private final String value;

        IPad(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
