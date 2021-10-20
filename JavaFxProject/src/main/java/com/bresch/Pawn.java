package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece {

	private int moveDirections;
	private boolean firstMove;
	private BoardManager boardManager;
	
	public Pawn(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.boardManager = boardManager;
		this.moveDirections = (team == 0 ? 1 : -1);
		this.firstMove = true;
	}
	
	@Override
	public ArrayList<String> moves(String locationString) {
		ArrayList<String> validMoves = new ArrayList<>();
		int[] loc = BoardManager.locationStringToArray(locationString);
		int x = loc[0];
		int y = loc[1] + moveDirections;
		if (getFirstMove()) {
			// TODO make for first move move two etc. En Passant dont know how to..but
			// yea...
		}
		String moveString = x + " " + y;
		if (!boardManager.isPieceAtLocation(moveString)) {
			validMoves.add(moveString);
		}

		// TODO refactor this?
		// Pawns can only move forward, white up, black down.
		ArrayList<int[]> pawnAttackDirection = new ArrayList<int[]>(
				Arrays.asList(new int[] { 1, moveDirections },
						new int[] { -1, moveDirections }));
		for (int[] attack : pawnAttackDirection) {
			x = loc[0] + attack[0];
			y = loc[1] + attack[1];
			if (x < 0 || x > 7)
				continue;
			moveString = x + " " + y;
			if (boardManager.isPieceAtLocation(moveString) && !boardManager.isFriendly(locationString, moveString)) {
				validMoves.add(moveString);
			}
		}
		return validMoves;
	}
	
	public boolean getFirstMove() {
		return firstMove;
	}
	public void setFirstMove() {
		firstMove = false;
	}

}
