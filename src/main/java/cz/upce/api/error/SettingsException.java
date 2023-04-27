package cz.upce.api.error;

/**
 * Exception when is some error with settings
 */
public class SettingsException extends Exception{
    public SettingsException(String message){
        super(message);
    }
}
