package cz.upce.printer_service.controller;

import cz.upce.printer_service.dto.PrinterDto;
import cz.upce.printer_service.error.SettingsException;
import cz.upce.printer_service.service.PrinterSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/settings")
public class PrinterSettingsController {

    private final PrinterSettingsService printerSettingsService;

    public PrinterSettingsController(PrinterSettingsService printerSettingsService) {
        this.printerSettingsService = printerSettingsService;
    }

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

    @GetMapping("/printer")
    public ResponseEntity<?> getAllPrinters(){
        return new ResponseEntity<>(printerSettingsService.getAllPrinters(),HttpStatus.OK);
    }

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
