package com.example.rummikubfrontscreen;

import com.example.rummikubfrontscreen.setup.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class GameBoardController {

    @FXML
    private Pane Pane;

    public GameApp gameApp;

    public Tile tile;

    private ArrayList<Tile> tiles;

    private FXTile fxTile = new FXTile();


    private ArrayList<Button> fxTileButtons = new ArrayList<>();

    private ArrayList<FXTile> fxTiles = new ArrayList<FXTile>();

    private ArrayList<Button> buttonsOnPlayingField = new ArrayList<>();

    private ArrayList<Double> buttonsOnPlayingFieldPosX = new ArrayList<>();
    private ArrayList<Double> buttonsOnPlayingFieldPosY = new ArrayList<>();
    private ArrayList<Double> prevButtonsOnPlayingFieldPosX;
    private ArrayList<Double> prevButtonsOnPlayingFieldPosY;
    private ArrayList<Node> buttonsToKeep = new ArrayList<>();
    private ArrayList<Node> prevButtonsToKeep;
    private ArrayList<Tile> tilesInField = new ArrayList<>();
    private ArrayList<Tile> prevTilesInField;
    private ArrayList<Tile> allTilesInField = new ArrayList<>();
    private ArrayList<Tile> currentHand = new ArrayList<>();
    HashMap<String, Tile> all_tiles = new HashMap<>();


    boolean gameStarted = false;
    boolean drawn = true;
    boolean endTurnBlocked = false;

    double minX = 1;
    double minY = 1;
    double maxX = 485;
    double maxY = 285;
    boolean winner = false;

    /**
     * Initializes the Tile object as a Button
     */
    @FXML
    private void initializeTileAsButton(){
        for (Button button: fxTileButtons){
            button.setOnAction(this::handleButtonClick);
        }

    }

    /**
     *
     * Creates new Button() from fxTileButton. factory-like
     */
    @FXML
    private void handlePaneClick(MouseEvent event) {
        if (fxTile.fxTileButton != null){
            fxTile.fxTileButton.setLayoutX(event.getSceneX());
            fxTile.fxTileButton.setLayoutY(event.getSceneY());
        }
    }

    /**
     * Handles clicking event of clicking on the tile
     **/
    @FXML
    private void handleButtonClick(ActionEvent event){
        fxTile.fxTileButton = (Button) event.getSource();
    }

    /**
     * Resets the individual buttons on the playing field
     */
    public void resetButtonsOnField(){

        for (Node node : Pane.getChildren()) {
            if (node instanceof Button){
                double buttonX = node.getLayoutX();
                double buttonY = node.getLayoutY();

                if (buttonX >= minX && buttonX <= maxX && buttonY >= minY && buttonY <= maxY) {
                    String id = node.getId();
                    buttonsToKeep.add(node);
                    buttonsOnPlayingFieldPosX.add(node.getLayoutX());
                    buttonsOnPlayingFieldPosY.add(node.getLayoutY());

                    Tile curTile = all_tiles.get(id);

                    // Should only be added when approved
                    tilesInField.add(curTile);
                    if (!allTilesInField.contains(curTile)){
                        allTilesInField.add(curTile);
                    }

                    curTile.setX(buttonX);
                    curTile.setY(buttonY);

                    if(gameApp.getCurPlr().getHand().contains(curTile) ){
                        gameApp.getCurPlr().removeTile(curTile);
                    }
                }
            }
        }
        winner = gameApp.isWinner();
        //endTurnBlocked = !wholeProcessChecker(tilesInField);
        if (winner){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("YOU WON!");
            alert.showAndWait();
        }

    }

    /**
     * Resets the playing field as a whole
     */

    public void resetPlayingField() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("YOU WON!");

        ArrayList<Tile> tiles = gameApp.getGs().getAllTiles();
        System.out.println("tiles list: " + tiles);

        // get the previous state of the game
        prevButtonsToKeep = new ArrayList<>(buttonsToKeep);
        prevButtonsOnPlayingFieldPosX = new ArrayList<>(buttonsOnPlayingFieldPosX);
        prevButtonsOnPlayingFieldPosY = new ArrayList<>(buttonsOnPlayingFieldPosY);
        prevTilesInField = new ArrayList<>(tilesInField);
        currentHand = new ArrayList<>(gameApp.getCurPlr().getHand());

        buttonsToKeep.clear();
        tilesInField.clear();
        buttonsOnPlayingFieldPosX.clear();
        buttonsOnPlayingFieldPosY.clear();

        // reset the field and buttons
        resetButtonsOnField();

        if(endTurnBlocked) return;

        // if the field is wrong revert to previous state
        if (!wholeProcessChecker(tilesInField)) {
            System.out.println("oopsie");
            buttonsToKeep = prevButtonsToKeep;
            buttonsOnPlayingFieldPosX = prevButtonsOnPlayingFieldPosX;
            buttonsOnPlayingFieldPosY = prevButtonsOnPlayingFieldPosY;
            tilesInField = prevTilesInField;
            gameApp.getCurPlr().setHand(currentHand);
        } else {
            System.out.println("The whole field appears to be valid");
            endTurnBlocked = false;
        }

        for (Button fxTileButton : fxTileButtons) {
            Pane.getChildren().remove(fxTileButton);
        }

        for (Button button : buttonsOnPlayingField) {
            Pane.getChildren().remove(button);
        }

        buttonsOnPlayingField = new ArrayList<>();
        for (int i = 0; i < buttonsToKeep.size(); i ++){
            if (buttonsToKeep.get(i) instanceof Button){
                buttonsOnPlayingField.add((Button) buttonsToKeep.get(i));
            }
        }
    }


    public void putButtonsBack(ArrayList<Node> buttonsToKeep){;
        for (Node buttonNode : buttonsToKeep){
            if (buttonNode instanceof Button){
                Button buttonToPutBack = (Button) buttonNode;
                Pane.getChildren().remove(buttonNode);
                System.out.println(buttonToPutBack);
                double initialX = 20;
                double initialY = 350;

                while (isButtonOccupyingCoordinates(initialX, initialY)) {
                    if (initialX < 270) {
                        initialX += 45;
                    } else {
                        initialX = 20;
                        initialY = 395;
                    }
                }

                buttonToPutBack.setLayoutX(initialX);
                buttonToPutBack.setLayoutY(initialY);
                Pane.getChildren().add(buttonToPutBack);
                System.out.println("Button coordinates: "+buttonToPutBack.getLayoutX()+" "+buttonToPutBack.getLayoutY());
                System.out.println("X, Y coordinates: "+initialX+" "+initialY);
            }
        }
    }

    /**
     * Initializes the tiles on the hand of the next player
     */

    @FXML
    private void changePlayer() {
        if (!gameStarted) return;
        if(endTurnBlocked) {
            endTurnBlocked = false;
            return;
        }

        resetPlayingField();

        if(!endTurnBlocked){
            endTurnBlocked = false;
            gameApp.nextPlayer();
        }
        tiles = gameApp.getCurPlr().getHand();

        for (int i = 0; i < buttonsOnPlayingField.size(); i++) {
            buttonsOnPlayingField.get(i).setLayoutX(buttonsOnPlayingFieldPosX.get(i));
            buttonsOnPlayingField.get(i).setLayoutY(buttonsOnPlayingFieldPosY.get(i));
            buttonsOnPlayingField.get(i).setPrefWidth(33);
            buttonsOnPlayingField.get(i).setPrefHeight(41);
            Pane.getChildren().add(buttonsOnPlayingField.get(i));
        }

        for (Tile value : tiles) {
            fxTile = initFXTile(value);
            fxTiles.add(fxTile);
            putButton();
        }
        initializeTileAsButton();

        // Setting up next round
        drawn = false;
    }

    /**
     * Starts the entire game and initializes the tiles on the hand of first player
     */
    @FXML
    private void start(){
        if (gameStarted) return;

        gameApp = new GameApp();
        tiles = gameApp.getCurPlr().getHand();

        for (Tile tile : tiles) {
            fxTile = initFXTile(tile);
            fxTiles.add(fxTile);
            putButton();
        }

        initializeTileAsButton();
        gameStarted = true;


        // allbuttons

        for (Tile tile : gameApp.getGs().getAllTiles()) {
            all_tiles.put(Integer.toString(tile.getId()), tile);

        }

    }

    /**
     * Checks whether the matchings on the game board are valid
     */
    private boolean wholeProcessChecker (ArrayList<Tile> unstructuredTiles) {
        System.out.println("Unstructered: "+unstructuredTiles);
        // Verifying game field
        TilePositionScanner tScanner = new TilePositionScanner();
        ArrayList<ArrayList<Tile>> structuredTiles = tScanner.scanner(unstructuredTiles);

        System.out.println("The series are :"+structuredTiles);
        return Board.boardVerifier(structuredTiles);
    }

    /**
     * Allows a player to draw a new tile randomly
     */
    @FXML
    private void drawButton() {
        //if (!drawn) return;
        //if (!gameStarted) return;
        System.out.println("drawn");
        tile = gameApp.draw();
        fxTile = initFXTile(tile);
        System.out.println(fxTile);
        putButton();
        initializeTileAsButton();

        drawn = true;
    }

    /**
     * Handles placing the button on the game board
     */
    private void putButton(){

        double initialX = 20;
        double initialY = 350;


        while (isButtonOccupyingCoordinates(initialX, initialY)) {
            if (initialX < 270) {
                initialX += 45;
            } else {
                initialX = 20;
                initialY = 395;
            }
        }

        fxTile.fxTileButton.setLayoutX(initialX);
        fxTile.fxTileButton.setLayoutY(initialY);

        fxTile.fxTileButton.setPrefWidth(33);
        fxTile.fxTileButton.setPrefHeight(41);

        Pane.getChildren().add(fxTile.fxTileButton);
        fxTileButtons.add(fxTile.fxTileButton);
    }

    /**
     * Checks whether a button is in a certain coordinates
     */

    private boolean isButtonOccupyingCoordinates(double x, double y) {
        for (javafx.scene.Node node : Pane.getChildren()) {
            if (node instanceof Button button) {
                if (button.getLayoutX() == x && button.getLayoutY() == y) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Initializes the FXTile
     */
    public FXTile initFXTile(Tile tile){
        FXTile fxTile = new FXTile(tile);
        return fxTile;
    }

    private Colour paintToColour(Paint paint){
        if (paint == Color.CRIMSON){
            return Colour.RED;
        }
        if (paint == Color.DARKCYAN){
            return Colour.BLUE;
        }
        if (paint == Color.ORANGE){
            return Colour.YELLOW;
        }
        if (paint == Color.BLACK){
            return Colour.BLACK;
        }
        else return null;
    }
}
