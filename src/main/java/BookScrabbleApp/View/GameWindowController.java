package BookScrabbleApp.View;

import BookScrabbleApp.*;
import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import java.net.*;
import java.util.*;
import java.util.function.*;

public class GameWindowController implements Observer, Initializable {

    @FXML
    private Label namePlayer1;
    @FXML
    private Label namePlayer2;
    @FXML
    private Label namePlayer3;
    @FXML
    private Label namePlayer4;
    @FXML
    private Label scorePlayer1;
    @FXML
    private Label scorePlayer2;
    @FXML
    private Label scorePlayer3;
    @FXML
    private Label scorePlayer4;
    @FXML
    GridPane handGrid = new GridPane();
    @FXML
    GridCanvas gameBoard = new GridCanvas();
    @FXML
    GridPane yourWord = new GridPane();
    @FXML
    Button startNewGameBtn;

    private Map<String, Consumer<String>> updatesMap; //map of all the updates

    BS_Host_ViewModel hostViewModel;

    BS_Guest_ViewModel guestViewModel;

    private List<Label> scoresFields;

    private List<TileField> handFields;

    List<List<TileField>> boardFields;

    public static TileField selectedTileField;

    private Map<KeyCode, EventHandler<KeyEvent>> keyEventsMap;

    private List<TileField> wordForTryPlace;

    public void setViewModel(BS_ViewModel ViewModel) {
        if (ViewModel instanceof BS_Host_ViewModel) {
            this.hostViewModel = (BS_Host_ViewModel) ViewModel;
            hostViewModel.addObserver(this);
            initializeWindow();
        } else if (ViewModel instanceof BS_Guest_ViewModel) {
            this.guestViewModel = (BS_Guest_ViewModel) ViewModel;
            guestViewModel.addObserver(this);
        }
    }

