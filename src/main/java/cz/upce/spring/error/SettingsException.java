package cz.upce.spring.error;

/**
 * Exception when is some error with settings
 */
public class SettingsException extends Exception{
    public SettingsException(String message){
        super(message);
    }
}
