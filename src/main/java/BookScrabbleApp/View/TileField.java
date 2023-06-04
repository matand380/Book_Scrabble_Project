package BookScrabbleApp.View;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Observable;

public class TileField extends StackPane implements EventHandler<MouseEvent>  {

TextField letter;
TextField score;

    public TileField() {
        super();
        setPrefSize(135, 200);
        setOnMouseClicked(this);

    }


    @Override
    public void handle(MouseEvent event) {


    }

    public void createTile(TextField letter, TextField score) {

        // Create background rectangle
        Rectangle tileBackground = new Rectangle(135, 200);
        tileBackground.setFill(Color.LIGHTGRAY);
        tileBackground.setStroke(Color.BLACK);

        // Create letter label
        Label letterLabel = new Label(letter.getText());
        letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        letterLabel.setTextFill(Color.BLACK);

        // Create score label
        Label scoreLabel = new Label(score.getText());
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        scoreLabel.setTextFill(Color.RED);

        // Create a VBox to hold the letter and score labels
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(letterLabel, scoreLabel);
        contentBox.setBorder(null);

        getChildren().addAll(tileBackground, contentBox);
    }


}
