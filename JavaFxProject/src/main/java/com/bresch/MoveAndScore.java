package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MoveAndScore {
static int counter = 0;
	private BoardManager boardManager;
	private HashMap<String, Piece> locations;
	private int team;
	private String moveFrom;
	private String moveTo;
	private double score;
	private int maxMoves;
//////IF TEAM == 0 THEN ITS WHITE!
	
	public MoveAndScore(BoardManager boardManager, int team, int maxMoves, String moveFrom, String moveTo) {
counter = 0;
		this.boardManager = boardManager;
		this.locations = new HashMap<>();
		locations.putAll(boardManager.getLocations());
		this.team = team;
		this.moveFrom = moveFrom;
		this.moveTo = moveTo;
		score = 0;
		this.maxMoves = maxMoves -1;
		// making the firstmove when its created since each move get a different object.
		boardManager.movePiece(moveTo, moveFrom, locations, true);
	}
	public ArrayList<String> getDecision(){
		return new ArrayList<>(Arrays.asList(moveFrom, moveTo, String.valueOf(counter)));
	}
	public double getScore() {
		return score;
	}
	public void updateScore(double valueToAdd) {
		score += valueToAdd;
	}
	@Override
	public String toString() {
		return moveFrom + " -> " + moveTo + " [" + score + "]";
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////// Recursive minimax ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// simulate ALL the enemy's moves and calculate points for that. Start there.
	public double calculateScore(HashMap<String, Piece> originalLocations, int maxMovesLocal, int teamsTurn, double alpha, double beta) {
		
		HashMap<String, Piece> localLocations = new HashMap<>();
	// create new clone locations
		localLocations.putAll(originalLocations);

	// setup, sorting enemy/friendly
		// maybe this loop is unnecessary? maybe everything should just be one giant loop 
		// and first check what team location belongs to?
		ArrayList<String> whiteLocStrings = new ArrayList<>();
		ArrayList<String> blackLocStrings = new ArrayList<>();
			localLocations.keySet().stream().forEach(locStr -> {
					if (localLocations.get(locStr).getTeam() == 0) {
						whiteLocStrings.add(locStr);
					} else {
						blackLocStrings.add(locStr);
					}
		});
			
counter++;
		
		HashMap<String, ArrayList<String>> validatedMovesWhite = new HashMap<>();
		HashMap<String, ArrayList<String>> validatedMovesBlack = new HashMap<>();
		
// getting moves is really heavy
				boardManager.getValidatedMoves(localLocations, teamsTurn, validatedMovesWhite, validatedMovesBlack);

// EXIT CASE
if (maxMovesLocal < 1) {
// return value of board sum with white pieces being positive and black negative.
	double whiteMoveScore = whiteLocStrings.stream().mapToDouble(locStr -> {
		ArrayList<String> validMoveList = validatedMovesWhite.get(locStr);
		if (validMoveList == null) return 0;
		return validMoveList.size() * 0.1; 
	}).sum();
	double blackMoveScore = blackLocStrings.stream().mapToDouble(locStr -> {
		ArrayList<String> validMoveList = validatedMovesBlack.get(locStr);
		if (validMoveList == null) return 0;
		return validMoveList.size() * -0.1; 
	}).sum();
	return boardManager.getScoreFromBoard(localLocations, whiteMoveScore + blackMoveScore);
}

///////////////////// minimax with alpha beta pruning
// Maybe also add counting nodes to get number of moves a player can make to add to score?/////////////////
		double childScore = 0;
		if (teamsTurn == 0) {
		// Maximizing player (white)
		// Main driver loop for new children 
			childScore = Integer.MIN_VALUE;
			
			for (String locStr : whiteLocStrings ) {

				ArrayList<String> moveArray = validatedMovesWhite.get(locStr);
				if (moveArray == null || moveArray.isEmpty()) {
					continue;
				}
		// for each piece (id by locStr and move to moveStr)
				for (String moveStr : moveArray) {
					HashMap<String, Piece> localLocationsCopy = new HashMap<>();
					localLocationsCopy.putAll(localLocations);
					
					boardManager.movePiece(moveStr, locStr, localLocationsCopy, true);
					childScore = Math.max(childScore, calculateScore(localLocationsCopy, maxMovesLocal-1, (teamsTurn == 0 ? 1 : 0), alpha, beta));
					
					alpha = Math.max(childScore, alpha);
	                if (beta <= alpha) break;
				}
			}
		} else {
		// Minimizing player
			childScore = Integer.MAX_VALUE;
			
			for (String locStr : blackLocStrings ) {
				ArrayList<String> moveArray = validatedMovesBlack.get(locStr);
				if (moveArray == null || moveArray.isEmpty()) {
					continue;
				}
		// for each piece (id by locStr and move to moveStr)
				for (String moveStr : moveArray) {
					HashMap<String, Piece> localLocationsCopy = new HashMap<>();
					localLocationsCopy.putAll(localLocations);
					
					boardManager.movePiece(moveStr, locStr, localLocationsCopy, true);
					childScore = Math.min(childScore, calculateScore(localLocationsCopy, maxMovesLocal-1, (teamsTurn == 0 ? 1 : 0), alpha, beta));

					beta = Math.min(childScore, beta);
	                if (beta <= alpha) break;

				}
			}
		}

		return childScore;
		
		
	}
	public void calculateScore() {
		updateScore(calculateScore(locations, maxMoves, (team == 0 ? 1 : 0), Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

}
