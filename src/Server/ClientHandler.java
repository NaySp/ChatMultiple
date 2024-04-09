import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.List;

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
                out.println("Ingresa tu nombre");
                clientName = in.readLine();
                if (clientName == null) {
                    return;
                }
                synchronized (clientName) {
                    // Verificar si el nombre está disponible y agregar al usuario al chat
                    if (!clientName.isBlank() && !clientes.existeUsr(clientName)) {
                        clientes.broadcastMessage(clientName + " se ha unido al chat.");
                        out.println("NOMBRE ACEPTADO: " + clientName);
                        clientes.addUsr(clientName, out, clientSocket);
                        
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
                    handleRecordAudio(message, "grupo");
                    
                  
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
            clientes.addUserToGroup(groupName, new Person(clientName, out, clientSocket));
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

    // Nuevo método para manejar la grabación de audio
    private void handleRecordAudio(String message, String groupName) {
        try {
            // Lógica para grabar el audio desde el socket del cliente y guardar en un archivo
            System.out.println("Grabando audio...");
            InputStream inputStream = this.clientSocket.getInputStream();
            int randomNumber = 1 + new Random().nextInt(100000);
            String fileName = "./db/received_audio" + randomNumber + ".wav";
            System.out.println("Archivo de audio: " + fileName);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }
            bufferedOutputStream.close();

            // Obtener el tipo de envío (grupo o usuario) desde el mensaje
            String sendOption = message.split(" ")[1]; // Cambiado a índice 1
            System.out.println("sendOption: " + sendOption);
            
            if (sendOption.equals("group")) {
                handleGroupAudio(fileName, groupName);
            } else if (sendOption.equals("user")) {
                handleUserAudio(message, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al grabar audio.");
        }
    }


    // Método para manejar el env<ío de audio a un grupo
    private void handleGroupAudio(String fileName, String groupName) {
        Set<Person> groupMembers = clientes.getGroup(groupName);
        try {
            for (Person member : groupMembers) {
                Socket memberSocket = member.getSocket();
                OutputStream outputStream = memberSocket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(fileName);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                bufferedInputStream.close();
                outputStream.close();
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error sending audio to group members.");
        }
    }

    // Método para manejar el envío de audio a un usuario
    private void handleUserAudio(String message, String fileName) {
        String receiver = message.split(" ")[3];
        try {
            clientes.sendAudioToUser(receiver, clientName, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error sending audio to user.");
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
