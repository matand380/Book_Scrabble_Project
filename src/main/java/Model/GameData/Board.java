package Model.GameData;

import java.util.ArrayList;

public class Board {
    Tile[][] mainBoard;
    char[][] scoreBoard;
    int wordCounter = 0;
    boolean wScount = false;
    private static final int width = 15;
    private static final int height = 15;

    ArrayList<Word> wordOnBoard = new ArrayList<>();

    /**
     * The Board function creates a new board object.
     * It initializes the mainBoard and scoreBoard arrays to null and 0 respectively.
     * It then sets the values of each tile in scoreBoard to their respective scores, as shown on a Scrabble board.
     *
     */
    public Board() {
        mainBoard = new Tile[15][15];
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                mainBoard[i][j] = null;
        scoreBoard = new char[15][15];
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                scoreBoard[i][j] = '0';
        scoreBoard[7][7] = 's';//yellow double word score
        scoreBoard[0][3] = 'p';//pale blue double letter score
        scoreBoard[0][11] = 'p';//pale blue double letter score
        scoreBoard[2][6] = 'p';//pale blue double letter score
        scoreBoard[2][8] = 'p';//pale blue double letter score
        scoreBoard[3][0] = 'p';//pale blue double letter score
        scoreBoard[3][7] = 'p';//pale blue double letter score
        scoreBoard[3][14] = 'p';//pale blue double letter score
        scoreBoard[6][2] = 'p';//pale blue double letter score
        scoreBoard[6][6] = 'p';//pale blue double letter score
        scoreBoard[6][8] = 'p';//pale blue double letter score
        scoreBoard[6][12] = 'p';//pale blue double letter score
        scoreBoard[7][3] = 'p';//pale blue double letter score
        scoreBoard[7][11] = 'p';//pale blue double letter score
        scoreBoard[8][2] = 'p';//pale blue double letter score
        scoreBoard[8][6] = 'p';//pale blue double letter score
        scoreBoard[8][8] = 'p';//pale blue double letter score
        scoreBoard[8][12] = 'p';//pale blue double letter score
        scoreBoard[11][0] = 'p';//pale blue double letter score
        scoreBoard[11][7] = 'p';//pale blue double letter score
        scoreBoard[11][14] = 'p';//pale blue double letter score
        scoreBoard[12][6] = 'p';//pale blue double letter score
        scoreBoard[12][8] = 'p';//pale blue double letter score
        scoreBoard[14][3] = 'p';//pale blue double letter score
        scoreBoard[14][11] = 'p';//pale blue double letter score
        scoreBoard[5][1] = 'b';//blue triple letter score
        scoreBoard[9][1] = 'b';//blue triple letter score
        scoreBoard[1][5] = 'b';//blue triple letter score
        scoreBoard[5][5] = 'b';//blue triple letter score
        scoreBoard[9][5] = 'b';//blue triple letter score
        scoreBoard[13][5] = 'b';//blue triple letter score
        scoreBoard[1][9] = 'b';//blue triple letter score
        scoreBoard[5][9] = 'b';//blue triple letter score
        scoreBoard[9][9] = 'b';//blue triple letter score
        scoreBoard[13][9] = 'b';//blue triple letter score
        scoreBoard[5][13] = 'b';//blue triple letter score
        scoreBoard[9][13] = 'b';//blue triple letter score
        scoreBoard[1][1] = 'y';//yellow double word score
        scoreBoard[2][2] = 'y';//yellow double word score
        scoreBoard[3][3] = 'y';//yellow double word score
        scoreBoard[4][4] = 'y';//yellow double word score
        scoreBoard[10][4] = 'y';//yellow double word score
        scoreBoard[11][3] = 'y';//yellow double word score
        scoreBoard[12][2] = 'y';//yellow double word score
        scoreBoard[13][1] = 'y';//yellow double word score
        scoreBoard[4][10] = 'y';//yellow double word score
        scoreBoard[3][11] = 'y';//yellow double word score
        scoreBoard[2][12] = 'y';//yellow double word score
        scoreBoard[1][13] = 'y';//yellow double word score
        scoreBoard[10][10] = 'y';//yellow double word score
        scoreBoard[11][11] = 'y';//yellow double word score
        scoreBoard[12][12] = 'y';//yellow double word score
        scoreBoard[13][13] = 'y';//yellow double word score
        scoreBoard[0][0] = 'r';//pale blue triple word score
        scoreBoard[0][7] = 'r';//pale blue triple word score
        scoreBoard[0][14] = 'r';//pale blue triple word score
        scoreBoard[7][0] = 'r';//pale blue triple word score
        scoreBoard[7][14] = 'r';//pale blue triple word score
        scoreBoard[14][0] = 'r';//pale blue triple word score
        scoreBoard[14][7] = 'r';//pale blue triple word score
        scoreBoard[14][14] = 'r';//pale blue triple word score

    }

