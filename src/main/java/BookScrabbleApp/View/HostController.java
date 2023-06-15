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

public class HostController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;

    //start method
    @FXML
    private Label invalidIPorPort;
    @FXML
    private TextField IpTextFiled = new TextField();
    @FXML
    private TextField PortTextFiled = new TextField();
    @FXML
    public TextField nameTextFiled;
    @FXML
    private Button nextBtn;
    @FXML
    private Button submitBtn;

    String ip;
    int port;

    public static String name;
    BS_Host_ViewModel host = new BS_Host_ViewModel();

    /**
     * The onPressSubmit function is called when the submit button is pressed.
     * It checks if the IP and Port fields are empty, and if they are not it will attempt to connect to a server at that address.
     * If it fails, an alert box will pop up telling you that there was an error connecting to the server.

     */
    @FXML
    public void onPressSubmit(){
        ip = IpTextFiled.getText();
        port = Integer.parseInt(PortTextFiled.getText());
        if (ip.equals("") || port == 0) {
            invalidIPorPort.setText("Please enter IP and Port");
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
        scene = new Scene(root, BookScrabbleApp.screenSize()[0],BookScrabbleApp.screenSize()[1]);
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
        if (nameTextFiled.getText().equals("Enter your name here")) {
            name = "Guest" + UUID.randomUUID().toString().substring(0, 4);
        } else {
            name = nameTextFiled.getText();
        }
        host.startHostServer();
        host.setPlayerProperties(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameMainWindow.fxml"));
        root = loader.load();
        GameWindowController controller = loader.getController();
        controller.setViewModel(host);
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        //scene.getStylesheets().add(String.valueOf(getClass().getResource("../resources/buttonStyleSheets.css")));
        scene.getStylesheets().add(getClass().getResource("/background.css").toExternalForm());
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());
    }
}
