package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class Opponent {
	private BoardManager boardManager;
	private Ui ui;
private Random random;
	
	public Opponent(BoardManager boardManager, Ui ui) {
		this.boardManager = boardManager;
		this.ui = ui;
this.random = new Random();
	}

	public ArrayList<String> makeDecision() {
		ArrayList<String> decisions = new ArrayList<>();
		HashMap<String, Piece> locations = boardManager.getLocations();
		ArrayList<String> enemyLocStrings = new ArrayList<>();
		ArrayList<String> friendlyLocStrings = new ArrayList<>();
		locations.keySet().stream().forEach(locStr -> {
					if (locations.get(locStr).getTeam() != boardManager.whosTurn()) {
						enemyLocStrings.add(locStr);
					} else {
						friendlyLocStrings.add(locStr);
					}
		});
		
		
		// TODO start with getting validMoves and pick a random move. Make that work first and look for bugs.
		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves();
		
//TEMPORARY RANDOM UTILITY
		for (int i = 0; i < 25; i++) {
			if (friendlyLocStrings.isEmpty()) return decisions;
			int index = random.nextInt(friendlyLocStrings.size());
			String locStr = friendlyLocStrings.get(index);
			ArrayList<String> moveArray = validatedMoves.get(locStr);
			if (moveArray == null || moveArray.isEmpty()) {
				friendlyLocStrings.remove(index);
				continue;
			}
			int index2 = random.nextInt(moveArray.size());
			String moveStr = moveArray.get(index2);
			decisions.add(locStr);
			decisions.add(moveStr);
			break;
		}
// END
		// TODO teamsTurn % 2 in recursion. start with 1 then ++ every simulation. Check for valid moves every turn, for player and computer.
		return decisions;
	}
	
	
	

}
