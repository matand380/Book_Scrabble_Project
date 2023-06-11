package BookScrabbleApp.View;

import BookScrabbleApp.*;
import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    @FXML
    Button passTurnBtn;
    @FXML
    Button challengeBtn;
    @FXML
    Button tryPlaceBtn;

    ExecutorService executorService = Executors.newFixedThreadPool(3);


    private Map<String, Consumer<String>> updatesMap; //map of all the updates

    BS_ViewModel viewModel;

    private List<Label> scoresFields;

    private List<Label> nameFields;

    private List<TileField> handFields;

    List<List<TileField>> boardFields;

    public static TileField selectedTileField;

    private Map<KeyCode, EventHandler<KeyEvent>> keyEventsMap;

    private List<TileField> wordForTryPlace;

    private List<SimpleStringProperty> wordsForChallenge;

    public void setViewModel(BS_ViewModel viewModel) {
        if (viewModel instanceof BS_Host_ViewModel) {
            this.viewModel = new BS_Host_ViewModel();
            this.viewModel.getObservable().addObserver(this);
            initializeWindow();
        } else if (viewModel instanceof BS_Guest_ViewModel) {
            this.viewModel = new BS_Guest_ViewModel();
            this.viewModel.getObservable().addObserver(this);
           initializeWindow();
            startNewGameBtn.setVisible(false);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String) arg;
        String[] messageSplit = message.split(":");
        String updateType = messageSplit[0];
        System.out.println("updateType: " + message);
        if (updatesMap.containsKey(updateType)) {
           executorService.submit(()-> updatesMap.get(updateType).accept(message));
        }
    }

    //initialize the window
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeProperties();
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
                double xCoordinate = mouseEvent.getX();
                double yCoordinate = mouseEvent.getY();

                int col = (int) (xCoordinate / (gameBoard.getWidth() / 15));
                int row = (int) (yCoordinate / (gameBoard.getHeight() / 15));

                TileField clickedTile = gameBoard.tileFields.get(row).get(col);

                if (!clickedTile.letter.getText().equals("")) {
                    if (gameBoard.tileFields.get(clickedTile.tileRow).get(clickedTile.tileCol).isUpdate()) {
                        selectedTileField.letter.setText("_");
                        selectedTileField.tileRow = gameBoard.tileFields.get(clickedTile.tileRow).get(clickedTile.tileCol).tileRow;
                        selectedTileField.tileCol = gameBoard.tileFields.get(clickedTile.tileRow).get(clickedTile.tileCol).tileCol;
                        gameBoard.tileFields.get(clickedTile.tileRow).get(clickedTile.tileCol).setClick(true);
                        setYourWord();
                    } else {
                        clickedTile.draw(gameBoard.getGraphicsContext2D(), row, col, gameBoard.getWidth() / 15, gameBoard.getHeight() / 15, 0);
                        selectedTileField = clickedTile;
                        selectedTileField.tileRow = row;
                        selectedTileField.tileCol = col;
                        gameBoard.tileFields.get(clickedTile.tileRow).get(clickedTile.tileCol).setClick(true);
                        setTileFieldOnBoard();
                        setYourWord();
                    }
                }
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

    private void  initializeProperties() {
        //initialize properties
        handFields = new ArrayList<>();
        boardFields = new ArrayList<>();
        updatesMap = new HashMap<>();
        wordForTryPlace = new ArrayList<>();
        wordsForChallenge = new ArrayList<>();

        //initialize the scores and names fields
        scoresFields = new ArrayList<>();
        nameFields = new ArrayList<>();
        nameFields.add(namePlayer1);
        scoresFields.add(scorePlayer1);
        nameFields.add(namePlayer2);
        scoresFields.add(scorePlayer2);
        nameFields.add(namePlayer3);
        scoresFields.add(scorePlayer3);
        nameFields.add(namePlayer4);
        scoresFields.add(scorePlayer4);
    }

    private void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            if ((viewModel.getPlayerIndex() != 0)) {
                tryPlaceBtn.setDisable(true);
                passTurnBtn.setDisable(true);
            } else {
                tryPlaceBtn.setDisable(false);
                passTurnBtn.setDisable(false);
            }
            //The hand of the player is updated
            redrawHand(handFields);
        });

        updatesMap.put("tileBoard updated", message -> {
            gameBoard.setTileFields(boardFields);
            gameBoard.tileFields.forEach(row -> {
                row.forEach(tileField -> {
                    if (!tileField.isUpdate()) {
                        tileField.setUpdate();
                    }
                });
            });
            gameBoard.redraw();
        });

        updatesMap.put("scores updated", message -> {
            setScoresFields(viewModel.getViewableScores());
        });

        updatesMap.put("invalidWord", message -> {
            //The word is invalid
            alertPopUp("Invalid Word", "Invalid Word", "The word you tried to place is invalid");
        });

        updatesMap.put("wordsForChallenge updated", message -> {
            setWordsForChallengeOnScreen(viewModel.getViewableWordsForChallenge());
        });

        updatesMap.put("challengeAlreadyActivated", message -> {
            alertPopUp("Challenge Error", "Challenge Error", "Challenge is Already Activated");
        });

        updatesMap.put("turnPassed", message -> {
            String currentPlayerIndex = message.split(":")[1];
            passTurn(currentPlayerIndex);
        });

        updatesMap.put("playersName updated", message -> {
            setNamesFields(viewModel.getViewableNames());
        });

        updatesMap.put("gameStart", message -> {

           initializeWindow();
        });
    }

    public void initializeWindow() {
        //bind the scores to the text fields
        for (int i = 0; i < viewModel.getViewableScores().size(); i++) {
            viewModel.getViewableScores().get(i).bind(scoresFields.get(i).textProperty());
            viewModel.getViewableNames().get(i).bind(nameFields.get(i).textProperty());
        }

        for (int i = 0; i < 7; i++) {
            handFields.add(new TileField());
        }

        //bind the hand to the hand fields
        for (int i = 0; i < viewModel.getViewableHand().size(); i++) {
            viewModel.getViewableHand().get(i).letterProperty().bindBidirectional(handFields.get(i).letter.textProperty());
            viewModel.getViewableHand().get(i).scoreProperty().bindBidirectional(handFields.get(i).score.textProperty());
        }

        for (int i = 0; i < 15; i++) {
            boardFields.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                boardFields.get(i).add(new TileField());
            }
        }
        for (int i = 0; i < 15; i++) {
            wordsForChallenge.add(new SimpleStringProperty());
        }

        for (int i = 0; i < viewModel.getViewableWordsForChallenge().size(); i++) {
            viewModel.getViewableWordsForChallenge().get(i).bindBidirectional(wordsForChallenge.get(i));
        }

        //bind the board to the board fields
        for (int boardRow = 0; boardRow < viewModel.getViewableBoard().size(); boardRow++) {
            for (int boardCol = 0; boardCol < viewModel.getViewableBoard().get(boardRow).size(); boardCol++) {
                viewModel.getViewableBoard().get(boardRow).get(boardCol).letterProperty().bindBidirectional(boardFields.get(boardRow).get(boardCol).letter.textProperty());
                viewModel.getViewableBoard().get(boardRow).get(boardCol).scoreProperty().bindBidirectional(boardFields.get(boardRow).get(boardCol).score.textProperty());
            }
        }
        //to be removed later on
        namePlayer1.setText(HostController.name);
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
                    if (t.letter.getText().equals(key.toString()) && !t.isSelect()) {
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
            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                if (!gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).isUpdate()) {
                    // TODO: 08/06/should return a tile instead of word
                    removeFromYourWord(gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()));
                    redrawYourWord(wordForTryPlace);
                    gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.setText("");
                    gameBoard.redraw();

                    for (TileField t : handFields) {
                        if (t.tileCol == gameBoard.getCol() && t.tileRow == gameBoard.getRow()) {
                            t.setUnlocked();
                        }
                    }
                }
            }
            Platform.runLater(() -> gameBoard.requestFocus());
        };
    }

    //button handlers
    @FXML
    protected void onTryButtonClick() {

        for (int boardRow = 0; boardRow < 15; boardRow++) {
            for (int boardCol = 0; boardCol < 15; boardCol++) {
                if (!gameBoard.tileFields.get(boardRow).get(boardCol).isUpdate()) {
                    boolean foundMatchingTile = false;
                    for (TileField tileField : wordForTryPlace) {
                        if (gameBoard.tileFields.get(boardRow).get(boardCol).tileRow == tileField.tileRow &&
                                gameBoard.tileFields.get(boardRow).get(boardCol).tileCol == tileField.tileCol &&
                                gameBoard.tileFields.get(boardRow).get(boardCol).letter.getText().equals(tileField.letter.getText())) {
                            foundMatchingTile = true;
                            break;
                        }
                    }
                    if (!foundMatchingTile) {
                        gameBoard.tileFields.get(boardRow).get(boardCol).letter.setText("");
                        gameBoard.tileFields.get(boardRow).get(boardCol).score.setText("");
                    }
                }
            }
        }

        gameBoard.redraw();
        if (wordForTryPlace.size() > 1) {
            if (checkFirstWord()) {
                StringBuilder word = new StringBuilder();
                boolean direction = false;
                int count = 0;
                for (TileField t : wordForTryPlace) {
                    if (t.letter.getText().equals("_")) {
                        count++;
                    }
                    word.append(t.letter.getText());
                }
                if (count != wordForTryPlace.size()) {
                    if (wordForTryPlace.size() > 0) {
                        if (wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol) {
                            direction = true;
                        }
                    }
                    viewModel.tryPlaceWord(word.toString(), wordForTryPlace.get(0).tileRow, wordForTryPlace.get(0).tileCol, direction);
                    wordForTryPlace.clear();
                    yourWord.getChildren().clear();
                    for (TileField t : handFields) {
                        t.setUnlocked();
                    }
                } else
                    alertPopUp("Error in word", "Error in word", "Must have at least one letter from your hand");
            } else
                alertPopUp("First Word Error", "First Word Error", "First Word has to be on the star");
        } else
            alertPopUp("Word Error", "Word Error", "Word has to be at least two letters long");

        Platform.runLater(() -> gameBoard.requestFocus());
    }

    @FXML
    public void onPassButtonClick() {
        viewModel.passTurn();
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

    public void startNewGame() {
        this.viewModel.startNewGame();
        startNewGameBtn.setVisible(false);
        Platform.runLater(() -> gameBoard.requestFocus());
    }

    //setters methods
    private void setScoresFields(List<SimpleStringProperty> list) {
        Platform.runLater(() -> {
            for (int i = 0; i < list.size(); i++) {
                scoresFields.get(i).setText(list.get(i).getValue());
            }
        });
    }

    private void setNamesFields(List<SimpleStringProperty> viewableNames) {
        Platform.runLater(() -> {
            for (int i = 0; i < viewableNames.size(); i++) {
                nameFields.get(i).setText(viewableNames.get(i).getValue());
                nameFields.get(i).setVisible(true);
                scoresFields.get(i).setVisible(true);
            }
        });
    }

    private void setTileFieldOnBoard() {
        if (selectedTileField != null && selectedTileField.isSelect() && gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.getText().equals("")) {
            gameBoard.tileFields.get(selectedTileField.tileRow).get(selectedTileField.tileCol).letter.setText(selectedTileField.letter.getText());
            gameBoard.tileFields.get(selectedTileField.tileRow).get(selectedTileField.tileCol).score.setText(selectedTileField.score.getText());
            gameBoard.tileFields.get(selectedTileField.tileRow).get(selectedTileField.tileCol).setUnlocked();
            gameBoard.redraw();
        } else if (selectedTileField != null)
            selectedTileField.setSelect(false);
    }

    private void setYourWord() {
        TileField t = new TileField();
        t.letter.setText(selectedTileField.letter.getText());
        t.score.setText(selectedTileField.score.getText());
        t.tileRow = selectedTileField.tileRow;
        t.tileCol = selectedTileField.tileCol;
        t.createTile(yourWord.getWidth() / 7, yourWord.getHeight());
        wordForTryPlace.add(t);
        redrawYourWord(wordForTryPlace);
    }

    private void setWordsForChallengeOnScreen(List<SimpleStringProperty> wordsForChallenge) {
        Platform.runLater(()->{
        // TODO: 2023-06-09  do a popUp for the client with all the words for challenge
        // TODO: 2023-06-09  with a checkBox for each word
        ChoiceDialog<SimpleStringProperty> dialog = new ChoiceDialog<>();
        dialog.setTitle("Word Selection");
        dialog.setHeaderText("Select a word to challenge");
        dialog.setContentText("Words:");

        // Set the list of words for the choice dialog
        dialog.getItems().add(wordsForChallenge.get(0));

        // Show the dialog and wait for the user's response
        Optional<SimpleStringProperty> result = dialog.showAndWait();

        // Process the selected word
        result.ifPresent(selectedWord -> {
            // TODO: 09/06/2023 sent the word to Challenge
            viewModel.challengeRequest(selectedWord.get());
            System.out.println("Selected Word: " + selectedWord);
        });
        });
    }

    public void ChallengeOnScreen(ActionEvent actionEvent) {
        setWordsForChallengeOnScreen(viewModel.getViewableWordsForChallenge());
    }

    private void passTurn(String indexCurrentPlayer) {
        if (!(viewModel.getPlayerIndex() == Integer.parseInt(indexCurrentPlayer))) {
            tryPlaceBtn.setDisable(true);
            passTurnBtn.setDisable(true);
        } else {
            tryPlaceBtn.setDisable(false);
            passTurnBtn.setDisable(false);
        }
    }

    private void removeFromYourWord(TileField removedTile) {
        wordForTryPlace.removeIf(tileField -> tileField.tileRow == removedTile.tileRow && tileField.tileCol == removedTile.tileCol && tileField.letter.getText().equals(removedTile.letter.getText()));
    }

    //popUp methods
    private void alertPopUp(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();

        rollBack();
    }

    //redraw methods
    private void redrawHand(List<TileField> list) {
        Platform.runLater(() -> {
            handGrid.getChildren().clear();
            for (int i = 0; i < list.size(); i++) {
                int finalI = i;
                handGrid.add(list.get(finalI).createTile(handGrid.getWidth() / 7, handGrid.getHeight()), finalI, 0);
            }
        });
    }

    private void redrawYourWord(List<TileField> list) {
        Platform.runLater(() -> {
            yourWord.getChildren().clear();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).letter.getText().equals("_")) { //tile from board//
                    TileField t = gameBoard.tileFields.get(list.get(i).tileRow).get(list.get(i).tileCol);
                    t.letter.setText(gameBoard.tileFields.get(list.get(i).tileRow).get(list.get(i).tileCol).letter.getText());
                    t.score.setText(gameBoard.tileFields.get(list.get(i).tileRow).get(list.get(i).tileCol).score.getText());
                    yourWord.add(t.createTile(yourWord.getWidth() / 7, yourWord.getHeight()), i, 0);
                } else //tile from hand//
                    yourWord.add(list.get(i).createTile(yourWord.getWidth() / 7, yourWord.getHeight()), i, 0);
            }
        });
    }

    private void rollBack() {
        for (TileField t : wordForTryPlace) {
            if (!gameBoard.tileFields.get(t.tileRow).get(t.tileCol).isUpdate()) {
                gameBoard.tileFields.get(t.tileRow).get(t.tileCol).letter.setText("");
            }
        }
        gameBoard.redraw();
        wordForTryPlace.clear();
        yourWord.getChildren().clear();
        for (TileField t : handFields) {
            t.setUnlocked();
        }
    }

    private boolean checkFirstWord() {
        return !gameBoard.tileFields.get(7).get(7).letter.getText().equals("");
    }

    //check if these methods is needed
    private boolean isVertical(List<TileField> wordTryPlace) {
        return wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol;
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
}




