package io.github.kongweiguang.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.nonNull;

/**
 * io工具
 *
 * @author kongweiguang
 */
public class IoUtil {

    /**
     * 将流转成byte数组
     *
     * @param input 输入流
     * @param size  byte大小
     * @return byte数组
     */
    public static byte[] toByteArray(final InputStream input, final int size) {

        try {
            if (size < 0 || size == 0) {
                return new byte[0];
            }

            final byte[] data = new byte[size];
            int offset = 0;
            int read;

            while (offset < size && (read = input.read(data, offset, size - offset)) != -1) {
                offset += read;
            }

            if (offset != size) {
                return new byte[0];

            }

            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭流
     *
     * @param c 流
     */
    public static void close(final Closeable c) {
        if (nonNull(c)) {
            try {
                c.close();
            } catch (IOException ignored) {

            }
        }
    }
}
