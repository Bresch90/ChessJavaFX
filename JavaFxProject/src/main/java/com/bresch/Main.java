package com.bresch;

public class Main {
	public static void main(String[] args) {
		Ui.go(args);
	}
}
// My first real project. Wanted to try javafx and making a "simple" chess playing around with all the
// steps in making the project from dragging pieces and deploying it to a computer without javafx.
// Also to get some experience using git, maven and making a computer opponent.
// Current implementation is way too bruteforce and simulates each move to check if that move results in a check
// on the player making the move. eg a pawn moves and reveals a check from the queen on the middle of the board.
// Then that move shouldn't be valid. I have no prior experience programming and first started learning about programming
// about 4-5months before starting on this project while working full time as a laboratory technician.
// A lot of the opponent move checking can be improved considerably using different methods but I just wanted to make
// something from scratch without any help except if I get stuck for a long time.
// The minimax approach is one of those things I got stuck at for a long time and couldn't figure out on my own
// but still. Getting it working reasonably was still a hassle.

// Thanks for reading my rambles and hope you enjoy/get a good laugh/inspiration or 
// whatever you're after reading through my project!