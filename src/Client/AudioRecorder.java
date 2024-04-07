import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {

  private static final int SAMPLE_RATE = 44100;
  private static final int BUFFER_SIZE = 1024;
  private static final AudioFormat format = null;

  public static byte[] recordAudio() throws LineUnavailableException, IOException {
    AudioFormat format = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        SAMPLE_RATE,
        16,
        2,
        4,
        SAMPLE_RATE,
        false
    );

    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
    line.open();
    line.start();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytesRead;

    while ((bytesRead = line.read(buffer, 0, BUFFER_SIZE)) != -1) {
      baos.write(buffer, 0, bytesRead);
    }

    line.stop();
    line.close();

    return baos.toByteArray();
  }

  public static void main(String[] args) {
    try {
      byte[] audioData = recordAudio();
      
      // Guardar los datos de audio en un archivo (opcional)
      
      // ...
      
      // Reproducir el audio (opcional)
      
      InputStream bais = new ByteArrayInputStream(audioData);

      AudioInputStream ais = new AudioInputStream(bais,format, audioData.length);
      AudioSystem.getClip().open(ais);
      AudioSystem.getClip().start();
      
    } catch (LineUnavailableException ex) {
      Logger.getLogger(AudioRecorder.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(AudioRecorder.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
