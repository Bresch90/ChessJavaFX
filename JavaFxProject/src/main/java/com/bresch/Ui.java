package com.bresch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Ui extends Application {
	
	private static Button draggingButton;
	static Button[][] buttons = new Button[8][8];
	private BoardManager boardManager = new BoardManager(this);
	public void go(String[] args) {
		System.out.println("hello");
		launch();
	}

	@Override
	public void start(Stage stage) {
		try {
			BorderPane root = new BorderPane();
			GridPane grid = new GridPane();
			root.setCenter(grid); //getChildren().add(grid);
			//TODO show label with status messsages
//			VBox vbox = new VBox();
//			vbox.getChildren().add( grid);
//			root.getChildren().add(vbox);
			Label infoLabel = new Label("Hello, White starts.");
			infoLabel.setMinSize(20, 70);
			infoLabel.setTranslateY(-23);
			infoLabel.setFont(new Font(20));
//			infoLabel.setBorder(new Border()));
//			BorderStroke?
//			vbox.getChildren().add(infoLabel); // why doesnt work? vbox no good?
//			VBox should have worked? but maybe can fix? Maybe put in borderpane?
			Scene scene = new Scene(root, 400, 425);
			
			this.setNewGame(grid);
			root.setBottom(infoLabel);//, 0, 8);
			
			//TODO get resizing to work pls
//			for (int j = 0; j < 8; j++) {
//			    ColumnConstraints cc = new ColumnConstraints();
//			    cc.setHgrow(Priority.ALWAYS);
//			    cc.setFillWidth(true);
//			    grid.getColumnConstraints().add(cc);
//			}
//
//			for (int j = 0; j < 8; j++) {
//			    RowConstraints rc = new RowConstraints();
//			    rc.setVgrow(Priority.ALWAYS);
//			    rc.setFillHeight(true);
//			    grid.getRowConstraints().add(rc);
//			}
			
			stage.setScene(scene);
			stage.show();
			
			this.setButtonEvents();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void setNewGame(GridPane grid) {
		boardManager.newGameSpawn();
		for (int i = 0, c = 1; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				String locationString = String.valueOf(i) + " " + String.valueOf(j);
				Button button = buttons[i][j] = new Button(locationString);	
				//TODO uncomment Transparency for location text
				button.setStyle("-fx-background-color: " + (c % 2 == 0 ? "#857135" : "white"));//+ "; -fx-text-fill: transparent");
				//TODO Make resizable pls...
				button.setMinSize(50, 50);
//			    buttons[i][j].setPrefSize(scene.getWidth()/8, scene.getHeight()/8);
				button.setMaxSize(50, 50);
				if (boardManager.isPieceAtLocation(locationString)) {
					ImageView imageView = new ImageView(new Image(boardManager.getPiece(locationString).getImagePath()));
//					imageView.fitHeightProperty().bind(buttons[i][j].heightProperty());;
//					imageView.fitWidthProperty().bind(buttons[i][j].widthProperty());
					
					button.setGraphic(imageView);
					button.setContentDisplay(ContentDisplay.CENTER);
				}
				grid.add(button, i, 7-j);
				c++;
			} 
			c++;
		}
		boardManager.updateMoves();
	}
	
	private void setButtonEvents() {
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				//Set piece selection
				buttons[i][j].setOnAction(e -> {
					Button button = (Button)e.getSource();
					setMoveColours(button);
				});
				
				// Set Drag Detected, 
				buttons[i][j].setOnDragDetected(e -> {
					Button button = (Button)e.getSource();
					if (!boardManager.isPieceAtLocation(button.getText())) return;
		            Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
		            db.setDragView(button.snapshot(null, null));
		            ClipboardContent cc = new ClipboardContent();
		            cc.putString("");
		            db.setContent(cc);
		            draggingButton = button;
		            setMoveColours(button);
				});
				
				// Set Drag Over, accept drag
				buttons[i][j].setOnDragOver(e -> {
					Button button = (Button)e.getSource();
					String draggingLocation = draggingButton.getText();
		            if (draggingButton != null && boardManager.getValidMoves(draggingLocation).contains(button.getText()) && boardManager.isMyTurn(draggingLocation)) {
		                e.acceptTransferModes(TransferMode.MOVE);
		            }
		        });
				
				// Set Drag Dropped, 
				buttons[i][j].setOnDragDropped(e -> {
					Button button = (Button)e.getSource();
					//check if target spot is empty or does NOT contain a friendly piece
		            if (!boardManager.isPieceAtLocation(button.getText()) || !boardManager.isFriendly(button.getText(), draggingButton.getText())) {
		            	button.setGraphic(new ImageView(new Image(boardManager.getPiece(draggingButton.getText()).getImagePath())));
		            	button.setContentDisplay(ContentDisplay.CENTER);
		            	
		            	boardManager.movePiece(button.getText(), draggingButton.getText());
		            	
		            	draggingButton.setGraphic(null);
		                e.setDropCompleted(true);
		            }
		            draggingButton = null;
		            boardManager.nextRound();
		            boardManager.updateMoves();
		            resetColours();
		            
		        });
		              
			}
		}
	}
	
	public void changeToQueen(String locationString) {
		
		int[] location = Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
		Piece queen = new Piece(boardManager.getPiece(locationString).getTeam(), "queen", location[0], location[1]);
		boardManager.setQueenLocation(locationString, queen);
		buttons[location[0]][location[1]].setGraphic(new ImageView(new Image(boardManager.getPiece(draggingButton.getText()).getImagePath())));
    	//buttons[location[0]][location[1]].setContentDisplay(ContentDisplay.CENTER);
	}
	
	private void setMoveColours(Button button) {
		resetColours();
		if (boardManager.isPieceAtLocation(button.getText())) {
			for (String locationStringValidMove : boardManager.getValidMoves(button.getText())) {
				int[] loc = Arrays.stream(locationStringValidMove.split(" ")).mapToInt(Integer::parseInt).toArray();
				buttons[loc[0]][loc[1]].setStyle("-fx-background-color: "  + (boardManager.isFriendly(button.getText(), locationStringValidMove) ? "green" : "red"));
			}
		}
	}
	private void resetColours() {
		for (int i = 0, c = 1; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				buttons[i][j].setStyle("-fx-background-color: " + (c % 2 == 0 ? "#857135" : "white"));
				c++;
			}
			c++;
		}
	}
}
