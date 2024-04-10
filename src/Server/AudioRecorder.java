import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {
    private static int SAMPLE_RATE = 16000; // Frecuencia de muestreo en Hz
    private static int SAMPLE_SIZE_IN_BITS = 16; // Tamaño de muestra en bits
    private static int CHANNELS = 1; // Mono
    private static boolean SIGNED = true; // Muestras firmadas
    private static boolean BIG_ENDIAN = false; // Little-endian
    private AudioFormat format;
    private ByteArrayOutputStream byteArrayOutputStream;
    private String audioFolderPath = "audios/"; // Ruta de la carpeta de audios

    public AudioRecorder() {
        this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        createAudioFolder();
    }

    private void createAudioFolder() {
        Path path = Paths.get(audioFolderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println("Directorio de audios creado: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error al crear el directorio de audios: " + e.getMessage());
            }
        }
    }

    public ByteArrayOutputStream recordAudio() {
        int duration = 15; // Duración de la grabación en segundos

        // Iniciar objeto de grabación de audio
        RecordAudio recorder = new RecordAudio(format, duration, byteArrayOutputStream);
        Thread recorderThread = new Thread(recorder);
        recorderThread.start();

        // Esperar a que la grabación termine
        System.out.println("Grabando...");
        try {
            recorderThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Grabación terminada");

        // Guardar el audio en un archivo
        String fileName = getAudioFileName();
        saveAudioToFile(fileName, byteArrayOutputStream.toByteArray());

        return byteArrayOutputStream;
    }

    public void reproduceAudio(ByteArrayOutputStream byteArrayOutputStream) {
        // Reproducir el audio grabado
        byte[] audioData = byteArrayOutputStream.toByteArray();
        PlayerRecording player = new PlayerRecording(format);
        player.initiateAudio(audioData);
    }

    private String getAudioFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return audioFolderPath + "audio_" + now.format(formatter) + ".wav";
    }

    private void saveAudioToFile(String fileName, byte[] audioData) {
        try {
            File audioFile = new File(fileName);
            FileOutputStream fos = new FileOutputStream(audioFile);
            fos.write(audioData);
            fos.close();
            System.out.println("Audio guardado en: " + audioFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}