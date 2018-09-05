package guis;

import constants.Mode;
import constants.Textures;
import main.Game;
import objects.Pawn;

public class PlayScreen extends Gui {
	
	private static PlayScreen instance;
	
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
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void render() {
		super.renderBackground();
		Game.map.render();
		super.renderElements();
		for(Pawn p : Game.pawns)
			p.render();
	}
	
	/**
	 * Highlighs squares the current player can move to
	 */
	public void highlightMoveableSquares() {
		// TODO
	}
	
}
