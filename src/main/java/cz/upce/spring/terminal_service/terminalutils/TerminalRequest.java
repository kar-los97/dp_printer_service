package cz.upce.spring.terminal_service.terminalutils;

import java.io.IOException;

public class TerminalRequest {

    private String deviceId;

    public TerminalRequest(String deviceId) {
        this.deviceId = deviceId;
    }


    private byte[] convert(String header, String[] message) throws IOException {
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createTicketRequest(String t) throws IOException{
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createPayment(double price) throws IOException {
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createRefund(double price) throws IOException {
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createCloseTotals() throws IOException{
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createBTmsCall() throws IOException{
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createNTmsCall() throws IOException{
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    public byte[] createHandshake() throws IOException{
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }
}
