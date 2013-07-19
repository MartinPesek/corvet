package co.orbu.parser;

public interface Parser {

    public boolean isValid(byte[] data);

    public String getMimeType();
}
