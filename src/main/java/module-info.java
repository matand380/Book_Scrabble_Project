module view.book_scrabble_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.junit.jupiter.api;
    requires junit;


    opens BookScrabbleApp.Model to org.junit.jupiter, org.junit.jupiter.api;
    opens BookScrabbleApp.Model.GameData to com.google.gson;
    opens BookScrabbleApp.Model.GameLogic to com.google.gson;

    exports BookScrabbleApp.View;
    opens BookScrabbleApp.View to javafx.fxml;

    exports BookScrabbleApp.ViewModel;
    opens BookScrabbleApp.ViewModel to javafx.fxml;
    exports BookScrabbleApp;
    opens BookScrabbleApp to javafx.fxml, org.junit.jupiter, org.junit.jupiter.api;
}
