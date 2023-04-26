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

@Service
public class TerminalService {

    private final TerminalSettingsService terminalSettingsService;

    public TerminalService(TerminalSettingsService terminalSettingsService) {
        this.terminalSettingsService = terminalSettingsService;
    }

    public boolean testConnection() {
        try {
            return test(getTerminalSettings());
        } catch (TerminalException ex) {
            return false;
        }
    }

    public boolean test(TerminalDto settings) throws TerminalException {
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        }
        //Podléhá interním pravidlům bankovní společnosti
        return true;
    }

    public ResultDto pay(PayDto payDto) throws TerminalException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return payByType(getTerminalSettings(), payDto);
        }
    }

    private TerminalDto getTerminalSettings() {
        try {
            return terminalSettingsService.getTerminalSettings();
        } catch (Exception e) {
            return null;
        }
    }

    private ResultDto payByType(TerminalDto settings, PayDto payDto) throws TerminalException {
        if (test(settings)) {
            switch (settings.getType()) {
                case "default":
                    try {
                        TerminalRequest terminalRequest = new TerminalRequest(settings.getId());
                        byte[] message = terminalRequest.createPayment(Double.parseDouble(payDto.getPrice()));
                        ConvertedResponse res = TerminalConnection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, terminalRequest);
                        return res.toResultDto();
                    } catch (IOException e) {
                        return null;
                    }
                default:
                    return null;

            }
        } else {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
    }


}
