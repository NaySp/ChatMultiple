import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

public class AudioRecorderPlayer {
    private static int SAMPLE_RATE = 16000; // Frecuencia de muestreo en Hz
    private static int SAMPLE_SIZE_IN_BITS = 16; // Tamaño de muestra en bits
    private static int CHANNELS = 1; // Mono
    private static boolean SIGNED = true; // Muestras firmadas
    private static boolean BIG_ENDIAN = false; // Little-endian
    private static AudioFormat format;
    private ArrayList<Audio> audioList;

    public AudioRecorderPlayer() {
        this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        this.audioList = new ArrayList<>();
    }

    public void record() {
        // Iniciar variables y objetos necesarios para definir formato y buffer donde se
        // guardara el audio
        int duration = 5; // Cuántos segundos vamos a grabar?

        // Iniciar objeto de grabacion de audio
        RecordAudio recorder = new RecordAudio(format, duration, audioList);
        Thread recorderTrh = new Thread(recorder);
        recorderTrh.start();

        // Esperar a que la grabacion termine
        try {
            recorderTrh.join();
        } catch (Exception e) {
            // Manejar la excepción si es necesario
            e.printStackTrace();
        }
    }

    public Audio getAudioToSend() {
        return audioList.get(audioList.size() - 1);
    }

    public void play(Audio audio) {
        PlayerRecording player = new PlayerRecording(format, audio);
        player.initiateAudio();
    }
}
