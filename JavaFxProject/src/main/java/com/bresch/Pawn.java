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
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal, ArrayList<Integer> checked) {
long timeStart = System.nanoTime();
		ArrayList<String> validMoves = new ArrayList<>();
//		int[] loc = BoardManager.locationStringToArray(locationString); // 11-12000ms
//	String[] strArray = locationString.split(" ");
//	int[] loc = {Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1])}; // 11242ms
	
//	String[] strArray = locationString.split(" ");
//	int[] loc = {Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1])};
	
//	int locX = Integer.parseInt(strArray[0]); // 10000ms
//	int locY = Integer.parseInt(strArray[1]);
	
	int locX = locationString.charAt(0)-'0'; // 1300ms!!!!!!
	int locY = locationString.charAt(2)-'0';
	
		int x = locX;
		int y = locY + moveDirections;
		StringBuilder moveStringBuilder = new StringBuilder(3);
//		StringBuilder moveStringBuilder = new StringBuilder();
		moveStringBuilder.append((char) ('0'+ x));
		moveStringBuilder.append(' ');
		moveStringBuilder.append((char) ('0'+ y));
		String moveStr = moveStringBuilder.toString();
		
		
		if (!boardManager.isPieceAtLocation(moveStr, locationsLocal)) {
			validMoves.add(moveStr);
			if (getFirstMove()) {
				// TODO check if this can be a problem when simulating more moves, that the piece doesn't get firstMove as true after first simulated move
				// doesnt seem to be when testing but haven't verified
				moveStringBuilder.setCharAt(2, (char) ('0'+ (y + moveDirections)));
				moveStr = moveStringBuilder.toString();
//				String moveString2 = x + " " + (y + moveDirections);
				if (!boardManager.isPieceAtLocation(moveStr, locationsLocal)) {
					validMoves.add(moveStr);
				}
			}
		}

		// TODO refactor this?

//		ArrayList<int[]> pawnAttackDirection = new ArrayList<int[]>(
//				Arrays.asList(new int[] { 1, moveDirections },
//						new int[] { -1, moveDirections }));
//		int attack1 = 1;
//		int attack2 = -1;
//		for (int[] attack : pawnAttackDirection) { // 23-26000ms
			x = locX + 1;
			y = locY + moveDirections;
			
			if (x > 0 || x < 7) {
				
//			moveString = x + " " + y;
	
				moveStringBuilder.setCharAt(0, (char) ('0'+ x));
				moveStringBuilder.setCharAt(2, (char) ('0'+ y));
				moveStr = moveStringBuilder.toString();
				
				if (boardManager.isPieceAtLocation(moveStr, locationsLocal)) {

					if (!boardManager.isFriendly(locationString, moveStr, locationsLocal)) {
						validMoves.add(moveStr);
						if (checked.get(0) == 1 && locationsLocal.get(moveStr).getKind().equals("king")) {
							checked.set(1, 1);
						}
					} 
					
				}
			}

			x = locX - 1;
			if (x > 0 || x < 7) {
				
				moveStringBuilder.setCharAt(0, (char) ('0'+ x));
				moveStr = moveStringBuilder.toString();
//			moveString = x + " " + y;
				if (boardManager.isPieceAtLocation(moveStr, locationsLocal)) {
					if (!boardManager.isFriendly(locationString, moveStr, locationsLocal)) {
						validMoves.add(moveStr);
						if (checked.get(0) == 1 && locationsLocal.get(moveStr).getKind().equals("king")) {
							checked.set(1, 1);
						}
					} 
					
				}
			}
//		}
		
long timeEnd = System.nanoTime();
Piece.time += (timeEnd-timeStart);
		return validMoves;
	}
	

}
