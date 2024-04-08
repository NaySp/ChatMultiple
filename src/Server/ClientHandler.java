import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

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
                    handleCreateGroup(message);
                } else if (message.startsWith("/join")) {
                    handleJoinGroup(message);
                } else if (message.startsWith("/group")) {
                    handleGroupMessage(message);
                } else if (message.startsWith("/leaveGroup")) {
                    handleLeaveGroup(message);

                } else if (message.contains(":")) {
                    handlePrivateMessage(message);
                } else if(message.startsWith("/recordAudio")){
                    handleRecordAudio();
                } else if(message.startsWith("/calling")){
                    new Thread(() -> {
                        try {
                          playCall();
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      })
                        .start();

                } else {
                    clientes.broadcastMessage(clientName + ": " + message);
                }
            }
        } catch (IOException e) {
            // Manejar errores de E/S
        } catch (Exception e) {
            e.printStackTrace();
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

    private void handleJoinGroup(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.addUserToGroup(groupName, new Person(clientName, out));
            out.println("Te has unido al grupo '" + groupName + "'.");
        }
    }

    private void handleGroupMessage(String message) {
        String[] parts = message.split(" ", 3);
        if (parts.length >= 3) {
            String groupName = parts[1];
            String groupMessage = parts[2];
            clientes.sendMessageToGroup(groupName, clientName + ": " + groupMessage);
            out.println("Mensaje enviado al grupo '" + groupName + "'.");
        }
    }

    private void handleCreateGroup(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.createGroup(groupName);
            out.println("Grupo '" + groupName + "' creado correctamente.");
        }
    }

    private void handleLeaveGroup(String message) {
        String[] parts = message.split(" ");
        if (parts.length >= 2) {
            String groupName = parts[1];
            clientes.removeUserFromGroup(groupName, clientName);
        }
    }

    private void handlePrivateMessage(String message) {
        String[] parts = message.split(":", 2);
        String receiver = parts[0].trim();
        String privateMessage = parts[1].trim();
        clientes.sendMessageToUser(clientName, receiver, privateMessage);
    }

    private void handleRecordAudio(){
        AudioRecorderPlayer recorderPlayer = new AudioRecorderPlayer();
        recorderPlayer.record();
        Audio audio = recorderPlayer.getAudioToSend();
        //Solicitar al remitente del mensaje el destinatario del audio
        out.println("Por favor, introduce el nombre del destinatario del audio:");
        try {
            // Obtener el nombre del destinatario del audio
            String recipientName = in.readLine(); // Corrected here
            // Obtener el PrintWriter del destinatario del audio
            PrintWriter recipientOut = clientes.getUserStream(recipientName);
            if (recipientOut != null) {
                // Enviar solicitud de reproducción al destinatario del audio
                recipientOut.println(clientName + " te ha enviado un audio. ¿Deseas reproducirlo? (y/n)");
                // Esperar la respuesta del destinatario
                String response = in.readLine(); // Corrected here
                if (response.equalsIgnoreCase("y")) {
                    // Si el destinatario desea reproducir el audio, entonces reproducirlo
                    recorderPlayer.play(audio);
                } else {
                    // Si el destinatario no desea reproducir el audio, no hacer nada
                    out.println("Audio no reproducido por el destinatario.");
                }
            } else {
                // Si el destinatario no está en línea, informar al remitente
                out.println("El destinatario no está en línea o no existe.");
            }
        } catch (IOException e) {
            // Manejar errores de E/S
            e.printStackTrace();
        }
    
    }
    

    private void playCall() throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(6789);
        System.out.println("\nServer started. Waiting for clients...\n");
    
        // Configurar la línea de audio para reproducir el audio recibido
        AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 2, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(
          SourceDataLine.class,
          audioFormat
        );
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(
          dataLineInfo
        );
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
    
        byte[] receiveData = new byte[1024];
    
        while (true) {
          DatagramPacket receivePacket = new DatagramPacket(
            receiveData,
            receiveData.length
          );
          serverSocket.receive(receivePacket);
    
          // Reproducir audio
          sourceDataLine.write(
            receivePacket.getData(),
            0,
            receivePacket.getLength()
          );
        }
      }

}
