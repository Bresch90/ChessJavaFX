package com.bresch;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class Logic {

	private boolean opponentActive;
	private Ui ui;
	private BoardManager boardManager;
	private Opponent opponent;
	
	public Logic(Ui ui, BoardManager boardManager) {
		this.ui = ui;
		this.boardManager = boardManager;
		this.opponent = new Opponent(boardManager, ui);
		this.opponentActive = true;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////// Button actions //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onButtonPress(Button button) {
		String locationString = button.getText();
		if (!boardManager.isPieceAtLocation(button.getText())) return;
		ui.setMoveColours(locationString, boardManager.getValidMovesForLoc(locationString));
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
        ui.setMoveColours(locationString, boardManager.getValidMovesForLoc(locationString));
	}

	public boolean isDragOverAccept(String draggingString, String dragOverString) {
		return (boardManager.isMyTurn(draggingString) && boardManager.isValidMove(draggingString, dragOverString));
	}

	public boolean onDragDropped(Button draggingButton, Button button) {
		String draggingString = draggingButton.getText();
		String targetString = button.getText();
		//return false if target spot is Not empty or contain a friendly piece
        if (boardManager.isPieceAtLocation(targetString) && boardManager.isFriendly(targetString, draggingString)) return false;
        
        button.setGraphic(new ImageView(new Image(boardManager.getPiece(draggingString).getImagePath())));
    	boardManager.movePiece(targetString, draggingString);
    	draggingButton.setGraphic(null);
        ui.setDraggingButton(null);
        ui.resetColours();
        
		boardManager.isThereNewQueen();
		boardManager.nextGameRound();
		ui.setCheck(boardManager.isThereCheck());
		ui.updateInfoLabel(boardManager.whosTurn());
		boardManager.updateValidMoves();
        return true;
	}
	
	// Separate thread for opponent so ui can continue running
	public void runOpponent() {
        Thread opponentThread = new Thread(new Runnable() {
			@Override
			public void run(){
				// only for running continuously, aka computer against itself
				for (int i = 0; i < 1001; i++) {
					CountDownLatch latch = new CountDownLatch(1);
					if (opponentActive && boardManager.whosTurn() < 2) {
				        try {
				        	ArrayList<String> decisions = opponent.makeDecision();
				    		if (decisions.isEmpty()) {
				    System.out.println("* I give up *");
				    			return;
				    		}
				    		Button draggingButton = ui.getButton(decisions.get(0));
				    		Button targetButton = ui.getButton(decisions.get(1));
System.out.println("Number of moves calculated ["+decisions.get(2)+"]");
				    		Platform.runLater(new Runnable(){
				    			// move piece for real in main thread
				    			@Override
				    			public void run() {
//System.out.println("I am RunLater");
				    				if (onDragDropped(draggingButton, targetButton)) {
//System.out.println("It was true? why wait?");
				    					latch.countDown();
				    				} else {
//System.out.println("It was false [" + draggingButton.getText() + "] to button ["+targetButton.getText()+"]");
				    				}
				    				
				    			}
				    		});
							latch.await();
//System.out.println("DONE WAITING");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
        	
        });
        // don't let thread prevent JVM shutdown
        opponentThread.setDaemon(true);
        opponentThread.start();
	}
	

	

	
}
