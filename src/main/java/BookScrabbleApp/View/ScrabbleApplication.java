package BookScrabbleApp.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ScrabbleApplication extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
       try{
           FXMLLoader fxmlLoader = new FXMLLoader(ScrabbleApplication.class.getResource("/View/startScreen.fxml"));
           Scene scene = new Scene(fxmlLoader.load(), 600, 600);

           StartScreen startScreen = fxmlLoader.getController();
           startScreen.setStage(stage); // Set the stage object

           stage.setScene(scene);
           stage.show();
       }
       catch (Exception e) {
           System.out.println("ec");
       }
    }

}