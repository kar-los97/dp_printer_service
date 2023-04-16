package cz.upce.spring.printer_service.controller;

import cz.upce.spring.error.SettingsException;
import cz.upce.spring.printer_service.dto.PrinterDto;
import cz.upce.spring.printer_service.service.PrinterSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller of RestApi points to operate with printer settings
 */
@RestController
@RequestMapping("/api/v1/print/settings")
public class PrinterSettingsController {

    private final PrinterSettingsService printerSettingsService;

    public PrinterSettingsController(PrinterSettingsService printerSettingsService) {
        this.printerSettingsService = printerSettingsService;
    }

    /**
     * Api point to get Printer settings
     * @return response with data
     */
    @GetMapping
    public ResponseEntity<?> getPrinterSettings(){
        try{
            return new ResponseEntity<>(printerSettingsService.getPrinterSettings(), HttpStatus.OK);
        }catch (Exception ex){
            if(ex instanceof SettingsException){
                return new ResponseEntity<>("SOME_ERROR_AT_CONFIG_FILE",HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }
    }

    /**
     * Api point to get all printers from system
     * @return response with data
     */
    @GetMapping("/printer")
    public ResponseEntity<?> getAllPrinters(){
        return new ResponseEntity<>(printerSettingsService.getAllPrinters(),HttpStatus.OK);
    }

    /**
     * Api point to change settings of printers
     * @param dto data to change
     * @return response with status
     */
    @PostMapping
    public ResponseEntity<?> changeSettings(@RequestBody Set<PrinterDto> dto){
        try{
            return new ResponseEntity<>(printerSettingsService.changeSettings(dto),HttpStatus.OK);
        }catch (Exception ex){
            if(ex instanceof SettingsException){
                return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Api point to delete settings of printers
     * @return response with status
     */
    @DeleteMapping
    public ResponseEntity<?> deleteSettings(){
        try{
            return new ResponseEntity<>(printerSettingsService.deleteSettings(),HttpStatus.OK);

        }catch (Exception ex){
            if(ex instanceof SettingsException){
                return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

    }
}
