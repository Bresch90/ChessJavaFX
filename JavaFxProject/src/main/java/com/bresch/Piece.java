package com.bresch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Piece {
protected static long time;
	protected int team;
	private String kind;
	private String imagePath;
	private boolean firstMove;
	
	
	public Piece(int team, String kind) {
		this.firstMove = true;
		this.team = team;
		String color = (team == 0 ? "l" : "d");

		
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
		long timeStart = System.nanoTime(); // 41-52000ms with original 30-40000ms with assigning Piece and targetPiece.
		int locX = locationString.charAt(0)-'0';
		int locY = locationString.charAt(2)-'0';
		ArrayList<String> potentialMoves = new ArrayList<>();
		StringBuilder moveStringBuilder = new StringBuilder(3);
		moveStringBuilder.append('0');
		moveStringBuilder.append(' ');
		moveStringBuilder.append('0');
		for (int[] moveDirection : moveDirections) {
			moveRecursion(moveStringBuilder, locX, locY, moveDirection, maxRange, potentialMoves, locationsLocal, checked);
		}
		long timeEnd = System.nanoTime();
		Piece.time += (timeEnd-timeStart);
		return potentialMoves;
	}
	private void moveRecursion(StringBuilder moveStringBuilder, int x, int y, int[] moveDirection, int maxRange, ArrayList<String> potentialMoves, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked) {
		x += moveDirection[0];
		y += moveDirection[1];
		if (maxRange == 0 || x < 0 || x > 7 || y < 0 || y > 7)
			return;
		
		moveStringBuilder.setCharAt(0, (char) ('0'+ x)); // faster than string = x + " " + y;
		moveStringBuilder.setCharAt(2, (char) ('0'+ y));
		String moveStr = moveStringBuilder.toString(); // 12-16000ms this way. instead of moveStringBuilder.toString() everywhere. 93000ms -> 73-82000ms first black move.
		Piece targetPiece = locationsLocal.get(moveStr);
		if (targetPiece != null) {
			if (team == targetPiece.getTeam()) {
				return;
			}
			if (checked.get(0) == 1 && targetPiece.getKind().equals("king")) {
// if this is true, then there is enemy checking current players king. Then this is not a valid move.
				checked.set(1, 1);
			}
			
			maxRange = 1;
		}
		
		potentialMoves.add(moveStr);
		moveRecursion(moveStringBuilder, x, y, moveDirection, maxRange - 1, potentialMoves, locationsLocal, checked);
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
