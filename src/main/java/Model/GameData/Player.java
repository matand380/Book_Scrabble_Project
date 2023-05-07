package Model.GameData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable, ObjectFactory {
    public int _id;
    String _name;
    int _score;
    List<Tile> _hand;
    Tile tileLottery;

    /**
     * The Player function is a constructor for the Player class.
     * It takes in a String name and sets it as the player's name,
     * initializes their score to 0, and creates an empty ArrayList of tiles called hand.
     *
     * <p>
     *
     * @ name Set the name of the player
     */
    public Player() {
        this._name = "Default";
        this._score = 0;
        this._hand = new ArrayList<>();
    }

    /**
     * The get_name function returns the name of the person.
     * <p>
     *
     * @return The name of the player
     */
    public String get_name() {
        return _name;
    }

    /**
     * The set_name function sets the name of the person.
     *
     * @param name Set the name of the contact
     * @return Nothing
     */
    public void set_name(String name) {
        this._name = name;
    }

    /**
     * The get_score function returns the score of the player.
     * <p>
     *
     * @return The score of the player
     */
    public int get_score() {
        return _score;
    }

    /**
     * The set_score function sets the score of a player.
     * <p>
     *
     * @param _score Set the score of the player
     */
    public void set_score(int _score) {
        this._score = _score;
    }

    /**
     * The get_hand function returns the hand of a player.
     * <p>
     *
     * @return The list of tiles in the hand
     */
    public List<Tile> get_hand() {
        return _hand;
    }

    /**
     * The addTilesTo7 function adds tiles to the hand until it has 7 tiles.
     */
    public void completeTilesTo7() {
        while (_hand.size() < 7) {
            if (Tile.Bag.getBag().size() == 0)
                break;
            _hand.add(Tile.Bag.getBag().getRand());
        }
    }

    public int get_id() {
        return _id;
    }

    /**
     * The set_id function sets the value of the _id variable.
     * @param int _id Set the id of a particular row in the database
     * @return A void, which means that it does not return anything
     */
    public void set_id(int _id) {
        this._id = _id;
    }


    /**
     * The getTileLottery function is used to get a random tile from the bag.
     *
     * @return A tile from the bag
     */
    public Tile getTileLottery() {

        tileLottery = Tile.Bag.getBag().getRand();
        return tileLottery;
    }

    /**
     * The create function is used to create a new instance of the Player class.
     *
     * @return A new player object
     */
    @Override
    public Player create() {
        return new Player();
    }

    /**
     * The isRackEmpty function is used to check if the player's rack is empty and the bag is empty
     * as well.
     *
     * @return True if the player's rack is empty, false otherwise
     */
    public boolean isRackEmpty() {
        return Tile.Bag.getBag().size() == 0 && _hand.isEmpty();

    }

    public Tile charToTile(char c) {
        for (Tile t : _hand) {
            if (t.letter == c) {
                return t;
            }
        }
        return null;

    }
}
