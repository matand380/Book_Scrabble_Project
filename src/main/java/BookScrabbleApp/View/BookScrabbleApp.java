package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.IOException;

public class BookScrabbleApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Get the primary screen
        Screen screen = Screen.getPrimary();

        // Get the bounds of the screen
        Rectangle2D bounds = screen.getBounds();

        // Retrieve the screen size
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        FXMLLoader fxmlLoader = new FXMLLoader(BookScrabbleApp.class.getResource("/BookScrabbleApp.View/welcomeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenWidth, screenHeight);
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