
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.TargetDataLine;

public class Recorder_thread extends Thread{
    public TargetDataLine audio_in = null;
    public DatagramSocket dout;
    byte byte_buff[] = new byte[1024];
    public InetAddress server_ip;
    public int server_port;
    @Override
    public void run(){
        int i=0;
        while (Client.calling) {
            try{
                audio_in.read(byte_buff, 0, byte_buff.length);
                DatagramPacket data = new DatagramPacket(byte_buff, byte_buff.length, server_ip, server_port);
                System.out.println("send #"+i++);
                dout.send(data);
            }catch (Exception ex){
                Logger.getLogger(Recorder_thread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        audio_in.close();
        audio_in.drain();
        System.out.println("Thread stop");
    }
    
}