import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

class PlayerRecording {
    private AudioFormat format;
    private Audio audio;

    public PlayerRecording(AudioFormat format, Audio audio) {
        this.format = format;
        this.audio = audio;
    }

    public void initiateAudio() {
        try {
            // Obtener un flujo de audio desde los datos del audio
            AudioInputStream in = new AudioInputStream(new ByteArrayInputStream(audio.getData()), format,
                    audio.getData().length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // Abrir línea de salida de audio
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start(); // Comenzar la reproducción de audio

            // Leer y reproducir el audio desde los datos del audio
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            // Detener y cerrar la línea de salida de audio y el flujo de audio
            line.drain();
            line.stop();
            line.close();
            in.close();
        } catch (Exception e) {
            // Manejar la excepción si es necesario
            e.printStackTrace();
        }
    }
}