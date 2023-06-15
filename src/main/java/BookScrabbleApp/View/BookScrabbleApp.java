package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.IOException;

public class BookScrabbleApp extends Application {
        static final double MIN_WIDTH = 800;
        static final double MIN_HEIGHT = 600;

    /**
     * The start function is the first function that runs when the program starts.
     * It loads in all the necessary files and sets up a scene for them to be displayed on.
     * @param stage to be displayed
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BookScrabbleApp.class.getResource("/BookScrabbleApp.View/welcomeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenSize()[0], screenSize()[1]);
        scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/buttonStyleSheets.css").toExternalForm());
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    /**
     * The screenSize function returns the width and height of the screen in an array.
     * @return An array of doubles that contain the width and height of the screen
     */
    public static double[] screenSize(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        double[] widthHeight = new double[2];
        widthHeight[0] = bounds.getWidth()-300;
        widthHeight[1] = bounds.getHeight()-100;
        return widthHeight;
    }
}