import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Set;
import javax.sound.sampled.*;
import java.io.IOException;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;
    private static int AUDIO_PORT_RECEIVER;
    private static int AUDIO_PORT_SENDER;
    private static AudioRecorderPlayer recorderPlayer = new AudioRecorderPlayer();

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            System.out.println("Connected to the server.");

            // Generate available ports for audio sender and receiver
            AUDIO_PORT_RECEIVER = findAvailablePort();
            AUDIO_PORT_SENDER = findAvailablePort();

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            OutputStream outputStream = socket.getOutputStream();

            Thread readerThread = new Thread(new Receiver(in, socket));
            readerThread.start();

            Thread senderThread = new Thread(new Sender(userInput, out, outputStream));
            senderThread.start();

            readerThread.join(); // Wait for the receiver thread to finish
            senderThread.join(); // Wait for the sender thread to finish

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    static class Receiver implements Runnable {
        private BufferedReader in;
        private Socket socket;

        public Receiver(BufferedReader in, Socket socket) {
            this.in = in;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                String receivedMessage;
                while ((receivedMessage = in.readLine()) != null) {
                    if (receivedMessage.equals("audio")) {
                        // Receive audio
                        receiveAudio();
                    } else if(receivedMessage.equals("CALL")){
                        System.out.println("Llamada empezada!");
                        // Listen for the ip
                        String ip = in.readLine();
                        // Start the call
                        Set<String> ips = Set.of(ip);
                        Thread callSenderThread = new Thread(new CallSender(ips));
                        callSenderThread.start();
                        Thread callReceiverThread = new Thread(new CallReceiver());
                        callReceiverThread.start();
                    } else {
                        // Print other messages
                        System.out.println(receivedMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void receiveAudio() throws IOException {
            InputStream inputStream = socket.getInputStream();
            int randomNumber = 1 + new Random().nextInt(100000);
            String fileName = "./Client/audios/received_audio" + randomNumber + ".wav";
            System.out.println("Audio file: " + fileName);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                new FileOutputStream(fileName));

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                // Check if the file transfer is complete
                if (inputStream.available() == 0) {
                    break;
                }
            }
            bufferedOutputStream.close();
            System.out.println("Has recibido un audio, lo encontraras en la carpeta audios bajo el nombre: received_audio" + randomNumber + ".wav");
        }
    }

    static class Sender implements Runnable {
        private BufferedReader userInput;
        private PrintWriter out;
        private OutputStream outputStream;

        public Sender(BufferedReader userInput, PrintWriter out, OutputStream outputStream) {
            this.userInput = userInput;
            this.out = out;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = userInput.readLine()) != null) {
                    if (message.startsWith("/record")) {
                        // Record audio and send
                        recordAndSendAudio(message);
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void recordAndSendAudio(String message) throws IOException {
            int duration = Integer.parseInt(message.split(" ")[1]);
            recorderPlayer.recordAudio(duration);
            out.println(message);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("recorded_audio.wav"));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            bufferedInputStream.close();

            Path audioFilePath = Paths.get("recorded_audio.wav");
            Files.deleteIfExists(audioFilePath);
        }
    }

    static class CallSender implements Runnable {
        private Set<String> ips;

        public CallSender(Set<String> ips) {
            this.ips = ips;
        }

        @Override
        public void run() {
            try {
                // Initialize audio capture
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                DatagramSocket socket = new DatagramSocket(AUDIO_PORT_SENDER);
                byte[] buffer = new byte[1024];
                while (true) {
                    // Capture audio
                    int bytesRead = line.read(buffer, 0, buffer.length);
    
                    // Send audio to all IPs
                    for (String ip : ips) {
                        InetAddress address = InetAddress.getByName(ip);
                        DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, AUDIO_PORT_RECEIVER);
                        socket.send(packet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class CallReceiver implements Runnable {
        @Override
        public void run() {
            try {
                // Initialize audio playback
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
    
                // Initialize network socket
                DatagramSocket socket = new DatagramSocket(AUDIO_PORT_RECEIVER);
    
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    // Play received audio
                    line.write(packet.getData(), 0, packet.getLength());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
