import javax.sound.sampled.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(new File(args[0]));
        byte[] buffer = audio.readAllBytes();

        double scale = .5F;
        short add = 12700;

        short prevAvg = 0;

        for(int bufferIndex = 0; bufferIndex < buffer.length; bufferIndex += 2) {
            short frame = (short) (((buffer[bufferIndex+1] & 0xff) << 8) | (buffer[bufferIndex] & 0xff));

            prevAvg = (short) ((prevAvg * 199 + Math.abs(frame)) / 200);

            if(Math.abs(prevAvg) < 10 && Math.abs(frame) < 127) {
                frame = 0;
                add *= -1;
            }

            frame *= scale;
            frame += add;
//            System.out.println(buffer[bufferIndex]);
            buffer[bufferIndex+1] = (byte) (frame >> 8);
//            buffer[bufferIndex] *= scale;
            buffer[bufferIndex] = (byte) frame;
        }
        AudioInputStream newAudio = new AudioInputStream(new ByteArrayInputStream(buffer), audio.getFormat(), buffer.length);
        AudioSystem.write(newAudio, AudioFileFormat.Type.WAVE, new File(args[1]));
    }
}
