package cz.upce.spring.terminal_service.service;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import cz.upce.spring.error.SettingsException;
import cz.upce.spring.error.TerminalSettingsException;
import cz.upce.spring.terminal_service.dto.TerminalDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class TerminalSettingsService {

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
