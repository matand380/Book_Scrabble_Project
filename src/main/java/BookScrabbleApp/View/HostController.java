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
    public Label stringYourIp;
    @FXML
    public Label stringYourPort;
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
    private Label yourIp;
    @FXML
    private Label yourPort;
    @FXML
    private Label welcomeText1;
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

    @FXML
    public void onPressSubmit() throws Exception {
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
//                invalidIPorPort.setText("Game server connected");
                nextBtn.setVisible(true);
                submitBtn.onActionProperty().set(null);
            }
        }
    }

    @FXML
    public void next() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostNextWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        stage.setOnCloseRequest(e -> Platform.exit());
        scene = new Scene(root, BookScrabbleApp.screenSize()[0],BookScrabbleApp.screenSize()[1]);
        stage.setScene(scene);
        stage.show();
    }


    public String getPublicIp() {
        String ip = null;
        try {
            URL url = new URL("https://ifconfig.me/ip");
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
        stringYourIp.setVisible(true);
        stringYourPort.setVisible(true);
        yourIp.setText(getPublicIp());
        yourPort.setText(String.valueOf(port));//doesn't work yet

    }

    @FXML
    public void switchToGameWindow() throws Exception {
        if (nameTextFiled.getText().equals("Enter your name here")) {
            name = "Guest" + UUID.randomUUID().toString().substring(0, 4);
            name = "Guest" + UUID.randomUUID().toString().substring(0, 4);
        } else {
            name = nameTextFiled.getText();
        }
        host.startHostServer();
        host.setPlayerProperties(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameMainWindow.fxml"));
        root = loader.load();
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root, BookScrabbleApp.screenSize()[0],BookScrabbleApp.screenSize()[1]);

        //window sizes
        stage.setMinWidth(BookScrabbleApp.MIN_WIDTH+100);
        stage.setMinHeight(BookScrabbleApp.MIN_HEIGHT+50);
        stage.setMaxHeight(BookScrabbleApp.MIN_WIDTH+200);
        stage.setMaxWidth(BookScrabbleApp.MIN_HEIGHT+200);

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());
        GameWindowController controller = loader.getController();
        controller.setViewModel(host);
    }
}
