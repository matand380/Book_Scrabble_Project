package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class welcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;


    @FXML
    public void switchToHostWindow() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostServerWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void switchToGuestWindow() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/guestWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}