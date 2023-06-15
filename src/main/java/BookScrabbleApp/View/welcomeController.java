package BookScrabbleApp.View;

import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class welcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label welcomeText;


    /**
     * The switchToHostWindow function is called when the user clicks on the &quot;Host Game&quot; button.
     * It switches to a new window, where the user can host a game.
     */
    @FXML
    public void switchToHostWindow() throws Exception {
        // Get the primary screen
        Screen screen = Screen.getPrimary();

        // Get the bounds of the screen
        Rectangle2D bounds = screen.getBounds();

        // Retrieve the screen size
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/hostServerWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root,BookScrabbleApp.screenSize()[0] ,BookScrabbleApp.screenSize()[1]);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.setMinWidth(BookScrabbleApp.MIN_WIDTH);
        stage.setMinHeight(BookScrabbleApp.MIN_HEIGHT);
        stage.show();
    }
    /**
     * The switchToGuestWindow function is called when the user clicks on the &quot;Guest&quot; button.
     * It switches to a new scene, which is the guestWindow.fxml file, and sets it as the current scene in our stage.
     */
    @FXML
    public void switchToGuestWindow() throws Exception {
        root = FXMLLoader.load(getClass().getResource("/BookScrabbleApp.View/guestWindow.fxml"));
        stage = (Stage) welcomeText.getScene().getWindow();
        scene = new Scene(root,BookScrabbleApp.screenSize()[0] ,BookScrabbleApp.screenSize()[1]);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(scene);
        stage.show();
    }
}
