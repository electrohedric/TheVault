package util;

import java.util.ArrayList;
import java.util.List;

import objects.Pawn;

public class TurnHandler {

	public static List<Pawn> turnOrder = new ArrayList<>();
	private static int currentTurn = 0;
	
	public static void init() {
		// just in case in the future we need it
	}
	
	/**
	 * Progresses to the next player's turn and sets up their turn
	 */
	public static void nextTurn() {
		currentTurn = (currentTurn + 1) % turnOrder.size();
		getPawn().resetTurn();
	}
	
	public static Pawn getPawn() {
		return turnOrder.get(currentTurn);
	}
}
