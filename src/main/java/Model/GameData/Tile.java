package Model.GameData;

import java.util.Objects;
import java.util.Random;

public class Tile {
    public
    final char letter;
    final int _score;

    private Tile(char _letter, int _score) {
        this.letter = _letter;
        this._score = _score;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return letter == tile.letter && _score == tile._score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, _score);
    }

    public static class Bag {
        public
        int[] _quantitiesCounter;
        final int[] _defaultQuantities = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        Tile[] _tilesArray;
        private static Bag bagInstance = null;

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

        public Tile getRand() {
            if (size() > 0) {
                int indexTile = new Random().nextInt(26);
                if (_quantitiesCounter[indexTile] != 0) {
                    _quantitiesCounter[indexTile]--;
                    return _tilesArray[indexTile];
                } else {
                    while (indexTile <= _defaultQuantities.length) {
                        indexTile++;
                        if (indexTile == _defaultQuantities.length)
                            indexTile = _defaultQuantities[0];
                        if (_quantitiesCounter[indexTile] != 0) {
                            _quantitiesCounter[indexTile]--;
                            return _tilesArray[indexTile];
                        }
                    }
                }
            }
            return null;
        }

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

        public void put(Tile t) {
            if (size() < 98) {
                if (t.letter >= 'A' && t.letter <= 'Z') {
                    int tileToInt = t.letter - 'A';
                    if (_quantitiesCounter[tileToInt] < _defaultQuantities[tileToInt])
                        _quantitiesCounter[tileToInt]++;
                }
            }
        }

        int size() {
            int count = 0;
            for (int quantity : _quantitiesCounter) {
                count += quantity;
            }
            return count;
        }

        public int[] getQuantities() {
            return this._quantitiesCounter.clone();
        }

        public static Bag getBag() {
            if (bagInstance == null)
                return bagInstance = new Bag();
            return bagInstance;
        }
    }
}

