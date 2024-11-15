import javax.sound.sampled.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(new File(args[0]));

        AudioFormat format = audio.getFormat();

        BufferedInputStream bufferedAudio = new BufferedInputStream(audio, format.getFrameSize());


        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

        AudioInputStream audioInputStream = new AudioInputStream(pipedInputStream, format, audio.getFrameLength());

        Frequency[] frequencies = new Frequency[]{
                new Frequency(format.getFrameRate() / 1.4 * 2 * Math.PI, 4400),
                new Frequency(format.getFrameRate() / 0.9 * 2 * Math.PI, 2000),
//                new Frequency(format.getFrameRate() / 80000 * 2 * Math.PI, 1000),
                new Frequency(format.getFrameRate() / 0.1 * 2 * Math.PI, 4000),
                new Frequency(format.getFrameRate() / 0.04 * 2 * Math.PI, 11000),
                new Frequency(format.getFrameRate() / 0.05 * 2 * Math.PI, -11000)


        };

        double dim = 2;
        new Thread(() -> {
            try {
                int channels = format.getChannels();
                int sampleSize = format.getSampleSizeInBits();

                long frameLength = audio.getFrameLength();
                System.out.println(frameLength);
                for(long frame = 0; frame < frameLength; frame++) {
                    for(int channel = 0; channel < channels; channel++) {
                        int sample = 0;

                        for(int samplePos = 0; samplePos < sampleSize; samplePos += 8)
                            sample |= bufferedAudio.read() << samplePos;

                        if (sample >> sampleSize - 1 == 1)
                            sample |= -1 << sampleSize;

                        sample /= dim;

                        for(Frequency frequency : frequencies) {
                            sample += frequency.getIntensity(frame);
                        }

                        for(int samplePos = 0; samplePos < sampleSize; samplePos += 8)
                            pipedOutputStream.write(sample >> samplePos);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(args[1]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