    private static Board single_instance = null;

    /**
     * The getBoard function is a static function that returns the single instance of the Board class.
     * If there is no instance, it creates one and then returns it.
     * <p>
     *
     * @return The single instance of the board class
     *
     */
    public static Board getBoard() {
        if (single_instance == null)
            return single_instance = new Board();
        return single_instance;
    }

    /**
     * The getTiles function returns a copy of the mainBoard array.
     * It is used to get the current state of the board.
     * <p>
     *
     *
     * @return A copy of the mainboard array
     *
     */
    Tile[][] getTiles() {
        return mainBoard.clone();
    }

    /**
     * The boardLegal function checks if the word is legal to be placed on the board.
     * It first checks if it is inside the board, then it checks if it connects with other words.
     * If this is not true, then we check for letter replacement and return false otherwise.
     *<p>
     * @param w w Check if the word is inside the board
     *
     * @return True if the word is legal and false otherwise
     *
     */
    public boolean boardLegal(Word w) {
        boolean gridLegal = checkIfInside(w);
        boolean notRequireReplacement;
        boolean tileConnected;
        if (gridLegal) {
            if (wordCounter == 0)
                return checkFirstWord(w);
            tileConnected = checkIfConnected(w);
            notRequireReplacement = notRequireLetterReplacement(w);
            return tileConnected && notRequireReplacement;
        }
        return false;
    }
    /**
     * The checkIfInside function checks if the word is inside the board.
     * <p>
     * @param w w Check if the word is inside the grid
     *
     * @return A boolean
     *
     */
    private boolean checkIfInside(Word w) {
        return w.getCol() < height && w.getRow() < width && w.getCol() >= 0 && w.getRow() >= 0;
    }

