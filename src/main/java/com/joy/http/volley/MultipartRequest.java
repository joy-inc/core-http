package com.joy.http.volley;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Daisw on 2017/12/11.
 */

public class MultipartRequest<T> extends ObjectRequest<T> {
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Charset DEFAULT_CHARSET = Charset.forName("US-ASCII");

    private static final ByteArrayBuffer FIELD_SEP;
    private static final ByteArrayBuffer CR_LF;
    private static final ByteArrayBuffer TWO_DASHES;

    static {
        FIELD_SEP = encode(DEFAULT_CHARSET, ": ");
        CR_LF = encode(DEFAULT_CHARSET, "\r\n");
        TWO_DASHES = encode(DEFAULT_CHARSET, "--");
    }

    private static final String boundary = generateBoundary();
    protected Map<String, ByteArrayPart> mByteArrayParts;

    public MultipartRequest(String url, Class<?> clazz) {
        super(Method.POST, url, clazz);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    writeBytes(TWO_DASHES, bos);
                    writeBytes(boundary, bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"", entry.getKey()), bos);
                    writeBytes(CR_LF, bos);
                    writeBytes("Content-Type: text/plain; charset=UTF-8", bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(String.format("Content-Transfer-Encoding: %s", "8bit"), bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(entry.getValue(), bos);
                    writeBytes(CR_LF, bos);
                }
            }

            Map<String, ByteArrayPart> byteArrayParts = getByteArrayParts();
            if (byteArrayParts != null && byteArrayParts.size() > 0) {
                for (Map.Entry<String, ByteArrayPart> entry : byteArrayParts.entrySet()) {
                    writeBytes(TWO_DASHES, bos);
                    writeBytes(boundary, bos);
                    writeBytes(CR_LF, bos);
                    final ByteArrayPart part = entry.getValue();
                    writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", entry.getKey(), part.getFilename()), bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(String.format("Content-Type: %s", part.getMimeType()), bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(String.format("Content-Transfer-Encoding: %s", part.getTransferEncoding()), bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(CR_LF, bos);
                    writeBytes(part.getData(), bos);
                    writeBytes(CR_LF, bos);
                }
            }
            writeBytes(TWO_DASHES, bos);
            writeBytes(boundary, bos);
            writeBytes(TWO_DASHES, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ByteArrayBuffer encode(Charset charset, String string) {
        ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        ByteArrayBuffer bab = new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.position(), encoded.remaining());
        return bab;
    }

    private static String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for (int i = 0; i < count; ++i) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    private static void writeField(String name, String value, OutputStream out) throws IOException {
        writeBytes(name, out);
        writeBytes(FIELD_SEP, out);
        writeBytes(value, out);
        writeBytes(CR_LF, out);
    }

    private static void writeBytes(ByteArrayBuffer b, OutputStream out) throws IOException {
        out.write(b.buffer(), 0, b.length());
    }

    private static void writeBytes(String s, OutputStream out) throws IOException {
        ByteArrayBuffer b = encode(DEFAULT_CHARSET, s);
        writeBytes(b, out);
    }

    private static void writeBytes(byte[] b, OutputStream out) throws IOException {
        ByteArrayBuffer bab = new ByteArrayBuffer(b.length);
        bab.append(b, 0, b.length);
        writeBytes(bab, out);
    }

    public void setByteArrayParts(Map<String, ByteArrayPart> parts) {
        mByteArrayParts = parts;
    }

    public void addByteArrayPart(String key, ByteArrayPart value) {
        if (mByteArrayParts == null) {
            mByteArrayParts = new HashMap<>();
        }
        mByteArrayParts.put(key, value);
    }

    public void addByteArrayParts(Map<String, ByteArrayPart> parts) {
        if (mByteArrayParts == null) {
            mByteArrayParts = parts;
        } else {
            mByteArrayParts.putAll(parts);
        }
    }

    public Map<String, ByteArrayPart> getByteArrayParts() throws AuthFailureError {
        if (mByteArrayParts != null && !mByteArrayParts.isEmpty()) {
            return mByteArrayParts;
        }
        return Collections.emptyMap();
    }

    public static class ByteArrayPart {
        private final byte[] data;
        private final String filename;
        private final String mimeType;

        public ByteArrayPart(byte[] data, String mimeType, String filename) {
            if (data == null) {
                throw new IllegalArgumentException("byte[] may not be null");
            } else {
                this.data = data;
                this.filename = filename;
                this.mimeType = mimeType;
            }
        }

        public ByteArrayPart(byte[] data, String filename) {
            this(data, "application/octet-stream", filename);
        }

        public byte[] getData() {
            return data;
        }

        public String getFilename() {
            return filename;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getTransferEncoding() {
            return "binary";
        }
    }
}
