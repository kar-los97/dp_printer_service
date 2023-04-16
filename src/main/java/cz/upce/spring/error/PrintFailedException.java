package cz.upce.spring.error;

/**
 * Exception to print failed
 */
public class PrintFailedException extends Exception{
    public PrintFailedException(String message) {
        super(message);
    }
}
