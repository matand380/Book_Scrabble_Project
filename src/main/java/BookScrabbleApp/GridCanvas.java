package BookScrabbleApp;

import BookScrabbleApp.View.*;
import javafx.application.Platform;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import java.util.*;

public class GridCanvas extends Canvas {
    public List<List<TileField>> tileFields;
    private int col;
    private int row;
    char[][] colorBoard = {
            {'r', '0', '0', 'p', '0', '0', '0', 'r', '0', '0', '0', 'p', '0', '0', 'r'},
            {'0', 'y', '0', '0', '0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'y', '0'},
            {'0', '0', 'y', '0', '0', '0', 'p', '0', 'p', '0', '0', '0', 'y', '0', '0'},
            {'p', '0', '0', 'y', '0', '0', '0', 'p', '0', '0', '0', 'y', '0', '0', 'p'},
            {'0', '0', '0', '0', 'y', '0', '0', '0', '0', '0', 'y', '0', '0', '0', '0'},
            {'0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'b', '0'},
            {'0', '0', 'p', '0', '0', '0', 'p', '0', 'p', '0', '0', '0', 'p', '0', '0'},
            {'r', '0', '0', 'p', '0', '0', '0', 's', '0', '0', '0', 'p', '0', '0', 'r'},
            {'0', '0', 'p', '0', '0', '0', 'p', '0', 'p', '0', '0', '0', 'p', '0', '0'},
            {'0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'b', '0'},
            {'0', '0', '0', '0', 'y', '0', '0', '0', '0', '0', 'y', '0', '0', '0', '0'},
            {'p', '0', '0', 'y', '0', '0', '0', 'p', '0', '0', '0', 'y', '0', '0', 'p'},
            {'0', '0', 'y', '0', '0', '0', 'p', '0', 'p', '0', '0', '0', 'y', '0', '0'},
            {'0', 'y', '0', '0', '0', 'b', '0', '0', '0', 'b', '0', '0', '0', 'y', '0'},
            {'r', '0', '0', 'p', '0', '0', '0', 'r', '0', '0', '0', 'p', '0', '0', 'r'}
    };

    public GridCanvas() {
        super();
        this.tileFields = new ArrayList<>();
        this.getStyleClass().add("grid-canvas");
        col = 7;
        row = 7;
    }

    public void setTileFields(List<List<TileField>> tileFields) {
        this.tileFields = tileFields;
        redraw();
    }

    public void placeTileFiled(TileField tile,int tileRow, int tileCol) {
        if(tile != null && !tile.letter.getText().equals("_"))
            tile.draw(getGraphicsContext2D(), tileRow, tileCol, this.getWidth()/15, this.getHeight()/15,15);
    }

    public void redraw() {
        Platform.runLater(()->{
            double W = getWidth();
            double H = getHeight();

            double w = W / colorBoard[0].length;
            double h = H / colorBoard.length;

            double xCoordinate = col * w;
            double YCoordinate = row * h;

            GraphicsContext gc = getGraphicsContext2D();

            gc.clearRect(0, 0, W, H);

            for (int boardRow = 0; boardRow < 15; boardRow++) {
                for (int boardCol = 0; boardCol < 15; boardCol++) {
                    if (tileFields.get(boardRow).get(boardCol).letter.getText().equals("")) {
                        double tileXCoordinate = boardCol * w;
                        double tileYCoordinate = boardRow * h;
                        gc.drawImage(getImageForGrid(colorBoard[boardRow][boardCol]), tileXCoordinate, tileYCoordinate, w, h);
                    } else {
                        this.placeTileFiled(tileFields.get(boardRow).get(boardCol), boardRow, boardCol);
                    }
                }
            }
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(xCoordinate, YCoordinate, w, h);
        });
    }


    private Image getImageForGrid(char score) {
        double imageSize = 70; // Set the desired image size

        return switch (score) {
            case 'r' -> new Image(getClass().getResource("/images/board/tripleWord.png").toExternalForm(), imageSize, imageSize, true, true);
            case 'p' -> new Image(getClass().getResource("/images/board/doubleLetter.png").toExternalForm(), imageSize, imageSize, true, true);
            case 'b' -> new Image(getClass().getResource("/images/board/tripleLetter.png").toExternalForm(), imageSize, imageSize, true, true);
            case 'y' -> new Image(getClass().getResource("/images/board/doubleWord.png").toExternalForm(), imageSize, imageSize, true, true);
            case 's' -> new Image(getClass().getResource("/images/board/starLetter.png").toExternalForm(), imageSize, imageSize, true, true);
            default -> new Image(getClass().getResource("/images/board/0Score.png").toExternalForm(), imageSize, imageSize, true, true);
        };
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setPlace(int row, int col) {
        this.col = col;
        this.row = row;
        redraw();
    }
}
