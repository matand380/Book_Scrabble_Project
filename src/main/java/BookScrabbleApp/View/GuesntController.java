package BookScrabbleApp.View;

import BookScrabbleApp.Model.*;
import BookScrabbleApp.ViewModel.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.util.regex.*;

public class GuestController {
    //start method
    @FXML
    private Label invalidIPorPort;
    @FXML
    private TextField IpTextFiled;
    @FXML
    private TextField PortTextFiled;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;

    String ip;
    int port;
    BS_Guest_ViewModel guest;

    @FXML
    public void onPressStart() throws Exception{
        ip = IpTextFiled.getText();
        port = Integer.parseInt(PortTextFiled.getText());
        if (ip.equals("") || port == 0) {
            invalidIPorPort.setText("Please enter IP and Port");
        }else{
            invalidIPorPort.setText("You are connected");
            guest.hostIp.bindBidirectional(IpTextFiled.textProperty());
            guest.hostPort.bindBidirectional(PortTextFiled.textProperty());
            guest.openSocket();
        }
    }

    @FXML
    public void startGameWindow() throws Exception {
        BS_Guest_ViewModel guestViewModel = new BS_Guest_ViewModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tsetViewModel/gameWindow.fxml"));
        root = loader.load();
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        gameWindowController gameWindowController = loader.getController();
        gameWindowController.setViewModel(guestViewModel);

    }
}
