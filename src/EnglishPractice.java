import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EnglishPractice extends Application {
    GUIManager guiManager;
    RecordManager recordManager;
    PlayManager playManager;

     public EnglishPractice(){
        System.out.println("constructor is called");
         // Initialize mediaPlayer
         playManager = new PlayManager();

         // Initialize guiManager
         guiManager = new GUIManager();

         // Initialize recordManager
         recordManager = new RecordManager(this);

         playManager.setGuiManager(guiManager);
         guiManager.setPlayManager(playManager);

         guiManager.init();
    }

    @Override
    public void start(Stage stage) {

        // initialization procedure
        // 1. new playmanager, in its construtor, it will read directry list
        // 2. new GUIManager, do nothing
        // 3. playNanager and guiNanager pass its own reference to each other
        // 4. initialize GUIManager. mediaplayer from playermanager will be initialized here
        // because it needs to reference functions from playbutton and a listview



        // Create a new scene with the play button and set it to the stage
        Scene scene = new Scene(guiManager.hbox);
        stage.setScene(scene);
        stage.setTitle("English Practice");
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