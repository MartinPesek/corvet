package co.orbu.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AutoDetectParser {

    private String mimeType;

    public AutoDetectParser(File file) throws IOException {
        byte[] data = new byte[32];

        try (FileInputStream fis = new FileInputStream(file)) {
            int count = fis.read(data, 0, data.length);

            if (count < data.length) {
                throw new IOException("Not enough data in source file.");
            }
        }

        Parser[] parsers = {PngParser.getInstance(), JpegParser.getInstance(), GifParser.getInstance()};

        for (Parser p : parsers) {
            if (p.isValid(data)) {
                mimeType = p.getMimeType();
                return;
            }
        }

        // no known image type found
        mimeType = null;
    }

    public boolean isKnownType() {
        return mimeType != null;
    }

    public String getMimeType() {
        return mimeType;
    }

}
