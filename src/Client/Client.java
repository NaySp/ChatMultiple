import java.io.*;
import java.net.*;

public class Client {
    // Dirección IP y puerto del servidor
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;
    public static boolean calling;

    public static void main(String[] args) {
        try {
            // Establecer conexión con el servidor
            Socket socket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor.");

            // Preparar para leer y escribir mensajes
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Leer mensajes del servidor
            String message;
            while ((message = in.readLine()) != null) {
                // Esperar hasta recibir un comando del servidor
                if (message.startsWith("SUBMITNAME")) {
                    System.out.print("Ingrese nombre de usuario: ");
                    String name = userInput.readLine();
                    out.println(name);
                } else if (message.startsWith("NAMEACCEPTED")) {
                    System.out.println("Nombre aceptado!!");
                    break;
                }
            }

            // Iniciar hilo para leer mensajes del servidor en segundo plano
            Lector lector = new Lector(in);
            new Thread(lector).start();

            // Leer mensajes del usuario y enviar al servidor
            while ((message = userInput.readLine()) != null) {
                if (message.startsWith("/createGroup")) {
                    out.println(message);
                } else if (message.startsWith("/leaveGroup")) {
                    out.println(message);
                } else {
                    out.println(message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
