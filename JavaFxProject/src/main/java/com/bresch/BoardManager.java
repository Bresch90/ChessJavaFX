package com.bresch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


//TODO undo move?
//TODO save board?
//TODO show gameround etc?
//TODO Ai moves?


public class BoardManager {
	HashMap<String, Piece> locations;
	HashMap<String, ArrayList<String>> validMoves;
	String whiteKingLocation;
	String blackKingLocation;
	HashMap<String, Piece> whiteLocations;
	HashMap<String, Piece> blackLocations;
	Ui ui;
	int gameRound;
	
	
	public BoardManager(Ui ui) {
		this.ui = ui;
		this.locations = new HashMap<>();
		this.validMoves = new HashMap<>();
		this.gameRound = 0;
		}

	public void newGameSpawn() {
		locations.clear();
		gameRound = 0;
		
		for (int i = 0; i < 8; i++) {
			locations.put(i + " " + 1, new Pawn(0, "pawn", this));
			locations.put(i + " " + 6, new Pawn(1, "pawn", this));
			Piece piece1 = null;
			Piece piece2 = null;
			String kind;
			switch (i) {
			case 0:
			case 7:	kind = "rook";
				piece1 = new Rook(0, kind, this);
				piece2 = new Rook(1, kind, this);
				break;
			case 1:
			case 6:	kind = "knight";
				piece1 = new Knight(0, kind, this);
				piece2 = new Knight(1, kind, this);
				break;
			case 2:
			case 5:	kind = "bishop";
				piece1 = new Bishop(0, kind, this);
				piece2 = new Bishop(1, kind, this);
				break;
			case 3:	kind = "queen";	
				piece1 = new Queen(0, kind, this);
				piece2 = new Queen(1, kind, this);
				break;
			case 4:	kind = "king";
				piece1 = new King(0, kind, this);
				piece2 = new King(1, kind, this);
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + i);
			}
			if (piece1 != null && piece2 != null) {
			locations.put(i + " " + 0, piece1);
			locations.put(i + " " + 7, piece2);
			}
		}
	}

	public boolean isFriendly(String loc1, String loc2) {
		if (!locations.containsKey(loc1) || !locations.containsKey(loc2))
			return true;
		return locations.get(loc1).getTeam() == locations.get(loc2).getTeam();
	}

	public boolean isPieceAtLocation(String locationString) {
		return locations.containsKey(locationString);
	}

	public Piece getPiece(String locationString) {
		return locations.get(locationString);
	}
	
	public void pawnToQueen(String locationString) {
		int[] location = BoardManager.locationStringToArray(locationString);
		Piece queen = new Queen(locations.get(locationString).getTeam(), "queen", this);
		locations.put(locationString, queen);
		ui.changeToQueen(location, queen.getImagePath());
	}
	
	public void movePiece(String locationString1, String locationString2) {
		Piece movingPiece = locations.get(locationString2);
		locations.put(locationString1, movingPiece);
		locations.remove(locationString2);
		movingPiece.setFirstMove();
	}

	public boolean isValidMove(String draggingString, String dragOverString) {
		if (!validMoves.containsKey(draggingString))
			return false;
		return validMoves.get(draggingString).contains(dragOverString);
	}
	
	public ArrayList<String> getValidMoves(String locationString) {
		if (!validMoves.containsKey(locationString)) return new ArrayList<>();
		return validMoves.get(locationString);
	}

	public void updateMoves() {
		//TODO How the F can I stop moves until solved Check????? Backup moves? Try all moved for friendly, only allow moves that solve check?
		HashMap<String, ArrayList<String>> backupValidMoves = new HashMap<>();
		backupValidMoves.putAll(validMoves);
		validMoves.clear();
		boolean seenCheck = false;
		for (String locationString : locations.keySet()) {
			Piece piece = locations.get(locationString);
			if (piece.getKind().equals("king")) {
				if (piece.getTeam() == 0) {
					whiteKingLocation = locationString;
				} else {
					blackKingLocation = locationString;
				}
			}
			ArrayList<String> moves = piece.moves(locationString);
			for (String moveString : moves) {
				if (!isFriendly(whiteKingLocation, locationString) && (moveString.equals(whiteKingLocation))
						||(!isFriendly(blackKingLocation, locationString) && moveString.equals(blackKingLocation))) {
					ui.setCheck(true);
					seenCheck = true;
				}
			}
			validMoves.put(locationString, moves);
		}
		if (!seenCheck) {
			ui.setCheck(false);
		}
	}
	
	public void nextRound() {
		for (String locationString : locations.keySet()) {
			Piece piece = locations.get(locationString);
			if (piece.getKind().equals("pawn")) {
				int team = piece.getTeam();
				int y = Integer.parseInt(locationString.split(" ")[1]);
				if ((team == 1 && y == 0) || (team == 0 && y == 7)) {
					pawnToQueen(locationString);
				} 
			}
		}
		gameRound++;
		ui.whosTurn(gameRound % 2);
	}
//	public int getGameRound() {
//		return gameRound;
//	}
	public boolean isMyTurn(String locationString) {
		return gameRound % 2 == locations.get(locationString).getTeam();
	}

	public static int[] locationStringToArray(String locationString) {
		return Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
	}
}
