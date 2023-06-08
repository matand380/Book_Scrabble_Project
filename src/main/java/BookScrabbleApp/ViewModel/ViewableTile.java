package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.GameData.*;
import javafx.beans.property.*;

public class ViewableTile extends Tile {



    public SimpleStringProperty letterProperty() {
        return letter;
    }

    public SimpleStringProperty scoreProperty() {
        return score;
    }

    public void setLetter(char letter) {
        this.letter.setValue(String.valueOf(letter));
    }

    public void setScore(int score) {
        this.score.setValue(String.valueOf(score));
    }

    SimpleStringProperty letter;
    SimpleStringProperty score;

    public ViewableTile(char _letter, int _score) {
        super(_letter, _score);
        letter = new SimpleStringProperty(String.valueOf(_letter));
        score = new SimpleStringProperty(String.valueOf(_score));
    }
}
