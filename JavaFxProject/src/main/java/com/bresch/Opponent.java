package com.bresch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Opponent {
	private BoardManager boardManager;
	private int maxMoves;
	private int teamsTurn;
private Random random;
	
	public Opponent(BoardManager boardManager) {
		this.boardManager = boardManager;
this.maxMoves = 3;
this.random = new Random();
	}

	public ArrayList<String> makeDecision() throws InterruptedException {
		
		ArrayList<String> decisions = new ArrayList<>();
		HashMap<String, Piece> locations = boardManager.getLocations();
		ArrayList<String> blackLocStrings = new ArrayList<>();
		ArrayList<String> whiteLocStrings = new ArrayList<>();
		teamsTurn = boardManager.whosTurn();
		locations.keySet().stream().forEach(locStr -> {
					if (locations.get(locStr).getTeam() == 0) {
						whiteLocStrings.add(locStr);
					} else {
						blackLocStrings.add(locStr);
					}
		});
		HashMap<String, ArrayList<String>> validatedMoves = boardManager.getValidatedMoves();
		
		

		// recursion with multiple threads for the calculation
		ArrayList<MoveAndScore> movesBeeingEvaluated = new ArrayList<>();
		for (String locStr : (teamsTurn == 0 ? whiteLocStrings : blackLocStrings) ) {
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

//// To run in single thread for debug

//		for (MoveAndScore moveObject : movesBeeingEvaluated) {
//			moveObject.calculateScore();
//			moveLatch.countDown();
//		}


//// To run in multi thread for performance		
		
		movesBeeingEvaluated.stream().forEach(moveObject -> {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					moveObject.calculateScore();
					moveLatch.countDown();
				}
			});
			thread.setDaemon(true);
			thread.start();
			
		});
		moveLatch.await();
		if (movesBeeingEvaluated.isEmpty()) {
			return decisions;
		}
		
		// next, biggest score is decision or random of biggest. if maximizing
		if (teamsTurn == 0) {
			double maxScore = movesBeeingEvaluated.stream().mapToDouble(MoveAndScore::getScore).max().orElse(Integer.MIN_VALUE);	
			ArrayList<MoveAndScore> movesFiltered = (ArrayList<MoveAndScore>) movesBeeingEvaluated.stream().filter(object -> object.getScore() == maxScore).collect(Collectors.toList());
			if (maxScore == Integer.MIN_VALUE) {
	System.out.println("Didn't get a Max? How did this happen? Did i just loose??");
			} else {
				int randomIndex = random.nextInt((int) movesFiltered.stream().count());
				decisions = movesFiltered.get(randomIndex).getDecision();
	System.out.println("getting filtered " + movesFiltered.get(randomIndex));
			}
		} else {
// If minimizing player (Black)
			double minScore = movesBeeingEvaluated.stream().mapToDouble(MoveAndScore::getScore).min().orElse(Integer.MAX_VALUE);	
			ArrayList<MoveAndScore> movesFiltered = (ArrayList<MoveAndScore>) movesBeeingEvaluated.stream().filter(object -> object.getScore() == minScore).collect(Collectors.toList());
			if (minScore == Integer.MAX_VALUE) {
	System.out.println("Didn't get a Max? How did this happen? Did i just loose??");
			} else {
				int randomIndex = random.nextInt((int) movesFiltered.stream().count());
				decisions = movesFiltered.get(randomIndex).getDecision();
	System.out.println("getting filtered " + movesFiltered.get(randomIndex));
			}
		}
		return decisions;
	} 

	public ArrayList<String> makeRandom(ArrayList<String> decisions, ArrayList<String> friendlyLocStrings, HashMap<String, ArrayList<String>> validatedMoves) {
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
