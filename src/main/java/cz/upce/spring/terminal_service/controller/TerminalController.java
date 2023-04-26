package cz.upce.spring.terminal_service.controller;

import cz.upce.spring.error.TerminalException;
import cz.upce.spring.error.TerminalSettingsException;
import cz.upce.spring.terminal_service.dto.PayDto;
import cz.upce.spring.terminal_service.dto.ResultDto;
import cz.upce.spring.terminal_service.service.TerminalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller of RestApi points to operate with terminal
 */
@RestController
@RequestMapping("/api/v1/terminal")
@CrossOrigin
public class TerminalController {

    private final TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    /**
     * Api point to test terminal connection
     * @return response (OK - terminal connected, TERMINAL_IS_NOT_READY - terminal not connected)
     */
    @GetMapping("/test")
    public ResponseEntity<?> testTerminal() {
        if (terminalService.testConnection()) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("TERMINAL_IS_NOT_READY");
        }
    }

    /**
     * Api point to send pay to terminal
     * @param payDto pay data to send to terminal
     * @return response
     */
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PayDto payDto) {
        try {
            ResultDto dto = terminalService.pay(payDto);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return handleError(ex,"pay");
        }


    }

    /**
     * Method to handle exception
     * @param ex catched exception
     * @return response with error message
     */
    private ResponseEntity<?> handleError(Exception ex,String methodName){
        if(ex instanceof TerminalException || ex instanceof TerminalSettingsException){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } else{
            return ResponseEntity.badRequest().body("BAD_REQUEST");
        }

    }


}
