package BookScrabbleApp.View;

import javafx.geometry.Pos;
import javafx.scene.canvas.*;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.*;


public class TileField extends StackPane {

    public TextField letter;

    public TextField score;
    private boolean isSelect = false;
    private boolean isUpdate = false;
    public int tileCol;
    public int tileRow;

    /**
     * The TileField function is a constructor for the TileField class.
     * It creates a new TextField object called letter and another called score.
     * It also sets the size of each TileField to 135 by 200 pixels.
     * @return A tilefield object
     */
    public TileField() {
        super();
        this.letter = new TextField();
        this.score = new TextField();
        setPrefSize(135, 200);
    }

    /**
     * The createTile function creates a tile for the handGrid.
     * @param width Set the width of the imageview
     * @param height Set the height of the imageview
     * @return A stackPane that contains an imageview
     */
    public StackPane createTile(double width, double height) {

        ImageView imageView = new ImageView(tileToImage(this.letter.getText()));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        StackPane imagePane = new StackPane(imageView);
        imagePane.setAlignment(Pos.CENTER);

        return imagePane;
    }

    /**
     * The draw function draws the tile onto the GridCanvas.
     * @param  gc Draw the image on the canvas
     * @param  row Determine the row of the tile on the board
     * @param  col Determine the column of the tile
     * @param  width Set the width of the tile
     * @param  height Set the height of the tile
     * @param  fontSize Set the font size of the letter on the tile
     */
    public void draw(GraphicsContext gc, int row, int col, double width, double height, int fontSize) {
        this.tileCol = col;
        this.tileRow = row;

        double xCoordinate = col * width;
        double YCoordinate = row * height;
        if(!this.letter.getText().equals("_"))
            gc.drawImage(tileToImage(this.letter.getText()), xCoordinate, YCoordinate, width, height);
    }

    /**
     * The setSelect function sets the boolean value of isSelect to true or false.
     * @param select Determine whether the item is selected
     */
    public void setSelect(boolean select) {
        isSelect = select;
    }

    /**
     * The setUnselected function sets the selected variable to false, and isUpdate to false.
     */
    public void setUnselected() {
        setSelect(false);
        isUpdate = false;
    }

    /**
     * The copy function copies all the parameter in Tile from one tile to another.
     * This function was created in order to simplify the process of copying tiles.
     * The copy function takes in a TileField object as an argument and
     * then sets all of its own values equal to those of the inputted TileField's.
     * @param tile Copy the values of the tile to this tile
     */
    public void copy(TileField tile){
        this.letter.setText(tile.letter.getText());
        this.score.setText(tile.score.getText());
        this.isSelect = tile.isSelect;
        this.isUpdate = tile.isUpdate;
        this.tileCol = tile.tileCol;
        this.tileRow = tile.tileRow;
    }

    /**
     * The isUpdate function returns a boolean value that indicates whether the
     * current tile is an update or not from the server.
     * @return True if the tile is an update, false otherwise
     */
    public boolean isUpdate() {
        return isUpdate;
    }

    /**
     * The setUpdate function sets the isUpdate variable to true.
     * its purpose is to tell the controller that the tile has been updated from the server.
     */
    public void setUpdate() {
        isUpdate = true;
    }

    /**
     * The isSelect function returns a boolean value that indicates whether the current tile is selected.
     * @return A boolean value
     */
    public boolean isSelect() {
        return isSelect;
    }

    /**
     * The tileToImage function takes in a tile letter and returns the corresponding image of that tile.
     * @param  tileLetter Determine which tile image to return
     * @return An image object
     */
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
