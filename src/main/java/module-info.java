module view.book_scrabble_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens BookScrabbleApp.Model.GameData to com.google.gson;
    opens BookScrabbleApp.Model.GameLogic to com.google.gson;
    opens BookScrabbleApp.View to javafx.fxml;
    exports BookScrabbleApp.View;
}