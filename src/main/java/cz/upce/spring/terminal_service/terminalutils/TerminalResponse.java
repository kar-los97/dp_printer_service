package cz.upce.spring.terminal_service.terminalutils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalResponse {


    private final byte[] bytes;
    final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final ArrayList<String> messages = new ArrayList<>();

    public TerminalResponse(byte[] message) {
        this.bytes = message;
        createBlockOfMessages();
    }

    public ConvertedResponse process() {
        return new ConvertedResponse(this.messages);
    }

    private void createBlockOfMessages() {

        char[] hexChars = new char[this.bytes.length * 2];
        int lastPosition = 0;

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];

            char[] test = new char[2];
            test[0] = hexChars[j * 2];
            test[1] = hexChars[j * 2 + 1];

            String[] startOfBlock  = {"1C","03","1D"};

            if(Arrays.asList(startOfBlock).contains(test)){
                messages.add(createMessage(j - lastPosition, lastPosition));
                lastPosition = j;
            }
        }
    }


    private String createMessage(int length, int starter) {
        byte[] newBytes = new byte[(length - 1)];

        for (int j = 0; j < newBytes.length; j++) {
            newBytes[j] = this.bytes[j + 1 + starter];
        }

        try{
           return new String(newBytes, "ISO-8859-2");
        }catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
