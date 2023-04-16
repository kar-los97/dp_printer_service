package cz.upce.spring.error;

/**
 * Exception when some error on Terminal
 */
public class TerminalException extends Exception{

    public TerminalException(String message){
        super(message);
    }
}
