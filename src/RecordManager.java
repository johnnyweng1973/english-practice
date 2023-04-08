import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.sound.sampled.*;

public class RecordManager {
    // for recording feature
    private AudioFormat format;
    private TargetDataLine microphone;
    private Thread recordingThread;
    private boolean isRecording = false;
    EnglishPractice mainModule;

    public RecordManager(EnglishPractice englishPractice){
        mainModule = englishPractice;
    }

    private void startRecording() {
        format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            return;
        }

        try {
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        recordingThread = new Thread(() -> {
            isRecording = true;
            AudioInputStream audioStream = new AudioInputStream(microphone);
            try {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new java.io.File("output.wav"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        recordingThread.start();
    }

    private void stopRecording() {
        isRecording = false;
        microphone.stop();
        microphone.close();
    }

}
