package co.orbu.utils;

public class MimeTypeExtension {

    private MimeTypeExtension() {}

    public static String getExtensionFromMimeType(String mimeType) {
        String extension = null;

        switch (mimeType.toLowerCase()) {
            case "image/png":
                extension = ".png";
                break;

            case "image/jpg":
                extension = ".jpg";
                break;

            case "image/gif":
                extension = ".gif";
                break;
        }

        return extension;
    }

}
