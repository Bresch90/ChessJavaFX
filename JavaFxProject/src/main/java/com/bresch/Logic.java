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
		this.opponent = new Opponent(boardManager);
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
        ////why is this needed???////
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
	// did not really get implemented correctly..my thought was getting a counter in a label updating as
	// often as possible to show that the computer is calculating moves and show how many moves it's currently calculated.
	public void runOpponent() {
        Thread opponentThread = new Thread(new Runnable() {
			@Override
			public void run(){
				// only for running continuously, aka computer against itself else 1 loop
				for (int i = 0; i < 1; i++) {
					CountDownLatch latch = new CountDownLatch(1);
					// I can't remember why I did boardManager.whosTurn() > -1 but if it's 0, it ignores the loop for 1001 move??..
					if (opponentActive && boardManager.whosTurn() > -1) {
				        try {
				        	ArrayList<String> decisions = opponent.makeDecision();       	
				    		if (decisions.isEmpty()) {
				    System.out.println("* I give up *");
				    			return;
				    		}
				    		Button draggingButton = ui.getButton(decisions.get(0));
				    		Button targetButton = ui.getButton(decisions.get(1));

				    		Platform.runLater(new Runnable(){
				    			// move piece for real in main thread
				    			@Override
				    			public void run() {
				    				if (onDragDropped(draggingButton, targetButton)) {
				    					latch.countDown();
				    				} else {
System.out.println("It was false, this should never happen! [" + draggingButton.getText() + "] to button ["+targetButton.getText()+"]");
				    				}
				    				
				    			}
				    		});
							latch.await();
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
