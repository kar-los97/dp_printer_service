package cz.upce.spring.terminal_service.service;

import cz.upce.spring.error.TerminalException;
import cz.upce.spring.terminal_service.csob.Connection;
import cz.upce.spring.terminal_service.csob.Payment;
import cz.upce.spring.terminal_service.csob.Response;
import cz.upce.spring.terminal_service.dto.PayDto;
import cz.upce.spring.terminal_service.dto.RefundDto;
import cz.upce.spring.terminal_service.dto.ResultDto;
import cz.upce.spring.terminal_service.dto.TerminalDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;

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
        Socket socket;
        try {
            socket = new Socket(settings.getIp(), Math.toIntExact(settings.getPort()));

        } catch (IOException e) {
            return false;
        }
        return socket.isConnected();
    }

    public ResultDto pay(PayDto payDto) throws TerminalException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return payByType(getTerminalSettings(), payDto);
        }
    }

    public ResultDto refund(RefundDto refundDto) throws TerminalException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return refundByType(getTerminalSettings(), refundDto);
        }
    }

    public ResultDto closeTotals() throws TerminalException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return closeTotalsByType(getTerminalSettings());
        }
    }

    public ResultDto handshake() throws TerminalException, IOException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return handshakeByType(settings);
        }
    }

    public ResultDto  tmsBCall() throws TerminalException, IOException {
        TerminalDto settings = getTerminalSettings();
        if (settings == null) {
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return tmsBCallByType(settings);
        }
    }

    public ResultDto tmsNCall() throws TerminalException, IOException {
        TerminalDto settings = getTerminalSettings();
        if(settings == null){
            throw new TerminalException("SETTINGS_DOESNT_EXIST");
        } else {
            return tmsBCallByType(settings);
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
                case "csob":
                    try {
                        Payment payment = new Payment(settings.getId());
                        byte[] message = payment.createPayment(Double.parseDouble(payDto.getPrice()));
                        Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
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

    private ResultDto refundByType(TerminalDto settings, RefundDto refundDto) throws TerminalException {
        if (!test(settings)) {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
        switch (settings.getType()) {
            case "csob":
                try {
                    Payment payment = new Payment(settings.getId());
                    byte[] message = payment.createRefund(Double.parseDouble(refundDto.getPrice()));
                    Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
                    return res.toResultDto();
                } catch (IOException e) {
                    return null;
                }
            default:
                return null;
        }

    }

    private ResultDto closeTotalsByType(TerminalDto settings) throws TerminalException {
        if (!test(settings)) {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
        switch (settings.getType()) {
            case "csob":
                try {
                    Payment payment = new Payment(settings.getId());
                    byte[] message = payment.createCloseTotals();
                    Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
                    return res.toResultDto();
                } catch (IOException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private ResultDto handshakeByType(TerminalDto settings) throws TerminalException, IOException {
        if (!test(settings)) {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
        switch (settings.getType()) {
            case "csob":
                Payment payment = new Payment(settings.getId());
                byte[] message = payment.createHandshake();
                Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
                return res.toResultDto();
            default:
                throw new TerminalException("INVALID_TERMINAL_TYPE");
        }
    }

    private ResultDto tmsBCallByType(TerminalDto settings) throws TerminalException, IOException {
        if (!test(settings)) {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
        switch (settings.getType()) {
            case "csob":
                Payment payment = new Payment(settings.getId());
                byte[] message = payment.createBTmsCall();
                Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
                return res.toResultDto();
            default:
                throw new TerminalException("INVALID_TERMINAL_TYPE");
        }
    }

    private ResultDto tmsNCallByType(TerminalDto settings) throws TerminalException, IOException {
        if (!test(settings)) {
            throw new TerminalException("TERMINAL_IS_NOT_READY");
        }
        switch (settings.getType()) {
            case "csob":
                Payment payment = new Payment(settings.getId());
                byte[] message = payment.createNTmsCall();
                Response res = Connection.sendMessage(settings.getIp(), Math.toIntExact(settings.getPort()), message, payment);
                return res.toResultDto();
            default:
                throw new TerminalException("INVALID_TERMINAL_TYPE");
        }
    }


}
