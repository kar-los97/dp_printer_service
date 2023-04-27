package cz.upce.api.error;

/**
 * Exception when some error with terminal settings
 */
public class TerminalSettingsException extends Exception {
    public TerminalSettingsException(String message) {
        super(message);
    }
}
