package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;

public class Rook extends Piece {
	ArrayList<int[]> moveDirections;
	int maxRange;
	
	public Rook(int team, String kind, int x, int y) {
		super(team, kind, x, y);
		this.moveDirections = new ArrayList<>();
		super.moveSides(moveDirections);
		this.maxRange = 7;
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<String, ArrayList<String>> moves(String locationString, int[] loc) {
		HashMap<String, ArrayList<String>> validMoves = new HashMap<>();
		for (int[] moveDirection : moveDirections) {
			moveRecursion(locationString, loc[0], loc[1], moveDirection, maxRange);
		}
		return validMoves;
	}

}
