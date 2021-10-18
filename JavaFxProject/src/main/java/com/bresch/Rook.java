package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rook extends Piece {
	ArrayList<int[]> moveDirections;
	int maxRange;
	
	public Rook(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.moveDirections = new ArrayList<>();
		super.moveSides(moveDirections);
		this.maxRange = 7;
	}
	
	public HashMap<String, ArrayList<String>> moves(String locationString) {
		int[] loc = Arrays.stream(locationString.split(" ")).mapToInt(Integer::parseInt).toArray();
		HashMap<String, ArrayList<String>> validMoves = new HashMap<>();
		for (int[] moveDirection : moveDirections) {
			super.moveRecursion(locationString, loc[0], loc[1], moveDirection, maxRange, validMoves);
		}
		return validMoves;
	}

}
