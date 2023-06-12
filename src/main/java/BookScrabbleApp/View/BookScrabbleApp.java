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

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BookScrabbleApp.class.getResource("/BookScrabbleApp.View/welcomeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenSize()[0], screenSize()[1]);
        scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    public static double[] screenSize(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        double[] widthHeight = new double[2];
        widthHeight[0] = bounds.getWidth()-300;
        widthHeight[1] = bounds.getHeight()-100;
        return widthHeight;
    }
}