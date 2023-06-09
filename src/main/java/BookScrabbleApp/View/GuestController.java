package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

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
        // Get the primary screen
        Screen screen = Screen.getPrimary();

        // Get the bounds of the screen
        Rectangle2D bounds = screen.getBounds();

        // Retrieve the screen size
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        BS_Guest_ViewModel guestViewModel = new BS_Guest_ViewModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameWindow.fxml"));
        root = loader.load();
        stage = (Stage) welcomeText.getScene().getWindow();
        stage.setOnCloseRequest(e -> Platform.exit());
        scene = new Scene(root,screenWidth,screenHeight);
        stage.setScene(scene);
        stage.show();
        GameWindowController gameWindowController = loader.getController();
        gameWindowController.setViewModel(guestViewModel);
    }
}
