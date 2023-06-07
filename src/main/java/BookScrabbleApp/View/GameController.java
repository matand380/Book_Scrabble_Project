package BookScrabbleApp.View;

import BookScrabbleApp.GridCanvas;
import BookScrabbleApp.ViewModel.*;
import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;

import java.net.*;
import java.util.*;
import java.util.function.*;

public class GameController implements Observer, Initializable {

    private static final int BOARD_SIZE = 15;

    @FXML
    private Label player1;

    @FXML
    private Button TryPlaceWordButton;

    @FXML
    GridPane handGrid = new GridPane();

    @FXML
    GridCanvas gameBoard;

    private Map<String, Consumer<String>> updatesMap; //map of all the updates

    //added:
    BS_Host_ViewModel hostViewModel;
    BS_Guest_ViewModel guestViewModel;

    private List<TextField> scoresFields;

    private List<TileField> handFields;

    List<List<TileField>> boardFields;
    List<List<Label>> boardFieldsLabel;
    public static TileField selectedTileField;

    @FXML
    GridPane yourWord = new GridPane();
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
            boardFieldsLabel.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                boardFields.get(i).add(new TileField());
                boardFieldsLabel.get(i).add(new Label());
            }
        }
        //bind the board to the board fields
        for (int i = 0; i < hostViewModel.viewableBoard.size(); i++) {
            for (int j = 0; j < hostViewModel.viewableBoard.get(i).size(); j++) {
                //boardLabel
                hostViewModel.viewableBoard.get(i).get(j).letterProperty().bindBidirectional(boardFieldsLabel.get(i).get(j).textProperty());

                //boardField
                hostViewModel.viewableBoard.get(i).get(j).letterProperty().bindBidirectional(boardFields.get(i).get(j).letter.textProperty());
                hostViewModel.viewableBoard.get(i).get(j).scoreProperty().bindBidirectional(boardFields.get(i).get(j).score.textProperty());
            }
        }
        player1.setText(HostScreen.name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scoresFields = new ArrayList<>();
        handFields = new ArrayList<>();
        boardFields = new ArrayList<>();
        boardFieldsLabel = new ArrayList<>();
        updatesMap = new HashMap<>();
        wordForTryPlace = new ArrayList<>();
        initializeUpdateMap();
        for (int i = 0; i < 15; i++) {
            boardFields.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                boardFields.get(i).add(new TileField());
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


        //added:
        gameBoard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                int col = gameBoard.getCol();
                int row = gameBoard.getRow();

                if (keyEvent.getCode() == KeyCode.UP) {
                    if (row > 0) {
                        gameBoard.setPlace(col, row - 1);
                    }
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    if (row < 14) {
                        gameBoard.setPlace(col, row + 1);
                    }
                } else if (keyEvent.getCode() == KeyCode.LEFT) {
                    if (col > 0) {
                        gameBoard.setPlace(col - 1, row);
                    }
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    if (col < 14) {
                        gameBoard.setPlace(col + 1, row);
                    }
                } else if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (selectedTileField != null && !selectedTileField.isLocked() && gameBoard.tileFields.get(col).get(row).letter.getText().equals("")) {
                        gameBoard.tileFields.get(col).get(row).letter.setText(selectedTileField.letter.getText());
                        gameBoard.tileFields.get(col).get(row).score.setText(selectedTileField.score.getText());
                        TileField t = new TileField();
                        t.letter.setText(selectedTileField.letter.getText());
                        t.score.setText(selectedTileField.score.getText());
                        t.tileRow = row;
                        t.tileCol = col;
                        t.createTile(t.letter, t.score,yourWord.getWidth()/7,yourWord.getHeight(),10);
                        t.setLocked();
                        wordForTryPlace.add(t);
                        redrawYourWord(wordForTryPlace);
                        selectedTileField.setLocked();
                        gameBoard.redraw();
                    }
                }

                Platform.runLater(() -> gameBoard.requestFocus());
            }
        });

    }

    @FXML
    protected void onTryButtonClick() {
        StringBuilder word = new StringBuilder();
        boolean direction = true;
        for (TileField t : wordForTryPlace) {
            t.setLocked();
            word.append(t.letter.getText());
        }
        if (wordForTryPlace.size() > 0) {
            if (wordForTryPlace.get(0).tileCol == wordForTryPlace.get(1).tileCol) {
                direction = false;
            }
        }
        hostViewModel.tryPlaceWord(word.toString(), wordForTryPlace.get(0).tileCol, wordForTryPlace.get(0).tileRow, direction);
        wordForTryPlace.clear();
    }

    @FXML
    public void onPassButtonClick() {
        hostViewModel.passTurn();
    }

    @FXML
    protected void onSortABCButtonClick() {
        handFields.sort(Comparator.comparing(t -> t.letter.getText()));
        redrawHand(handFields);
    }

    @FXML
    protected void onSortScoreButtonClick() {
        handFields.sort(Comparator.comparingInt(t -> Integer.parseInt(t.score.getText())));
        redrawHand(handFields);
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

    private void redrawBoardFields(List<List<TileField>> list) {
        gameBoard.redraw();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                TileField tileField = boardFields.get(i).get(j);
                tileField.createTile(list.get(i).get(j).letter, list.get(i).get(j).score, gameBoard.getWidth() / 15, gameBoard.getHeight() / 15, 15);
                gameBoard.tileFields.get(i).get(j).letter = tileField.letter;
                gameBoard.tileFields.get(i).get(j).score = tileField.score;
            }
        }
        boardFields.get(7).get(7).createTile(new TextField("A"), new TextField("1"), gameBoard.getWidth() / 15, gameBoard.getHeight() / 15, 10);
    }

    private void setScoresFields(List<TextField> list) {
        for (int i = 0; i < list.size(); i++) {
            scoresFields.get(i).setText(list.get(i).getText());
        }
    }

    private void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            //The hand of the player is updated
            redrawHand(handFields);
        });

        updatesMap.put("tileBoard updated", message -> {
            gameBoard.setTileFields(boardFields);
        });

        updatesMap.put("scores updated", message -> {
            setScoresFields(scoresFields);
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
    }

}

