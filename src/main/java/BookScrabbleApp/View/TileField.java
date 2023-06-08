package BookScrabbleApp.View;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TileField extends StackPane {

    public TextField letter;

    public TextField score;
    private boolean isLocked;
    private boolean isSelect;

    public int tileCol;
    public int tileRow;

    public TileField() {
        super();

        this.letter = new TextField();
        this.score = new TextField();
        this.isLocked = false;
        setPrefSize(135, 200);

        getStyleClass().add("tile-field");

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                GameWindowController.selectedTileField = TileField.this;
                event.consume();
                setSelect(true);
            }
        });

    }

    public void createTile(TextField letter, TextField score, double width, double height, int fontSize) {
        // Create background rectangle
        Rectangle tileBackground = new Rectangle(width, height);
        tileBackground.setFill(Color.LIGHTGRAY);
        tileBackground.setStroke(Color.BLACK);

        // Create letter label
        Label letterLabel = new Label(letter.getText());
        letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        letterLabel.setTextFill(Color.BLACK);

        // Create score label
        Label scoreLabel = new Label(score.getText());
        scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
        scoreLabel.setTextFill(Color.RED);

        // Create a VBox to hold the letter and score labels
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(letterLabel, scoreLabel);
        contentBox.setBorder(null);

        getChildren().addAll(tileBackground, contentBox);
    }

    public void draw(GraphicsContext gc, int row, int col, double width, double height, int fontSize) {
        this.tileCol = col;
        this.tileRow = row;
        double xCoordinate = col * width;
        double YCoordinate = row * height;

        // Create background rectangle
        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.fillRect(xCoordinate, YCoordinate, width, height);
        gc.strokeRect(xCoordinate, YCoordinate, width, height);

        // Draw letter
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(this.letter.getText()), xCoordinate + width / 2, YCoordinate + height / 2);

        // Draw score
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
        gc.setFill(Color.RED);
        gc.fillText(String.valueOf(this.score.getText()), xCoordinate + width / 2, YCoordinate + height / 2 + 20);
    }

    public void setSelect(boolean select) {
        isSelect = select;
        if (isSelect) {
            setOpacity(0.5); // Dim the tile when locked
        } else {
            setOpacity(1.0);
        }
    }

    public void setLocked() {
        isLocked = true;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setUnlocked() {
        isLocked = false;
        setSelect(false);
    }
}
