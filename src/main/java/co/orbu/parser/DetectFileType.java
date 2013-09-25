package co.orbu.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DetectFileType {

    private String mimeType;

    public DetectFileType(File file) throws IOException {
        byte[] data = new byte[32];

        try (FileInputStream fis = new FileInputStream(file)) {
            int count = fis.read(data, 0, data.length);

            if (count < data.length) {
                throw new IOException("Not enough data in source file.");
            }
        }

        MagicNumbersParser[] parsers = {PngMagicNumbersParser.getInstance(), JpegMagicNumbersParser.getInstance(), GifMagicNumbersParser.getInstance()};

        for (MagicNumbersParser p : parsers) {
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
