package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Opponent {

	private BoardManager boardManager;
	private Ui ui;
	private int maxMoves;
private Random random;
	
	public Opponent(BoardManager boardManager, Ui ui) {
		this.boardManager = boardManager;
		this.ui = ui;
this.maxMoves = 1;
this.random = new Random();
	}

	public ArrayList<String> makeDecision() throws InterruptedException {
		ArrayList<String> decisions = new ArrayList<>();
		HashMap<String, Piece> locations = boardManager.getLocations();
		ArrayList<String> enemyLocStrings = new ArrayList<>();
		ArrayList<String> friendlyLocStrings = new ArrayList<>();
		int teamsTurn = boardManager.whosTurn();
		locations.keySet().stream().forEach(locStr -> {
					if (locations.get(locStr).getTeam() != teamsTurn) {
						enemyLocStrings.add(locStr);
					} else {
						friendlyLocStrings.add(locStr);
					}
		});
		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves();
		
		
		// TODO implement some sort of ranking of moves.
		// new class with moves to store moves and score?
		// only arraylist or hashmap with moves and score stored as string? e.g. 2 4:2 6 -> 20 (move from 2 4 to 2 6 gives a score of 20 in x moves forward)
		// recursion with multiple threads for the calculation?
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
		int numberOfMoves = (int) movesBeeingEvaluated.stream().count();
		CountDownLatch moveLatch = new CountDownLatch(numberOfMoves);
		movesBeeingEvaluated.stream().forEach(moveObject -> {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					moveObject.calculateScore();
					moveLatch.countDown();
//System.out.println(moveObject);
				}
			});
			thread.setDaemon(true);
			thread.start();
			
		});
		moveLatch.await(60, TimeUnit.SECONDS);
		moveLatch.await();
		// next, biggest score is decision or random of biggest.
		int maxScore = movesBeeingEvaluated.stream().mapToInt(MoveAndScore::getScore).max().orElse(Integer.MIN_VALUE);	
		ArrayList<MoveAndScore> movesFiltered = (ArrayList<MoveAndScore>) movesBeeingEvaluated.stream().filter(object -> object.getScore() == maxScore).collect(Collectors.toList());
//System.out.println(movesFiltered.stream().count() + "; " + movesBeeingEvaluated.toString());
		
		if (maxScore == Integer.MIN_VALUE) {
System.out.println("Didn't get a Max? How did this happen? Did i just loose??");
		} else {
			int randomIndex = random.nextInt((int) movesFiltered.stream().count());
			decisions = movesFiltered.get(randomIndex).getDecision();
System.out.println("getting filtered " + movesFiltered.get(randomIndex));
		}
		// TODO teamsTurn % 2 in recursion. start with 1 then ++ every simulation. Check for valid moves every turn, for player and computer.
//		makeRandom(decisions, friendlyLocStrings, validatedMoves);
		return decisions;
	}

	private ArrayList<String> makeRandom(ArrayList<String> decisions, ArrayList<String> friendlyLocStrings, HashMap<String, ArrayList<String>> validatedMoves) {
		//TEMPORARY RANDOM UTILITY
				// multiple random tries needed? why? if king is checked, and tries are limited to friendlyLocStrings.size() (even +2) 
				// it fails and gives up. Letting it go and moving on to scoring moves.
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
				return decisions;
		// END
		
	}
	
	
	

}
