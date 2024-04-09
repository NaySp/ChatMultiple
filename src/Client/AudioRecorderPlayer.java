import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class AudioRecorderPlayer {
    private int SAMPLE_RATE = 16000; // Frecuencia de muestreo en Hz
    private int SAMPLE_SIZE_IN_BITS = 16; // Tamaño de muestra en bits
    private int CHANNELS = 1; // Mono
    private boolean SIGNED = true; // Muestras firmadas
    private boolean BIG_ENDIAN = false; // Little-endian

    public AudioRecorderPlayer(){
    }

    public byte[] recordAudio(int duration){
        //Iniciar variables y objetos necesarios para definir formato y buffer donde se guardara el audio
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        //iniciar objeto de grabacion de audio
        RecordAudio recorder = new RecordAudio(format, duration,byteArrayOutputStream);
        Thread recorderTrh   = new Thread(recorder);
        recorderTrh.start();
        //esperar a que la grabacion termine
        try{
            recorderTrh.join();
        }catch(Exception e){
            //TODO
        }

        // Reproducir el audio grabado
        byte[] audioData = byteArrayOutputStream.toByteArray();
        // Guardar el audio en un archivo
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("recorded_audio.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioData;
        
    }

    
}
