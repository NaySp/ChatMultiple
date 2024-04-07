import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    Chatters clientes;

    // Constructor que recibe el socket del cliente y la lista de clientes del chat
    public ClientHandler(Socket socket, Chatters clientes) {
        this.clientSocket = socket;
        this.clientes = clientes;
        try {
            // Se inicializan los flujos de entrada y salida
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Pedir al cliente que ingrese su nombre
            while (true) {
                out.println("SUBMITNAME");
                clientName = in.readLine();
                if (clientName == null) {
                    return;
                }
                synchronized (clientName) {
                    // Verificar si el nombre está disponible y agregar al usuario al chat
                    if (!clientName.isBlank() && !clientes.existeUsr(clientName)) {
                        clientes.broadcastMessage(clientName + " se ha unido al chat.");
                        out.println("NAMEACCEPTED" + clientName);
                        clientes.addUsr(clientName, out);
                        break;
                    }
                }
            }

            String message;
            // Esperar y manejar mensajes de los clientes
            while ((message = in.readLine()) != null) {
                if (message.contains(":")) {
                    handlePrivateMessage(message);
                } else {
                    clientes.broadcastMessage(clientName + ": " + message);
                }

            }

        } catch (IOException e) {
            // Manejar errores de E/S
        } finally {
            try {
                // Cerrar el socket y eliminar al usuario del chat al salir
                clientSocket.close();
                System.out.println(clientName + " ha abandonado el chat.");
                clientes.broadcastMessage(clientName + " ha abandonado el chat.");
                clientes.removeUsr(clientName);
            } catch (IOException e) {
                // Manejar errores al cerrar el socket
            }
        }
    }

    private void handlePrivateMessage(String message) {
        String[] parts = message.split(":", 2);
        String receiver = parts[0].trim();
        String privateMessage = parts[1].trim();
        clientes.sendMessageToUser(clientName, receiver, privateMessage);
    }
}
