package BookScrabbleApp.View;

import BookScrabbleApp.Model.BS_Guest_Model;
import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import BookScrabbleApp.ViewModel.BS_Host_ViewModel;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartScreen implements Initializable {
    BS_Host_ViewModel bsHostModel;
    BS_Guest_ViewModel bsGuestModel;
    @FXML
    private Label welcomeText;
    @FXML
    private Button hostModButton;
    @FXML
    private Button guestModButton;
    Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // welcomeText.setText("Welcome to Scrabble Application!!!");

        // Create a PauseTransition to delay the execution by 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            // After 3 seconds, change the welcomeText label or perform other actions
            welcomeText.setText("Ready to play Scrabble!");
            showMainView();
        });

        // Start the delay
        delay.play();
    }

    public void createHost(ActionEvent actionEvent) {
        bsHostModel = new BS_Host_ViewModel();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/HostScreen.fxml"));
            Parent secondFXML = fxmlLoader.load();
            Scene scene = new Scene(secondFXML);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error with changing screen from start to Host");
        }
    }

    public void createGuest(ActionEvent actionEvent) {
        bsGuestModel = new BS_Guest_ViewModel();
    }

    private void showMainView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/mainGameScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}