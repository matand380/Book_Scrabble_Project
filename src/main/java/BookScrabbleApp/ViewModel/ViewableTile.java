package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.GameData.Tile;
import javafx.beans.property.SimpleStringProperty;

public class ViewableTile extends Tile {

    SimpleStringProperty letter;
    SimpleStringProperty score;

    public ViewableTile(char _letter, int _score) {
        super(_letter, _score);
        letter = new SimpleStringProperty(String.valueOf(_letter));
        score = new SimpleStringProperty(String.valueOf(_score));
    }
}
