package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece {

	private int moveDirections;
	private BoardManager boardManager;
	private int movesMade;
	
	public Pawn(int team, String kind, BoardManager boardManager) {
		super(team, kind, boardManager);
		this.boardManager = boardManager;
		// Pawns can only move forward, white up, black down.
		this.moveDirections = (team == 0 ? 1 : -1);
		this.movesMade = 0;
		
	}
	
	@Override
	public void setFirstMove() {
		movesMade++;
		super.setFirstMove();
	}
	
	@Override
	public ArrayList<String> moves(String locationString) {
		ArrayList<String> validMoves = new ArrayList<>();
		int[] loc = BoardManager.locationStringToArray(locationString);
		int x = loc[0];
		int y = loc[1] + moveDirections;
		String moveString = x + " " + y;
		if (!boardManager.isPieceAtLocation(moveString)) {
			validMoves.add(moveString);
			if (getFirstMove()) {
				// TODO make for first move move two etc. En Passant dont know how to..but
				String moveString2 = x + " " + (y + moveDirections);
				if (!boardManager.isPieceAtLocation(moveString2)) {
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
			if (boardManager.isPieceAtLocation(moveString) && !boardManager.isFriendly(locationString, moveString)) {
				validMoves.add(moveString);
			}
		}
		return validMoves;
	}
	

}
