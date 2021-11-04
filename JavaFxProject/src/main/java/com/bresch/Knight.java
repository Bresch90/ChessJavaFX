package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Knight extends Piece {

	private ArrayList<int[]> moveDirections;
	private int maxRange;
	
	public Knight(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.moveDirections = new ArrayList<>();
		moveKnight(moveDirections);
		this.maxRange = 1;
	}
	
	@Override
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal) {
		return super.movesPiece(locationString, moveDirections, maxRange, locationsLocal);
	}
	
	private void moveKnight(ArrayList<int[]> moveDirections) {
		moveDirections.addAll(
				Arrays.asList(new int[] { 1, -2 }, new int[] { 1, 2 }, new int[] { 2, 1 }, new int[] { -2, 1 }, 
							new int[] { -1, -2 }, new int[] { -1, 2 }, new int[] { 2, -1 }, new int[] { -2, -1 }));
	}
}
