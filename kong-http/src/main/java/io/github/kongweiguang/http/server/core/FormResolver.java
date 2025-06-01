package io.github.kongweiguang.http.server.core;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 解析form表单数据，简易实现
 * 支持multipart/form-data格式的表单数据解析
 *
 * @author kongweiguang
 */
public class FormResolver {

    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; ";
    private static final String TEXT_TYPE = "text";
    private static final String FILE_TYPE = "file";

    /**
     * 解析form表单
     *
     * @param req http请求
     */
    public static void parser(HttpReq req) {
        if (req == null || req.contentType() == null) {
            return;
        }
        
        try {
            // 提取boundary
            String contentType = req.contentType();
            int boundaryIndex = contentType.indexOf("=");
            if (boundaryIndex == -1) {
                return;
            }
            
            byte[] boundary = ("--" + contentType.substring(boundaryIndex + 1)).getBytes(StandardCharsets.UTF_8);
            byte[] body = req.bytes();
            
            parseMultipartBody(req, body, boundary);
        } catch (Exception e) {
            // 解析异常时记录日志或处理错误，但不中断请求处理
            System.err.println("解析表单数据时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 解析multipart/form-data格式的请求体
     *
     * @param req HTTP请求对象
     * @param body 请求体字节数组
     * @param boundary 分隔符字节数组
     */
    private static void parseMultipartBody(HttpReq req, byte[] body, byte[] boundary) {
        int count = 0;
        int crlfCount = 0;
        int length = body.length;
        int boundaryLen = boundary.length;
        int cursor = boundaryLen + 2;
        boolean paramStart = false;
        boolean paramEnd = false;
        Part part = null;

        for (int i = 0; i < length; i++) {
            // 检查是否匹配boundary
            count = matchBoundary(body, boundary, i, length);

            if (count == boundaryLen) {
                if (i > 0) {
                    paramEnd = true;
                }

                paramStart = true;
                i += boundaryLen + 2 - 1;
            }

            if (paramStart) {
                crlfCount = matchCRLF(body, i, length);

                if (crlfCount == 2) {
                    processHeaderLine(body, i, cursor, req, part);
                    
                    byte[] line = extractLine(body, cursor, i);

                    if (isLineBlank(line)) {
                        paramStart = false;
                        paramEnd = false;
                        cursor = i + 2;
                    } else {
                        String lineStr = new String(line, StandardCharsets.UTF_8);
                        if (lineStr.startsWith(CONTENT_DISPOSITION)) {
                            part = resolveParam(lineStr);
                        }

                        cursor = i;
                    }

                    i += 1;
                }

                crlfCount = 0;
            }

            if (paramEnd) {
                processFormPart(req, body, i, cursor, boundaryLen, part);
                cursor = i + 1;
                paramEnd = false;
            }

            count = 0;
        }
    }
    
    /**
     * 匹配boundary
     */
    private static int matchBoundary(byte[] body, byte[] boundary, int position, int length) {
        int count = 0;
        for (int j = 0; j < boundary.length; j++) {
            if (position + j >= length) {
                return 0;
            }
            if (body[position + j] == boundary[j]) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
    
    /**
     * 匹配CRLF (\r\n)
     */
    private static int matchCRLF(byte[] body, int position, int length) {
        int count = 0;
        for (int j = 0; j < 2; j++) {
            if (position + j >= length) {
                return 0;
            }
            if (body[position + j] == CRLF[j]) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
    
    /**
     * 提取一行数据
     */
    private static byte[] extractLine(byte[] body, int start, int end) {
        if (end <= start || start < 0 || end > body.length) {
            return new byte[0];
        }
        byte[] line = new byte[end - start];
        System.arraycopy(body, start, line, 0, end - start);
        return line;
    }
    
    /**
     * 处理头部行
     */
    private static void processHeaderLine(byte[] body, int position, int cursor, HttpReq req, Part part) {
        // 此方法为将来扩展预留，可以处理Content-Type等其他头部信息
    }
    
    /**
     * 处理表单部分
     */
    private static void processFormPart(HttpReq req, byte[] body, int position, int cursor, int boundaryLen, Part part) {
        if (part == null) {
            return;
        }

        int contentLength = position - cursor - boundaryLen - 1 - 2;
        if (contentLength < 0) {
            return; // 防止数组越界
        }

        if (TEXT_TYPE.equals(part.type())) {
            byte[] val = new byte[contentLength];
            System.arraycopy(body, cursor, val, 0, contentLength);
            req.params().computeIfAbsent(part.name(), k -> new ArrayList<>()).add(new String(val, StandardCharsets.UTF_8));
        } else {
            UploadFile uf = new UploadFile();
            uf.content(new ByteArrayInputStream(body, cursor, contentLength));
            uf.fileName(part.filename());
            req.fileMap().computeIfAbsent(part.name(), k -> new ArrayList<>()).add(uf);
        }
    }


    /**
     * 解析Content-Disposition头部参数
     *
     * @param lineStr Content-Disposition行内容
     * @return 解析后的Part对象
     */
    private static Part resolveParam(String lineStr) {
        if (lineStr == null || !lineStr.startsWith(CONTENT_DISPOSITION)) {
            return null;
        }

        String[] kvs = lineStr.substring(CONTENT_DISPOSITION.length()).split(";");
        Part part = new Part();
        part.type(TEXT_TYPE);

        for (String kv : kvs) {
            kv = kv.trim();
            int eqIndex = kv.indexOf('=');
            if (eqIndex <= 0) {
                continue;
            }
            
            String key = kv.substring(0, eqIndex);
            String value = kv.substring(eqIndex + 1).replace("\"", "");
            
            if ("name".equals(key)) {
                part.name(value);
            } else if ("filename".equals(key)) {
                part.setFilename(value).type(FILE_TYPE);
            }
        }

        return part;
    }


    /**
     * 判断行是否为空行
     *
     * @param line 行数据
     * @return 是否为空行
     */
    private static boolean isLineBlank(byte[] line) {
        if (line == null || line.length == 0) {
            return true;
        }

        if (line.length == 2) {
            return line[0] == CRLF[0] && line[1] == CRLF[1];
        }

        // 检查是否只包含空白字符
        for (byte b : line) {
            if (b != ' ' && b != '\t' && b != '\r' && b != '\n') {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 表单部分数据结构
     * 用于存储解析的表单字段信息
     */
    private static final class Part {

        private String type;
        private String name;
        private String filename;

        public String type() {
            return type;
        }

        public Part type(final String type) {
            this.type = type;
            return this;
        }

        public String name() {
            return name;
        }

        public Part name(final String name) {
            this.name = name;
            return this;
        }

        public String filename() {
            return filename;
        }

        public Part setFilename(final String filename) {
            this.filename = filename;
            return this;
        }
    }
}
