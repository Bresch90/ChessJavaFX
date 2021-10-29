package com.bresch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

//TODO undo move?
//TODO save board?
//TODO show gameround etc?
//TODO Ai moves?

public class BoardManager {
	private HashMap<String, Piece> locations;
	private HashMap<String, ArrayList<String>> validMoves;
	private String whiteKingLocation;
	private String blackKingLocation;
	private HashMap<String, Piece> whiteLocations;
	private HashMap<String, Piece> blackLocations;
	private Ui ui;
	private int gameRound;

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

	public void movePiece(String locationString1, String locationString2, HashMap<String, Piece> locationsLocal) {
		Piece movingPiece = locationsLocal.get(locationString2);
		locationsLocal.put(locationString1, movingPiece);
		locationsLocal.remove(locationString2);
		movingPiece.setFirstMove();
	}

	public void movePiece(String locationString1, String locationString2) {
		movePiece(locationString1, locationString2, locations);
	}

	public boolean isValidMove(String draggingString, String dragOverString) {
		if (!validMoves.containsKey(draggingString))
			return false;
		return validMoves.get(draggingString).contains(dragOverString);
	}

	public ArrayList<String> getValidMoves(String locationString) {
		if (!validMoves.containsKey(locationString))
			return new ArrayList<>();
		return validMoves.get(locationString);
	}

	// TODO new plan. updateMoves should ask possibleMoves(locations).
	// Then if (possibleMoves.get(xx).contains(kingLocations) && !isFriendly(xx))
	// Then boardManager should movePiece(string, string, simulatedLocations), ask
	// possibleMoves(simulatedLocations) ask if still checked (make sure same
	// player!)
	// If NOT checked, validMove.add(thatMove). Else its not valid.
	public void updateValidMoves() {
		validMoves.clear();
		validMoves.putAll(validateMoves(whosTurn(), getPotentialMovesAndUpdateKingsLocation()));
	}

	private HashMap<String, ArrayList<String>> getPotentialMovesAndUpdateKingsLocation() {
		HashMap<String, ArrayList<String>> potentialMoves = new HashMap<>();
		//update king locations and ask the piece how it can potentially move
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
			potentialMoves.put(locationString, moves);
		}
		return potentialMoves;
	}

	// validates that the moves from the current player wont result in a check
	private HashMap<String, ArrayList<String>> validateMoves(int teamsTurn, HashMap<String, ArrayList<String>> potentialMoves) {
		HashMap<String, ArrayList<String>> validatedMoves = new HashMap<>();
		String kingLocation = (teamsTurn == 0 ? whiteKingLocation : blackKingLocation);
		ArrayList<String> currentTeamLocationStrings = new ArrayList<>();
		ArrayList<String> otherTeamLocationStrings = new ArrayList<>();
		potentialMoves.keySet().stream().forEach(locStr -> {
			if (teamsTurn == locations.get(locStr).getTeam()) {
				currentTeamLocationStrings.add(locStr);
			} else {
				otherTeamLocationStrings.add(locStr);
			}
		});
		
		for (String locationString : currentTeamLocationStrings) {
			for (ArrayList<String> moves : potentialMoves.values()) {
				ArrayList<String> validMoves = (ArrayList<String>) moves.stream()
						.filter(moveString -> //should isFriendly be there? is it necessary? and moveString? locationString?
							(!(!isFriendly(kingLocation, locationString) && (moveString.equals(kingLocation))) )
						).collect(Collectors.toList());
				validatedMoves.put(locationString, validMoves);
			}
		}
		return validatedMoves;
	}
	//parameter, locations?, moves? ValidMoves?
	public boolean isThereCheck() {
		//TODO I have no idea what ive done........
		int teamsTurn = whosTurn();
		ArrayList<String> locationStringOnlyOtherTeams = (ArrayList<String>) validMoves.keySet().stream().filter(
				streamLocationString -> teamsTurn != locations.get(streamLocationString).getTeam()).collect(Collectors.toList());
		for (String locationString : locationStringOnlyOtherTeams) {
			for (String moveString : validMoves.get(locationString)) {
				// should only be one player? and simulate move?
				if (!isFriendly(whiteKingLocation, locationString) && (moveString.equals(whiteKingLocation))
						||(!isFriendly(blackKingLocation, locationString) && moveString.equals(blackKingLocation))) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isThereCheck() {
		// this.isThereCheck(xxx)
	}
	
	// original check


	public void nextGameRound() {
		gameRound++;
		ui.updateInfoLabel(whosTurn());
	}

//	public int getGameRound() {
//		return gameRound;
//	}
	public int whosTurn() {
		return gameRound % 2;
	}
	public boolean isMyTurn(String locationString) {
		return whosTurn() == locations.get(locationString).getTeam();
	}

	public static int[] locationStringToArray(String locationString) {
		return Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
	}

	public void isThereNewQueen() {
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
	}


}
