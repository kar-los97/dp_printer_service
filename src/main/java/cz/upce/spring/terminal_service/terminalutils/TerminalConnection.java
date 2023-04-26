package cz.upce.spring.terminal_service.terminalutils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class that represents connection
 */
public class TerminalConnection {

    /**
     * Helpful array with Hexadecimal chars
     */
    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final int MAX_LENGTH_OF_MESSAGE = 1024;

    /**
     * Method to create new Socket connection
     * @param ipAddress ip address of socket
     * @param port port of socket
     * @return new instance of socket or null if socket cannot be connected
     */
    private Socket createConnection(String ipAddress, int port) {
        try {
            return new Socket(ipAddress, port);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Method to send message to terminal
     * @param ipAddress ip address of terminal
     * @param port of terminal
     * @param message prepared message to send
     * @return instance of ConvertedResponse with result from terminal
     * @throws IOException if some error with sending message throw the socket
     */
    public ConvertedResponse sendMessage(String ipAddress, int port, byte[] message) throws IOException {
        Socket socket = createConnection(ipAddress, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(message);
        out.flush();

        ConvertedResponse convertedResponse = waitForResponse(socket);

        socket.close();
        return convertedResponse;
    }

    /**
     * Method to wait for response, terminal can send progress messages and then result message
     * @param socket opened socket to terminal
     * @return instance of ConvertedResponse or null if no message read
     */
    private ConvertedResponse waitForResponse(Socket socket) {
        boolean isDone = false;
        ConvertedResponse res = null;
        try {
            while (!isDone) {
                byte[] data = new byte[MAX_LENGTH_OF_MESSAGE];
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

    /**
     * Helpful method to convert only important bytes from array to new array
     * @param bytes read byte array
     * @return new byte array
     */
    private byte[] bytesToMessage(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int count = 0;
        String endOfMessage = "03";
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];

            char[] test = new char[2];
            test[0] = hexChars[j * 2];
            test[1] = hexChars[j * 2 + 1];

            if (new String(test).equals(endOfMessage)) {
                count = j;
            }

        }

        byte[] newBytes = new byte[(count + 1)];

        System.arraycopy(bytes, 0, newBytes, 0, newBytes.length);


        return newBytes;
    }

}
