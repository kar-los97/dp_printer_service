import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        while(true){
            try {
                Socket socket = new Socket("localhost", 6699);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String input = dis.readUTF();
                System.out.println(input);
            }catch (IOException ex){
            }
        }
    }
}
