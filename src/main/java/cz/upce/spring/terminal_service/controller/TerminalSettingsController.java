package cz.upce.spring.terminal_service.controller;

import cz.upce.spring.error.SettingsException;
import cz.upce.spring.error.TerminalSettingsException;
import cz.upce.spring.terminal_service.dto.TerminalDto;
import cz.upce.spring.terminal_service.service.TerminalSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller of RestApi points to operations with terminal settings
 */
@RestController
@RequestMapping("/api/v1/terminal/settings")
public class TerminalSettingsController {

    private final TerminalSettingsService terminalSettingsService;

    public TerminalSettingsController(TerminalSettingsService terminalSettingsService) {
        this.terminalSettingsService = terminalSettingsService;
    }

    /**
     * Api point to get actual terminal settings
     * @return response with data
     */
    @GetMapping
    public ResponseEntity<?>getTerminalSettings(){
        try {
            return ResponseEntity.ok(terminalSettingsService.getTerminalSettings());
        } catch (Exception e) {
            if(e instanceof TerminalSettingsException){
                return ResponseEntity.badRequest().body(e.getMessage());
            }else{
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Api point to change settings of terminal
     * @param dto changed data
     * @return response with status
     */
    @PostMapping
    public ResponseEntity<?>changeSettings(@RequestBody TerminalDto dto){
        try{
            return ResponseEntity.ok(terminalSettingsService.changeSettings(dto));
        }catch (Exception e){
            if(e instanceof TerminalSettingsException){
                return ResponseEntity.badRequest().body(e.getMessage());
            }else{
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Api point to delete settings of terminal
     * @return response with status
     */
    @DeleteMapping
    public ResponseEntity<?>deleteSettings(){
        try{
            return new ResponseEntity<>(terminalSettingsService.deleteSettings(), HttpStatus.OK);

        }catch (Exception ex){
            if(ex instanceof SettingsException){
                return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

}
