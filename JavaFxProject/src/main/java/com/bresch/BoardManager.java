package com.bresch;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO undo move?
//TODO save board?

public class BoardManager {
private static long time;
	private HashMap<String, Piece> locations;
	private HashMap<String, ArrayList<String>> validMoves;
	private String whiteKingLocation;
	private String blackKingLocation;
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
		return Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
	}
	public boolean isFriendly(String loc1, String loc2, HashMap<String, Piece> locationsLocal) {
		if (!locationsLocal.containsKey(loc1) || !locationsLocal.containsKey(loc2))
			return true;
		return locationsLocal.get(loc1).getTeam() == locationsLocal.get(loc2).getTeam();
	}
	public boolean isFriendly(String loc1, String loc2) {
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
		for (String locationString : locationsLocal.keySet()) {
			Piece piece = locationsLocal.get(locationString);
			if (piece.getKind().equals("pawn")) {
				int team = piece.getTeam();
				int y = Integer.parseInt(locationString.split(" ")[1]);
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
	public String getkings() {
		return "["+ whiteKingLocation + "] [" + blackKingLocation + "]";
	}
	public long getTime() {
		return time;
	}
	public long getTimeInGetPotentialMovesTotal() {
		return timeInGetPotentialMoves;
	}
	public long getTimeInPieceMove() {
		return locations.get("4 7").getTime();
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
		if (!simulated) movingPiece.setFirstMove();
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
//System.out.println("tring to get team ["+teamsTurn+"] s validatedmoves from: \n" + locationsLocal.toString());
		HashMap<String, ArrayList<String>> potentialMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesBlack = new HashMap<>();
		
		ArrayList<Integer> checked = new ArrayList<>(List.of(0,0,0));
		// (0, 1) turn on that this is from isThereCheck and should log checks and only get enemies moves
		// (1, 0) reset seen checks to 0, false
		// (2, teamsTurn) set who's move it is. ignored if (0,0)
		
		getPotentialMovesAndUpdateKingsLocation(potentialMovesWhite, potentialMovesBlack, locationsLocal, checked);
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
		
		ArrayList<Integer> checked = new ArrayList<>(List.of(0,0,0));
		// (0, 1) turn on that this is from isThereCheck and should log checks and only get enemies moves
		// (1, 0) reset seen checks to 0, false
		// (2, teamsTurn) set who's move it is. ignored if (0,0)
		
		getPotentialMovesAndUpdateKingsLocation(potentialMovesWhite, potentialMovesBlack, locations, checked);
		
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
long timeStart = System.nanoTime();
		HashMap<String, ArrayList<String>> potentialMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> potentialMovesBlack = new HashMap<>();
// (0, 1) turn on that this is from isThereCheck and should log checks and only get enemies moves
// (1, 0)reset seen checks to 0, false
// (2, teamsTurn) set who's move it is
		ArrayList<Integer> checked = new ArrayList<>(List.of(1,0,teamsTurn));
	    getPotentialMovesAndUpdateKingsLocation(potentialMovesWhite, potentialMovesBlack, locationsLocal, checked);
		if (checked.get(1) == 1) {
//			System.out.println("Omg there was check in: " + (teamsTurn == 0 ? potentialMovesWhite.toString() : potentialMovesBlack.toString()));
			long timeEnd = System.nanoTime();
			time += (timeEnd - timeStart);
			return true;
		} else {
			long timeEnd = System.nanoTime();
			time += (timeEnd - timeStart);
			return false;
		}
//		ArrayList<String> otherTeamLocationStrings = (ArrayList<String>) locationsLocal.keySet().stream()
//				.filter(locStr -> teamsTurn != locationsLocal.get(locStr).getTeam())
//				.collect(Collectors.toList());
		
//		if (teamsTurn != 0) {
//	// change HashMap<String, ArrayList<String>> to HashMap<String, HashMap<String>> for faster search, benchmark this
//			
//			int test = potentialMovesWhite.values().stream().mapToInt(arr -> (arr.contains(kingLocation) ? 1 : 0)).sum();
//			System.out.println("test if contains king = [" + test + "]" );
//			if (potentialMovesWhite.values().contains(kingLocation)) {
//long timeEnd = System.nanoTime();
//time += (timeEnd - timeStart);
//				return true;
//			}
//		} else {
//			if (potentialMovesBlack.values().contains(kingLocation)) {
//long timeEnd = System.nanoTime();
//time += (timeEnd - timeStart);
//				return true;
//			}
//		}
		
//		for (String locationString : otherTeamLocationStrings) {
//			for (String moveString : newPotentialMoves.get(locationString)) {
//				if (!isFriendly(kingLocation, locationString, locationsLocal) && (moveString.equals(kingLocation))) {
//					return true;
//				}
//			}
//		}
//long timeEnd = System.nanoTime();
//time += (timeEnd - timeStart);
//		return false;
	}
	public boolean isThereCheck() {
		return isThereCheck(locations, whosTurn());
	}
	public int getScoreFromKind(String kind) {
		return scoreMap.get(kind);
	}
	
	// makes a sum of the pieces of a board thrown at it.
	// it gives negative value to black and positive to white.
	public double getScoreFromBoard(HashMap<String, Piece> locationsLocal, double moveScoreTotal) {
		
//		return locationsLocal.values().stream().mapToInt(piece -> (piece.getTeam() == 0? 1 : -1) * getScoreFromKind(piece.getKind())).sum();
		
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
	private void getPotentialMovesAndUpdateKingsLocation(HashMap<String, ArrayList<String>> potentialMovesWhite,
			HashMap<String, ArrayList<String>> potentialMovesBlack, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked) {
long timeStart = System.nanoTime();
	// when checked.get(0) == 1 it checks in piece.moves() if the target of an attack is the king. Then that moveseries is checking the other player.
		//update king locations and ask the piece how it can potentially move
		for (String locationString : locationsLocal.keySet()) {
			Piece piece = locationsLocal.get(locationString);
			int teamOfPiece = piece.getTeam();
			if (teamOfPiece == 0) {
				if (piece.getKind().equals("king")) {
					whiteKingLocation = locationString;
				} else {
					blackKingLocation = locationString;
				}
			}
// special part in function if its from isThereCheck function?
			
// get checked info from moves? pass with boolean value?
	// if checked.get(0) == 0 add all moves. Otherwise only add enemies moves. aka when checking if checked only enemies is needed.
			if (checked.get(0) == 0) { 
				
				ArrayList<String> moves = piece.moves(locationString, locationsLocal, checked);
				if (teamOfPiece == 0) {
					potentialMovesWhite.put(locationString, moves);
				} else {
					potentialMovesBlack.put(locationString, moves);
				}
			} else if (checked.get(2) != teamOfPiece) {
			// only add enemies moves in potetialMoves
				ArrayList<String> moves = piece.moves(locationString, locationsLocal, checked);
				if (teamOfPiece == 0) {
					potentialMovesWhite.put(locationString, moves);
				} else {
					potentialMovesBlack.put(locationString, moves);
				}
			}
			
		}
long timeEnd = System.nanoTime();
timeInGetPotentialMoves = timeEnd - timeStart;
	}
	
	// validates the moves,
	// needs to be reworked. Checking through moves here to validate no checks.
	private void validateMoves(HashMap<String, ArrayList<String>> potentialMovesWhite, HashMap<String, ArrayList<String>> potentialMovesBlack, 
			HashMap<String, Piece> locationsLocal, int teamsTurn, HashMap<String, ArrayList<String>> validatedMovesWhite, HashMap<String, ArrayList<String>> validatedMovesBlack) {
		
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
	//System.out.println("there is check! from:[" + locationString + "]->["+move+"]");
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
