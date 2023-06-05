package BookScrabbleApp.View;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import BookScrabbleApp.ViewModel.BS_Host_ViewModel;
import BookScrabbleApp.ViewModel.BS_ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;

public class GUIController implements Observer, Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView imageView;


    List<TileField> hand;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Scrabble Application!!!");
    }

    @Override
    public void initialize (URL url, ResourceBundle rb){

        System.out.println("welcome!");

    }


    @Override
    public void update(Observable o, Object arg) {

    }

    public void initialize(ActionEvent actionEvent) {
    }
}