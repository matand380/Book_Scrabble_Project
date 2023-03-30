module view.book_scrabble_project {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens view.book_scrabble_project to javafx.fxml;
    exports view.book_scrabble_project;
}