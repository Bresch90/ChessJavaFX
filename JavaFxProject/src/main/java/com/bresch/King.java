package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;

public class King extends Piece{

	private ArrayList<int[]> moveDirections;
	private int maxRange;
	
	public King(int team, String kind, BoardManager boardManager) {
		super(team, kind);
		this.moveDirections = new ArrayList<>();
		super.moveDiagonally(moveDirections);
		super.moveSides(moveDirections);
		this.maxRange = 1;
	}
	
	@Override
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal, int[] checked) {
		return super.movesPiece(locationString, moveDirections, maxRange, locationsLocal, checked);
	}

}
