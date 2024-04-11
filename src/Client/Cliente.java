import javax.sound.sampled.*;
import java.io.*;
import java.net.*;

public class Cliente {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;
    private static final int PUERTO_VOZ = 55555;

    public static void main(String[] args) {
        try {
            // Conexión al servidor para informar sobre la disponibilidad para recibir llamadas
            Socket servidorSocket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor para recibir llamadas.");

            // Hilo para manejar la recepción de llamadas
            Thread receptorLlamadas = new Thread(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(PUERTO_VOZ);
                    System.out.println("Esperando llamadas entrantes...");

                    Socket clienteSocket = serverSocket.accept();
                    System.out.println("Llamada entrante recibida.");

                    // Stream de audio de entrada
                    AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                    SourceDataLine speaker = AudioSystem.getSourceDataLine(format);
                    speaker.open(format);
                    speaker.start();

                    DataInputStream dis = new DataInputStream(clienteSocket.getInputStream());

                    byte[] buffer = new byte[4096];

                    System.out.println("Recibiendo audio...");

                    while (true) {
                        int bytesRead = dis.read(buffer, 0, buffer.length);
                        speaker.write(buffer, 0, bytesRead);
                    }
                } catch (IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
            });
            receptorLlamadas.start();

            // Envío de solicitud de llamada al servidor
            PrintWriter pw = new PrintWriter(servidorSocket.getOutputStream(), true);
            pw.println("llamada");

            // Captura de audio del micrófono y transmisión al otro cliente
            AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
            TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            // Conexión al otro cliente para transmitir audio
            Socket clienteAudioSocket = new Socket(SERVER_IP, PUERTO_VOZ);
            DataOutputStream dos = new DataOutputStream(clienteAudioSocket.getOutputStream());

            byte[] buffer = new byte[4096];

            System.out.println("Transmitiendo audio...");

            while (true) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                dos.write(buffer, 0, bytesRead);
            }

        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