    /**
     * The checkFirstWord function checks if the first word is placed on a star.
     * The first word must be placed at the center of the board.
     * <p>
     *
     * @param w w Check if the word is vertical or horizontal
     *
     * @return True if the first word is placed
     *
     */
    private boolean checkFirstWord(Word w) {
        if (w.isVertical() && w.getRow() + w.getTiles().length < width) {
            for (int i = 0; w.getCol() + i < width; i++) {
                if (scoreBoard[w.getRow() + i][w.getCol()] == 's') {
                    return true;
                }
            }
        }
        if (!w.isVertical() && w.getCol() + w.getTiles().length < height) {
            for (int i = 0; w.getRow() + i < height; i++) {
                if (scoreBoard[w.getRow()][w.getCol() + i] == 's') {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * The checkIfConnected function checks if the word is connected to any other words on the board.
     * <p>
     *
     * @param w w Get the row and column of the word
     *
     * @return True if the word is connected to another word on the board
     *
     */
    private boolean checkIfConnected(Word w) {
        int CurRow = w.getRow();
        int CurCol = w.getCol();
        for (int i = 0; i < w.getTiles().length; i++) {
            if (CurRow - 1 >= 0 && mainBoard[CurRow - 1][CurCol] != null)
                return true;
            if (CurRow + 1 >= 0 && mainBoard[CurRow + 1][CurCol] != null)
                return true;
            if (CurCol - 1 >= 0 && mainBoard[CurRow][CurCol - 1] != null)
                return true;
            if (CurCol + 1 >= 0 && mainBoard[CurRow][CurCol + 1] != null)
                return true;
            if (w.isVertical())
                CurRow += 1;
            else
                CurCol += 1;
        }
        return false;
    }

    /**
     * The notRequireLetterReplacement function checks to see if the word being placed on the board
     * requires any letter replacement. If it does not, then this function returns true. Otherwise,
     * it returns false. This is done by checking each tile in a word and seeing if there is already
     * a tile in that position on the mainBoard array or not. If there isn't one, then we know that
     * no letters need to be replaced for this particular word placement, and so we return true; otherwise,
     * we return false because at least one letter needs to be replaced for this particular word placement.
     *<p>
     * @param word word Pass the word that is being placed on the board
     *
     * @return True if the word does not require letter replacement
     *
     */
    private boolean notRequireLetterReplacement(Word word) {
        int i;
        if (word.isVertical()) {
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (mainBoard[word.getRow() + i][word.getCol()] != null)
                        return false;
                } else if (word.getTiles()[i] == null && mainBoard[word.getRow() + i][word.getCol()] == null)
                    return false;
            }
        } else
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (mainBoard[word.getRow()][word.getCol() + i] != null)
                        return false;
                } else if (word.getTiles()[i] == null && mainBoard[word.getRow()][word.getCol() + i] == null) {
                    return false;
                }
            }
        return true;
    }

    private boolean dictionaryLegal(Word w) {
        return true;
    }

    /**
     * The checkWordNull function takes in a Word object and returns a new Word object.
     * The new word is the same as the old one, except that any null tiles are replaced with
     * tiles from the main board. This function is used to check if words are valid when they contain blanks.
     *<p>
     * @param w w Get the row and column of the word
     *
     * @return A new word object with the same row, col and vertical as w
     *
     */
    private Word checkWordNull(Word w) {
        Tile[] newWord = new Tile[w.getTiles().length];
        if (w.isVertical()) {
            for (int j = 0; j < w.getTiles().length; j++) {
                if (w.getTiles()[j] != null)
                    newWord[j] = w.getTiles()[j];
                else
                    newWord[j] = mainBoard[w.getRow() + j][w.getCol()];
            }
        } else {
            for (int j = 0; j < w.getTiles().length; j++) {
                if (w.getTiles()[j] != null)
                    newWord[j] = w.getTiles()[j];
                else
                    newWord[j] = mainBoard[w.getRow()][w.getCol() + j];
            }
        }
        return new Word(newWord, w.getRow(), w.getCol(), w.isVertical());
    }

    /**
     * The checkVerticalWord function checks the vertical word that is formed by placing a tile at
     * (row, col) on the board. It returns a Word object containing all the tiles in this word.
     *<p>
     * @param row row Determine the row of the tile that is being placed
     * @param col col Determine the column of the tile that is being placed
     * @param tile tile Add the tile that is being placed to the word
     *
     * @return A word object
     *
     */
    private Word checkVerticalWord(int row, int col, Tile tile) {
        int curCol = row;
        int rowBegin;
        while (curCol - 1 >= 0 && mainBoard[curCol - 1][col] != null) {
            curCol--;
        }

        rowBegin = curCol;
        if (mainBoard[curCol][col] == null) curCol++;
        ArrayList<Tile> temp = new ArrayList<>();
        while (curCol < 15 && curCol < row && mainBoard[curCol][col] != null) {
            temp.add(mainBoard[curCol][col]);
            curCol++;
        }
        temp.add(tile);
        curCol = row + 1;
        while (curCol < 15 && mainBoard[curCol][col] != null) {
            temp.add(mainBoard[curCol][col]);
            curCol++;
        }
        Tile[] tiles = new Tile[temp.size()];
        for (int j = 0; j < temp.size(); j++) tiles[j] = temp.get(j);
        return new Word(tiles, rowBegin, col, true);
    }


