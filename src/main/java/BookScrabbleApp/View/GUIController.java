package BookScrabbleApp.View;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import BookScrabbleApp.ViewModel.BS_Host_ViewModel;
import BookScrabbleApp.ViewModel.BS_ViewModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.*;

public class GUIController implements Observer, Initializable {
    public Button tileButton1;
    @FXML
    private Label welcomeText;
    @FXML
    private AnchorPane pane;


    


    List<TileField> hand;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Scrabble Application!!!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/board.JPG"));
//        imageView.setImage(backgroundImage);
//        imageView.setPreserveRatio(true);
//        imageView.setSmooth(true);
//        imageView.setCache(true);

        //url="@../images/board.JPG"
    }


    @Override
    public void update(Observable o, Object arg) {

    }

    @FXML
    private void onButtonDragDetected(MouseEvent event) {
        Button button = (Button) event.getSource();
        Dragboard dragboard = button.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(button.getText());
        dragboard.setContent(content);
        dragboard.setDragView(button.snapshot(null, null));
        event.consume();
    }

    @FXML
    private void onButtonDragDone(DragEvent event) {
        if (event.getTransferMode() == TransferMode.MOVE) {
            // Perform any cleanup or post-drag logic here
            Button draggedButton = (Button) event.getSource();
            draggedButton.setText(""); // Remove the text from the button after dragging
        }
        event.consume();
    }

    @FXML
    private void onCellDragOver(DragEvent event) {
        if (event.getGestureSource() != event.getSource() && event.getDragboard().hasString()) {
            Button targetCell = (Button) event.getSource();
            int columnIndex = GridPane.getColumnIndex(targetCell);
            int rowIndex = GridPane.getRowIndex(targetCell);

            // Check if the target cell is empty
            if (targetCell.getText().isEmpty()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        }
        event.consume();
    }

    @FXML
    private void onCellDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasString()) {
            Button targetCell = (Button) event.getSource();
            int columnIndex = GridPane.getColumnIndex(targetCell);
            int rowIndex = GridPane.getRowIndex(targetCell);

            // Check if the target cell is empty
            if (targetCell.getText().isEmpty()) {
                // Remove the button from its previous cell, if any
                Button draggedButton = (Button) event.getGestureSource();
                GridPane gridPane = (GridPane) targetCell.getParent();
                Button previousCell = (Button) getNodeByRowColumnIndex(GridPane.getRowIndex(draggedButton), GridPane.getColumnIndex(draggedButton), gridPane);
                if (previousCell != null) {
                    previousCell.setText("");
                }

                // Set the button text in the target cell
                targetCell.setText(dragboard.getString());
            }

            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }



}