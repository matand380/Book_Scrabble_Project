package BookScrabbleApp.View;

import javafx.geometry.Pos;
import javafx.scene.canvas.*;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.*;


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
    }

    public StackPane createTile(double width, double height) {

        ImageView imageView = new ImageView(tileToImage(this.letter.getText()));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        StackPane imagePane = new StackPane(imageView);
        imagePane.setAlignment(Pos.CENTER);

        return imagePane;
    }

    public void draw(GraphicsContext gc, int row, int col, double width, double height, int fontSize) {
        this.tileCol = col;
        this.tileRow = row;

        double xCoordinate = col * width;
        double YCoordinate = row * height;
        if(!this.letter.getText().equals("_"))
            gc.drawImage(tileToImage(this.letter.getText()), xCoordinate, YCoordinate, width, height);
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

    public void setClick(boolean click) {
        isClick = click;
    }

    public boolean isClick() {
        return isClick;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate() {
        isUpdate = true;
    }

    public boolean isSelect() {
        return isSelect;
    }

    private Image tileToImage(String tileLetter) {
        return switch (tileLetter) {
            case "A" -> new Image(getClass().getResource("/images/Tiles/aLetter.png").toExternalForm());
            case "B" -> new Image(getClass().getResource("/images/Tiles/bLetter.png").toExternalForm());
            case "C" -> new Image(getClass().getResource("/images/Tiles/cLetter.png").toExternalForm());
            case "D" -> new Image(getClass().getResource("/images/Tiles/dLetter.png").toExternalForm());
            case "E" -> new Image(getClass().getResource("/images/Tiles/eLetter.png").toExternalForm());
            case "F" -> new Image(getClass().getResource("/images/Tiles/fLetter.png").toExternalForm());
            case "G" -> new Image(getClass().getResource("/images/Tiles/gLetter.png").toExternalForm());
            case "H" -> new Image(getClass().getResource("/images/Tiles/hLetter.png").toExternalForm());
            case "I" -> new Image(getClass().getResource("/images/Tiles/iLetter.png").toExternalForm());
            case "J" -> new Image(getClass().getResource("/images/Tiles/jLetter.png").toExternalForm());
            case "K" -> new Image(getClass().getResource("/images/Tiles/kLetter.png").toExternalForm());
            case "L" -> new Image(getClass().getResource("/images/Tiles/lLetter.png").toExternalForm());
            case "M" -> new Image(getClass().getResource("/images/Tiles/mLetter.png").toExternalForm());
            case "N" -> new Image(getClass().getResource("/images/Tiles/nLetter.png").toExternalForm());
            case "O" -> new Image(getClass().getResource("/images/Tiles/oLetter.png").toExternalForm());
            case "P" -> new Image(getClass().getResource("/images/Tiles/pLetter.png").toExternalForm());
            case "Q" -> new Image(getClass().getResource("/images/Tiles/qLetter.png").toExternalForm());
            case "R" -> new Image(getClass().getResource("/images/Tiles/rLetter.png").toExternalForm());
            case "S" -> new Image(getClass().getResource("/images/Tiles/sLetter.png").toExternalForm());
            case "T" -> new Image(getClass().getResource("/images/Tiles/tLetter.png").toExternalForm());
            case "U" -> new Image(getClass().getResource("/images/Tiles/uLetter.png").toExternalForm());
            case "V" -> new Image(getClass().getResource("/images/Tiles/vLetter.png").toExternalForm());
            case "W" -> new Image(getClass().getResource("/images/Tiles/wLetter.png").toExternalForm());
            case "X" -> new Image(getClass().getResource("/images/Tiles/xLetter.png").toExternalForm());
            case "Y" -> new Image(getClass().getResource("/images/Tiles/yLetter.png").toExternalForm());
            case "Z" -> new Image(getClass().getResource("/images/Tiles/zLetter.png").toExternalForm());
            default -> null;
        };
    }
}
