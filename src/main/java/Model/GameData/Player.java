package Model.GameData;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String _name;
    int _score;
    List<Tile> _hand;

    Player(String name){
        this._name = name;
        this._score = 0;
        this._hand = new ArrayList<>();
    }

    public String get_name() {
        return _name;
    }

    public int get_score() {
        return _score;
    }

    public List<Tile> get_hand() {
        return _hand;
    }

    public void set_score(int _score) {
        this._score = _score;
    }

    private void addTilesTo7() {
        while (_hand.size() < 7) {
            _hand.add(Tile.Bag.getBag().getRand());
        }
    }
}
