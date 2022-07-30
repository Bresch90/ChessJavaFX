package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;

public class Pawn extends Piece {

	private int moveDirections;
	private BoardManager boardManager;

	public Pawn(int team, String kind, BoardManager boardManager) {
		super(team, kind);
		this.boardManager = boardManager;
		// Pawns can only move forward, white up, black down.
		this.moveDirections = (team == 0 ? 1 : -1);
	}
	
	@Override
	public void setFirstMove() {
		super.setFirstMove();
	}
	
	@Override
	public ArrayList<String> moves(String locationString, HashMap<String, Piece> locationsLocal, int[] checked) {
		ArrayList<String> validMoves = new ArrayList<>();
//		int[] loc = BoardManager.locationStringToArray(locationString); // 11-12000ms
//	String[] strArray = locationString.split(" ");
//	int[] loc = {Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1])}; // 11242ms
	
//	String[] strArray = locationString.split(" ");
//	int[] loc = {Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1])};
	
//	int locX = Integer.parseInt(strArray[0]); // 10000ms
//	int locY = Integer.parseInt(strArray[1]);
	
	int locX = locationString.charAt(0)-'0'; // 1300ms!!!!!! looks uggly but is sooo much faster..improved performance with more than 25%
	int locY = locationString.charAt(2)-'0';
	
		int x = locX;
		int y = locY + moveDirections;
		StringBuilder moveStringBuilder = new StringBuilder(3);
		moveStringBuilder.append((char) ('0'+ x));
		moveStringBuilder.append(' ');
		moveStringBuilder.append((char) ('0'+ y));
		String moveStr = moveStringBuilder.toString();
		
		
		
		if (!boardManager.isPieceAtLocation(moveStr, locationsLocal)) {
			validMoves.add(moveStr);
			if (getFirstMove()) {
				// TODO check if this can be a problem when simulating more moves, that the piece doesn't get firstMove as true after first simulated move
				// doesn't seem to be when testing but haven't verified. Though in theory the computer can move a pawn forward 2 steps multiple times when simulating...
				// don't know how to fix this without ruining performance even more..
				moveStringBuilder.setCharAt(2, (char) ('0'+ (y + moveDirections)));
				moveStr = moveStringBuilder.toString();
//				String moveString2 = x + " " + (y + moveDirections); old prettier...
				if (!boardManager.isPieceAtLocation(moveStr, locationsLocal)) {
					validMoves.add(moveStr);
				}
			}
		}

			x = locX + 1;
			y = locY + moveDirections;
			
			if (x > 0 || x < 7) {
				moveStringBuilder.setCharAt(0, (char) ('0'+ x));
				moveStringBuilder.setCharAt(2, (char) ('0'+ y));
				moveStr = moveStringBuilder.toString();
				Piece targetPiece = locationsLocal.get(moveStr);
				
				if (targetPiece != null) {
					if (super.team != targetPiece.getTeam()) {
						validMoves.add(moveStr);
						if (checked[0] == 1 && targetPiece.getKind().equals("king")) {
							checked[1] = 1;
						}
					} 
					
				}
			}

			x = locX - 1;
			if (x > 0 || x < 7) {
				
				moveStringBuilder.setCharAt(0, (char) ('0'+ x));
				moveStr = moveStringBuilder.toString();
				Piece targetPiece = locationsLocal.get(moveStr);
				
				if (targetPiece != null) {
					if (super.team != targetPiece.getTeam()) {
						validMoves.add(moveStr);
						if (checked[0] == 1 && targetPiece.getKind().equals("king")) {
							checked[1] = 1;
						}
					} 
					
				}
			}
		return validMoves;
	}
	

}
