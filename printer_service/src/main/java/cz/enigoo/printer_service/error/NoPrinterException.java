package cz.enigoo.printer_service.error;

/**
 * Exception when no printer found
 */
public class NoPrinterException extends Exception{
    public NoPrinterException(String message) {
        super(message);
    }
}
