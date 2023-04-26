package cz.upce.spring.terminal_service.terminalutils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TerminalConnection {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


    public static Socket createConnection(String ipAddress, int port) {
        try {
            return new Socket(ipAddress, port);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ConvertedResponse sendMessage(String ipAddress, int port, byte[] message, TerminalRequest terminalRequest) throws IOException {
        Socket socket = createConnection(ipAddress, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(message);
        out.flush();

        ConvertedResponse convertedResponse = waitForResponse(socket);

        socket.close();
        return convertedResponse;
    }

    private static ConvertedResponse waitForResponse(Socket socket) {
        boolean isDone = false;
        ConvertedResponse res = null;
        try {
            while (!isDone) {
                byte[] data = new byte[1024];
                int count = socket.getInputStream().read(data);

                TerminalResponse terminalResponse = new TerminalResponse(bytesToMessage(data));
                ConvertedResponse convertedResponse = terminalResponse.process();


                if (convertedResponse.isDone()) {
                    res = convertedResponse;
                }
                isDone = convertedResponse.isDone();

            }

            return res;

        } catch (IOException e) {
            return res;
        }
    }

    private static byte[] bytesToMessage(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int count = 0;
        String startOfMessage = "03";
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];

            char[] test = new char[2];
            test[0] = hexChars[j * 2];
            test[1] = hexChars[j * 2 + 1];

            if (new String(test).equals(startOfMessage)) {
                count = j;
            }

        }

        byte[] newBytes = new byte[(count + 1)];

        for (int j = 0; j < newBytes.length; j++) {
            newBytes[j] = bytes[j];
        }


        return newBytes;
    }

}
