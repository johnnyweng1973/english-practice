import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class demo extends Application {
    private int currentMediaIndex;
    private List<Media> mediaList;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private List<String> dirList;
    ComboBox<String> comboBox;
    ComboBox<String> comboBoxPlayMode;

    ListView<String> listView;
    String localAudioDir = "audio";
    final String PLAY_MODE_PLAY_AND_QUEUE_NEXT = "play clip and queue next";
    final String PLAY_MODE_PLAY_SAME_CLIP = "play same clip";
    final String PLAY_MODE_PLAY_TILL_END = "play till end";

    private void newMedia(Integer index) {
        currentMediaIndex = (index != null)
                            ? index
                            : (currentMediaIndex + 1) % mediaList.size();

        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer(mediaList.get(currentMediaIndex));
        mediaPlayer.setOnPlaying(() -> playButton.setText("Stop"));
        mediaPlayer.setOnEndOfMedia(this::onEndOfMedia);
        mediaPlayer.setOnStopped(this::onStopped);

        listView.refresh();

        System.out.println("end of play media : next index " + currentMediaIndex);
    }

    private void onEndOfMedia() {
        playButton.setText("Play");

        if (PLAY_MODE_PLAY_AND_QUEUE_NEXT.equals(comboBoxPlayMode.getValue())) {
            newMedia(null);
        } else if (PLAY_MODE_PLAY_SAME_CLIP.equals(comboBoxPlayMode.getValue())) {
            // Rewind the media to the beginning
            mediaPlayer.stop();
        } else if (PLAY_MODE_PLAY_TILL_END.equals(comboBoxPlayMode.getValue())) {
            if (currentMediaIndex != 0) {
                newMedia(null);
                mediaPlayer.play();
            }
        }
    }

    private void onStopped() {
        // if we dont choose play and stay, we will go to next clip
        if (!PLAY_MODE_PLAY_SAME_CLIP.equals(comboBoxPlayMode.getValue()))
            newMedia(null);
    }

    private void handleComboBoxEvent(javafx.event.ActionEvent event) {
        String newValue = comboBox.getValue();
        System.out.println("combo value: " + newValue);
        loadMedia(localAudioDir + "/" + newValue);
        currentMediaIndex = 0;

        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer(mediaList.get(currentMediaIndex));
        mediaPlayer.setOnPlaying(() -> playButton.setText("Stop"));
        mediaPlayer.setOnEndOfMedia(this::onEndOfMedia);
        mediaPlayer.setOnStopped(this::onStopped);

        playButton.setText("Play");
    }

    private void loadMedia(String dirString) {
        // Load media files from a directory
        mediaList = new ArrayList<>();
        File dir = new File(dirString);
        File[] files = dir.listFiles();

        if (files == null){
            System.out.println("no media files under this dir ");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                System.out.println("load media file " + file.getName());
                if (file.getName().contains(".txt")) {
                    try {
                        //String fileText = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
                        // Read the contents of the file into an ObservableList
                        ObservableList<String> lines = FXCollections.observableArrayList();
                        //Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).forEach(lines::add);
                        lines.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
                        // Create a ListView and set its items to the ObservableList
                        listView.setItems(lines);
                    } catch (IOException e) {
                        System.out.println("An error occurred while reading the file: " + e.getMessage());
                    }
                    continue;
                }
                Media media = new Media(file.toURI().toString());
                mediaList.add(media);
            }
        }
    }

    private void getSubDirNames(String dirString) {
        // Load media files from a directory
        dirList = new ArrayList<>();
        File dir = new File(dirString);
        File[] files = dir.listFiles();
        if (files == null){
            System.out.println("no media files under this dir ");
            return;
        }

        for (File file : files) {
            if (!file.isFile()) {
                dirList.add(file.getName());
            }
        }
    }

    @Override
    public void start(Stage stage) {
        // Create a new button to play the media file
        playButton = new Button("Play");
        playButton.setPrefWidth(100); // Set the preferred width of the button to 100 pixels
        playButton.setPrefHeight(50); // Set the preferred height of the button to 50 pixels

        comboBox = new ComboBox<>();
        getSubDirNames(localAudioDir);
        comboBox.getItems().addAll(dirList);
        comboBox.setValue(dirList.get(0)); // Set a default value
        comboBox.setOnAction(this::handleComboBoxEvent);

        comboBoxPlayMode = new ComboBox<>();
        comboBoxPlayMode.getItems().addAll(
                PLAY_MODE_PLAY_AND_QUEUE_NEXT,
                PLAY_MODE_PLAY_SAME_CLIP,
                PLAY_MODE_PLAY_TILL_END
        );
        comboBoxPlayMode.setValue(PLAY_MODE_PLAY_AND_QUEUE_NEXT); // Set a default value

        listView = new ListView<>();
        listView.setPrefWidth(350);
        loadMedia(localAudioDir + "/" + dirList.get(0));

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setBackground(null);
                } else {
                    setText(item);

                    // Set the background color based on the item's index
                    int index = getIndex();
                    if (index == 2 * currentMediaIndex || index == 2 * currentMediaIndex + 1) {
                        setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
                    } else {
                        setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
                        //setBackground(null);
                    }
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < listView.getItems().size()) {
                    //Add an event handler to the play button to start playing the media when clicked
                    currentMediaIndex = index / 2;
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        // Remove the onStop event handler
                        mediaPlayer.setOnStopped(null);

                        // Stop the media playback immediately
                        mediaPlayer.stop();
                    }
                    newMedia(currentMediaIndex);
                } else {
                    // The index is out of range, so we don't access the collection
                    System.out.println("Invalid index: " + index);
                }
            }
        });

        HBox hbox = new HBox(10); // Create an HBox with spacing of 10 pixels
        hbox.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 250), null, null)));
        hbox.setAlignment(Pos.CENTER); // Center the buttons horizontally in the HBox
        hbox.setPrefSize(800, 600); // set the size of the scene

        hbox.getChildren().addAll(comboBoxPlayMode, comboBox, playButton, listView);

        // Initialize mediaPlayer
        currentMediaIndex = 0;
        mediaPlayer = new MediaPlayer(mediaList.get(currentMediaIndex));

        //Add an event handler to the play button to start playing the media when clicked
        playButton.setOnAction(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.stop();
            } else {
                mediaPlayer.play();
            }
        });

        mediaPlayer.setOnPlaying(() -> playButton.setText("Stop"));
        mediaPlayer.setOnEndOfMedia(this::onEndOfMedia);
        mediaPlayer.setOnStopped(this::onStopped);

        // Create a new scene with the play button and set it to the stage
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.setTitle("Media Player Demo");
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}

/*
public class Main   {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
*/