package com.bresch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MoveAndScore {
static int counter = 0;
static long timeInValidatesMoveTotal = 0;
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
		// or this.locations = new HashMap<>(); + locations.putAll();
		this.locations = new HashMap<>();
		locations.putAll(boardManager.getLocations());
		this.team = team;
		this.moveFrom = moveFrom;
		this.moveTo = moveTo;
		score = 0;
		// simulate the move and add point if any
		this.maxMoves = maxMoves -1;
		boardManager.movePiece(moveTo, moveFrom, locations, true);
		
		
		// setup, sorting enemy/friendly to get score from moves
				ArrayList<String> whiteLocStrings = new ArrayList<>();
				ArrayList<String> blackLocStrings = new ArrayList<>();
					locations.keySet().stream().forEach(locStr -> {
							if (locations.get(locStr).getTeam() == 0) {
								whiteLocStrings.add(locStr);
							} else {
								blackLocStrings.add(locStr);
							}
				});
				HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves(locations, team);
				
			double whiteMoveScore = whiteLocStrings.stream().mapToDouble(locStr -> {
				ArrayList<String> validMoveList = validatedMoves.get(locStr);
				if (validMoveList == null) return 0;
				return validMoveList.size() * 0.1; 
			}).sum();
			double blackMoveScore = blackLocStrings.stream().mapToDouble(locStr -> {
				ArrayList<String> validMoveList = validatedMoves.get(locStr);
				if (validMoveList == null) return 0;
				return validMoveList.size() * -0.1; 
			}).sum();
		updateScore(boardManager.getScoreFromBoard(locations, whiteMoveScore + blackMoveScore));
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
	
	public long getTimeValidateMoves() {
		return timeInValidatesMoveTotal;
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
		ArrayList<String> whiteLocStrings = new ArrayList<>();
		ArrayList<String> blackLocStrings = new ArrayList<>();
//System.out.println("team is now = ["+team+"]" + "maxMovesLocal is now = ["+maxMovesLocal+"]" +"teamsTurn is then = ["+teamsTurn+"]");
			localLocations.keySet().stream().forEach(locStr -> {
					if (localLocations.get(locStr).getTeam() == 0) {
						whiteLocStrings.add(locStr);
					} else {
						blackLocStrings.add(locStr);
					}
		});
			
//counter++;
long timeStartValidateMoves = System.nanoTime();
		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves(localLocations, teamsTurn);
// Is validatedMoves only supplying root teams turns moves? not local? also maybe supply all moves and filter here? to score easier.
long timeEndValidateMoves = System.nanoTime();
timeInValidatesMoveTotal += (timeEndValidateMoves - timeStartValidateMoves);

// EXIT CASE
if (maxMovesLocal < 1) {
counter++;
//System.out.println("counter is now = ["+counter+"]");
// return value of board sum with white pieces being positive and black negative.

	double whiteMoveScore = whiteLocStrings.stream().mapToDouble(locStr -> {
		ArrayList<String> validMoveList = validatedMoves.get(locStr);
		if (validMoveList == null) return 0;
		return validMoveList.size() * 0.1; 
	}).sum();
	double blackMoveScore = blackLocStrings.stream().mapToDouble(locStr -> {
		ArrayList<String> validMoveList = validatedMoves.get(locStr);
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
				ArrayList<String> moveArray = validatedMoves.get(locStr);
				if (moveArray == null || moveArray.isEmpty()) {
					continue;
				}
		// for each piece (id by locStr and move to moveStr)
				for (String moveStr : moveArray) {
					HashMap<String, Piece> localLocationsCopy = new HashMap<>();
					localLocationsCopy.putAll(localLocations);
	//				if (localLocations.containsKey(moveStr)) {
	////System.out.println("omg yes more score!");
	////System.out.println("tring to get pos ["+moveStr+"] s from: \n" + newLocations.toString());
	//					updateScore((teamsTurn == 0 ? 1 : -1) * boardManager.getScoreFromKind(localLocations.get(moveStr).getKind()));
	//				}
					
		// Should this return ONLY ONE VALUE FOR THE LEAFNODE? 
		// does this fuck up if we can't go to maximum depth? eg. not that many moves can be done since we loose?
					
					boardManager.movePiece(moveStr, locStr, localLocationsCopy, true);
					childScore = Math.max(childScore, calculateScore(localLocationsCopy, maxMovesLocal-1, (teamsTurn == 0 ? 1 : 0), alpha, beta));
					alpha = Math.max(childScore, alpha);
		                if (beta <= alpha) break;
		                
	/// this seems to work but it values checking too much and offers pieces that get instant captured 
    // eg. checking king with its queen. putting it within striking distance of king without "cover" from other pieces
				
				}
			}
		} else {
		// Minimizing player
			childScore = Integer.MAX_VALUE;
			
			for (String locStr : blackLocStrings ) {
				ArrayList<String> moveArray = validatedMoves.get(locStr);
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
