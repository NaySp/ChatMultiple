
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class Lector implements Runnable {
    public int port_server  = 2500;
    public String add_server = "192.168.70.145";
    public static AudioFormat getAudioformat(){
        float sampleRate = 8000.0F;
        int sampleSizeInbits = 16;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
    }
    TargetDataLine audio_in;

    private BufferedReader in;

    // Constructor que recibe un BufferedReader para leer mensajes del servidor
    public Lector(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            // Leer mensajes del servidor línea por línea y mostrar en pantalla
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            // Manejar cualquier excepción de E/S imprimiendo la traza de la pila
            e.printStackTrace();
        } finally {
            // Cerrar el flujo de entrada una vez que se termina de leer
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void init_audio(){
        try{
            AudioFormat format = getAudioformat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if(!AudioSystem.isLineSupported(info)){
                System.out.println("not supported");
                System.exit(0);
            }
            audio_in = (TargetDataLine) AudioSystem.getLine(info);
            audio_in.open(format);
            audio_in.start();
            Recorder_thread r = new Recorder_thread();
            InetAddress inet = InetAddress.getByName(add_server);
            r.audio_in = audio_in;
            r.dout = new DatagramSocket();
            r.server_ip = inet;
            r.server_port = port_server;
            
            r.start();
        }catch(LineUnavailableException | UnknownHostException | SocketException ex){
            ex.printStackTrace();
        }

    }
   

}
