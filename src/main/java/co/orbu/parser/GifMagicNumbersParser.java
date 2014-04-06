package co.orbu.parser;

import java.util.Arrays;

public class GifMagicNumbersParser implements MagicNumbersParser {

    private static final String MIME_TYPE = "image/gif";
    private static final byte[] HEADER = {0x47, 0x49, 0x46};

    private GifMagicNumbersParser() {
    }

    public static GifMagicNumbersParser getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public boolean isValid(byte[] data) {
        if (data == null || HEADER.length > data.length) {
            throw new NullPointerException("data");
        }

        if (HEADER.length > data.length) {
            throw new IllegalArgumentException("data");
        }

        byte[] d = Arrays.copyOf(data, HEADER.length);

        return Arrays.equals(HEADER, d);
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    private static class SingletonHelper {
        private static final GifMagicNumbersParser INSTANCE = new GifMagicNumbersParser();
    }
}
