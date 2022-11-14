package cz.enigoo.printer_service.service;

import cz.enigoo.printer_service.dto.PrinterDto;
import cz.enigoo.printer_service.entity.Printer;
import cz.enigoo.printer_service.enums.PrinterType;
import cz.enigoo.printer_service.error.SettingsException;
import cz.enigoo.printer_service.utils.MessageType;
import cz.enigoo.printer_service.utils.PrinterLogger;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EnigooPrinterSettingsService {

    public List<Printer> getPrinterSettings() throws SettingsException {
        List<Printer> printersSet = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("config.json"));
            JSONArray printers = (JSONArray) jsonObject.get("printers");
            if (printers.isEmpty()) {
                PrinterLogger.getInstance().log("NO_PRINTER_SET", MessageType.WARN);
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
            PrinterLogger.getInstance().log("CONFIG_FILE_DOESNT_EXIST", MessageType.ERROR);
            throw new SettingsException("CONFIG_FILE_DOESNT_EXIST");
        } catch (ParseException | IOException e) {
            PrinterLogger.getInstance().log("CONFIG_FILE_NO_VALID", MessageType.ERROR);
            throw new SettingsException("CONFIG_FILE_NO_VALID");
        }
        return printersSet;
    }

    public List<String> getAllPrinters() {
        List<String> printers = new ArrayList<>();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        for (PrintService item : services) {
            printers.add(item.getName());
        }

        return printers;
    }

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
            FileWriter file = new FileWriter("config.json");
            file.write(object.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            PrinterLogger.getInstance().log("SAVING_FAILED", MessageType.ERROR);
            throw new SettingsException("SAVING_FAILED");
        }

        return getPrinterSettings();

    }

    public boolean deleteSettings() throws SettingsException {
        JSONObject object = new JSONObject();
        JSONArray printers = new JSONArray();

        object.put("printers", printers);

        try {
            FileWriter file = new FileWriter("config.json");
            file.write(object.toJSONString());

            file.flush();
            file.close();
        } catch (IOException e) {
            PrinterLogger.getInstance().log("DELETING_SETTINGS_FAILED", MessageType.ERROR);
            throw new SettingsException("DELETING_SETTINGS_FAILED");
        }
        return true;
    }
}
