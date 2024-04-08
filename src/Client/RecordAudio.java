import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

class RecordAudio implements Runnable {

  private AudioFormat format;
  private int duration;
  private ArrayList<Audio> audioList;

  public RecordAudio(
    AudioFormat format,
    int duration,
    ArrayList<Audio> audioList
  ) {
    this.format = format;
    this.duration = duration;
    this.audioList = audioList;
  }

  @Override
  public void run() {
    int bytesRead;
    try {
      // Abrir línea de captura de audio
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
      targetLine.open(format);
      targetLine.start(); // Comenzar la captura de audio
      System.out.println("\nGrabando durante " + duration + " segundos...\n");
      // grabar audio durante t segundos
      byte[] buffer = new byte[targetLine.getBufferSize() / 5];
      long startTime = System.currentTimeMillis();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      while (
        System.currentTimeMillis() -
        startTime <
        TimeUnit.SECONDS.toMillis(duration)
      ) {
        bytesRead = targetLine.read(buffer, 0, buffer.length);
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
      targetLine.stop();
      targetLine.close();
      // Agregar los datos de audio al ArrayList
      //
      //
      //
      Audio a = new Audio(byteArrayOutputStream.toByteArray());
      //
      //
      //

      audioList.add(a);
      byteArrayOutputStream.close();
    } catch (Exception e) {
      // Manejar la excepción si es necesario
      e.printStackTrace();
    }
  }
}
