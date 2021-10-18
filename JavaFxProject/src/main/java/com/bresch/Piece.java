package com.bresch;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Piece {
	
	private int team;
	private String kind;
	private String imagePath;
	private String loc;
	private boolean firstMove;
	
	public Piece(int team, String kind, int x, int y) {
		this.firstMove = true;
		this.team = team;
		String color = (team == 0 ? "l" : "d");
		this.loc = x + " " + y;
		
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

	public int getTeam() {
		return team;
	}

	public String getKind() {
		return kind;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(int x, int y) {
		this.loc = x + " " + y;
	}
	public boolean getFirstMove() {
		return firstMove;
	}
	public void setFirstMove() {
		firstMove = false;
	}
	//TODO this is never used. how should locations be dealt with?
	public void setLoc(String x, String y) {
		this.setLoc(Integer.parseInt(x), Integer.parseInt(y));
	}
	
	//TODO should know all the moves i can do etc.
	
	public void moveSides(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { 1, 0 }, new int[] { -1, 0 }));
	}

	public void moveDiagonally(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 1, 1 }, new int[] { 1, -1 }, new int[] { -1, -1 }, new int[] { -1, 1 }));
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
	
	
	
	
}
