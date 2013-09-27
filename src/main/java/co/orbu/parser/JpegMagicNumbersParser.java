package co.orbu.parser;

import java.util.Arrays;

public class JpegMagicNumbersParser implements MagicNumbersParser {

    private static final String MIME_TYPE = "image/jpg";
    private static final byte HEADER1[] = { -1, -40, -1, -32 };
    private static final byte HEADER2[] = { 74, 70, 73, 70 };

    // skipping two bytes between HEADER1 and HEADER2
    // because these two bytes are used by vendors for their identification/some stuff
    private static final int HEADER_SKIP_COUNT = 2;

    private JpegMagicNumbersParser() {}

    @Override
    public boolean isValid(byte[] data) {
        if (data == null || (HEADER1.length + HEADER2.length + HEADER_SKIP_COUNT) > data.length)
            throw new IllegalArgumentException("data");

        byte[] d = Arrays.copyOf(data, HEADER1.length);
        boolean result = Arrays.equals(HEADER1, d);

        d = Arrays.copyOfRange(data, HEADER1.length + HEADER_SKIP_COUNT, HEADER1.length + HEADER_SKIP_COUNT + HEADER2.length);
        return result && Arrays.equals(HEADER2, d);
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    private static class SingletonHelper {
        private static final JpegMagicNumbersParser INSTANCE = new JpegMagicNumbersParser();
    }

    public static JpegMagicNumbersParser getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
