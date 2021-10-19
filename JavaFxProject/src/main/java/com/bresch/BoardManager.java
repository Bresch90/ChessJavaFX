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
	
	public void setQueenLocation(String locationString, Piece queen) {
		locations.put(locationString, queen);
	}
	public void movePiece(String locationString1, String LocationString2) {
		locations.put(locationString1, locations.get(LocationString2));
		locations.remove(LocationString2);
		locations.get(locationString1).setFirstMove();
	}

	public ArrayList<String> getValidMoves(String locationString) {
		if (!validMoves.containsKey(locationString))
			return new ArrayList<>();
		return validMoves.get(locationString);
	}

	public void updateMoves() {
		validMoves.clear();
		for (String locationString : locations.keySet()) {
			Piece piece = locations.get(locationString);
			String kind = piece.getKind();
			int[] loc = Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
			// ArrayList<int[]> moveDirections = new ArrayList<>();
			// up, down, right, left
			// new int[] {0,1}, new int[] {0,-1}, new int[] {1,0}, new int[] {-1,0}

			ArrayList<int[]> moveDirections = new ArrayList<>();
			int maxRange = 7;
			// diagonals
			// new int[] {1,1}, new int[] {1,-1}, new int[] {-1,-1}, new int[] {-1,1}

			switch (kind) {
			case "king": {
				moveSides(moveDirections);
				moveDiagonally(moveDirections);
				maxRange = 1;
				break;
			}
			case "queen": {
				moveSides(moveDirections);
				moveDiagonally(moveDirections);
				break;
			}
			case "bishop": {
				moveDiagonally(moveDirections);
				break;
			}
			case "knight": {
				maxRange = 1;
				moveKnight(moveDirections);
				break;
			}
			case "rook": {
				//TODO Finished? Possible without casting this way?
				validMoves.put(locationString, ((Rook) piece).moves(locationString));
				continue;
			}
			case "pawn": {
				int x = loc[0] + 0;
				if (locations.get(locationString).getFirstMove()) {
					//TODO make for first move move two etc. En Passant dont know how to..but yea...
				}
				int y = loc[1] + (locations.get(locationString).getTeam() == 1 ? -1 : 1);
				if (x < 0 || x > 7 || y < 0 || y > 7)
					break; //is this a problem??
				String moveString = x + " " + y;
				if (!locations.containsKey(moveString)) {
					if (validMoves.containsKey(locationString)) {
						validMoves.get(locationString).add(moveString);
					} else {
						validMoves.put(locationString, new ArrayList<>(Arrays.asList(moveString)));
					}
				}

				// TODO refactor this?
				//Pawns can only move forward, white up, black down.
				ArrayList<int[]> pawnAttack = new ArrayList<int[]>(
						Arrays.asList(new int[] { 1, (locations.get(locationString).getTeam() == 0 ? 1 : -1) },
								new int[] { -1, (locations.get(locationString).getTeam() == 0 ? 1 : -1) }));
				for (int[] attack : pawnAttack) {
					x = loc[0] + attack[0];
					y = loc[1] + attack[1];
					if (x < 0 || x > 7 || y < 0 || y > 7)
						continue;
					moveString = x + " " + y;
					if (locations.containsKey(moveString)) {
						if (this.isFriendly(locationString, moveString))
							continue;
						if (validMoves.containsKey(locationString)) {
							validMoves.get(locationString).add(moveString);
						} else {
							validMoves.put(locationString, new ArrayList<>(Arrays.asList(moveString)));
						}
					}
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + kind);
			}

			for (int[] moveDirection : moveDirections) {
				moveRecursion(locationString, loc[0], loc[1], moveDirection, maxRange);
			}
		}
	}
	
	public void nextRound() {
		for (String locationString : locations.keySet()) {
			Piece piece = locations.get(locationString);
			if (piece.getKind().equals("pawn")) {
				int team = piece.getTeam();
				int y = Integer.parseInt(locationString.split(" ")[1]);
				if (team == 1 && y == 0) {
					ui.changeToQueen(locationString);
					System.out.println("He should be black queeen!!!!");
				} else if (team == 0 && y == 7) {
					ui.changeToQueen(locationString);
					System.out.println("He should be white queeen!!!!");
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
	

	private void moveRecursion(String locationString, int x, int y, int[] moveDirection, int maxRange) {
		x += moveDirection[0];
		y += moveDirection[1];
		if (maxRange == 0 || x < 0 || x > 7 || y < 0 || y > 7)
			return;

		String moveString = x + " " + y;

		if (locations.containsKey(moveString)) {
			if (this.isFriendly(locationString, moveString))
				return;
			maxRange = 1;
		}

		if (validMoves.containsKey(locationString)) {
			validMoves.get(locationString).add(moveString);
		} else {
			validMoves.put(locationString, new ArrayList<>(Arrays.asList(moveString)));
		}

		moveRecursion(locationString, x, y, moveDirection, maxRange - 1);
	}

	private void moveKnight(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 1, -2 }, new int[] { 1, 2 }, new int[] { 2, 1 }, new int[] { -2, 1 }, 
							new int[] { -1, -2 }, new int[] { -1, 2 }, new int[] { 2, -1 }, new int[] { -2, -1 }));
	}
	
	private void moveSides(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { 1, 0 }, new int[] { -1, 0 }));
	}

	private void moveDiagonally(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 1, 1 }, new int[] { 1, -1 }, new int[] { -1, -1 }, new int[] { -1, 1 }));
	}
}
