package Model.GameData;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Word implements Serializable, ObjectFactory {
    Tile[] tiles;
    int row;
    int col;
    boolean vertical; //true = vertical and false = horizontal

    /**
     * the Word constructor creates a word from the tiles passed in
     * <p>
     *
     * @param tiles tiles Create a deep copy of the tiles array passed in
     * @param row row Set the row of the word
     * @param col col Store the column of the word
     * @param vertical vertical Determine whether the word is vertical or horizontal
     *
     */
    public Word(Tile[] tiles, int row, int col, boolean vertical) {
        this.tiles = new Tile[tiles.length];
        System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);// deep copy tiles array
        this.row = row;
        this.col = col;
        this.vertical = vertical;
    }

    /**
     * The Word function is a default constructor that creates a new Word object.
     * @return A word object
     */
    public Word() {
        this.tiles = new Tile[0];
        this.row = 0;
        this.col = 0;
        this.vertical = false;
    }

    /**
     * The getRow function returns the row of the current position.
     * <p>
     * @return The row of the cell
     *

     */
    public int getRow() {
        return row;
    }

    /**
     * The getTiles function returns the tiles array.
     * <p>
     * @return The tiles array
     *
     */
    public Tile[] getTiles() {
        return tiles;
    }

    /**
     * The getCol function returns the column of the current position.
     *<p>
     *
     * @return The column number of the cell
     *
     */
    public int getCol() {
        return col;
    }

    /**
     * The isVertical function returns whether the word is vertical or horizontal.
     * <p>
     * @return True if the word is vertical and false if the word is horizontal
     *
     */
    public boolean isVertical() {
        return vertical;
    }


    /**
     * The equals function checks if two words are equal.
     * <p>
     * @param o o Compare the object to another object
     *
     * @return True if the two words are equal
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return row == word.row && col == word.col && vertical == word.vertical && Arrays.equals(tiles, word.tiles);
    }

    /**
     * The hashCode function is used to generate a unique hash code for each object.
     * This is useful when storing objects in data structures such as HashMaps, where
     * the hashCode of an object can be used to determine which bucket it should be stored in.
     * The default implementation of this function simply returns the memory address of the object, but we override it here so that two BoardStates with identical tiles will have identical hash codes.

     * <p>
     *
     * @return A hash code value for the object
     *
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(row, col, vertical);
        result = 31 * result + Arrays.hashCode(tiles);
        return result;
    }

    /**
     * The create function is used to create a new instance of the Word class.
     * @return A new word object with the parameters given
     */
    @Override
    public Object create() {
        return new Word(new Tile[0], 0, 0, false);
    }

    public String toMessage(){
        StringBuilder sb = new StringBuilder();
        for(Tile tile : this.tiles){
            if(tile == null){
                sb.append("_");
                continue;
            }
            sb.append(tile.toString());
        }
        sb.append(":").append(this.row).append(":").append(this.col).append(":").append(this.vertical);
        return sb.toString();
    }
}
