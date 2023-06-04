package BookScrabbleApp.View;

import BookScrabbleApp.Model.BS_Guest_Model;
import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import BookScrabbleApp.ViewModel.BS_Host_ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreen {
    BS_Host_ViewModel bsHostModel;
    BS_Guest_ViewModel bsGuestModel;

    Stage stage;

    public void createHost(ActionEvent actionEvent) {

        bsHostModel = new BS_Host_ViewModel();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/HostScreen.fxml"));
            Parent secondFXML = fxmlLoader.load();
            Scene scene = new Scene(secondFXML);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            //  stage.setTitle("Hello!");
        } catch (Exception e) {
            System.out.println("Error with changing screen from start to Host");
        }

    }

    public void createGuest(ActionEvent actionEvent) {
        bsGuestModel = new BS_Guest_ViewModel();
    }
}
