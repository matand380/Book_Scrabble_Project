package Model.GameData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player implements Serializable, ObjectFactory {
    private int _index;
    private String _socketID; //socketID of the player
    private String _name;
    private int _score;
    private List<Tile> _hand;
    private String tileLottery;

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
        this._socketID = "";
        this._hand = new ArrayList<>();
    }

    /**
     * The addTilesTo7 function adds tiles to the hand until it has 7 tiles.
     */
    public String completeTilesTo7() {
        while (this.get_hand().size() < 7) {
            if (Tile.Bag.getBag().size() == 0)
                break;
            this.get_hand().add(Tile.Bag.getBag().getRand());
        }
        return this._socketID;
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
        return Tile.Bag.getBag().size() == 0 && this.get_hand().isEmpty();

    }

    /**
     * The charToTile function takes in a character and returns the tile with that letter from the hand of the player.
     *
     * @param c Find the tile with the corresponding letter
     * @return The tile with the given letter
     */
    public Tile charToTile(char c) {
        for (Tile t : this.get_hand()) {
            if (t.letter == c) {
                return t;
            }
        }
        return null;
    }

    /**
     * The getTileLottery function is used to get a random tile from the bag.
     *
     * @return A tile from the bag
     */
    public String getTileLottery() {
        return tileLottery;
    }

    public void setTileLottery() {
        Random rand = new Random();
        int value = rand.nextInt(26) + 'A';
        char c = (char) value;
        tileLottery = String.valueOf(c);
    }

    /**
     * The get_index function returns the index of the current node.
     *
     * @return The _index variable
     */
    public int get_index() {
        return _index;
    }

    /**
     * The set_index function sets the index of a player.
     *
     * @param _index Set the index of the player
     * @return Null
     */
    public void set_index(int _index) {
        this._index = _index;
    }

    /**
     * The get_socketID function returns the _socketID of the user.
     *
     * @return The id of the student
     */
    public String get_socketID() {
        return this._socketID;
    }

    /**
     * The set_socketID function sets the value of the _socketID variable.
     * <p>
     * Set the _socketID of a particular row in the database
     */
    public void set_socketID(String socketID) {
        this._socketID = socketID;

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

    public void set_hand(List<Tile> _hand) {
        this._hand = _hand;
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
        if (_score >= 0)
            this._score = _score;
        else {
            this._score = 0;
        }
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
     *             Nothing
     */
    public void set_name(String name) {
        this._name = name;
    }

    /**
     * The updateHand function is used to update the hand of a player
     * when receiving a new hand from the server.
     * <p>
     *
     * @param inObject Update the hand of a player
     */
    public void updateHand(List<Tile> inObject) {
        this.get_hand().clear();
        this.get_hand().addAll(inObject);
    }
}
