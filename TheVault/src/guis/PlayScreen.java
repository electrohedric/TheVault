package guis;

import constants.Mode;
import constants.Textures;
import main.Game;
import objects.Pawn;
import util.PawnHandler;

public class PlayScreen extends Gui {
	
	private static PlayScreen instance;
	// TODO do text rendering (mostly for cards.) not particularly in this class but just do it
	// FIXME NEXT add buttons that may pop up if they're on a square that can do an action
	// such stuff like forges would then bring up card lists to choose from.
	// Gem squares and such would add a button that when clicked gives them a random card
	
	public PlayScreen() {
		super(Textures.get("shaded"));
		
		instance = this;
	}
	
	public static PlayScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new PlayScreen();
	}

	@Override
	public void switchTo() {
		Game.mode = Mode.PLAY;
		for(Pawn p : Game.pawns) // disable all pawns initially 
			p.setGrabable(false);
		PawnHandler.getInstance().getPawn().resetTurn(); // enable first pawn to act
	}
	
	@Override
	public void update() {
		for(Pawn p : Game.pawns)
			p.update();
	}
	
	@Override
	public void render() {
		super.renderBackground();
		Game.map.render();
		super.renderElements();
		PawnHandler.getInstance().renderPawns();
	}
	
}
