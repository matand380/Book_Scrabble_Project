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
import javafx.scene.text.*;

public class TileField extends StackPane {

    public TextField letter;

    public TextField score;
    private boolean isLocked;
    private boolean isSelect = false;

    private boolean isClick = false;

    private boolean isUpdate = false;

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

        if (this.isUpdate()) {
            gc.setFill(Color.LIGHTGRAY);
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
        }else if(fontSize == 0){
            gc.setFill(Color.LIGHTGRAY);
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(2);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.setStroke(Color.BLACK);
        }
        gc.fillRect(xCoordinate, YCoordinate, width, height);
        gc.strokeRect(xCoordinate, YCoordinate, width, height);

        // Draw letter
        String letter = String.valueOf(this.letter.getText());
        Text letterText = new Text(letter);
        letterText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        double letterWidth = letterText.getBoundsInLocal().getWidth();
        double letterHeight = letterText.getBoundsInLocal().getHeight();
        double letterX = xCoordinate + (width - letterWidth) / 2;
        double letterY = YCoordinate + (height + letterHeight) / 2 - 4;
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.setFill(Color.BLACK);
        gc.fillText(letter, letterX, letterY);

        // Draw score
        String score = String.valueOf(this.score.getText());
        Text scoreText = new Text(score);
        scoreText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        double scoreWidth = scoreText.getBoundsInLocal().getWidth();
        double scoreX = xCoordinate + (width - scoreWidth) - 10;
        double scoreY = YCoordinate + (height + letterHeight) / 2 + 5;
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gc.setFill(Color.RED);
        gc.fillText(score, scoreX, scoreY);
    }

    public void setSelect(boolean select) {
        isSelect = select;
        if (isSelect) {
            setOpacity(0.5); // Dim the tile when locked
        } else {
            setOpacity(1.0);
        }
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setUnlocked() {
        isLocked = false;
        setSelect(false);
        isUpdate = false;
        isClick = false;
    }
    public void setClick(boolean click){
        isClick = click;
    }
    public boolean isClick(){
        return isClick;
    }
    public boolean isUpdate() {
        return isUpdate;
    }
    public void setUpdate() {
        isUpdate = true;
    }

    public boolean isSelect(){
        return isSelect;
    }
}
