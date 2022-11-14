package cz.enigoo.printer_service.controller;

import cz.enigoo.printer_service.dto.PrinterDto;
import cz.enigoo.printer_service.dto.PrinterSettingsDto;
import cz.enigoo.printer_service.error.NoPrinterException;
import cz.enigoo.printer_service.error.SettingsException;
import cz.enigoo.printer_service.service.EnigooPrinterSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/settings")
public class PrinterSettingsController {

    private final EnigooPrinterSettingsService enigooPrinterSettingsService;

    public PrinterSettingsController(EnigooPrinterSettingsService enigooPrinterSettingsService) {
        this.enigooPrinterSettingsService = enigooPrinterSettingsService;
    }

    @GetMapping
    public ResponseEntity<?> getPrinterSettings(){
        try{
            return new ResponseEntity<>(enigooPrinterSettingsService.getPrinterSettings(), HttpStatus.OK);
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
        return new ResponseEntity<>(enigooPrinterSettingsService.getAllPrinters(),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> changeSettings(@RequestBody Set<PrinterDto> dto){
        try{
            return new ResponseEntity<>(enigooPrinterSettingsService.changeSettings(dto),HttpStatus.OK);
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
            return new ResponseEntity<>(enigooPrinterSettingsService.deleteSettings(),HttpStatus.OK);

        }catch (Exception ex){
            if(ex instanceof SettingsException){
                return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

    }
}
