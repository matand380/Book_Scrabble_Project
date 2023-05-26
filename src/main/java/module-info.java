module view.book_scrabble_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens Model.GameData to com.google.gson;
    opens Model.GameLogic to com.google.gson;
    opens View to javafx.fxml;
    exports View;
}