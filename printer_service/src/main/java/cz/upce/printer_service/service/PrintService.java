package cz.upce.printer_service.service;

import cz.upce.printer_service.dto.PrintDto;
import cz.upce.printer_service.entity.Printer;
import cz.upce.printer_service.enums.PrinterType;
import cz.upce.printer_service.error.NoPrinterException;
import cz.upce.printer_service.error.PrintFailedException;
import cz.upce.printer_service.utils.Html2ImgConvertor;
import cz.upce.printer_service.utils.MessageType;
import cz.upce.printer_service.utils.PrinterLogger;
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
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to do operations with printers, communicate with
 */
@Service
public class PrintService {

    /**
     * Types of printers
     */
    private final PrinterType []PRINTER_TYPES = {PrinterType.THERMO,PrinterType.OTHER};

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
    public boolean print(PrintDto printDto) throws NoPrinterException, PrintFailedException {

        //Get printer and them printservice
        printer = getPrinter(PrinterType.getTypeFromString(printDto.getType()));
        service = getPrintService(printer);

        //Get convertor instance
        Html2ImgConvertor html2ImgConvertor = Html2ImgConvertor.getInstance();
        //foreach all data, convert them and print
        for (String data : printDto.getData()) {
            InputStream fis = html2ImgConvertor.convert(data, printer.getPageHeight(), printer.getPageWidth());
            printImage(fis);
        }
        return true;
    }

    /**
     * Check all printers if is ready
     * @return list of results (PRINTER_XX_NOT_SET_UP, PRINTER_XX_READY, PRINTER_XX_SHUTDOWN)
     */
    public List<String> checkPrinters() {
        List<String> results = new ArrayList<>();
        List<Printer> printers = new ArrayList<>();
        for(int i = 0; i< PRINTER_TYPES.length;i++){
            try {
                printers.add(getPrinter(PRINTER_TYPES[i]));
            } catch (NoPrinterException e) {
                results.add("PRINTER_"+PRINTER_TYPES[i].value.toUpperCase()+"_NOT_SET_UP");
            }
        }

        for (Printer printer:printers) {
            if(isPrinterReady(printer)){
                results.add("PRINTER_"+printer.getType().value.toUpperCase()+"_READY");
            }else{
                results.add("PRINTER_"+printer.getType().value.toUpperCase()+"_SHUT_DOWN");
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

        job.addPrintJobListener(new PrintJobAdapter() {
            public void printDataTransferCompleted(PrintJobEvent event) {
                PrinterLogger.getInstance().log("PRINT_COMPLETE",MessageType.INFO);
            }

            @Override
            public void printJobFailed(PrintJobEvent pje) {
                PrinterLogger.getInstance().log("PRINT_FAILED",MessageType.WARN);
            }

            public void printJobNoMoreEvents(PrintJobEvent event) {
                PrinterLogger.getInstance().log("RECEIVED_NO_MORE_EVENTS",MessageType.INFO);
            }

        });
        Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PNG, null);
        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        attrib.add(new Copies(1));
        attrib.add(new MediaPrintableArea(0, 0, printer.getPageWidth(), printer.getPageHeight(), Size2DSyntax.MM));
        try {
            job.print(doc, attrib);
        } catch (PrintException e) {
            PrinterLogger.getInstance().log("PRINT_FAILED",MessageType.WARN);
            throw new PrintFailedException("PRINT_FAILED");
        }
    }

    /**
     * Helpful method to get printer config from config.json based on type
     * @param type ("other", "ticket")
     * @return instance of Printer if found, else exception
     * @throws NoPrinterException this type of printer isn't set, or config file doesn't exist
     */
    private Printer getPrinter(PrinterType type) throws NoPrinterException {
        if(type.equals(PrinterType.UNKNOWN)){
            PrinterLogger.getInstance().log("UNKNOWN_TYPE_OF_PRINTER",MessageType.ERROR);
            throw new NoPrinterException("UNKNOWN_TYPE_OF_PRINTER");
        }
        Printer printer = new Printer();
        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("config.json"));
            JSONArray printers = (JSONArray) jsonObject.get("printers");
            if (printers.isEmpty()) {
                PrinterLogger.getInstance().log("NO_PRINTER_SET",MessageType.WARN);
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
            PrinterLogger.getInstance().log("PRINTER_DOESNT_EXIST",MessageType.ERROR);
            throw new NoPrinterException("PRINTER_DOESNT_EXIST");
        } catch (FileNotFoundException ex) {
            PrinterLogger.getInstance().log("CONFIG_FILE_DOESNT_EXIST", MessageType.ERROR);
            throw new NoPrinterException("CONFIG_FILE_DOESNT_EXIST");
        } catch (ParseException | IOException e) {
            PrinterLogger.getInstance().log("CONFIG_FILE_NO_VALID", MessageType.ERROR);
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
        //if (isPrinterReady(printer)) {
            javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            for (javax.print.PrintService item : services) {
                if (item.getName().equals(printer.getName())) {
                    return item;
                }
            }
            PrinterLogger.getInstance().log("PRINTER_DOESNT_EXIST",MessageType.ERROR);
            throw new NoPrinterException("PRINTER_DOESNT_EXIST");
        //} else {
          //  PrinterLogger.getInstance().log("PRINTER_SHUT_DOWN",MessageType.WARN);
            //throw new PrintFailedException("PRINTER_SHUT_DOWN");
        //}

    }

    /**
     * Helpful method to check if printer is ready, method call powershell functions to check this
     * @param printer instance of printer
     * @return true - ready, false - not ready
     */
    private boolean isPrinterReady(Printer printer){
        ProcessBuilder builder;
        if(printer.getType().equals(PrinterType.THERMO)){
            builder = new ProcessBuilder("powershell.exe", "get-wmiobject -class win32_printer | Select-Object Name,WorkOffline,PrinterStatus | where {$_.Name -eq '" + printer.getName() + "'}");
        }else{
            builder = new ProcessBuilder("powershell.exe", "Get-Printer -Name "+printer.getName()+"  | Name,PrinterStatus");
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
