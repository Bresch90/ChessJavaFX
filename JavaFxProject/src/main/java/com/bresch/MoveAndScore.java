package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;

public class MoveAndScore {
	
	private BoardManager boardManager;
	private HashMap<String, Piece> locations;
	private int team;
	private String moveFrom;
	private String moveTo;
	private int score;
	private int maxMoves;
	
	
	public MoveAndScore(BoardManager boardManager, int teamsTurn, int maxMoves, String moveFrom, String moveTo) {
		this.boardManager = boardManager;
		// or this.locations = new HashMap<>(); + locations.putAll();
		this.locations = boardManager.getLocations();
		this.team = teamsTurn;
		this.moveFrom = moveFrom;
		this.moveTo = moveTo;
		score = 0;
		// simulate the move and add point if any
		maxMoves *= 2;
		if (locations.containsKey(moveTo)) updateScore(boardManager.getScorePosition(moveTo));
		boardManager.movePiece(moveTo, moveFrom, locations, true);
	}
	
	public void updateScore(int valueToAdd) {
		score += valueToAdd;
	}
	public String getMoveFromTo() {
		return moveFrom + " -> " + moveTo + " [" + score + "]";
	}
	
	// simulate ALL the enemy's moves and calculate points for that. Start there.
	public void calculateScore(HashMap<String, Piece> localLocations) {
		if (maxMoves < 1) return;
		
		ArrayList<String> enemyLocStrings = new ArrayList<>();
		ArrayList<String> friendlyLocStrings = new ArrayList<>();
		int teamsTurn = maxMoves % 2;
		localLocations.keySet().stream().forEach(locStr -> {
					if (localLocations.get(locStr).getTeam() != teamsTurn) {
						enemyLocStrings.add(locStr);
					} else {
						friendlyLocStrings.add(locStr);
					}
		});
		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves(localLocations, teamsTurn);
		// continue here, think validatedMoves is correct? might be something wierd with the kings..
		ArrayList<MoveAndScore> movesBeeingEvaluated = new ArrayList<>();
		for (String locStr : friendlyLocStrings ) {
			ArrayList<String> moveArray = validatedMoves.get(locStr);
			if (moveArray == null || moveArray.isEmpty()) {
				continue;
			}
			for (String moveStr : moveArray) {
				movesBeeingEvaluated.add(new MoveAndScore(boardManager, teamsTurn, maxMoves, locStr, moveStr));
			}
		}
		
		maxMoves--;
		calculateScore(localLocations);
	}
	public void calculateScore() {
		calculateScore(locations);
	}

}
