package test;

import java.util.Arrays;
import java.util.Objects;

public class Word {
    Tile[] tiles;
    int row;
    int col;
    boolean vertical; //true = vertical and false = horizontal

    public Word(Tile[] tiles, int row, int col, boolean vertical) {
        this.tiles = new Tile[tiles.length];
        System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);// deep copy tiles array
        this.row = row;
        this.col = col;
        this.vertical = vertical;
    }

    public int getRow() {
        return row;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public int getCol() {
        return col;
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return row == word.row && col == word.col && vertical == word.vertical && Arrays.equals(tiles, word.tiles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(row, col, vertical);
        result = 31 * result + Arrays.hashCode(tiles);
        return result;
    }

}
