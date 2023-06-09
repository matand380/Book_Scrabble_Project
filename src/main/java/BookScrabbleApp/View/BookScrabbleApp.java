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


        FXMLLoader fxmlLoader = new FXMLLoader(BookScrabbleApp.class.getResource("/BookScrabbleApp.View/welcomeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenSize()[0], screenSize()[1]);
        // TODO: 09/06/2023 make the background to work 
        scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    public static double[] screenSize(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        double[] widthHeight = new double[2];
        widthHeight[0] = bounds.getWidth();
        widthHeight[1] = bounds.getHeight();
        return widthHeight;
    }
}