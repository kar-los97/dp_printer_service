package cz.upce.api.terminal_service.service;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import cz.upce.api.error.SettingsException;
import cz.upce.api.error.TerminalSettingsException;
import cz.upce.api.terminal_service.dto.TerminalDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Service for terminal settings
 */
@Service
public class TerminalSettingsService {

    /**
     * Method to get actual terminal settings
     * @return object with actual settings
     * @throws TerminalSettingsException if terminal settings not OK
     */
    public TerminalDto getTerminalSettings() throws TerminalSettingsException {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("cfg/config_terminal.json"));
            JSONObject terminalObject = (JSONObject) jsonObject.get("terminal");
            if (terminalObject != null || !terminalObject.containsKey("type") || !terminalObject.containsKey("ip") || !terminalObject.containsKey("port") || !terminalObject.containsKey("id")) {
                TerminalDto dto = new TerminalDto();
                dto.setType((String) terminalObject.get("type"));
                dto.setIp((String) terminalObject.get("ip"));
                dto.setPort((Long) terminalObject.get("port"));
                dto.setId((String) terminalObject.get("id"));
                return dto;
            }else{
                throw new TerminalSettingsException("INVALID_TERMINAL_SETTINGS");
            }
        } catch (FileNotFoundException e) {
            throw new TerminalSettingsException("CONFIG_FILE_DOESNT_EXIST");
        } catch (IOException | ParseException e) {
            throw new TerminalSettingsException("CONFIG_NO_VALID");
        }

    }

    /**
     * Method to change terminal settings
     * @param dto new terminal settings
     * @return new terminal settings if is OK
     * @throws TerminalSettingsException if some error at saving config file
     */
    public TerminalDto changeSettings(TerminalDto dto) throws TerminalSettingsException {
        JSONObject jsonObject = new JSONObject();
        JSONObject terminalObject = new JSONObject();
        terminalObject.put("type",dto.getType());
        terminalObject.put("ip",dto.getIp());
        terminalObject.put("port",dto.getPort());
        terminalObject.put("id",dto.getId());

        jsonObject.put("terminal",terminalObject);
        try {
            FileWriter file = new FileWriter("cfg/config_terminal.json");
            file.write(jsonObject.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            throw new TerminalSettingsException("SAVING_FAILED");
        }

        return getTerminalSettings();
    }

    /**
     * Method to delete all settings of terminal
     * @return true - if ok
     * @throws SettingsException if some error at deleting settings
     */
    public boolean deleteSettings() throws SettingsException {
        JSONObject object = new JSONObject();

        object.put("terminal", new JSONObject());

        try {
            FileWriter file = new FileWriter("cfg/config_printer.json");
            file.write(object.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            JsonStringEncoder EnigooCashierServiceLogger;
            throw new SettingsException("DELETING_SETTINGS_FAILED");
        }
        return true;
    }
}
