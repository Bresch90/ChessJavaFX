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
System.out.println("making decision");
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
		for (int i = 0; i < friendlyLocStrings.size() ; i++) {
			int index = random.nextInt(friendlyLocStrings.size());
			String locStr = friendlyLocStrings.get(index);
			ArrayList<String> moveArray = validatedMoves.get(locStr);
			if (moveArray == null || moveArray.isEmpty()) {
System.out.println("Can't move ["+locStr+"]");
				continue;
			}
			System.out.println(moveArray.toString());
			int index2 = random.nextInt(moveArray.size());
			String moveStr = moveArray.get(index2);
			decisions.add(locStr);
			decisions.add(moveStr);
			break;
		}
// END
if (decisions.isEmpty()) {
	System.out.println("* I give up *");
	return decisions;
}
		// TODO teamsTurn % 2 in recursion. start with 1 the ++ every simulation. Check for valid moves every turn, for player and computer.
System.out.println("* I decide on [" + decisions.get(0) + "] to [" + decisions.get(1) + "]");
		return decisions;
	}
	
	
	

}
