package cz.enigoo.printer_service.controller;

import cz.enigoo.printer_service.dto.PrintDto;
import cz.enigoo.printer_service.error.NoPrinterException;
import cz.enigoo.printer_service.error.PrintFailedException;
import cz.enigoo.printer_service.service.EnigooPrintService;
import cz.enigoo.printer_service.utils.MessageType;
import cz.enigoo.printer_service.utils.PrinterLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Controller of RestApi points to operations with printers
 */
@RestController
@RequestMapping("/api/v1/print")
public class PrintController {

    private ServerSocket serverSocket;
    private final EnigooPrintService enigooPrintService;

    public PrintController(EnigooPrintService enigooPrintService) {
        this.enigooPrintService = enigooPrintService;
        try{
            serverSocket = new ServerSocket(6699);
        }catch (IOException ex){

        }

    }

    /**
     * Api point to print data
     * @param dto print data and type of printer
     * @return response
     */
    @PostMapping
    public ResponseEntity<?> print(@RequestBody PrintDto dto){
        try{
            boolean result = enigooPrintService.print(dto);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    /**
     * Api point to get all printers
     * @return response
     */
    @GetMapping("/printers")
    public ResponseEntity<?> getPrinters(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * If somewhere catch the exception, this method return the response with corresponding status
     * @param ex exception
     * @return response
     */
    private ResponseEntity<?> handleError(Exception ex){
        if(ex instanceof NoPrinterException){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
        if(ex instanceof PrintFailedException){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
        PrinterLogger.getInstance().log(ex.getMessage(), MessageType.ERROR);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Check every 30 seconds state of printers and send result to socket
     */
    @Scheduled(cron = "*/30 * * * * *")
    private void checkPrinters() {
        try {
            Socket socket = serverSocket.accept();
            List<String> result = enigooPrintService.checkPrinters();
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            StringBuilder sb = new StringBuilder();
            for (String r : result) {
                sb.append(r).append("\n");
            }
            dout.writeUTF(sb.toString());
        }catch (IOException ex){

        }
    }
}
