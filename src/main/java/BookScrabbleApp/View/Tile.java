package BookScrabbleApp.View;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Tile extends StackPane implements EventHandler<MouseEvent>{
    private String letter;
    private int score;

    public Tile() {
        this.letter = "A";
        this.score = 1;
        setPrefSize(135, 200);
        setOnDragDetected(this);
    }

    public void createTile(String letter, int score) {
        //set letter and score
        this.letter = letter;
        this.score = score;

        // Create background rectangle
        Rectangle tileBackground = new Rectangle(135, 200);
        tileBackground.setFill(Color.LIGHTGRAY);
        tileBackground.setStroke(Color.BLACK);

        // Create letter label
        Label letterLabel = new Label(letter);
        letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        letterLabel.setTextFill(Color.BLACK);

        // Create score label
        Label scoreLabel = new Label(Integer.toString(score));
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        scoreLabel.setTextFill(Color.RED);

        // Create a VBox to hold the letter and score labels
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(letterLabel, scoreLabel);
        contentBox.setBorder(null);

        getChildren().addAll(tileBackground, contentBox);
    }

    @Override
    public void handle(MouseEvent event) {
        boolean success = false;
        if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            // Start drag-and-drop
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(letter);
            dragboard.setContent(content);
            event.consume();
            success = true;
        }
        if(success) {
            //gameWindowController.hand.remove(this);
//            TileShowHand tile = new TileShowHand();
//            tile.createTile("A",1);
            //gameWindowController.hand.add(tile);
            System.out.println("Drag detected");
        }
    }

    public String getLetter() {
        return this.letter;
    }

    public int getScore() {
        return score;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
