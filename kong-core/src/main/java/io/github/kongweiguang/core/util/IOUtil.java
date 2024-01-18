package io.github.kongweiguang.core.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * io工具
 *
 * @author kongweiguang
 */
public class IOUtil {

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

}
