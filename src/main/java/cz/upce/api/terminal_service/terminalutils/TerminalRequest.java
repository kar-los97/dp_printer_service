package cz.upce.api.terminal_service.terminalutils;


/**
 * Class that represent terminal request
 */
public class TerminalRequest {
    /**
     * Unique terminalID assigned from bank
     */
    private String terminalId;

    public TerminalRequest(String terminalId) {
        this.terminalId = terminalId;
    }


    /**
     * Helpful method to convert header and messages to byte array, that can be accepted for terminal protocol
     * @param header array of headers (header contain date, terminalId, etc.)
     * @param message array of messages (price to pay, order id, etc.)
     * @return new byte array of converted
     */
    private byte[] convert(String header, String[] message){
        //Podléhá interním předpisům bankovní společnosti
        return new byte[0];
    }

    /**
     * Method to create payment request
     * @param price to pay
     * @return new byte array with request
     */
    public byte[] createPayment(double price) {
        //Podléhá interním předpisům bankovní společnosti
        return convert("header",new String[0]);
    }
}