    /**
     * The checkHorizontalWord function checks the horizontal word that is formed by a tile placed on the board.
     *<p>
     *
     * @param row row Determine the row that the tile is being placed in
     * @param col col Determine the column of the tile that is being placed
     * @param tile tile Add the tile to the word
     *
     * @return A word object
     *

     */
    private Word checkHorizontalWord(int row, int col, Tile tile) {
        int curCol = col;
        while (curCol > 0 && mainBoard[row][curCol - 1] != null) {
            curCol--;
        }

        int colBegin = curCol;
        ArrayList<Tile> temp = new ArrayList<>();
        while (curCol < mainBoard[row].length && mainBoard[row][curCol] != null) {
            temp.add(mainBoard[row][curCol]);
            curCol++;
        }
        temp.add(tile);
        curCol = col + 1;
        while (curCol < mainBoard[row].length && mainBoard[row][curCol] != null) {
            temp.add(mainBoard[row][curCol]);
            curCol++;
        }
        Tile[] tiles = new Tile[temp.size()];
        for (int j = 0; j < temp.size(); j++) {
            tiles[j] = temp.get(j);
        }
        return new Word(tiles, row, colBegin, false);
    }

    /**
     * The getWords function takes in a Word object and returns an ArrayList of all the words that are formed by placing
     * the tiles from this word on the board. The first element of this ArrayList is always w itself, since it is guaranteed
     * to be a valid word. If there are no other words formed by placing w on the board, then getWords will return an
     * ArrayList with only one element: w itself. Otherwise, if there are other words formed by placing w on the board,
     * then these additional words will be added to newArrayWord after being checked for validity using checkVerticalWord or
     * checkHorizontalWord.
     *<p>
     * @param w w Check if the word is vertical or horizontal
     *
     * @return An arraylist of word objects
     *
     */
    public ArrayList<Word> getWords(Word w) {
        ArrayList<Word> newArrayWord = new ArrayList<Word>();
        newArrayWord.add(checkWordNull(w));
        if (!w.isVertical()) {
            for (int i = 0; i < w.getTiles().length; i++) {
                if (w.getTiles()[i] != null) {
                    if (mainBoard[w.getRow() - 1][w.getCol() + i] != null || mainBoard[w.getRow() + 1][w.getCol() + i] != null) {
                        newArrayWord.add(checkVerticalWord(w.getRow(), w.getCol() + i, w.getTiles()[i]));
                    }
                }
            }

        } else {
            for (int j = 0; j < w.getTiles().length; j++) {
                if (w.getTiles()[j] != null) {
                    if (mainBoard[w.getRow() + j][w.getCol() - 1] != null || mainBoard[w.getRow() + j][w.getCol() + 1] != null) {
                        newArrayWord.add(checkHorizontalWord(w.getRow() + j, w.getCol(), w.getTiles()[j]));
                    }
                }
            }

        }
        return newArrayWord;
    }

    /**
     * The getScore function takes a Word object as an argument and returns the score of the all the words that are formed
     *<p>
     *
     * @param w w Pass the word that is being scored
     *
     * @return The score of the all the words that are formed by placing the tiles from this word on the board
     *
     */
    int getScore(Word w) {
        int sum = 0;
        sum += wordScore(w);
        return sum;
    }

