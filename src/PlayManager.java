import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PlayManager {
    GUIManager guiManager;
    public List<String> dirList;
    public int currentMediaIndex;
    public MediaPlayer mediaPlayer;

    // this is the text of dialogue and will be displayed by listview
    public ObservableList<String> lines;

    private List<Media> mediaList;

    final String PLAY_MODE_PLAY_AND_QUEUE_NEXT = "play clip and queue next";
    final String PLAY_MODE_PLAY_SAME_CLIP = "play same clip";
    final String PLAY_MODE_PLAY_TILL_END = "play till end";

    String localAudioDir = "audio";

    public PlayManager() {
        getSubDirNames(localAudioDir);
        loadMedia(localAudioDir + "/" + dirList.get(0));
    }

    public void setGuiManager(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void switchDir(String dir) {
        loadMedia(localAudioDir + "/" + dir);
        newMediaPlayer(0);
    }

    private void loadMedia(String dirString) {
        // Load media files from a directory
        mediaList = new ArrayList<>();
        File dir = new File(dirString);
        File[] files = dir.listFiles();

        if (files == null) {
            System.out.println("no media files under this dir ");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                System.out.println("load media file " + file.getName());
                try {
                    if (file.getName().contains(".txt")) {
                        lines = FXCollections.observableArrayList();
                        lines.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
                    } else {
                        Media media = new Media(file.toURI().toString());
                        mediaList.add(media);
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file: " + e.getMessage());
                } catch (Exception e) {
                    // Handle the exception
                    e.printStackTrace();
                }
            }
        }
    }

    private void getSubDirNames(String dirString) {
        // Load media files from a directory
        dirList = new ArrayList<>();
        File dir = new File(dirString);
        File[] files = dir.listFiles();
        if (files == null) {
            System.out.println("no media files under this dir ");
            return;
        }

        for (File file : files) {
            if (!file.isFile()) {
                dirList.add(file.getName());
            }
        }
    }


    public void newMediaPlayer(Integer index) {
        currentMediaIndex = (index != null)
                ? index
                : (currentMediaIndex + 1) % mediaList.size();

        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer(mediaList.get(currentMediaIndex));
        mediaPlayer.setOnPlaying(() -> guiManager.playButton.setText("Stop"));
        mediaPlayer.setOnEndOfMedia(this::onEndOfMedia);
        mediaPlayer.setOnStopped(this::onStopped);

        guiManager.playListView.refresh();

        System.out.println("end of play media : next index " + currentMediaIndex);
    }

    private void onEndOfMedia() {
        guiManager.playButton.setText("Play");

        if (PLAY_MODE_PLAY_AND_QUEUE_NEXT.equals(guiManager.comboBoxPlayMode.getValue())) {
            newMediaPlayer(null);
        } else if (PLAY_MODE_PLAY_SAME_CLIP.equals(guiManager.comboBoxPlayMode.getValue())) {
            // Rewind the media to the beginning
            mediaPlayer.stop();
        } else if (PLAY_MODE_PLAY_TILL_END.equals(guiManager.comboBoxPlayMode.getValue())) {
            if (currentMediaIndex != 0) {
                newMediaPlayer(null);
                mediaPlayer.play();
            }
        }
    }

    private void onStopped() {
        // if we dont choose play and stay, we will go to next clip
        if (!PLAY_MODE_PLAY_SAME_CLIP.equals(guiManager.comboBoxPlayMode.getValue()))
            newMediaPlayer(null);
    }


}