//    @FXML
//    private void onButtonDragDetected(MouseEvent event) {
//        Button button = (Button) event.getSource();
//        Dragboard dragboard = button.startDragAndDrop(TransferMode.MOVE);
//        ClipboardContent content = new ClipboardContent();
//        content.putString(button.getText());
//        dragboard.setContent(content);
//        dragboard.setDragView(button.snapshot(null, null));
//        event.consume();
//    }
//
//    @FXML
//    private void onButtonDragDone(DragEvent event) {
//        if (event.getTransferMode() == TransferMode.MOVE) {
//            // Perform any cleanup or post-drag logic here
//            Button draggedButton = (Button) event.getSource();
//            draggedButton.setText(""); // Remove the text from the button after dragging
//        }
//        event.consume();
//    }
//
//    @FXML
//    private void onCellDragOver(DragEvent event) {
//        if (event.getGestureSource() != event.getSource() && event.getDragboard().hasString()) {
//            Button targetCell = (Button) event.getSource();
//            int columnIndex = GridPane.getColumnIndex(targetCell);
//            int rowIndex = GridPane.getRowIndex(targetCell);
//
//            // Check if the target cell is empty
//            if (targetCell.getText().isEmpty()) {
//                event.acceptTransferModes(TransferMode.MOVE);
//            }
//        }
//        event.consume();
//    }
//
//    @FXML
//    private void onCellDragDropped(DragEvent event) {
//        Dragboard dragboard = event.getDragboard();
//        if (dragboard.hasString()) {
//            Button targetCell = (Button) event.getSource();
//            int columnIndex = GridPane.getColumnIndex(targetCell);
//            int rowIndex = GridPane.getRowIndex(targetCell);
//
//            // Check if the target cell is empty
//            if (targetCell.getText().isEmpty()) {
//                // Remove the button from its previous cell, if any
//                Button draggedButton = (Button) event.getGestureSource();
//                GridPane gridPane = (GridPane) targetCell.getParent();
//                Button previousCell = (Button) getNodeByRowColumnIndex(GridPane.getRowIndex(draggedButton), GridPane.getColumnIndex(draggedButton), gridPane);
//                if (previousCell != null) {
//                    previousCell.setText("");
//                }
//
//                // Set the button text in the target cell
//                targetCell.setText(dragboard.getString());
//            }
//
//            event.setDropCompleted(true);
//        } else {
//            event.setDropCompleted(false);
//        }
//        event.consume();
//    }
//
//    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
//        Node result = null;
//        ObservableList<Node> children = gridPane.getChildren();
//
//        for (Node node : children) {
//            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
//                result = node;
//                break;
//            }
//        }
//
//        return result;
//    }

//        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/board.JPG"));
//        imageView.setImage(backgroundImage);
//        imageView.setPreserveRatio(true);
//        imageView.setSmooth(true);
//        imageView.setCache(true);

    //url="@../images/board.JPG"

