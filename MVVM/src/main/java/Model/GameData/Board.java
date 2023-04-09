package test;

import java.util.ArrayList;

public class Board {
    Tile[][] mainBoard;
    char[][] scoreBoard;
    int count = 0;
    boolean wScount = false;

    ArrayList<Word> wordOnBoard;

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

    public static Board getBoard() {
        if (single_instance == null)
            return single_instance = new Board();
        return single_instance;
    }

    Tile[][] getTiles() {
        return mainBoard.clone();
    }

    public boolean boardLegal(Word w) {
        boolean flag = true;
        if (checkIfInside(w)) {
            if (count == 0)
                return checkFirstWord(w);
            if (count >= 1) {
                flag = checkNeighbors(w);
                flag = checkIfConect(w);
            }
            if (w.isVertical() && flag) {
                if (w.getRow() + w.getTiles().length <= 14) {
                    return true;//check if the col+length or the row+length are outside the board
                }
            }
            if (!w.isVertical() && flag) {
                return w.getCol() + w.getTiles().length <= 14;
            }
        }
        return false;
    }

    private boolean checkFirstWord(Word w) {
        if (w.vertical && w.getRow() + w.getTiles().length <= 14) {
            for (int i = 0; w.getCol() + i < 15; i++) {
                if (scoreBoard[w.getRow() + i][w.getCol()] == 's') {
                    return true;
                }
            }
        }
        if (!w.vertical && w.getCol() + w.getTiles().length <= 14) {
            for (int i = 0; w.getRow() + i < 15; i++) {
                if (scoreBoard[w.getRow()][w.getCol() + i] == 's') {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfInside(Word w) {
        //check if the col or the row are outside the board
        return w.getCol() <= 14 && w.getRow() <= 14 && w.getCol() >= 0 && w.getRow() >= 0;
    }

    private boolean checkIfConect(Word w) {
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

    private boolean checkNeighbors(Word w) {
        if (w.isVertical()) {
            for (int i = 0; i < w.getTiles().length; i++) {
                if (w.getRow() + i + 1 <= 14 && mainBoard[w.getRow() + i + 1][w.getCol()] != null || w.getRow() + i - 1 >= 0 && mainBoard[w.getRow() + i - 1][w.getCol()] != null
                        || w.getCol() + 1 <= 14 && mainBoard[w.getRow() + i][w.getCol() + 1] != null || w.getCol() - 1 >= 0 && mainBoard[w.getRow() + i][w.getCol() - 1] != null)
                    return true;
            }
        } else {
            for (int i = 0; i < w.getTiles().length; i++) {
                if (w.getCol() + i + 1 <= 14 && mainBoard[w.getRow()][w.getCol() + i + 1] != null || w.getCol() + i - 1 >= 0 && mainBoard[w.getRow()][w.getCol() + i - 1] != null
                        || w.getRow() + 1 <= 14 && mainBoard[w.getRow() + 1][w.getCol() + i] != null || w.getRow() - 1 >= 0 && mainBoard[w.getRow() - 1][w.getCol() + i] != null)
                    return true;
            }
        }
        return false;
    }

    private boolean dictionaryLegal(Word w) {
        return true;
    }

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
        Word currectWord = new Word(newWord, w.getRow(), w.getCol(), w.isVertical());
        return currectWord;
    }

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

    private Word checkHorizontalWord(int row, int col, Tile tile) {
        int curCol = col;
        int colBegin;
        while (curCol - 1 >= 0 && mainBoard[row][curCol - 1] != null) {
            curCol--;
        }

        colBegin = curCol;
        if (mainBoard[row][curCol] == null) curCol++;
        ArrayList<Tile> temp = new ArrayList<>();
        while (curCol < 15 && curCol < row && mainBoard[row][curCol] != null) {
            temp.add(mainBoard[row][curCol]);
            curCol++;
        }
        temp.add(tile);
        curCol = row + 1;
        while (curCol < 15 && mainBoard[row][curCol] != null) {
            temp.add(mainBoard[row][curCol]);
            curCol++;
        }
        Tile[] tiles = new Tile[temp.size()];
        for (int j = 0; j < temp.size(); j++) tiles[j] = temp.get(j);
        return new Word(tiles, row, colBegin, false);
    }

    public ArrayList<Word> getWord(Word w) {
        ArrayList<Word> newArrayWord = new ArrayList<Word>();
        Tile[] newWord = new Tile[w.getTiles().length];
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

    int getScore(Word w) {
        int sum = 0;
        sum += wordScore(w);
        return sum;
    }

    private int wordScore(Word w) { //Checking on the scoreBoard if the word landed on a special slot
        int sum = 0;
        int r = 1, y = 1;
        if (w.vertical) {
            for (int j = 0; j < w.tiles.length; j++) {
                switch (scoreBoard[w.row + j][w.col]) {
                    case 'p':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow() + j][w.col]._score * 2;
                            continue;
                        } else {
                            sum += w.tiles[j]._score * 2;
                            continue;
                        }
                    case 'b':
                        if (w.tiles[j] == null) {
                            sum += mainBoard[w.getRow() + j][w.col]._score * 3;
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

    int tryPlaceWord(Word w) {
        int sum = 0;
        if (!dictionaryLegal(w)) return 0;
        if (!boardLegal(w)) return 0;
        ArrayList<Word> newWord = getWord(w);
        for (Word word : newWord) {
            if (dictionaryLegal(word)) {
                sum += getScore(word);
                count++;
            } else return 0;
        }
        for (int i = 0; i < w.getTiles().length; i++) {
            if (w.isVertical()) {
                if (w.tiles[i] == null) continue;
                mainBoard[w.getRow() + i][w.col] = w.tiles[i];
            } else {
                if (w.tiles[i] == null) continue;
                mainBoard[w.getRow()][w.col + i] = w.tiles[i];
            }
        }
        return sum;
    }
}

