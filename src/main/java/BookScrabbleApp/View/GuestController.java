package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.util.*;
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
    @FXML
    private Button submitBtn;
    @FXML
    private Button switchToGame;
    @FXML
    public TextField nameTextFiled;

    public static String name;

    String ip;
    int port;
    BS_Guest_ViewModel guest = new BS_Guest_ViewModel();

    @FXML
    public void onPressSubmit() throws Exception {
        if (nameTextFiled.getText().equals("")) {
            name = "Guest";
        } else {
            name = nameTextFiled.getText();
        }
        guest.setPlayerProperties(name);
        ip = IpTextFiled.getText();
        port = Integer.parseInt(PortTextFiled.getText());
        if (ip.equals("") || port == 0) {
            invalidIPorPort.setText("Please enter IP and Port");
        } else if (!validatePort(String.valueOf(port)) && validateIp(ip)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Problem with ip or port");
            alert.setHeaderText("Ip or port number is not valid");
            alert.setContentText("Please enter a valid ip and port number");
            alert.showAndWait();
        }else {
            boolean connected = true;
            guest.hostIp.bindBidirectional(IpTextFiled.textProperty());
            guest.hostPort.bindBidirectional(PortTextFiled.textProperty());
            try {
                guest.openSocket();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connect Error");
                alert.setHeaderText("Connect Error");
                alert.setContentText("The game server is not connected\n" + "Connect the game server and click submit again");
                alert.showAndWait();
                connected = false;
            }
            if (connected) {
//                invalidIPorPort.setText("Game server connected");
                switchToGame.setVisible(true);
                submitBtn.onActionProperty().set(null);
            }
        }
    }

    @FXML
    public void switchToGameWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameMainWindow.fxml"));
        root = loader.load();
        GameWindowController controller = loader.getController();
        controller.setViewModel(guest);
        stage = (Stage) welcomeText.getScene().getWindow();
        stage.setOnCloseRequest(e -> Platform.exit());
        scene = new Scene(root);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * The validatePort function checks if the port number is valid.
     * @param  port Validate the port number
     * @return A boolean value
     */
    private boolean validatePort(String port) {
        // Regular expression for port number (1-65535)
        String portRegex = "^([1-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
        // Validate port number
        return Pattern.matches(portRegex, port);
    }

    /**
     * The validateIp function takes a String as an argument and returns true if the string is a valid IPv4 address.
     * @param  ip Pass the ip address to be validated
     * @return A boolean value
     */
    private boolean validateIp(String ip) {
        // Regular expression for IPv4 address
        String ipv4Regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        // Validate IP address
        return Pattern.matches(ipv4Regex, ip);
    }
}
