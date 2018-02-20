package co.orbu.parser;

interface MagicNumbersParser {

    boolean isValid(byte[] data);

    String getMimeType();
}
