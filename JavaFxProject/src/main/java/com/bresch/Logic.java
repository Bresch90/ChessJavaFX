package com.bresch;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class Logic {

	private Ui ui;
	private BoardManager boardManager;
	private int gameRound;
	
	public Logic(Ui ui, BoardManager boardManager) {
		this.ui = ui;
		this.boardManager = boardManager;
		this.gameRound = 0;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////// Button actions //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onButtonPress(Button button) {
		String locationString = button.getText();
		if (!boardManager.isPieceAtLocation(button.getText())) return;
		ui.setMoveColours(locationString, boardManager.getValidMoves(locationString));
	}

	public void onDragDetected(Button button) {
		String locationString = button.getText();
		if (!boardManager.isPieceAtLocation(locationString)) return;
        Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
        //db.setDragView(button.snapshot(null, null));
        db.setDragView(new Image(boardManager.getPiece(locationString).getImagePath()));
        ////why is this needed?////
        ClipboardContent cc = new ClipboardContent();
        cc.putString("");
        db.setContent(cc);
        ///////////////////////////
        ui.setDraggingButton(button);
        ui.setMoveColours(locationString, boardManager.getValidMoves(locationString));
	}

	public boolean isDragOverAccept(String draggingString, String dragOverString) {
		return (boardManager.isMyTurn(draggingString) && boardManager.isValidMove(draggingString, dragOverString));
	}

	public boolean onDragDropped(Button draggingButton, Button button) {
		String draggingString = draggingButton.getText();
		String dragOverString = button.getText();
		//check if target spot is empty or does NOT contain a friendly piece
        if (boardManager.isPieceAtLocation(dragOverString) && boardManager.isFriendly(dragOverString, draggingString)) return false;
        button.setGraphic(new ImageView(new Image(boardManager.getPiece(draggingString).getImagePath())));
    	boardManager.movePiece(dragOverString, draggingString);
    	draggingButton.setGraphic(null);
        ui.setDraggingButton(null);
        ui.resetColours();
        
		boardManager.isThereNewQueen();
		boardManager.updateValidMoves();
		boardManager.nextGameRound();
        
        
        //TODO also, Ask boardManager.possibleMoves(locations) -> logic for AI.
        
       
        return true;
	}
}
