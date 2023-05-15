module view.book_scrabble_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens View to javafx.fxml;
    exports View;
}