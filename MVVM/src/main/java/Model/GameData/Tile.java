package test;

import java.util.Objects;
import java.util.Random;

public class Tile {
    private Tile(char _letter, int _score) {
        this.letter = _letter;
        this._score = _score;
    }

    public
    final char letter;
    final int _score;

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
        int[] _quantitiestile;
        final int[] _startArray = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        Tile[] _rand;
        private static Bag single_instance = null;

        private Bag() {
            _quantitiestile = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
            _rand = new Tile[26];
            _rand[0] = new Tile('A', 1);
            _rand[1] = new Tile('B', 3);
            _rand[2] = new Tile('C', 3);
            _rand[3] = new Tile('D', 2);
            _rand[4] = new Tile('E', 1);
            _rand[5] = new Tile('F', 4);
            _rand[6] = new Tile('G', 2);
            _rand[7] = new Tile('H', 4);
            _rand[8] = new Tile('I', 1);
            _rand[9] = new Tile('J', 8);
            _rand[10] = new Tile('K', 5);
            _rand[11] = new Tile('L', 1);
            _rand[12] = new Tile('M', 3);
            _rand[13] = new Tile('N', 1);
            _rand[14] = new Tile('O', 1);
            _rand[15] = new Tile('P', 3);
            _rand[16] = new Tile('Q', 10);
            _rand[17] = new Tile('R', 1);
            _rand[18] = new Tile('S', 1);
            _rand[19] = new Tile('T', 1);
            _rand[20] = new Tile('U', 1);
            _rand[21] = new Tile('V', 4);
            _rand[22] = new Tile('W', 4);
            _rand[23] = new Tile('X', 8);
            _rand[24] = new Tile('Y', 4);
            _rand[25] = new Tile('Z', 10);
        }

        public Tile getRand() {
            if (size() == 0)
                return null;
            Random rn = new Random();
            int index = rn.nextInt(26);
            if (_quantitiestile[index] == 0)
                return null;
            _quantitiestile[index]--;
            return _rand[index];
        }

        Tile getTile(char c) {
            if (size() == 0)
                return null;
            if(c<65||c>90)
                return null;
            int index = (c - 65);
            if (_quantitiestile[index] == 0)
                return null;
            _quantitiestile[index]--;
            return _rand[index];
        }


        void put(Tile t) {
            int index = (t.letter - 65);
            if(t.letter<'A'||t.letter>'Z')
                return;
            if (_quantitiestile[index] == _startArray[index])
                return;
            _quantitiestile[index]++;
        }

        int size() {
            int sizeq = 0;
            for (int i = 0; i < 26; i++) {
                sizeq += (_quantitiestile[i]);
            }
            return sizeq;
        }

        public int[] getQuantities() {
            return this._quantitiestile.clone();
        }

        public static Bag getBag() {
            if (single_instance == null)
                return single_instance = new Bag();
            return single_instance;
        }

    }

}

