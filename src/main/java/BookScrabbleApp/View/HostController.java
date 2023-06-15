package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class HostController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;

    //start method
    @FXML
    private TextField IpTextFiled = new TextField();
    @FXML
    private TextField PortTextFiled = new TextField();
    @FXML
    public TextField nameTextFiled;
    @FXML
    public TextField portField;
    @FXML
    private Button nextBtn;
    @FXML
    private Button submitBtn;
    int hostPort;

    public static String name;
    BS_Host_ViewModel host = new BS_Host_ViewModel();

    /**
     * The onPressSubmit function is called when the submit button is pressed.
     * It checks if the IP and Port fields are empty, and if they are not it will attempt to connect to a server at that address.
     * If it fails, an alert box will pop up telling you that there was an error connecting to the server.
     */
    @FXML
    public void onPressSubmit() {
        if (!validatePort(PortTextFiled.getText()) || !validateIp(IpTextFiled.getText())) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Problem with ip or port");
            alert.setHeaderText("Ip or port number is not valid");
            alert.setContentText("Please enter a valid ip and port number");
            alert.showAndWait();
        } else {
            boolean connected = true;
            host.ip.bindBidirectional(IpTextFiled.textProperty());
            host.port.bindBidirectional(PortTextFiled.textProperty());
            try {
                host.openSocket();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connect Error");
                alert.setHeaderText("Connect Error");
                alert.setContentText("The game server is not connected\n" + "Connect the game server and click submit again");
                alert.showAndWait();
                connected = false;
            }
            if (connected) {
                nextBtn.setVisible(true);
                submitBtn.onActionProperty().set(null);
            }
        }
    }


    /**
     * The next function is called when the user clicks on the next button.
     * It loads a new scene, which is hostNextWindow.fxml, and sets it as the current scene of this stage.
     */
    @FXML
    public void next() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostNextWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        stage.setOnCloseRequest(e -> Platform.exit());
        scene = new Scene(root, BookScrabbleApp.screenSize()[0], BookScrabbleApp.screenSize()[1]);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * The switchToGameWindow function is responsible for switching the scene from the welcome window to
     * the game window.
     * It also sets up a host server and starts it, as well as setting up a player object with properties such as name, score etc.
     * The function also passes on this player object to the GameWindowController class so that it can be used there too.
     */
    @FXML
    public void switchToGameWindow() throws Exception {
        if (nameTextFiled.getText().equals("")) {
            name = "Host";
        } else {
            name = nameTextFiled.getText();
        }
        if (portCheck(portField.getText())) {
            if (portField.getText().equals("")) {
                hostPort = 23346;
            } else if (!validatePort(portField.getText())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Problem with port");
                alert.setHeaderText("port number is not valid");
                alert.setContentText("Please enter a valid port number");
                alert.showAndWait();
                return;
            } else {
                hostPort = Integer.parseInt(portField.getText());
                host.hostFacade.setCommunicationServer(hostPort);
                host.startHostServer(hostPort);
                host.setPlayerProperties(name);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameMainWindow.fxml"));
                root = loader.load();
                GameWindowController controller = loader.getController();
                controller.setViewModel(host);
                stage = (Stage) welcomeText.getScene().getWindow();
                scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.setScene(scene);
                stage.show();
                stage.setOnCloseRequest(e -> Platform.exit());
            }
        }
    }


    /**
     * The validatePort function checks if the port number is valid.
     *
     * @param port Validate the port number
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
     *
     * @param ip Pass the ip address to be validated
     * @return A boolean value
     */
    private boolean validateIp(String ip) {
        // Regular expression for IPv4 address
        String ipv4Regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        // Validate IP address
        return Pattern.matches(ipv4Regex, ip);
    }

    private boolean portCheck(String port) {
        int portNumber = Integer.parseInt(port);
        boolean flag = true;
        try {
            ServerSocket socket = new ServerSocket(portNumber);
            socket.close();
        } catch (IOException e) {
            flag = false;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Problem with port");
            alert.setHeaderText("port number is already in use");
            alert.setContentText("Please enter another port number");
            alert.showAndWait();
        }
        return flag;
    }
}
