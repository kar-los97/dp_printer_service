package cz.upce.api.printer_service.service;

import cz.upce.api.error.NoPrinterException;
import cz.upce.api.error.PrintFailedException;
import cz.upce.api.printer_service.dto.PrintDto;
import cz.upce.api.printer_service.dto.StatusDto;
import cz.upce.api.printer_service.entity.Printer;
import cz.upce.api.printer_service.enums.PrinterType;
import cz.upce.api.printer_service.utils.Html2ImgConvertor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static cz.upce.api.printer_service.enums.PrinterType.OTHER;
import static cz.upce.api.printer_service.enums.PrinterType.THERMO;

/**
 * Service to do operations with printers, communicate with
 */
@Service
public class PrintService {

    /**
     * Types of printers
     */
    private final PrinterType[]PRINTER_TYPES = {THERMO, OTHER};

    /**
     * Founded printer
     */
    private Printer printer;

    /**
     * Founded printService
     */
    private javax.print.PrintService service;

    /**
     *
     * @param printDto data to print
     * @return true - printing is OK, else exception
     * @throws NoPrinterException printer doesn't find
     * @throws PrintFailedException if printer shut down or printing failed
     */
    public boolean print(PrintDto printDto) throws NoPrinterException, PrintFailedException, FileNotFoundException {
        //Get printer and them printservice
        printer = getPrinter(PrinterType.getTypeFromString(printDto.getType()));
        service = getPrintService(printer);

        //Get convertor instance
        Html2ImgConvertor html2ImgConvertor = new Html2ImgConvertor();
        //foreach all data, convert them and print
        for (String data : printDto.getData()) {
            FileInputStream fis = null;
            try {
                fis = html2ImgConvertor.convert(data, printer.getPageHeight(), printer.getPageWidth());
                printImage(fis);
            } catch (IOException e) {
                throw new PrintFailedException("NO_VALID_PRINT_DATA");
            }
        }
        return true;
    }

    /**
     * Check all printers if is ready
     * @return list of results (PRINTER_XX_NOT_SET_UP, PRINTER_XX_READY, PRINTER_XX_SHUTDOWN)
     */
    public List<StatusDto> checkPrinters() {
        List<StatusDto> results = new ArrayList<>();
        List<Printer> printers = new ArrayList<>();
        for (PrinterType printer_type : PRINTER_TYPES) {
            try {
                printers.add(getPrinter(printer_type));
            } catch (NoPrinterException e) {
                StatusDto dto = new StatusDto();
                dto.setType(printer_type.value);
                dto.setStatus("NOT_SET_UP");
                results.add(dto);
            }
        }

        for (Printer printer:printers) {
            if(isPrinterReady(printer)){
                StatusDto dto = new StatusDto();
                dto.setStatus("READY");
                dto.setType(printer.getType().value);
                results.add(dto);
            }else{
                StatusDto dto = new StatusDto();
                dto.setStatus("SHUT_DOWN");
                dto.setType(printer.getType().value);
                results.add(dto);
            }
        }

        return results;
    }

    /**
     * Send image to the printer and print it
     * @param fis Input Stream of converted *.png file
     * @throws PrintFailedException if gone some error at printing
     */
    private void printImage(InputStream fis) throws PrintFailedException {
        DocPrintJob job = service.createPrintJob();
        Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PNG, null);
        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        attrib.add(new Copies(1));
        attrib.add(new MediaPrintableArea(0, 0, printer.getPageWidth(), printer.getPageHeight(), Size2DSyntax.MM));
        try {
            job.print(doc, attrib);
        } catch (PrintException e) {
            throw new PrintFailedException("PRINT_FAILED");
        }
    }

    /**
     * Helpful method to get printer config from config_printer.json based on type
     * @param type ("other", "thermo")
     * @return instance of Printer if found, else exception
     * @throws NoPrinterException this type of printer isn't set, or config file doesn't exist
     */
    private Printer getPrinter(PrinterType type) throws NoPrinterException {
        if(type.equals(PrinterType.UNKNOWN)){
            throw new NoPrinterException("UNKNOWN_TYPE_OF_PRINTER");
        }
        Printer printer = new Printer();
        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("cfg/config_printer.json"));
            JSONArray printers = (JSONArray) jsonObject.get("printers");
            if (printers.isEmpty()) {
                throw new NoPrinterException("NO_PRINTER_SET");
            }
            for (Object object : printers) {
                JSONObject objct = (JSONObject) object;
                if ((objct.get("type")).equals(type.value.toUpperCase())) {
                    printer.setType(type);
                    printer.setName((String) objct.get("name"));
                    printer.setPageHeight((Long) objct.get("height"));
                    printer.setPageWidth((Long) objct.get("width"));
                    return printer;
                }
            }
            throw new NoPrinterException("PRINTER_DOESNT_EXIST");
        } catch (FileNotFoundException ex) {
            throw new NoPrinterException("CONFIG_FILE_DOESNT_EXIST");
        } catch (ParseException | IOException e) {
            throw new NoPrinterException("CONFIG_FILE_NO_VALID");
        }
    }

    /**
     * Helpful method to find print service by name
     * @param printer instance of printer
     * @return instance of PrintService, if exist
     * @throws NoPrinterException printer doesn't exist in this computer
     * @throws PrintFailedException printer is shut down
     */
    private javax.print.PrintService getPrintService(Printer printer) throws NoPrinterException, PrintFailedException {
        if (isPrinterReady(printer)) {
            javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            for (javax.print.PrintService item : services) {
                if (item.getName().equals(printer.getName())) {
                    return item;
                }
            }
            throw new NoPrinterException("PRINTER_DOESNT_EXIST");
        } else {
            throw new PrintFailedException("PRINTER_SHUT_DOWN");
        }

    }

    /**
     * Helpful method to check if printer is ready, method call powershell functions to check this
     * @param printer instance of printer
     * @return true - ready, false - not ready
     */
    private boolean isPrinterReady(Printer printer){
        ProcessBuilder builder;
        String script;
        if(printer.getType().equals(THERMO)){
            script = "get-wmiobject -class win32_printer | Select-Object Name,WorkOffline,PrinterStatus | where {$_.Name -eq '" + printer.getName() + "'}";
            builder = new ProcessBuilder("powershell.exe", script);
        }else{
            builder = new ProcessBuilder("powershell.exe","Get-Printer","-Name","'"+printer.getName()+"'"," | Format-List Name, PrinterStatus");
        }

        Process reg;
        builder.redirectErrorStream(true);
        try {
            reg = builder.start();
            int exitVal = reg.waitFor();
            if(exitVal==0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(reg.getInputStream()));
                String status;
                StringBuilder sb = new StringBuilder();
                while (br.ready()) {
                    sb.append(br.readLine());
                }

                status = sb.toString();
                status = status.replace(printer.getName(), "");
                reg.destroy();
                return switch (printer.getType()) {
                    case THERMO -> status.contains("False");
                    case OTHER -> status.contains("Normal");
                    default -> false;
                };
            }else{
                return false;
            }

        } catch (IOException | InterruptedException e1) {
            return false;
        }
    }

}
