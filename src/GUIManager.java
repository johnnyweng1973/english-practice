import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class GUIManager {
    PlayManager playManager;

    HBox hbox;
    Button playButton;
    ComboBox<String> comboBoxPlayList;
    ComboBox<String> comboBoxPlayMode;
    ListView<String> playListView;
    ListView<String> recordListView;

    public GUIManager() {
        // Create a new button to play the media file
        playButton = new Button("Play");
        playButton.setPrefWidth(100); // Set the preferred width of the button to 100 pixels
        playButton.setPrefHeight(50); // Set the preferred height of the button to 50 pixels

        comboBoxPlayList = new ComboBox<>();
        comboBoxPlayMode = new ComboBox<>();

        playListView = new ListView<>();
        playListView.setPrefWidth(350);

        recordListView = new ListView<>();
        VBox vbox = new VBox();
        vbox.getChildren().addAll(comboBoxPlayList, comboBoxPlayMode, playButton);


        hbox = new HBox(10); // Create an HBox with spacing of 10 pixels
        hbox.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 250), null, null)));
        hbox.setAlignment(Pos.CENTER); // Center the buttons horizontally in the HBox
        hbox.setPrefSize(800, 600); // set the size of the scene

        hbox.getChildren().addAll(recordListView, vbox, playListView);

    }


    public void setPlayManager(PlayManager playManager) {

        this.playManager = playManager;
    }

    private void handleComboBoxEvent(javafx.event.ActionEvent event) {
        String newValue = comboBoxPlayList.getValue();
        System.out.println("combo value: " + newValue);
        playManager.switchDir(newValue);
        // Create a ListView and set its items to the ObservableList
        playListView.setItems(playManager.lines);
        playButton.setText("Play");
    }

    void init() {

        //Add an event handler to the play button to start playing the media when clicked
        playButton.setOnAction(event -> {
            // start media play, when media end, it will notify us with listener function onMediaEnd we registered
            // we have three choices. 1. move to nex clip but not to play, this will create a new mediaplayer 2. stay at the same but
            // will stop it to rewind it. 3. continue playing until the end of dialogue.
            if (playManager.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                playManager.mediaPlayer.stop();
            } else {
                playManager.mediaPlayer.play();
            }
        });


        comboBoxPlayList.getItems().addAll(playManager.dirList);
        comboBoxPlayList.setValue(playManager.dirList.get(0)); // Set a default value
        comboBoxPlayList.setOnAction(this::handleComboBoxEvent);

        comboBoxPlayMode.getItems().addAll(
                playManager.PLAY_MODE_PLAY_AND_QUEUE_NEXT,
                playManager.PLAY_MODE_PLAY_SAME_CLIP,
                playManager.PLAY_MODE_PLAY_TILL_END
        );
        comboBoxPlayMode.setValue(playManager.PLAY_MODE_PLAY_AND_QUEUE_NEXT); // Set a default value


        playListView.setCellFactory(param -> new ListCell<>() {
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
                    if (index == 2 * playManager.currentMediaIndex || index == 2 * playManager.currentMediaIndex + 1) {
                        setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
                    } else {
                        setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
                    }
                }
            }
        });

        playListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = playListView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < playListView.getItems().size()) {
                    //Add an event handler to the play button to start playing the media when clicked
                    playManager.newMediaPlayer(index / 2);
                } else {
                    // The index is out of range, so we don't access the collection
                    System.out.println("Invalid index: " + index);
                }
            }
        });
        playListView.setItems(playManager.lines);


        playManager.newMediaPlayer(0);

    }


}
