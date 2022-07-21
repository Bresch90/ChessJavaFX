package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pawn extends Piece {

	private int moveDirections;
	private BoardManager boardManager;
	
	public Pawn(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.boardManager = boardManager;
		// Pawns can only move forward, white up, black down.
		this.moveDirections = (team == 0 ? 1 : -1);
	}
	
	@Override
	public void setFirstMove() {
		super.setFirstMove();
	}
	
	@Override
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal) {
		ArrayList<String> validMoves = new ArrayList<>();
		int[] loc = BoardManager.locationStringToArray(locationString);
		int x = loc[0];
		int y = loc[1] + moveDirections;
		String moveString = x + " " + y;
		if (!boardManager.isPieceAtLocation(moveString, locationsLocal)) {
			validMoves.add(moveString);
			if (getFirstMove()) {
				// TODO check if this can be a problem when simulating more moves, that the piece doesn't get firstMove as true after first simulated move
				// doesnt seem to be when testing but haven't verified
				String moveString2 = x + " " + (y + moveDirections);
				if (!boardManager.isPieceAtLocation(moveString2, locationsLocal)) {
					validMoves.add(moveString2);
				}
			}
		}

		// TODO refactor this?
		ArrayList<int[]> pawnAttackDirection = new ArrayList<int[]>(
				Arrays.asList(new int[] { 1, moveDirections },
						new int[] { -1, moveDirections }));
		for (int[] attack : pawnAttackDirection) {
			x = loc[0] + attack[0];
			y = loc[1] + attack[1];
			if (x < 0 || x > 7)
				continue;
			moveString = x + " " + y;
			if (boardManager.isPieceAtLocation(moveString, locationsLocal) && !boardManager.isFriendly(locationString, moveString, locationsLocal)) {
				validMoves.add(moveString);
			}
		}
		return validMoves;
	}
	

}
