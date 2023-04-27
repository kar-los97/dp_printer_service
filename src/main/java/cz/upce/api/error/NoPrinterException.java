package cz.upce.api.error;

/**
 * Exception when no printer found
 */
public class NoPrinterException extends Exception{
    public NoPrinterException(String message) {
        super(message);
    }
}
