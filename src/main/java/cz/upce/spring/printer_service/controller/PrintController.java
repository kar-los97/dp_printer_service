package cz.upce.spring.printer_service.controller;

import cz.upce.spring.Main;
import cz.upce.spring.error.NoPrinterException;
import cz.upce.spring.error.PrintFailedException;
import cz.upce.spring.printer_service.dto.PrintDto;
import cz.upce.spring.printer_service.dto.StatusDto;
import cz.upce.spring.printer_service.service.PrintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Controller of RestApi points to operations with printers
 */
@RestController
@RequestMapping("/api/v1/print")
@CrossOrigin
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
            return handleError(e,"print");
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
     * Api point to check printers status
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkPrinterStatus(){
        List<StatusDto> status = printService.checkPrinters();
        return new ResponseEntity<>(status,HttpStatus.OK);
    }

    /**
     * If somewhere catch the exception, this method return the response with corresponding status
     * @param ex exception
     * @return response
     */
    private ResponseEntity<?> handleError(Exception ex,String methodName){
        if(ex instanceof NoPrinterException){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
        if(ex instanceof PrintFailedException){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
