import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static Set<PrintWriter> writers = new HashSet<>();
    public static boolean calling = false;
    public static Server_voice sv = new Server_voice(); 

    public static void main(String[] args) {

        sv.init_audio();        
        int PORT = 6789;
        Chatters clientes = new Chatters();

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado. Esperando clientes...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket);
                // crea el objeto Runable
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientes);
                // inicia el hilo con el objeto Runnable
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    


}
