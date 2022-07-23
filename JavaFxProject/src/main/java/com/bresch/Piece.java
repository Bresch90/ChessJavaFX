package com.bresch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Piece {
protected static long time;
private static long timeInPieceGetMoves;
	private BoardManager boardManager;
	private int team;
	private String kind;
	private String imagePath;
	private boolean firstMove;
	
	public Piece(int team, String kind, BoardManager boardManager) {
		this.firstMove = true;
		this.team = team;
		String color = (team == 0 ? "l" : "d");
		this.boardManager = boardManager;
		
		//Determining the kind and imagefile to use
		kind = kind.toLowerCase();
		this.kind = kind;
		
		switch (kind) {
			case "king": 	kind = "k"; break;
			case "queen":	kind = "q"; break;	
			case "bishop":  kind = "b"; break;
			case "knight":  kind = "n"; break;
			case "rook":	kind = "r"; break;
			case "pawn":	kind = "p"; break;
			default: throw new IllegalArgumentException("Unexpected value: " + kind);
		}
		
		//this.imagePath =  "File:" + kind + color + "t60.png";
		//Changed to work with maven resources
		this.imagePath = Piece.class.getResource("/" + kind + color + "t60.png").toString();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////// Setters/Getters /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getTeam() {
		return team;
	}

	public String getKind() {
		return kind;
	}

	public String getImagePath() {
		return imagePath;
	}

	public boolean getFirstMove() {
		return firstMove;
	}
	public void setFirstMove() {
		firstMove = false;
	}
	public long getTime() {
		return time;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// Moves //////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ArrayList<String> movesPiece(String locationString, ArrayList<int[]> moveDirections, int maxRange, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked) {
Long timeStart = System.nanoTime();
		int[] loc = BoardManager.locationStringToArray(locationString);
		ArrayList<String> potentialMoves = new ArrayList<>();
		for (int[] moveDirection : moveDirections) {
			moveRecursion(locationString, loc[0], loc[1], moveDirection, maxRange, potentialMoves, locationsLocal, checked);
		}
Long timeEnd = System.nanoTime();
//time += (timeEnd-timeStart);
		return potentialMoves;
	}
	private void moveRecursion(String locationString, int x, int y, int[] moveDirection, int maxRange, ArrayList<String> potentialMoves, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked) {
		x += moveDirection[0];
		y += moveDirection[1];
		if (maxRange == 0 || x < 0 || x > 7 || y < 0 || y > 7)
			return;

		String moveString = x + " " + y;

		if (boardManager.isPieceAtLocation(moveString, locationsLocal)) {
			if (boardManager.isFriendly(locationString, moveString, locationsLocal))
				return;
// if this is true there is check. Do something with this info!
// boolean won't work?
// this wont work. Have to check if the Unfriendly piece is KING!!! ///////////////////////////////////////////////////////////////////////
			if (checked.get(0) == 1 && locationsLocal.get(moveString).kind.equals("king")) {
				checked.set(1, 1);
			}
			
			maxRange = 1;
		}
		potentialMoves.add(moveString);
		moveRecursion(locationString, x, y, moveDirection, maxRange - 1, potentialMoves, locationsLocal, checked);
	}
	public void moveSides(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { 1, 0 }, new int[] { -1, 0 }));
	}
	public void moveDiagonally(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 1, 1 }, new int[] { 1, -1 }, new int[] { -1, -1 }, new int[] { -1, 1 }));
	}

	public abstract ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked);
}
