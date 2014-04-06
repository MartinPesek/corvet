package co.orbu.parser;

interface MagicNumbersParser {

    public boolean isValid(byte[] data);

    public String getMimeType();
}
