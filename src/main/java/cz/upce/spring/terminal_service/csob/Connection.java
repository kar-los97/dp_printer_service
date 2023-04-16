package cz.upce.spring.terminal_service.csob;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


    public static Socket createConnection(String ipAddress, int port) {
        try {
            return new Socket(ipAddress, port);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Response sendMessage(String ipAddress, int port, byte[] message, Payment payment) throws IOException {
        Socket socket = createConnection(ipAddress, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(message);
        out.flush();

        Response response = waitForResponse(socket);
        if(response.isWantTicket()){
            List<String> merchantRecipes = new ArrayList<>();
            byte [] req = payment.createTicketRequest("tM");
            Response merchResponse;
            boolean next = true;
            while(next){
                out.write(req);
                out.flush();
                merchResponse = waitForResponse(socket);
                merchantRecipes.addAll(merchResponse.getRecipes());
                next = merchResponse.wantNext();
                req = payment.createTicketRequest("t-");
            }
            response.setMerchantRecipe(merchantRecipes);

            List<String> customerRecipes = new ArrayList<>();
            req = payment.createTicketRequest("tC");
            Response custResponse;
            next = true;
            while(next){
                out.write(req);
                out.flush();
                custResponse = waitForResponse(socket);
                customerRecipes.addAll(custResponse.getRecipes());
                next = custResponse.wantNext();
                req = payment.createTicketRequest("t-");
            }
            response.setCustomerRecipe(customerRecipes);
        }

        socket.close();
        return response;
    }

    private static Response waitForResponse(Socket socket) {
        boolean isDone = false;
        Response res = null;
        try {
            while (!isDone) {
                byte[] data = new byte[1024];
                int count = socket.getInputStream().read(data);

                Message message = new Message(bytesToMessage(data));
                Response response = message.process();


                if (response.isDone()) {
                    res = response;
                }
                isDone = response.isDone();

            }

            return res;

        } catch (IOException e) {
            return res;
        }
    }

    private static byte[] bytesToMessage(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int count = 0;
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];

            char[] test = new char[2];
            test[0] = hexChars[j * 2];
            test[1] = hexChars[j * 2 + 1];

            if (new String(test).equals("03")) {
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
