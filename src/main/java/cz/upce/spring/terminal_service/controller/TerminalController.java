package cz.upce.spring.terminal_service.controller;

import cz.upce.spring.error.TerminalException;
import cz.upce.spring.error.TerminalSettingsException;
import cz.upce.spring.terminal_service.dto.PayDto;
import cz.upce.spring.terminal_service.dto.RefundDto;
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
     * Api point to send refund to terminal
     * @param refundDto refund data
     * @return response
     */
    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody RefundDto refundDto) {
        try{
            ResultDto dto = terminalService.refund(refundDto);
            return ResponseEntity.ok(dto);
        }catch (Exception ex){
            return handleError(ex,"refund");
        }

    }

    /**
     * Api point to close totals on terminal
     * @return response
     */
    @GetMapping("/close-totals")
    public ResponseEntity<?> closeTotals(){
        try{
            ResultDto dto = terminalService.closeTotals();
            return ResponseEntity.ok(dto);
        }catch (Exception ex){
            return handleError(ex,"close-totals");
        }
    }

    @GetMapping("/handshake")
    public ResponseEntity<?> handshake(){
        try{
            ResultDto dto = terminalService.handshake();
            return ResponseEntity.ok(dto);
        }catch (Exception ex){
            return handleError(ex,"handshake");
        }
    }

    @GetMapping("/tms-b-call")
    public ResponseEntity<?> tmsBCall(){
        try{
            ResultDto dto = terminalService.tmsBCall();
            return ResponseEntity.ok(dto);
        }catch (Exception ex){
            return handleError(ex,"tms-b-call");
        }
    }

    @GetMapping("/tms-n-call")
    public ResponseEntity<?> tmsNCall(){
        try{
            ResultDto dto = terminalService.tmsNCall();
            return ResponseEntity.ok(dto);
        }catch (Exception ex){
            return handleError(ex,"tms-n-call");
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
