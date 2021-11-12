package com.bresch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

//TODO undo move?
//TODO save board?
//TODO Ai moves?

public class BoardManager {
	private HashMap<String, Piece> locations;
	private HashMap<String, ArrayList<String>> validMoves;
	private String whiteKingLocation;
	private String blackKingLocation;
	private Ui ui;
	private int gameRound;
	private int checkedForChecksNumber;

	public BoardManager(Ui ui) {
		this.ui = ui;
		this.locations = new HashMap<>();
		this.validMoves = new HashMap<>();
		this.gameRound = 0;
		this.checkedForChecksNumber = 1;
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////// public utility /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void pawnToQueen(String locationString) {
		int[] location = BoardManager.locationStringToArray(locationString);
		Piece queen = new Queen(locations.get(locationString).getTeam(), "queen", this);
		locations.put(locationString, queen);
		ui.changeToQueen(location, queen.getImagePath());
	}

	public void movePiece(String locationString1, String locationString2, HashMap<String, Piece> locationsLocal, boolean simulated) {
		Piece movingPiece = locationsLocal.get(locationString2);
//System.out.println("moving piece:["+movingPiece.getKind() +"]from["+locationString2+"]to["+locationString1+"]simulated["+simulated+"]");
		locationsLocal.put(locationString1, movingPiece);
		locationsLocal.remove(locationString2);
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
	public HashMap<String, ArrayList<String>> getValidatedMoves(){
		return validMoves;
	}
	// TODO new plan. 
	// updateMoves should ask possibleMoves(locations).
	// Then if (possibleMoves.get(xx).contains(kingLocations) && !isFriendly(xx))
	// Then boardManager should movePiece(string, string, simulatedLocations), ask
	// possibleMoves(simulatedLocations) ask if still checked (make sure same
	// player!)
	// If NOT checked, validMove.add(thatMove). Else its not valid.
	public void updateValidMoves(HashMap<String, ArrayList<String>> validatedMoves, int teamsTurn) {
		validatedMoves.clear();
		validatedMoves.putAll(validateMoves(getPotentialMovesAndUpdateKingsLocation(), teamsTurn));
		ArrayList<ArrayList<String>> validMovesValues = (ArrayList<ArrayList<String>>) validatedMoves.values().stream().filter(array -> !array.isEmpty()).collect(Collectors.toList());
		if (validMovesValues.isEmpty()) {
			ui.setCheckMate();
			ui.updateInfoLabel(whosTurn());
		}
	}
	public void updateValidMoves() {
		updateValidMoves(validMoves, whosTurn());
	}
	
	//should check simulated locations if there is a check by other team. ALSO needs to update moves for every simulation!!!!
	public boolean isThereCheck(HashMap<String, Piece> locationsLocal) {
		HashMap<String, ArrayList<String>> newPotentialMoves = getPotentialMovesAndUpdateKingsLocation(locationsLocal);
		int teamsTurn = whosTurn();
		String kingLocation = (teamsTurn == 0 ? whiteKingLocation : blackKingLocation);
		ArrayList<String> otherTeamLocationStrings = (ArrayList<String>) locationsLocal.keySet().stream()
				.filter(locStr -> teamsTurn != locationsLocal.get(locStr).getTeam())
				.collect(Collectors.toList());
		for (String locationString : otherTeamLocationStrings) {
			for (String moveString : newPotentialMoves.get(locationString)) {
				checkedForChecksNumber++;
				if (!isFriendly(kingLocation, locationString, locationsLocal) && (moveString.equals(kingLocation))) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isThereCheck() {
		return isThereCheck(locations);
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////// private utility ////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private HashMap<String, ArrayList<String>> getPotentialMovesAndUpdateKingsLocation(HashMap<String, Piece> locationsLocal) {
		HashMap<String, ArrayList<String>> potentialMoves = new HashMap<>();
		//update king locations and ask the piece how it can potentially move
		for (String locationString : locationsLocal.keySet()) {
			Piece piece = locationsLocal.get(locationString);
			if (piece.getKind().equals("king")) {
				if (piece.getTeam() == 0) {
					whiteKingLocation = locationString;
				} else {
					blackKingLocation = locationString;
				}
			}
			ArrayList<String> moves = piece.moves(locationString, locationsLocal);
			potentialMoves.put(locationString, moves);
		}
		return potentialMoves;
	}
	
	private HashMap<String, ArrayList<String>> getPotentialMovesAndUpdateKingsLocation() {
		return getPotentialMovesAndUpdateKingsLocation(locations);
	}

	// validates the moves,
	// body: split in teams, get every move and simulate one at a time. Check if its checked after each move if its not, add to validatedMoves.
	private HashMap<String, ArrayList<String>> validateMoves(HashMap<String, ArrayList<String>> potentialMoves, int teamsTurn) {
		HashMap<String, ArrayList<String>> validatedMoves = new HashMap<>();
		ArrayList<String> currentTeamLocationStrings = (ArrayList<String>) locations.keySet().stream()
				.filter(locStr -> teamsTurn == locations.get(locStr).getTeam())
				.collect(Collectors.toList());
		//Simulate one move at a time.
int times = 1;
		for (String locationString : currentTeamLocationStrings) {
			ArrayList<String> validMoves = new ArrayList<>();
			for (String move : potentialMoves.get(locationString)) {
				HashMap<String, Piece> simulatedLocations = new HashMap<>();
				simulatedLocations.putAll(locations);
//System.out.println("simulationNumber: [" + times + "] checkedForChecks: [" + checkedForChecksNumber + "]");
times++;
checkedForChecksNumber = 1;
				movePiece(move, locationString, simulatedLocations, true);
				
				if (isThereCheck(simulatedLocations)) {
//System.out.println("there is check! from:[" + locationString + "]->["+move+"]");
					continue;
				}
				validMoves.add(move);
			}
			validatedMoves.put(locationString, validMoves);
		}
		return validatedMoves;
	}
}
