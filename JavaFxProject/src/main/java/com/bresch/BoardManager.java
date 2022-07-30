package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO undo move?
//TODO save board?

public class BoardManager {
private static long time;
	private HashMap<String, Piece> locations;
	private HashMap<String, ArrayList<String>> validMoves;
	private Ui ui;
	private int gameRound;
	private HashMap<String, Integer> scoreMap;
	private long timeInGetPotentialMoves;


	public BoardManager(Ui ui) {
		this.ui = ui;
		this.locations = new HashMap<>();
		this.validMoves = new HashMap<>();
		this.gameRound = 0;
		this.scoreMap = new HashMap<>(Map.of("pawn", 1, "rook", 5, "knight", 3, "bishop", 3, "queen",
				9, "king", 90));
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////// Game States /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			case 7:
				kind = "rook";
				piece1 = new Rook(0, kind, this);
				piece2 = new Rook(1, kind, this);
				break;
			case 1:
			case 6:
				kind = "knight";
				piece1 = new Knight(0, kind, this);
				piece2 = new Knight(1, kind, this);
				break;
			case 2:
			case 5:
				kind = "bishop";
				piece1 = new Bishop(0, kind, this);
				piece2 = new Bishop(1, kind, this);
				break;
			case 3:
				kind = "queen";
				piece1 = new Queen(0, kind, this);
				piece2 = new Queen(1, kind, this);
				break;
			case 4:
				kind = "king";
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
	
/////////////////////////////////////////////// Special States /////////////////////////////////////////////////////////
	public void specialStateSpawn() {
		locations.clear();
		gameRound = 0;

			locations.put(3 + " " + 2, new Pawn(0, "pawn", this));
			locations.put(3 + " " + 5, new Pawn(1, "pawn", this));
			locations.put(4 + " " + 2, new King(0, "king", this));
			locations.put(4 + " " + 5, new King(1, "king", this));
			locations.put(5 + " " + 2, new Queen(0, "queen", this));
			locations.put(5 + " " + 5, new Queen(1, "queen", this));
			locations.put(0 + " " + 0, new Pawn(0, "pawn", this));
			locations.put(0 + " " + 7, new Pawn(1, "pawn", this));
		
	}
	
	public void specialStateSpawnKingMakeUnblockedKill() {
		locations.clear();
		gameRound = 0;

			locations.put(3 + " " + 2, new Pawn(0, "pawn", this));
			locations.put(3 + " " + 5, new Pawn(1, "pawn", this));
			locations.put(4 + " " + 2, new King(0, "king", this));
			locations.put(4 + " " + 5, new King(1, "king", this));
			locations.put(3 + " " + 4, new Queen(0, "queen", this));
			locations.put(4 + " " + 6, new Queen(1, "queen", this));
			locations.put(0 + " " + 0, new Pawn(0, "pawn", this));
			locations.put(0 + " " + 6, new Pawn(1, "pawn", this));
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////// Setters/Getters /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void nextGameRound() {
		gameRound++;
	}
	public int whosTurn() {
		return gameRound % 2;
	}
	public static int[] locationStringToArray(String locationString) {
		int[] intArray = {locationString.charAt(0)-'0', locationString.charAt(2)-'0'};
		return intArray;
	}
	public boolean isFriendly(String loc1, String loc2, HashMap<String, Piece> locationsLocal) {
		return locationsLocal.get(loc1).getTeam() == locationsLocal.get(loc2).getTeam();
	}
	public boolean isFriendly(String loc1, String loc2) {
		if (!locations.containsKey(loc1) || !locations.containsKey(loc2))
			return true;
		return isFriendly(loc1, loc2, locations);
	}
	public boolean isPieceAtLocation(String locationString, HashMap<String, Piece> locationsLocal) {
		return locationsLocal.containsKey(locationString);
	}
	public boolean isPieceAtLocation(String locationString) {
		return isPieceAtLocation(locationString, locations);
	}
	public boolean isMyTurn(String locationString) {
		return whosTurn() == locations.get(locationString).getTeam();
	}
	public Piece getPiece(String locationString) {
		return locations.get(locationString);
	}
	public void isThereNewQueen(HashMap<String, Piece> locationsLocal) {
	// if a pawn has reached the end it should be a queen
	// technically it should give the user a choice of what to upgrade to but I didn't feel like implementing that ui etc so I just went with a queen. 
		for (String locationString : locationsLocal.keySet()) {
			Piece piece = locationsLocal.get(locationString);
			if (piece.getKind().equals("pawn")) {
				int team = piece.getTeam();
				int y = locationString.charAt(2)-'0';
				if ((team == 1 && y == 0) || (team == 0 && y == 7)) {
					pawnToQueen(locationString);
				}
			}
		}
	}
	public void isThereNewQueen() {
		isThereNewQueen(locations);
	}
	public HashMap<String, Piece> getLocations() {
		return locations;
	}
	// all the time units was just to check/improve performance
	public long getTime() {
		return time;
	}
	public long getTimeInGetPotentialMovesTotal() {
		return timeInGetPotentialMoves;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////// public utility /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void pawnToQueen(String locationString) {
		int[] location = BoardManager.locationStringToArray(locationString);
		Piece queen = new Queen(locations.get(locationString).getTeam(), "queen", this);
		locations.put(locationString, queen);
		ui.changeToQueen(location, queen.getImagePath());
	}

	public void movePiece(String moveTo, String moveFrom, HashMap<String, Piece> locationsLocal, boolean simulated) {
		Piece movingPiece = locationsLocal.get(moveFrom);
//System.out.println("moving piece:["+movingPiece.getKind() +"]from["+moveFrom+"]to["+moveTo+"]simulated["+simulated+"]");
		locationsLocal.put(moveTo, movingPiece);
		locationsLocal.remove(moveFrom);
	// only setFirstMove if its moving for real and not simulated.
	// also put castling here to not impact performance too much and mess up code..feel finished with the project anyways
	// and will not implement enpassant.
		if (!simulated && movingPiece.getFirstMove()) {
			if (movingPiece.getKind().equals("king")) {
				if (moveTo.equals("2 0")) {
					movePiece("3 0", "0 0");
					ui.getButton("0 0").setGraphic(null);
				} else if (moveTo.equals("6 0")) {
					movePiece("5 0", "7 0");
					ui.getButton("7 0").setGraphic(null);
				} else if (moveTo.equals("2 7")) {
					movePiece("3 7", "0 7");
					ui.getButton("0 7").setGraphic(null);
				} else if (moveTo.equals("6 7")) {
					movePiece("5 7", "7 7");
					ui.getButton("7 7").setGraphic(null);
				}
			}
			movingPiece.setFirstMove();
		}
	}
	
	public void movePiece(String locationString1, String locationString2) {
		movePiece(locationString1, locationString2, locations, false);
	}
	public boolean isValidMove(String draggingString, String dragOverString) {
		if (!validMoves.containsKey(draggingString))
			return false;
		return validMoves.get(draggingString).contains(dragOverString);
	}
	public ArrayList<String> getValidMovesForLoc(String locationString) {
		if (!validMoves.containsKey(locationString))
			return new ArrayList<>();
		return validMoves.get(locationString);
	}
	public void getValidatedMoves(HashMap<String, Piece> locationsLocal, int teamsTurn, 
			HashMap<String, ArrayList<String>> validatedMovesWhite, HashMap<String, ArrayList<String>> validatedMovesBlack){
		HashMap<String, ArrayList<String>> potentialMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesBlack = new HashMap<>();
		
		// I know this is ugly but this was added later to fix performance of the isThereCheck so EVERY move calculated wouldn't be cycled through multiple times when it only needs to cycle once.
		// Probably should have made another method only for isThereCheck when generating moves to make it more clear..
//		ArrayList<Integer> checked = new ArrayList<>(List.of(0,0,0)); // 771606-784934ms in 5moves
		int[] checked = {0,0,0};  // 759196-774300ms in 5moves marginal improvement but I'm sticking with this.
		// [0] == 1 turn on that this is from isThereCheck and should log checks and only get enemies moves
		// [1] == 0 reset seen checks to 0, false
		// [2] == teamsTurn set who's move it is. ignored if [0] == 0
		
		getPotentialMoves(potentialMovesWhite, potentialMovesBlack, locationsLocal, checked);
		validateMoves(potentialMovesWhite, potentialMovesBlack, locationsLocal, teamsTurn, validatedMovesWhite, validatedMovesBlack);
	}
	public HashMap<String, ArrayList<String>> getValidatedMoves(){
		return validMoves;
	}
	public void updateValidMoves(HashMap<String, ArrayList<String>> validatedMoves, int teamsTurn) {
		validatedMoves.clear();
		HashMap<String, ArrayList<String>> validatedMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> validatedMovesBlack = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesBlack = new HashMap<>();
		
		int[] checked = {0,0,0}; 
		// [0] == 1 turn on that this is from isThereCheck and should log checks and only get enemies moves
		// [1] == 0 reset seen checks to 0, false
		// [2] == teamsTurn set who's move it is. ignored if [0] == 0
		
		getPotentialMoves(potentialMovesWhite, potentialMovesBlack, locations, checked);
		
		validateMoves(potentialMovesWhite, potentialMovesBlack, locations, teamsTurn, validatedMovesWhite, validatedMovesBlack);
		validatedMoves.putAll((teamsTurn == 0 ? validatedMovesWhite : validatedMovesBlack));
		ArrayList<ArrayList<String>> validMovesValues = (ArrayList<ArrayList<String>>) validatedMoves.values().stream().filter(array -> !array.isEmpty()).collect(Collectors.toList());
		if (validMovesValues.isEmpty()) {
			ui.setCheckMate();
			ui.updateInfoLabel(whosTurn());
		}
	}
	public void updateValidMoves() {
		updateValidMoves(validMoves, whosTurn());
	}
	
	public boolean isThereCheck(HashMap<String, Piece> locationsLocal, int teamsTurn) {
		HashMap<String, ArrayList<String>> potentialMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesBlack = new HashMap<>();
		int[] checked = {1,0,teamsTurn}; 
		// [0] == 1 turn on that this is from isThereCheck and should log checks and only get enemies moves
		// [1] == 0 reset seen checks to 0, false
		// [2] == teamsTurn set who's move it is. ignored if [0] == 0
	    getPotentialMoves(potentialMovesWhite, potentialMovesBlack, locationsLocal, checked);
		if (checked[1] == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isThereCheck() {
		return isThereCheck(locations, whosTurn());
	}
	
	public int getScoreFromKind(String kind) {
		return scoreMap.get(kind);
	}
	
	public double getScoreFromBoard(HashMap<String, Piece> locationsLocal, double moveScoreTotal) {
		// makes a sum of the pieces of a board thrown at it.
		// it gives negative value to black and positive to white.
		int total = 0;
		for (Piece piece : locationsLocal.values()) {
			if (piece.getTeam() == 0) {
				total += getScoreFromKind(piece.getKind());
			} else {
				total += -1 * getScoreFromKind(piece.getKind());				
			}
		}
		return total + moveScoreTotal;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////// private utility ////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void getPotentialMoves(HashMap<String, ArrayList<String>> potentialMovesWhite,
			HashMap<String, ArrayList<String>> potentialMovesBlack, HashMap<String, Piece> locationsLocal, int[] checked) {
	// when checked[0] == 1 it checks in piece.moves() if the target of an attack is the enemy king. Then that moveseries is checking the other player.
		//update king locations and ask the piece how it can potentially move
		String whiteKingLocation = null;
		String blackKingLocation = null;

		for (String locationString : locationsLocal.keySet()) {
			Piece piece = locationsLocal.get(locationString);
			int teamOfPiece = piece.getTeam();
			if (piece.getKind().equals("king")) {
				if (teamOfPiece == 0) {
					whiteKingLocation = locationString;
				} else {
					blackKingLocation = locationString;
				}
			}
// special part in function if its from isThereCheck function? (checked[0] == 1)
			if (checked[0] == 0) { 
			// if checked[0] == 0 add all moves. Otherwise only add enemies moves. aka when checking if checked only enemies is needed.
				ArrayList<String> moves = piece.moves(locationString, locationsLocal, checked);
				if (teamOfPiece == 0) {
					potentialMovesWhite.put(locationString, moves);
				} else {
					potentialMovesBlack.put(locationString, moves);
				}
			} else if (checked[2] != teamOfPiece) {
			// only add enemies moves in potetialMoves
				ArrayList<String> moves = piece.moves(locationString, locationsLocal, checked);
				if (teamOfPiece == 0) {
					potentialMovesWhite.put(locationString, moves);
				} else {
					potentialMovesBlack.put(locationString, moves);
				}
			}
		}
		// shitty castling implementation 										// before it was 47-57000ms in potentialMoves // after it was 55-61000ms
		// if not from isThereCheck then check if castling is a valid move.
		if (checked[0] == 0) {
			// current players move
			if (checked[2] == 0) {
				if (locationsLocal.get(whiteKingLocation).getFirstMove()) {
					Piece leftRook = locationsLocal.get("0 0");
					Piece rightRook = locationsLocal.get("7 0");
					if (leftRook != null && leftRook.getFirstMove() && leftRook.getTeam() == 0 && leftRook.getKind().equals("rook")) {
						if (potentialMovesWhite.get("0 0").contains("3 0")) {
							potentialMovesWhite.get(whiteKingLocation).add("2 0");
						}
					}
					if (rightRook != null && rightRook.getFirstMove() && rightRook.getTeam() == 0 && rightRook.getKind().equals("rook")) {
						if (potentialMovesWhite.get("7 0").contains("5 0")) {
							potentialMovesWhite.get(whiteKingLocation).add("6 0");
						}
					}
					
				}
			} else {
				if (locationsLocal.get(blackKingLocation).getFirstMove()) {
					Piece leftRook = locationsLocal.get("0 7");
					Piece rightRook = locationsLocal.get("7 7");
					if (leftRook != null && leftRook.getFirstMove() && leftRook.getTeam() == 1 && leftRook.getKind().equals("rook")) {
						if (potentialMovesBlack.get("0 7").contains("3 7")) {
							potentialMovesBlack.get(blackKingLocation).add("2 7");
						}
					}
					if (rightRook != null && rightRook.getFirstMove() && rightRook.getTeam() == 1 && rightRook.getKind().equals("rook")) {
						if (potentialMovesBlack.get("7 7").contains("5 7")) {
							potentialMovesBlack.get(blackKingLocation).add("6 7");
						}
					}
				}
			}
		}
	}
	
	private void validateMoves(HashMap<String, ArrayList<String>> potentialMovesWhite, HashMap<String, ArrayList<String>> potentialMovesBlack, 
			HashMap<String, Piece> locationsLocal, int teamsTurn, HashMap<String, ArrayList<String>> validatedMovesWhite, HashMap<String, ArrayList<String>> validatedMovesBlack) {
		// validates the moves,
		// needs to be reworked. Checking through moves here to validate no checks.
		// I have no idea how this works..got it working with a showerthought and I think it needs to be simplified.
		for (String locationString : locationsLocal.keySet()) {
			ArrayList<String> validMovesLocWhite = new ArrayList<>();
			ArrayList<String> validMovesLocBlack = new ArrayList<>();
			int currentLocTeam = locationsLocal.get(locationString).getTeam();
		// check friendly moves for checks (own king getting in check)
			if (currentLocTeam == 0) {
				if (potentialMovesWhite.get(locationString) == null) {
					continue;
				}
			} else {
				if (potentialMovesBlack.get(locationString) == null) {
					continue;
				}
			}
			if (currentLocTeam == teamsTurn) {
				for (String moveStr : (currentLocTeam == 0 ? potentialMovesWhite.get(locationString) : potentialMovesBlack.get(locationString))) {
					HashMap<String, Piece> simulatedLocations = new HashMap<>();
					simulatedLocations.putAll(locationsLocal);
					movePiece(moveStr, locationString, simulatedLocations, true);
					
					if (isThereCheck(simulatedLocations, teamsTurn)) {
						continue;
					}
					if (currentLocTeam == 0) {
						validMovesLocWhite.add(moveStr);
					} else {
						validMovesLocBlack.add(moveStr);
					}
				}
		// Don't check enemies moves for checks
			} else {
				if (currentLocTeam == 0) {
					validMovesLocWhite.addAll(potentialMovesWhite.get(locationString));
				} else {
					validMovesLocBlack.addAll(potentialMovesBlack.get(locationString));
				}
			}
		// Wjat am i doing here??
			if (currentLocTeam == 0) {
				validatedMovesWhite.put(locationString, validMovesLocWhite);
			} else {
				validatedMovesBlack.put(locationString, validMovesLocBlack);
			}
		}
	}
	
}
