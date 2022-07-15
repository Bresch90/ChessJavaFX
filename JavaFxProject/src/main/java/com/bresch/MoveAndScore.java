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
	private int score;
	private int maxMoves;
	
	
	public MoveAndScore(BoardManager boardManager, int teamsTurn, int maxMoves, String moveFrom, String moveTo) {
counter = 0;
		this.boardManager = boardManager;
		// or this.locations = new HashMap<>(); + locations.putAll();
		this.locations = new HashMap<>();
		locations.putAll(boardManager.getLocations());
		this.team = teamsTurn;
		this.moveFrom = moveFrom;
		this.moveTo = moveTo;
		score = 0;
		// simulate the move and add point if any
		this.maxMoves = maxMoves * 2;
		this.maxMoves--;
		if (locations.containsKey(moveTo)) updateScore(boardManager.getScorePosition(locations.get(moveTo).getKind()));
		boardManager.movePiece(moveTo, moveFrom, locations, true);
	}
	public ArrayList<String> getDecision(){
		return new ArrayList<>(Arrays.asList(moveFrom, moveTo, String.valueOf(counter)));
	}
	public int getScore() {
		return score;
	}
	public void updateScore(int valueToAdd) {
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
	public int calculateScore(HashMap<String, Piece> localLocations, int maxMovesLocal, int localScore) {
		HashMap<String, Piece> newLocations = new HashMap<>();
	// create new clone locations
		newLocations.putAll(localLocations);
		if (newLocations.values().contains(null)) {
System.out.println("when is here beeing null??\n" + newLocations.toString());
		}
	// EXIT CASE
		if (maxMovesLocal < 1) {
counter++;
//System.out.println("counter is now = ["+counter+"]");
			return localScore;
		}

	// setup, sorting enemy/friendly
		ArrayList<String> enemyLocStrings = new ArrayList<>();
		ArrayList<String> friendlyLocStrings = new ArrayList<>();
		int teamsTurn = (maxMovesLocal) % 2;
//System.out.println("team is now = ["+team+"]" + "maxMovesLocal is now = ["+maxMovesLocal+"]" +"teamsTurn is then = ["+teamsTurn+"]");
			newLocations.keySet().stream().forEach(locStr -> {
					if (newLocations.get(locStr).getTeam() != teamsTurn) {
						enemyLocStrings.add(locStr);
					} else {
						friendlyLocStrings.add(locStr);
					}
		});

		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves(newLocations, teamsTurn);
		
	// Main driver code for new children 
		for (String locStr : friendlyLocStrings ) {
			ArrayList<String> moveArray = validatedMoves.get(locStr);
			if (moveArray == null || moveArray.isEmpty()) {
				continue;
			}
	// for each piece (id by locStr and move to moveStr)
			for (String moveStr : moveArray) {
				HashMap<String, Piece> newLocations2 = new HashMap<>();
				newLocations2.putAll(newLocations);
				if (newLocations.containsKey(moveStr)) {
//System.out.println("omg yes more score!");
//System.out.println("tring to get pos ["+moveStr+"] s from: \n" + newLocations.toString());
					updateScore(((maxMovesLocal) % 2 == 0 ? 1 : -1) * boardManager.getScorePosition(newLocations.get(moveStr).getKind()));
				}
				boardManager.movePiece(moveStr, locStr, newLocations2, true);
				calculateScore(newLocations2, maxMovesLocal-1, localScore);
/// Dont know how to fix scoreing with minimax.
/// should chose a path fitting the move that is the best move. Should this sum() all moves? then chose? is this logic too simple? seems like it.
//				Tried implementing localScore but burned out.
			
			}
		}
		return localScore;
		
		
	}
	public void calculateScore() {
		calculateScore(locations, maxMoves, 0);
	}

}
