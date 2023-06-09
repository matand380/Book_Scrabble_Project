package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BookScrabbleApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BookScrabbleApp.class.getResource("/BookScrabbleApp.View/welcomeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1512, 865);
        // TODO: 09/06/2023 make the background to work 
        scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}