    /**
     * The wordScore function takes a Word object as an argument and returns the score of that word.
     * The function first initializes two variables, sum and r, to 0 and 1 respectively.
     * It then checks if the word is vertical or horizontal by checking its boolean value in the Word class.
     * If it is vertical, it loops through each tile in w's tiles array (which contains all of its letters) from index 0 to length-2 (inclusive).
     * For each letter in w's tiles array: 	If there is no letter at that position on mainBoard (i.e., if w has placed a new letter there),
     * then the score of that letter is multiplied by 2 or 3 depending on the value of the letter at that position on scoreBoard.
     *<p>
     * @param w w Get the row and column of the word
     *
     * @return The score of a word
     *
     */
    private int wordScore(Word w) {
        int sum = 0;
        int r = 1, y = 1;
        if (w.vertical) {
            for (int j = 0; j < w.tiles.length; j++) {
                switch (scoreBoard[w.row + j][w.col]) {
                    case 'p':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow() + j][w.col]._score * 2;
                        } else {
                            sum += w.tiles[j]._score * 2;
                        }
                        continue;
                    case 'b':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow() + j][w.col]._score * 3;
                        } else {
                            sum += w.tiles[j]._score * 3;
                            continue;
                        }
                    case 'y': {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow()][w.col + j]._score;
                        else sum += w.tiles[j]._score;
                        y *= 2;
                        continue;
                    }
                    case 'r': {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow()][w.col + j]._score;
                        else sum += w.tiles[j]._score;
                        r *= 3;
                        continue;
                    }
                    case 's':
                        if (!wScount) {
                            wScount = true;
                            y *= 2;
                        }
                    default: {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow() + j][w.col]._score;
                        else sum += w.tiles[j]._score;
                    }
                }
            }
        } else {
            for (int j = 0; j < w.tiles.length; j++) {
                switch (scoreBoard[w.row][w.col + j]) {
                    case 'p':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow()][w.col + j]._score * 2;
                            continue;
                        } else {
                            sum += w.tiles[j]._score * 2;
                            continue;
                        }
                    case 'b':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow()][w.col + j]._score * 3;
                            continue;
                        } else {
                            sum += w.tiles[j]._score * 3;
                            continue;
                        }
                    case 'y': {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow()][w.col + j]._score;
                        else sum += w.tiles[j]._score;
                        y *= 2;
                        continue;
                    }
                    case 'r': {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow()][w.col + j]._score;
                        else sum += w.tiles[j]._score;
                        r *= 3;
                        continue;
                    }
                    case 's':
                        if (!wScount) {
                            wScount = true;
                            y *= 2;
                        }
                    default: {
                        if (w.tiles[j] == null) sum += mainBoard[w.getRow()][w.col + j]._score;
                        else sum += w.tiles[j]._score;
                    }
                }
            }
        }
        sum *= y;
        sum *= r;
        return sum;
    }

    /**
     * The tryPlaceWord function takes in a Word object and checks if the word is legal.
     * If it is, then it places the word on the board and returns the score of all the words formed by placing the word on the board.
     * <p>
     *
     * @param w w Determine if the word is legal on the board
     *
     * @return The sum of the scores of all words formed by placing w on the board
     *
     * @see #dictionaryLegal(Word)
     * @see #boardLegal(Word)
     * @see #getWords(Word)
     * @see #getScore(Word)

     */
    public int tryPlaceWord(Word w) {
        int sum = 0;
        if (!dictionaryLegal(w)) return 0;
        if (!boardLegal(w)) return 0;
        ArrayList<Word> newWord = getWords(w);
        for (Word word : newWord) {
            if (dictionaryLegal(word)) {
                sum += getScore(word);
                wordCounter++;
            } else return 0;
        }
        placeWord(w);
        return sum;
        }

   private void placeWord(Word w) {
        for (int i = 0; i < w.getTiles().length; i++) {
            if (w.isVertical()) {
                if (w.tiles[i] == null) continue;
                mainBoard[w.getRow() + i][w.col] = w.tiles[i];
            } else {
                if (w.tiles[i] == null) continue;
                mainBoard[w.getRow()][w.col + i] = w.tiles[i];
            }
        }
    }
}


