import javax.sound.sampled.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(new File(args[0]));
        float sampleRate = audio.getFormat().getSampleRate();
        double pulseRate = 0.07;
        double secondaryRate = 0.04;

//        double x = 0;

//        double xMaxRate = 60;

//        double xRate = xMaxRate;

//        double highPulseRate = 0.00003571428;
        byte[] buffer = audio.readAllBytes();

        double scale = .25F;
        short addRange = 15400;
//        short add = addRange;
//        int addInterpolate = add;

        int flipStartCooldown = 60000;
        int flipcooldown = 0;

        short prevAvg = 0;

        for(int bufferIndex = 0; bufferIndex < buffer.length; bufferIndex += 2) {
            short frame = (short) (((buffer[bufferIndex+1] & 0xff) << 8) | (buffer[bufferIndex] & 0xff));

//            x += xRate;

//            xRate = (xRate * 2000 + Math.random() * xMaxRate) / 20001;

//            addInterpolate = (addInterpolate * 10000 + add) / 10001;

            prevAvg = (short) ((prevAvg * 199 + Math.abs(frame)) / 200);

//            flipcooldown--;
//
//            if(Math.abs(prevAvg) < 10 && Math.abs(frame) < 127) {
//                if(flipcooldown < 0) {
//                    add = (short) ((Math.random() - 0.5) * 2 * addRange);
////                    frame = add;
//                    flipcooldown = flipStartCooldown;
//                }
//            }

            frame *= scale;
//            frame += addInterpolate;
            frame += (Math.sin(bufferIndex*2/sampleRate/pulseRate) + Math.sin(bufferIndex*2/sampleRate/secondaryRate))/2 * addRange;
//            System.out.println(buffer[bufferIndex]);
            buffer[bufferIndex+1] = (byte) (frame >> 8);
//            buffer[bufferIndex] *= scale;
            buffer[bufferIndex] = (byte) frame;
        }
        AudioInputStream newAudio = new AudioInputStream(new ByteArrayInputStream(buffer), audio.getFormat(), buffer.length);
        AudioSystem.write(newAudio, AudioFileFormat.Type.WAVE, new File(args[1]));
    }
}
