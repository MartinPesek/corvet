package co.orbu.parser;

import java.util.Arrays;

public class PngParser implements Parser {

    private static final String MIME_TYPE = "image/png";
    private static final byte HEADER[] = { -119, 80, 78, 71, 13, 10, 26, 10 };

    private PngParser() {}

    @Override
    public boolean isValid(byte[] data) {
        if (data == null || HEADER.length > data.length)
            throw new IllegalArgumentException("data");

        byte[] d = Arrays.copyOf(data, HEADER.length);

        return Arrays.equals(HEADER, d);
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    private static class SingletonHelper {
        private static final PngParser INSTANCE = new PngParser();
    }

    public static PngParser getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
