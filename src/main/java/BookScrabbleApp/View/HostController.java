package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
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
    private Label yourIp ;
    @FXML
    private Label yourPort;
    @FXML
    private Label welcomeText1;
    @FXML
    public TextField nameTextFiled;

    String ip;
    int port;
    public static String name;
    BS_Host_ViewModel host = new BS_Host_ViewModel();

    @FXML
    public void onPressSubmit() throws Exception {
        ip = IpTextFiled.getText();
        port = Integer.parseInt(PortTextFiled.getText());
        if (ip.equals("") || port == 0) {
            invalidIPorPort.setText("Please enter IP and Port");
        }
        else {
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
                invalidIPorPort.setText("Game server connected");
            }
        }
    }

    @FXML
    public void next() throws Exception {
        // Get the primary screen
        Screen screen = Screen.getPrimary();

        // Get the bounds of the screen
        Rectangle2D bounds = screen.getBounds();

        // Retrieve the screen size
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostNextWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        stage.setOnCloseRequest(e -> Platform.exit());
        scene = new Scene(root,screenWidth,screenHeight);
        stage.setScene(scene);
        stage.show();
    }


    public String getPublicIp() {
        String ip = null;
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = in.readLine();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

    @FXML
    private void initializeNextHostWindow() {
        welcomeText1.setText("Welcome this is the host window");
        yourIp.setText(getPublicIp());
        yourPort.setText(String.valueOf(port));//doesn't work yet
    }

    @FXML
    public void switchToGameWindow() throws Exception {
        if (nameTextFiled.getText().equals("")) {
            name = "Guest"+ UUID.randomUUID().toString().substring(0,4);
            name = "Guest"+ UUID.randomUUID().toString().substring(0,4);
        } else {
            name = nameTextFiled.getText();
        }
        host.setPlayerProperties(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameWindow.fxml"));
        root = loader.load();
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());
        GameWindowController controller = loader.getController();
        controller.setViewModel(host);
    }


}
