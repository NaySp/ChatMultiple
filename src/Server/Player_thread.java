
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.SourceDataLine;

public class Player_thread extends Thread{
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[1024];
    @Override
    public void run(){
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        int i = 0;;
        while (Server.calling) {
            try {
                din.receive(incoming);
                buffer = incoming.getData();
                audio_out.write(buffer, 0, buffer.length);
                System.out.println("#"+i++);
            } catch (IOException e) {
                e.printStackTrace();
            }  
            
        }
        audio_out.close();
        audio_out.drain();
        System.out.println("stop");
    }    

    
}