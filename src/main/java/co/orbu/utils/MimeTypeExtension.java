package co.orbu.utils;

public class MimeTypeExtension {

    private MimeTypeExtension() {
    }

    public static String getExtensionFromMimeType(String mimeType) {
        String extension;

        switch (mimeType.toLowerCase()) {
            case "image/png":
                extension = ".png";
                break;

            case "image/jpg":
            case "image/jpeg":
                extension = ".jpg";
                break;

            case "image/gif":
                extension = ".gif";
                break;

            default:
                throw new IllegalArgumentException("Unknown mimeType: " + mimeType);
        }

        return extension;
    }

}
