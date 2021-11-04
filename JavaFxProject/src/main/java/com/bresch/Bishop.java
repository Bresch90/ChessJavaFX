package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;

public class Bishop extends Piece {

	private ArrayList<int[]> moveDirections;
	private int maxRange;
	
	public Bishop(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.moveDirections = new ArrayList<>();
		super.moveDiagonally(moveDirections);
		this.maxRange = 7;
	}
	
	@Override
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal) {
		return super.movesPiece(locationString, moveDirections, maxRange, locationsLocal);
	}

	

}
