package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import java.io.*;
import java.net.*;

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
            invalidIPorPort.setText("Game server connected");
            host.ip.bindBidirectional(IpTextFiled.textProperty());
            host.port.bindBidirectional(PortTextFiled.textProperty());
            host.openSocket();
        }
    }

    @FXML
    public void next() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostNextWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
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
        name = nameTextFiled.getText();
        host.setPlayerProperties(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookScrabbleApp.View/gameWindow.fxml"));
        root = loader.load();
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        GameWindowController controller = loader.getController();
        controller.setViewModel(host);
    }


}
