package cz.upce.api.printer_service.service;

import cz.upce.api.error.SettingsException;
import cz.upce.api.printer_service.dto.PrinterDto;
import cz.upce.api.printer_service.entity.Printer;
import cz.upce.api.printer_service.enums.PrinterType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service for printer settings
 */
@Service
public class PrinterSettingsService {
    /**
     * Method to get actual printer settings
     * @return List of set printers
     * @throws SettingsException when no printer set or error with config file
     */
    public List<Printer> getPrinterSettings() throws SettingsException {
        List<Printer> printersSet = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("cfg/config_printer.json"));
            JSONArray printers = (JSONArray) jsonObject.get("printers");
            if (printers.isEmpty()) {
                throw new SettingsException("NO_PRINTER_SET");
            }
            for (Object object : printers) {
                Printer printer = new Printer();
                JSONObject objct = (JSONObject) object;
                printer.setType(PrinterType.getTypeFromString((String) objct.get("type")));
                printer.setName((String) objct.get("name"));
                printer.setPageHeight((Long) objct.get("height"));
                printer.setPageWidth((Long) objct.get("width"));
                printersSet.add(printer);
            }
        } catch (FileNotFoundException ex) {
            throw new SettingsException("CONFIG_FILE_DOESNT_EXIST");
        } catch (ParseException | IOException e) {
            throw new SettingsException("CONFIG_FILE_NO_VALID");
        }
        return printersSet;
    }

    /**
     * Method to get all printers that are connected to computer
     * @return list of printers' name
     */
    public List<String> getAllPrinters() {
        List<String> printers = new ArrayList<>();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        for (PrintService item : services) {
            printers.add(item.getName());
        }

        return printers;
    }

    /**
     * Method to change printers settings
     * @param dto new settings
     * @return updated settings
     * @throws SettingsException when error with saving settings
     */
    public List<Printer> changeSettings(Set<PrinterDto> dto) throws SettingsException {
        JSONObject object = new JSONObject();
        JSONArray printers = new JSONArray();

        for (PrinterDto pr : dto) {
            JSONObject printerObject = new JSONObject();
            printerObject.put("type", pr.getType());
            printerObject.put("name", pr.getName());
            printerObject.put("height", pr.getPageHeight());
            printerObject.put("width", pr.getPageWidth());
            printers.add(printerObject);
        }
        object.put("printers", printers);

        try {
            FileWriter file = new FileWriter("cfg/config_printer.json");
            file.write(object.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            throw new SettingsException("SAVING_FAILED");
        }

        return getPrinterSettings();

    }

    /**
     * Method to delete all settings of printers
     * @return true - deleted, false - not deleted
     * @throws SettingsException if some error with saving config file
     */
    public boolean deleteSettings() throws SettingsException {
        JSONObject object = new JSONObject();
        JSONArray printers = new JSONArray();

        object.put("printers", printers);

        try {
            FileWriter file = new FileWriter("cfg/config_printer.json");
            file.write(object.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            throw new SettingsException("DELETING_SETTINGS_FAILED");
        }
        return true;
    }
}
