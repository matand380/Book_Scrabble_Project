package BookScrabbleApp.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ScrabbleApplication extends Application  {
//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        primaryStage.setTitle("Hello World");
//        Group root = new Group();
//        Scene scene = new Scene(root, 300, 250);
//        Button btn = new Button();
//        btn.setLayoutX(100);
//        btn.setLayoutY(80);
//        btn.setText("Hello World");
//        btn.setOnAction( actionEvent ->
//                System.out.println("Hello World"));
//        root.getChildren().add(btn);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
@Override
public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ScrabbleApplication.class.getResource("/View/mainGameScreen.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();
}

    public static void main(String[] args) {
    launch(args);
    }

}