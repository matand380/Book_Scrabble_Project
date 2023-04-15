package Model.GameData;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String _name;
    int _score;
    List<Tile> _hand;

    /**
     * The Player function is a constructor for the Player class.
     * It takes in a String name and sets it as the player's name,
     * initializes their score to 0, and creates an empty ArrayList of tiles called hand.

     *<p>
     * @param name Set the name of the player
     *
     */
    public Player(String name){
        this._name = name;
        this._score = 0;
        this._hand = new ArrayList<>();
    }

    /**
     * The get_name function returns the name of the person.
     *<p>
     *
     * @return The name of the student
     *
     */
    public String get_name() {
        return _name;
    }

    /**
     * The get_score function returns the score of the player.
     *<p>
     *
     * @return The score of the player
     *
     */
    public int get_score() {
        return _score;
    }

    /**
     * The get_hand function returns the hand of a player.
     *<p>
     * @return The list of tiles in the hand
     *
     */
    public List<Tile> get_hand() {
        return _hand;
    }

    /**
     * The set_score function sets the score of a player.
     *<p>
     * @param _score Set the score of the player
     *
     *
     */
    public void set_score(int _score) {
        this._score = _score;
    }

    /**
     * The addTilesTo7 function adds tiles to the hand until it has 7 tiles.
     */
    public void addTilesTo7() {
        while (_hand.size() < 7) {
            _hand.add(Tile.Bag.getBag().getRand());
        }
    }
}
