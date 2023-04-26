package cz.upce.spring.terminal_service.service;

import cz.upce.spring.error.TerminalException;
import cz.upce.spring.terminal_service.terminalutils.TerminalConnection;
import cz.upce.spring.terminal_service.terminalutils.TerminalRequest;
import cz.upce.spring.terminal_service.terminalutils.ConvertedResponse;
import cz.upce.spring.terminal_service.dto.PayDto;
import cz.upce.spring.terminal_service.dto.ResultDto;
import cz.upce.spring.terminal_service.dto.TerminalDto;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service class for realize operation with terminal
 */
@Service
public class TerminalService {

    private final TerminalSettingsService terminalSettingsService;

    public TerminalService(TerminalSettingsService terminalSettingsService) {
        this.terminalSettingsService = terminalSettingsService;
    }

    /**
     * Method to test terminal connection
     * @return true - terminal is ready, false - terminal is not ready
     */
    public boolean testConnection() {
        try {
            return test(getTerminalSettings());
        } catch (TerminalException ex) {
            return false;
        }
    }

    /**
     * Method to test terminal connection
     * @param settings
     * @return true - terminal is ready, false - terminal is not ready
     * @throws TerminalException if settings not exist
     */
    private boolean test(TerminalDto settings) throws TerminalException {
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        }
        //Podléhá interním pravidlům bankovní společnosti
        return true;
    }

    /**
     * Method to realize pay on terminal
     * @param payDto object with parameters to pay
     * @return object with result if is OK
     * @throws TerminalException if terminal not set
     */
    public ResultDto pay(PayDto payDto) throws TerminalException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return payByType(getTerminalSettings(), payDto);
        }
    }

    /**
     * Helpful method to get terminal settings
     * @return object with terminal settings or null if not exist
     */
    private TerminalDto getTerminalSettings() {
        try {
            return terminalSettingsService.getTerminalSettings();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helpful method to pay by terminal type (if we have more terminal types)
     * @param settings actual settings of terminal
     * @param payDto object with parameters to pay
     * @return result of pay if everything is OK
     * @throws TerminalException Terminal is not ready
     */
    private ResultDto payByType(TerminalDto settings, PayDto payDto) throws TerminalException {
        if (test(settings)) {
            if ("default".equals(settings.getType())) {
                try {
                    TerminalRequest terminalRequest = new TerminalRequest(settings.getId());
                    byte[] message = terminalRequest.createPayment(Double.parseDouble(payDto.getPrice()));
                    TerminalConnection connection = new TerminalConnection();
                    ConvertedResponse res = connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message);
                    return res.toResultDto();
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        } else {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
    }


}
