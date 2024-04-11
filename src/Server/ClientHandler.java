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
                if (message.startsWith("/createGroup")) {
                    crearGrupo(message);
                } else if (message.startsWith("/join")) {
                    unirseGrupo(message);
                } else if (message.startsWith("/group")) {
                    envioMensajeGrupo(message);
                } else if (message.startsWith("/leaveGroup")) {
                    salirDelGrupo(message);
                }else if (message.startsWith("/recordAudio")) {
                    String[] parts = message.split(" ", 2);
                    String target = parts[1];
                    if (target.startsWith("group")) {
                        String groupName = target.substring(1);
                        clientes.mandarMensajeVozGrupo(groupName, clientName);
                    } else if (target.startsWith("-")) {
                        String receiverUser = target.substring(1);
                        clientes.mandarMensajeVozPrivado(clientName, receiverUser);
                    }
                } else if (message.contains(":")) {
                    envioMensajePrivado(message);
                } else {
                    clientes.broadcastMessage(clientName + ": " + message);
                }
            }
        } catch (IOException e) {
           
        } finally {
            try {
               
                clientSocket.close();
                System.out.println(clientName + " ha abandonado el chat.");
                clientes.broadcastMessage(clientName + " ha abandonado el chat.");
                clientes.removeUsr(clientName);
            } catch (IOException e) {
            
            }
        }
    }

    private void unirseGrupo(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.addUserToGroup(groupName, new Person(clientName, out));
            out.println("Te has unido al grupo '" + groupName + "'.");
        }
    }


    private void crearGrupo(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.crearGrupo(groupName);
            out.println("Grupo '" + groupName + "' creado correctamente.");
        }
    }
    private void envioMensajeGrupo(String message) {
        String[] parts = message.split(" ", 3);
        if (parts.length >= 3) {
            String groupName = parts[1];
            String groupMessage = parts[2];
            clientes.enviarMensajeAlGrupo(groupName, clientName + ": " + groupMessage);
            out.println("Mensaje enviado al grupo '" + groupName + "'.");
        }
    }

    private void salirDelGrupo(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.removeUserFromGroup(groupName, clientName);
        }
    }

    private void envioMensajePrivado(String message) {
        String[] parts = message.split(":", 2);
        String receiver = parts[0].trim();
        String privateMessage = parts[1].trim();
        clientes.sendMessageToUser(clientName, receiver, privateMessage);
    }


    
}
