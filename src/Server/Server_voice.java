import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Server_voice {
    public int port = 2500;

    public static AudioFormat getAudioformat() {
        float sampleRate = 8000.0F;
        int sampleSizeInbits = 16;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
    }

    public SourceDataLine audio_out;
    public void init_audio() {
        try {
            AudioFormat format = getAudioformat();
            DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info_out)) {
                System.out.println("not supported");
                System.exit(0);
            }

            audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
            audio_out.open(format);
            audio_out.start();
            Player_thread p = new Player_thread();
            p.din = new DatagramSocket(port);
            p.audio_out = audio_out;
            Server.calling = true;
            p.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
}
