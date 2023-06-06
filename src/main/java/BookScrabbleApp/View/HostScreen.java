package BookScrabbleApp.View;

import BookScrabbleApp.ViewModel.BS_Host_ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class HostScreen implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;
    //start method
    @FXML
    private Label invalidIPorPort;
    @FXML
    private TextField IpTextFiled;
    @FXML
    private TextField PortTextFiled;
    @FXML
    private Label yourIp;
    @FXML
    private Label yourPort;
    @FXML
    private Label welcomeText1;
    @FXML
    private TextField nameTextFiled;

    String ip;
    int port;
    String name;

    public void submit(ActionEvent actionEvent) {
        ip = IpTextFiled.getText();
        port = Integer.parseInt(PortTextFiled.getText());
        if (ip.equals("") || port == 0) {
            invalidIPorPort.setText("Please enter IP and Port");
        } else if (validateIpPort(ip, port)) {
            // TODO: 2023-06-04 open socket and wait for connection
            invalidIPorPort.setText("Start with IP: " + ip + " Port: " + port);
        } else {
            invalidIPorPort.setText("Invalid IP or Port");
        }
    }

    private boolean validateIpPort(String ip, int port) {
        // Regular expression for IPv4 address
        String ipv4Regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        // Regular expression for port number (1-65535)
        String portRegex = "^([1-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
        // Validate IP address
        if (!Pattern.matches(ipv4Regex, ip)) {
            return false;
        }
        // Validate port number
        return Pattern.matches(portRegex, Integer.toString(port));
    }

    public void next(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/View/NewGameBoard.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root,600, 600);
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeText.setText("Welcome this is the host window");
//        yourIp.setText(getPublicIp());
//        yourPort.setText(String.valueOf(port));//doesn't work yet

    }
}
