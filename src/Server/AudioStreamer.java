import javax.sound.sampled.*;

public class AudioStreamer {
    private TargetDataLine line;
    private AudioFormat audioFormat;

    public AudioStreamer() {
        this.audioFormat = getAudioFormat();
    }

    public void startStreaming() {
        try {
            line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat);
            line.start();

            byte[] buffer = new byte[1024];
            System.out.println("Streaming audio...");

            // Reproducir la voz capturada localmente
            SourceDataLine speaker = AudioSystem.getSourceDataLine(audioFormat);
            speaker.open(audioFormat);
            speaker.start();

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                speaker.write(buffer, 0, bytesRead);
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}

