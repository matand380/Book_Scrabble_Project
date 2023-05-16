package Model.GameData;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class Tile implements Serializable, ObjectFactory {
    public char getLetter() {
        return letter;
    }

    public int get_score() {
        return _score;
    }

     final char letter;
     final int _score;


    public Tile() {
        this.letter = ' ';
        this._score = 0;
    }

    /**
     * The Tile function is a constructor for the Tile class.
     * It takes in two parameters, a character and an integer.
     * The character represents the letter of the tile, while
     * the integer represents its score value.
     * <p>
     *
     * @param _letter _letter Set the letter of the tile
     * @param _score  _score Set the score of a tile
     */
    public Tile(char _letter, int _score) {
        this.letter = _letter;
        this._score = _score;
    }

    public int getScore() {
        return _score;
    }

    /**
     * The equals function is used to compare two objects.
     * In this case, we are comparing two tiles.
     * The function returns true if the letter and score of both tiles are equal, otherwise it returns false.
     * <p>
     *
     * @param o o Compare the object passed in to the current instance of tile
     * @return True if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return letter == tile.letter && _score == tile._score;
    }

    /**
     * The hashCode function is used to generate a unique hash value for each
     * instance of the Letter class. This function uses the Objects.hash() method,
     * which takes in an arbitrary number of arguments and returns an integer that
     * represents a unique hash code for all instances where the same arguments are passed in.
     * <p>
     *
     * @return The letter and score of the tile
     */
    @Override
    public int hashCode() {
        return Objects.hash(letter, _score);
    }

    /**
     * The create function is used to create a new instance of the Tile class.
     *
     * @return A tile object
     */
    @Override
    public Object create() {
        return new Tile();
    }

    public static class Bag implements Serializable, ObjectFactory {
        public
        int[] _quantitiesCounter;
        final int[] _defaultQuantities = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        Tile[] _tilesArray;
        private static Bag bagInstance = null;

        /**
         * The Bag function is a constructor that creates an array of 26 tiles, each with its own letter and point value.
         * The function also creates an array of integers that keeps track of how many tiles are left in the bag for each letter.
         */
        private Bag() {
            this._quantitiesCounter = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
            this._tilesArray = new Tile[26];
            _tilesArray[0] = new Tile('A', 1);
            _tilesArray[1] = new Tile('B', 3);
            _tilesArray[2] = new Tile('C', 3);
            _tilesArray[3] = new Tile('D', 2);
            _tilesArray[4] = new Tile('E', 1);
            _tilesArray[5] = new Tile('F', 4);
            _tilesArray[6] = new Tile('G', 2);
            _tilesArray[7] = new Tile('H', 4);
            _tilesArray[8] = new Tile('I', 1);
            _tilesArray[9] = new Tile('J', 8);
            _tilesArray[10] = new Tile('K', 5);
            _tilesArray[11] = new Tile('L', 1);
            _tilesArray[12] = new Tile('M', 3);
            _tilesArray[13] = new Tile('N', 1);
            _tilesArray[14] = new Tile('O', 1);
            _tilesArray[15] = new Tile('P', 3);
            _tilesArray[16] = new Tile('Q', 10);
            _tilesArray[17] = new Tile('R', 1);
            _tilesArray[18] = new Tile('S', 1);
            _tilesArray[19] = new Tile('T', 1);
            _tilesArray[20] = new Tile('U', 1);
            _tilesArray[21] = new Tile('V', 4);
            _tilesArray[22] = new Tile('W', 4);
            _tilesArray[23] = new Tile('X', 8);
            _tilesArray[24] = new Tile('Y', 4);
            _tilesArray[25] = new Tile('Z', 10);
        }

        /**
         * The getRand function returns a random tile from the bag.
         * <p>
         *
         * @return A random tile from the bag
         */
        public Tile getRand() {
            if (size() > 0) {
                Random rand = new Random();
                Set<Integer> nonEmptyIndices = new HashSet<>();
                for (int i = 0; i < _quantitiesCounter.length; i++) {
                    if (_quantitiesCounter[i] > 0) {
                        nonEmptyIndices.add(i);
                    }
                }
                if (nonEmptyIndices.isEmpty()) {
                    return null;
                }
                int randomIndex = (int) nonEmptyIndices.toArray()[rand.nextInt(nonEmptyIndices.size())];
                _quantitiesCounter[randomIndex]--;
                return _tilesArray[randomIndex];
            }
            return null;
        }

        /**
         * The getTile function returns a tile from the bag.
         * <p>
         *
         * @param c c Determine the index of the tile in the array
         * @return The tile that corresponds to the letter c
         */
        public Tile getTile(char c) {
            if (size() > 0) {
                if (c >= 'A' && c <= 'Z') {
                    int indexTile = c - 'A';
                    if (_quantitiesCounter[indexTile] != 0) {
                        _quantitiesCounter[indexTile]--;
                        return _tilesArray[indexTile];
                    }
                } else return null;
            }
            return null;
        }

        /**
         * The put function adds a tile to the bag.
         * <p>
         *
         * @param t t Determine the letter of the tile to be put into the bag
         */
        public void put(Tile t) {
            if (size() < 98) {
                if (t.letter >= 'A' && t.letter <= 'Z') {
                    int tileToInt = t.letter - 'A';
                    if (_quantitiesCounter[tileToInt] < _defaultQuantities[tileToInt])
                        _quantitiesCounter[tileToInt]++;
                }
            }
        }

        /**
         * The size function returns the number of items in the bag.
         * <p>
         *
         * @return The number of elements in the bag
         */
        public int size() {
            int count = 0;
            for (int quantity : _quantitiesCounter) {
                count += quantity;
            }
            return count;
        }

        /**
         * The getQuantities function returns a copy of the quantities array.
         * <p>
         *
         * @return The quantities array
         */
        public int[] getQuantities() {
            return this._quantitiesCounter.clone();
        }


        private static class BagHolder {
            private static final Bag bagInstance = new Bag();
        }

        /**
         * The getBag function is a static function that returns the singleton instance of the Bag class.
         * <p>
         *
         * @return A bag object
         */
        public static Bag getBag() {
            return BagHolder.bagInstance;
        }

        public void remove() {
            //remove single tile from bag
            for (int i = 0; i < _quantitiesCounter.length; i++) {
                if (_quantitiesCounter[i] > 0) {
                    _quantitiesCounter[i]--;
                    break;
                }
            }

        }

        @Override
        public Bag create() {
            return Bag.getBag();
        }
    }
}