    public void initializeWindow() {
        //bind the scores to the text fields
        for (int i = 0; i < hostViewModel.viewableScores.size(); i++) {
            hostViewModel.viewableScores.get(i).bind(scoresFields.get(i).textProperty());
        }

        for (int i = 0; i < 7; i++) {
            handFields.add(new TileField());
        }

        //bind the hand to the hand fields
        for (int i = 0; i < hostViewModel.viewableHand.size(); i++) {
            hostViewModel.viewableHand.get(i).letterProperty().bindBidirectional(handFields.get(i).letter.textProperty());
            hostViewModel.viewableHand.get(i).scoreProperty().bindBidirectional(handFields.get(i).score.textProperty());
        }

        for (int i = 0; i < 15; i++) {
            boardFields.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                boardFields.get(i).add(new TileField());
            }
        }
        //bind the board to the board fields
        for (int boardRow = 0; boardRow < hostViewModel.viewableBoard.size(); boardRow++) {
            for (int boardCol = 0; boardCol < hostViewModel.viewableBoard.get(boardRow).size(); boardCol++) {
                hostViewModel.viewableBoard.get(boardRow).get(boardCol).letterProperty().bindBidirectional(boardFields.get(boardRow).get(boardCol).letter.textProperty());
                hostViewModel.viewableBoard.get(boardRow).get(boardCol).scoreProperty().bindBidirectional(boardFields.get(boardRow).get(boardCol).score.textProperty());
            }
        }
        //to be removed later on
        namePlayer1.setText(HostController.name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scoresFields = new ArrayList<>();
        handFields = new ArrayList<>();
        boardFields = new ArrayList<>();
        updatesMap = new HashMap<>();
        wordForTryPlace = new ArrayList<>();
        scoresFields.add(scorePlayer1);
        scoresFields.add(scorePlayer2);
        scoresFields.add(scorePlayer3);
        scoresFields.add(scorePlayer4);

        initializeUpdateMap();
        initializeKeyEventMap();


        for (int boardRow = 0; boardRow < 15; boardRow++) {
            boardFields.add(new ArrayList<>());
            for (int boardCol = 0; boardCol < 15; boardCol++) {
                boardFields.get(boardRow).add(new TileField());
            }
        }
        gameBoard.setTileFields(boardFields);

        //focus on the board
        gameBoard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                gameBoard.requestFocus();
            }
        });

        gameBoard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEventsMap.containsKey(keyEvent.getCode())) {
                    keyEventsMap.get(keyEvent.getCode()).handle(keyEvent);
                } else
                    System.out.println("key not found");
            }
        });
    }

    @FXML
    protected void onTryButtonClick() {
        if (wordForTryPlace.size() > 1) {
            if (checkFirstWord()) {
                StringBuilder word = new StringBuilder();
                boolean direction = false;
                for (TileField t : wordForTryPlace) {
                    word.append(t.letter.getText());
                }
                if (wordForTryPlace.size() > 0) {
                    if (wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol) {
                        direction = true;
                    }
                }
                hostViewModel.tryPlaceWord(word.toString(), wordForTryPlace.get(0).tileRow, wordForTryPlace.get(0).tileCol, direction);
                wordForTryPlace.clear();
                yourWord.getChildren().clear();
                for (TileField t : handFields) {
                    t.setUnlocked();
                }
            }else
                alertPopUp("First Word Error","First Word Error","First Word has to be on the star");
        }else{
            alertPopUp("Word Error","Word Error","Word has to be at least two letters long");
        }
        Platform.runLater(() -> gameBoard.requestFocus());
    }

    @FXML
    public void onPassButtonClick() {
        hostViewModel.passTurn();
        Platform.runLater(() -> gameBoard.requestFocus());
    }

    @FXML
    protected void onSortABCButtonClick() {
        handFields.sort(Comparator.comparing(t -> t.letter.getText()));
        redrawHand(handFields);
        Platform.runLater(() -> gameBoard.requestFocus());
    }

    @FXML
    private void onSortScoreButtonClick() {
        handFields.sort(Comparator.comparingInt(t -> Integer.parseInt(t.score.getText())));
        redrawHand(handFields);
        Platform.runLater(() -> gameBoard.requestFocus());
    }

    private void redrawHand(List<TileField> list) {
        handGrid.getChildren().clear();
        for (int i = 0; i < list.size(); i++) {
            handFields.get(i).createTile(list.get(i).letter, list.get(i).score, handGrid.getWidth() / 7, handGrid.getHeight(), 28);
            handGrid.add(list.get(i), i, 0);
        }
    }

    private void redrawYourWord(List<TileField> list) {
        yourWord.getChildren().clear();
        for (int i = 0; i < list.size(); i++) {
            yourWord.add(list.get(i), i, 0);
        }
    }

    private void alertPopUp(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();

        rollBack();
    }

    private void setScoresFields(List<SimpleStringProperty> list) {
        for (int i = 0; i < list.size(); i++) {
            scoresFields.get(i).setText(list.get(i).get());
        }
    }

    private void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            //The hand of the player is updated
            redrawHand(handFields);
        });

        updatesMap.put("tileBoard updated", message -> {
            gameBoard.setTileFields(boardFields);
            gameBoard.tileFields.forEach(row -> {
                row.forEach(tileField -> {
                   if (!tileField.isUpdate()){
                       tileField.setUpdate();
                    }
                });
            });
        });

        updatesMap.put("scores updated", message -> {
            setScoresFields(hostViewModel.viewableScores);
        });
        updatesMap.put("invalidWord", message -> {
            //The word is invalid
            alertPopUp("Invalid Word","Invalid Word","The word you tried to place is invalid");
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String) arg;
        String[] messageSplit = message.split(":");
        String updateType = messageSplit[0];
        System.out.println("updateType: " + message);
        if (updatesMap.containsKey(updateType)) {
            updatesMap.get(updateType).accept(message);
        }
    }

    public void startNewGame() {
        this.hostViewModel.startNewGame();
        startNewGameBtn.setVisible(false);
    }

    private void rollBack() {
        for (TileField t : wordForTryPlace) {
            gameBoard.tileFields.get(t.tileRow).get(t.tileCol).letter.setText("");
        }

        gameBoard.redraw();
        wordForTryPlace.clear();
        yourWord.getChildren().clear();

        for (TileField t : handFields) {
            t.setUnlocked();
        }
    }

    private void initializeKeyEventMap() {
        keyEventsMap = new HashMap<>();

        for (int i = 0; i < 26; i++) {
            keyEventsMap.put(KeyCode.valueOf(String.valueOf((char) (i + 65))), functionForEachKey(KeyCode.valueOf(String.valueOf((char) (i + 65)))));
        }
        keyEventsMap.put(KeyCode.UP, functionForEachKey(KeyCode.UP));
        keyEventsMap.put(KeyCode.DOWN, functionForEachKey(KeyCode.DOWN));
        keyEventsMap.put(KeyCode.LEFT, functionForEachKey(KeyCode.LEFT));
        keyEventsMap.put(KeyCode.RIGHT, functionForEachKey(KeyCode.RIGHT));
        keyEventsMap.put(KeyCode.BACK_SPACE, functionForEachKey(KeyCode.BACK_SPACE));
        keyEventsMap.put(KeyCode.ENTER, functionForEachKey(KeyCode.ENTER));
    }

    private EventHandler<KeyEvent> functionForEachKey(KeyCode key) {
        return keyEvent -> {
            if (key.isLetterKey()) {
                for (TileField t : handFields) {
                    if (t.letter.getText().equals(key.toString())) {
                        selectedTileField = t;
                        selectedTileField.setSelect(true);
                        selectedTileField.tileRow = gameBoard.getRow();
                        selectedTileField.tileCol = gameBoard.getCol();
                        setTileFieldOnBoard();
                    }
                }
            } else if (keyEvent.getCode() == KeyCode.UP) {
                if (gameBoard.getRow() > 0) {
                    gameBoard.setPlace(gameBoard.getRow() - 1, gameBoard.getCol());
                }
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                if (gameBoard.getRow() < 14) {
                    gameBoard.setPlace(gameBoard.getRow() + 1, gameBoard.getCol());
                }
            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                if (gameBoard.getCol() > 0) {
                    gameBoard.setPlace(gameBoard.getRow(), gameBoard.getCol() - 1);
                }
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                if (gameBoard.getCol() < 14) {
                    gameBoard.setPlace(gameBoard.getRow(), gameBoard.getCol() + 1);
                }
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                setTileFieldOnBoard();
            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                if (gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).isLocked()) {
                    return;
                }
                // TODO: 08/06/should return a tile instead of word
                wordForTryPlace.removeIf(t -> t.tileCol == gameBoard.getCol() && t.tileRow == gameBoard.getRow());
                redrawYourWord(wordForTryPlace);
                gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.setText("");
                gameBoard.redraw();

                for (TileField t : handFields) {
                    if (t.tileCol == gameBoard.getCol() && t.tileRow == gameBoard.getRow()) {
                        t.setUnlocked();
                    }
                }
            }
            Platform.runLater(() -> gameBoard.requestFocus());
        };
    }

    private void setTileFieldOnBoard() {
        if (selectedTileField != null && !selectedTileField.isLocked() && gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.getText().equals("")) {
            if (ifConnected(selectedTileField)) {
                gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.setText(selectedTileField.letter.getText());
                gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).score.setText(selectedTileField.score.getText());
                TileField t = new TileField();
                t.letter.setText(selectedTileField.letter.getText());
                t.score.setText(selectedTileField.score.getText());
                t.tileRow = gameBoard.getRow();
                t.tileCol = gameBoard.getCol();
                t.createTile(t.letter, t.score, yourWord.getWidth() / 7, yourWord.getHeight(), 10);
                wordForTryPlace.add(t);
                redrawYourWord(wordForTryPlace);
                gameBoard.redraw();
            }
        } else if (selectedTileField != null)
            selectedTileField.setSelect(false);
    }

    private boolean ifConnected(TileField t) {
        if (wordForTryPlace.isEmpty()) {
            return true;
        }

        TileField lastTile = wordForTryPlace.get(wordForTryPlace.size() - 1);
        int lastTileRow = lastTile.tileRow;
        int lastTileCol = lastTile.tileCol;

        boolean adjacent = ((t.tileRow == lastTileRow && Math.abs(t.tileCol - lastTileCol) == 1)
                || (t.tileCol == lastTileCol && Math.abs(t.tileRow - lastTileRow) == 1)) && wordForTryPlace.get(0).tileCol <= t.tileCol;

        boolean vertical = false;

        if (adjacent && wordForTryPlace.size() > 1) {
            // Check if the word is horizontal or vertical
            vertical = isVertical(wordForTryPlace);
        } else
            return adjacent;

        if (vertical) {
            for (TileField tile : wordForTryPlace) {
                if (tile.tileCol != t.tileCol) {
                    return false;
                }
            }
        } else {
            for (TileField tile : wordForTryPlace) {
                if (tile.tileRow != t.tileRow) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isVertical(List<TileField> wordTryPlace) {
        return wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol;
    }

    /**
     * The checkFirstWord function checks if the first word is placed on a star.
     * The first word must be placed at the center of the board.
     * <p>
     * @return True if the first word is placed
     */
    private boolean checkFirstWord() {
        return !gameBoard.tileFields.get(7).get(7).letter.getText().equals("");
    }
}




