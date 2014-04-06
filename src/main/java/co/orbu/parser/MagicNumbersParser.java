package co.orbu.parser;

public interface MagicNumbersParser {

    public boolean isValid(byte[] data);

    public String getMimeType();
}
