package cz.upce.printer_service.error;

/**
 * Exception to print failed
 */
public class PrintFailedException extends Exception{
    public PrintFailedException(String message) {
        super(message);
    }
}
