package BookScrabbleApp.View;

import BookScrabbleApp.*;
import BookScrabbleApp.ViewModel.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class GameWindowController implements Observer, Initializable {

    @FXML
    public Text turnInstructionTitle;
    @FXML
    public Text turnInstruction;
    @FXML
    public Text CHInstructionTitle;
    @FXML
    public Text challengeInstructions;
    @FXML
    Label namePlayer1 = new Label();
    @FXML
    Label namePlayer2 = new Label();
    @FXML
    Label namePlayer3 = new Label();
    @FXML
    Label namePlayer4 = new Label();
    @FXML
    Rectangle player1Rect = new Rectangle();
    @FXML
    Rectangle player2Rect = new Rectangle();
    @FXML
    Rectangle player3Rect = new Rectangle();
    @FXML
    Rectangle player4Rect = new Rectangle();
    @FXML
    Label scorePlayer1 = new Label();
    @FXML
    Label scorePlayer2 = new Label();
    @FXML
    Label scorePlayer3 = new Label();
    @FXML
    Label scorePlayer4 = new Label();

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
    Button tryPlaceBtn;

    ExecutorService executorService = Executors.newFixedThreadPool(4);


    private Map<String, Consumer<String>> updatesMap; //map of all the updates

    BS_ViewModel viewModel;

    private List<Label> scoresFields;

    private List<Label> nameFields;
    private List<Rectangle> rectanglesPlayer;

    private List<TileField> handFields;

    List<List<TileField>> boardFields;

    public static TileField selectedTileField;

    private Map<KeyCode, EventHandler<KeyEvent>> keyEventsMap;

    private List<TileField> wordForTryPlace;

    private List<SimpleStringProperty> wordsForChallenge;

    public GameWindowController() {
        initializeProperties();
        initializeUpdateMap();
        initializeKeyEventMap();
    }

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
        turnInstructionTitle.setText("Turn instructions:");
        turnInstruction.setText("Use the arrows to position the cursor where you\n" +
                "want to add a Tile.\n" + "Use the keyboard to type the desired Tile.\n" + "Select the Tiles with the mouse to validate them.\n" +
                "If your word is legal, it will be placed on the board,\n" + "and you will earn points.\n" + "Bonuses will be awarded if applicable.\n");
                CHInstructionTitle.setText("Challenge instructions:");
                challengeInstructions.setText("When the 'Challenge' pop-up appears,\n" +"select the checkbox\n" +
                "next to the word you want to challenge.\n" +
                "Click the challenge button.\n" +
                "Please note that you have only 7 seconds.\n" +
                "After that, the window will disappear");
        turnInstructionTitle.setVisible(true);
        turnInstructionTitle.underlineProperty().setValue(true);
        CHInstructionTitle.setVisible(true);
        CHInstructionTitle.underlineProperty().setValue(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String) arg;
        String[] messageSplit = message.split(":");
        String updateType = messageSplit[0];
        System.out.println("\n -- updateType GameWindow: " + message + " -- \n");
        if (updatesMap.containsKey(updateType)) {
            executorService.submit(() -> updatesMap.get(updateType).accept(message));
            updatesMap.get(updateType).accept(message);
        }
    }

    //initialize the window
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameFields.add(namePlayer1);
        scoresFields.add(scorePlayer1);
        rectanglesPlayer.add(player1Rect);
        rectanglesPlayer.get(0).getStyleClass().add("player-rectangle");
        namePlayer1.getStyleClass().add("player-name");

        nameFields.add(namePlayer2);
        scoresFields.add(scorePlayer2);
        rectanglesPlayer.add(player2Rect);
        nameFields.add(namePlayer3);
        scoresFields.add(scorePlayer3);
        rectanglesPlayer.add(player3Rect);
        nameFields.add(namePlayer4);
        scoresFields.add(scorePlayer4);
        rectanglesPlayer.add(player4Rect);

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

                boolean adjacent = true;

                if (wordForTryPlace.size() > 0) {
                    adjacent = (clickedTile.tileRow >= wordForTryPlace.get(wordForTryPlace.size()-1).tileRow && clickedTile.tileCol >= wordForTryPlace.get(wordForTryPlace.size()-1).tileCol);
                }

                if (adjacent) {
                    if (!clickedTile.letter.getText().equals("") && !clickedTile.isSelect()) {
                        clickedTile.setSelect(true);
                        if (clickedTile.isUpdate()) {
                            selectedTileField = new TileField();
                            selectedTileField.copy(clickedTile);
                            selectedTileField.letter.setText("_");
                            setYourWord();
                        } else {
                            selectedTileField = new TileField();
                            selectedTileField.copy(clickedTile);
                            setTileFieldOnBoard();
                            setYourWord();
                        }
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

    private void initializeProperties() {
        //initialize properties
        handFields = new ArrayList<>();
        boardFields = new ArrayList<>();
        updatesMap = new HashMap<>();
        wordForTryPlace = new ArrayList<>();
        wordsForChallenge = new ArrayList<>();

        //initialize the scores and names fields
        scoresFields = new ArrayList<>();
        nameFields = new ArrayList<>();
        rectanglesPlayer = new ArrayList<>();
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
                        tileField.setSelect(false);
                    }
                });
            });
            Platform.runLater(() -> {
                wordForTryPlace.clear();
                yourWord.getChildren().clear();
                unlockHand();
            });
        });

        updatesMap.put("scores updated", message -> {
            setScoresFields(viewModel.getViewableScores());
        });

        updatesMap.put("invalidWord", message -> {
            //The word is invalid
            alertPopUp("Invalid Word", "Invalid Word", "The word you tried to place is invalid");
        });

        updatesMap.put("wordsForChallenge updated", message -> {
            Platform.runLater(() -> showChallengePopup(viewModel.getViewableWordsForChallenge()));
        });

        updatesMap.put("challengeAlreadyActivated", message -> {
            alertPopUp("Challenge Error", "Challenge Error", "Challenge is Already Activated");
        });
        updatesMap.put("challengeSuccess", message -> {
            alertPopUp("challengeSuccess", "challengeSuccess", "challenge succeed and \nthe challenger get 10 points bonus");
        });

        updatesMap.put("turnPassed", message -> {
            String currentPlayerIndex = message.split(":")[1];
            passTurn(currentPlayerIndex);
        });

        updatesMap.put("playersName updated", message -> {
            setNamesFields(viewModel.getViewableNames());
        });

        updatesMap.put("gameStart", message -> {
            Platform.runLater(() -> gameBoard.requestFocus());
        });

        updatesMap.put("winner updated", message -> {
            alertPopUp("Game Over", "Game Over", viewModel.getWinnerProperty().get() + "\n" + "We would love to see you again!");
        });

        updatesMap.put("endGame", message -> {
            alertPopUp("Game Over", "Game Over", "The Game is over with no winner");
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
                        t.setSelect(true);
                        selectedTileField = t;
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
        TileField tile = new TileField();
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
                        } else {
                            tile = tileField;
                        }
                    }
                    if (!foundMatchingTile) {
                        if (boardRow == tile.tileRow && boardCol == tile.tileCol && tile.letter.getText().equals("_")) {
                            gameBoard.tileFields.get(boardRow).get(boardCol).letter.setText(tile.letter.getText());
                            gameBoard.tileFields.get(boardRow).get(boardCol).score.setText(tile.score.getText());
                        } else {
                            gameBoard.tileFields.get(boardRow).get(boardCol).letter.setText("");
                            gameBoard.tileFields.get(boardRow).get(boardCol).score.setText("");
                        }
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

                } else
                    alertPopUp("Word Error", "Word Error", "Must have at least one letter from your hand");
            } else
                alertPopUp("Word Error", "Word Error", "First Word has to be on the star");
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
    }

    //setters methods
    private void setScoresFields(List<SimpleStringProperty> list) {
        Platform.runLater(() -> {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null) {
                    scoresFields.get(i).setText(list.get(i).getValue());
                }
            }
        });
    }

    private void setNamesFields(List<SimpleStringProperty> viewableNames) {
        Platform.runLater(() -> {
            for (int i = 0; i < viewableNames.size(); i++) {
                nameFields.get(i).setText(viewableNames.get(i).getValue());
                nameFields.get(i).setVisible(true);

                scoresFields.get(i).setVisible(true);

                rectanglesPlayer.get(i).setVisible(true);
            }
        });
    }

    private void setTileFieldOnBoard() {
        if (selectedTileField != null) {
            int tileRow = selectedTileField.tileRow;
            int tileCol = selectedTileField.tileCol;
            if (selectedTileField.isSelect() && gameBoard.tileFields.get(gameBoard.getRow()).get(gameBoard.getCol()).letter.getText().equals("")) {
                gameBoard.tileFields.get(tileRow).get(tileCol).copy(selectedTileField);
                gameBoard.tileFields.get(tileRow).get(tileCol).setUnlocked();
                gameBoard.redraw();
            } else if (selectedTileField != null)
                selectedTileField.setSelect(false);
        }
    }

    private void setYourWord() {
        TileField t = new TileField();
        t.letter.setText(selectedTileField.letter.getText());
        t.score.setText(selectedTileField.score.getText());
        t.tileRow = selectedTileField.tileRow;
        t.tileCol = selectedTileField.tileCol;
        t.setSelect(true);
        t.createTile(yourWord.getWidth() / 7, yourWord.getHeight() - 10);
        wordForTryPlace.add(t);
        redrawYourWord(wordForTryPlace);
        selectedTileField = null;
    }

    private void showChallengePopup(List<SimpleStringProperty> wordsForChallenge) {
        Platform.runLater(() -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Scrabble Challenge");

            popupStage.initStyle(StageStyle.UTILITY);

            VBox popupRoot = new VBox(10);
            popupRoot.setAlignment(Pos.CENTER);
            popupRoot.setPadding(new Insets(10));

            Label titleLabel = new Label("Words for challenge:");
            Label clockLabel = new Label("Time left: ");
            popupRoot.getChildren().add(clockLabel);
            popupRoot.getChildren().add(titleLabel);

            StringBuilder challengeWord = new StringBuilder();

            for (int i = 0; i < wordsForChallenge.size(); i++) {
                CheckBox checkBox = new CheckBox(wordsForChallenge.get(i).get());
                int finalI = i;
                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        challengeWord.setLength(0);
                        challengeWord.append(wordsForChallenge.get(finalI).get());
                        for (Node node : popupRoot.getChildren()) {
                            if (node instanceof CheckBox && !node.equals(checkBox)) {
                                ((CheckBox) node).setSelected(false);
                            }
                        }
                    } else {
                        challengeWord.setLength(0);
                    }
                });

                popupRoot.getChildren().add(checkBox);
            }

            Button challengeButton = new Button("Challenge");
            challengeButton.setOnAction(e -> {
                viewModel.challengeRequest(challengeWord.toString());
                popupStage.close();
            });

            popupRoot.getChildren().add(challengeButton);

            popupStage.setScene(new Scene(popupRoot, 350, 250));

            popupStage.show();


            AtomicInteger timeLeft = new AtomicInteger(7); // Time in seconds
            clockLabel.setText("Time left: " + timeLeft);

            Timeline popupTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                timeLeft.getAndDecrement();
                clockLabel.setText("Time left: " + timeLeft);
                if (timeLeft.get() == 0) {
                    popupStage.close();
                }
            }));
            popupTimer.setCycleCount(timeLeft.get());

            popupTimer.play();
        });
        viewModel.unPark();
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
        Platform.runLater(() -> {
                for (TileField t : wordForTryPlace) {
                    if (gameBoard.tileFields.get(t.tileRow).get(t.tileCol).isUpdate()) {
                        gameBoard.tileFields.get(t.tileRow).get(t.tileCol).setSelect(false);
                    } else
                        gameBoard.tileFields.get(t.tileRow).get(t.tileCol).letter.setText("");
                }
                gameBoard.redraw();
                wordForTryPlace.clear();
                yourWord.getChildren().clear();
                unlockHand();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(text);

            alert.showAndWait();
        });
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
                    yourWord.add(t.createTile(yourWord.getWidth() / 7, yourWord.getHeight() - 8), i, 0);
                } else //tile from hand//
                    yourWord.add(list.get(i).createTile(yourWord.getWidth() / 7, yourWord.getHeight() - 8), i, 0);
            }
        });
    }



    private boolean checkFirstWord() {
        return !gameBoard.tileFields.get(7).get(7).letter.getText().equals("");
    }

    //check if these methods is needed
    private boolean isVertical(List<TileField> wordTryPlace) {
        return wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol;
    }



    public void unlockHand() {
        for (TileField t : handFields) {
            t.setUnlocked();
        }
    }

}




