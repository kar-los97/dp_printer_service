package cz.upce.printer_service.controller;

import cz.upce.printer_service.dto.PrintDto;
import cz.upce.printer_service.error.NoPrinterException;
import cz.upce.printer_service.error.PrintFailedException;
import cz.upce.printer_service.service.PrintService;
import cz.upce.printer_service.utils.MessageType;
import cz.upce.printer_service.utils.PrinterLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller of RestApi points to operations with printers
 */
@RestController
@RequestMapping("/api/v1/print")
public class PrintController {

    private final PrintService printService;

    public PrintController(PrintService printService) {
        this.printService = printService;
    }

    /**
     * Api point to print data
     * @param dto print data and type of printer
     * @return response
     */
    @PostMapping
    public ResponseEntity<?> print(@RequestBody PrintDto dto){

        try{
            boolean result = printService.print(dto);
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
}
